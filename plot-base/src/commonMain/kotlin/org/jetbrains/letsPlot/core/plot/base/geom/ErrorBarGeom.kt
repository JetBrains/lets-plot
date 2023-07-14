/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleSegment
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.aes.AesScaling
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomHelper
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomUtil
import org.jetbrains.letsPlot.core.plot.base.geom.util.HintColorUtil
import org.jetbrains.letsPlot.core.plot.base.geom.util.HintsCollection
import org.jetbrains.letsPlot.core.plot.base.geom.util.HintsCollection.HintConfigFactory
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetCollector
import org.jetbrains.letsPlot.core.plot.base.tooltip.TipLayoutHint
import org.jetbrains.letsPlot.core.plot.base.render.LegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgGElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgLineElement

class ErrorBarGeom(private val isVertical: Boolean) : GeomBase() {

    override val legendKeyElementFactory: LegendKeyElementFactory
        get() = ErrorBarLegendKeyElementFactory()

    override val wontRender: List<Aes<*>>
        get() {
            return if (isVertical) {
                listOf(Aes.Y, Aes.XMIN, Aes.XMAX, Aes.HEIGHT)
            } else {
                listOf(Aes.X, Aes.YMIN, Aes.YMAX, Aes.WIDTH)
            }
        }

    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val geomHelper = GeomHelper(pos, coord, ctx)
        val colorsByDataPoint = HintColorUtil.createColorMarkerMapper(GeomKind.ERROR_BAR, ctx)

        val xAes = if (isVertical) Aes.X else Aes.Y
        val minAes = if (isVertical) Aes.YMIN else Aes.XMIN
        val maxAes = if (isVertical) Aes.YMAX else Aes.XMAX
        val widthAes = if (isVertical) Aes.WIDTH else Aes.HEIGHT

        val dataPoints = GeomUtil.withDefined(aesthetics.dataPoints(), xAes, minAes, maxAes, widthAes)

        for (p in dataPoints) {
            val x = p[xAes]!!
            val ymin = p[minAes]!!
            val ymax = p[maxAes]!!

            val width = p[widthAes]!! * ctx.getResolution(xAes)
            val height = ymax - ymin

            val rect = DoubleRectangle(x - width / 2, ymin, width, height)
            val segments = errorBarShapeSegments(rect).map {
                when (isVertical) {
                    true -> it
                    false -> DoubleSegment(it.start.flip(), it.end.flip())
                }
            }
            val g = errorBarShape(segments, p, geomHelper)
            root.add(g)

            val hintRect = DoubleRectangle(rect.left, rect.center.y, rect.width, 0.0).let {
                when (isVertical) {
                    true -> it
                    false -> it.flip()
                }
            }
            buildHints(
                hintRect,
                p,
                ctx,
                geomHelper,
                colorsByDataPoint,
                isVerticalGeom = isVertical
            )
        }
    }

    private fun buildHints(
        rect: DoubleRectangle,
        p: DataPointAesthetics,
        ctx: GeomContext,
        geomHelper: GeomHelper,
        colorsByDataPoint: (DataPointAesthetics) -> List<Color>,
        isVerticalGeom: Boolean
    ) {
        val isVerticallyOriented = if (isVerticalGeom) !ctx.flipped else ctx.flipped

        val clientRect = geomHelper.toClient(rect, p)
        val objectRadius = clientRect?.run {
            if (isVerticallyOriented) {
                width / 2.0
            } else {
                height / 2.0
            }
        }!!

        val aes = if (isVerticalGeom) Aes.X else Aes.Y
        val hint = HintConfigFactory()
            .defaultObjectRadius(objectRadius)
            .defaultCoord(p[aes]!!)
            .defaultKind(
                if (isVerticallyOriented) {
                    TipLayoutHint.Kind.HORIZONTAL_TOOLTIP
                } else {
                    TipLayoutHint.Kind.ROTATED_TOOLTIP
                }
            )

        val minAes = if (isVerticalGeom) Aes.YMIN else Aes.XMIN
        val maxAes = if (isVerticalGeom) Aes.YMAX else Aes.XMAX
        val hints = HintsCollection(p, geomHelper)
            .addHint(hint.create(minAes))
            .addHint(hint.create(maxAes))
            .hints

        ctx.targetCollector.addRectangle(
            p.index(),
            clientRect,
            GeomTargetCollector.TooltipParams(
                tipLayoutHints = hints,
                markerColors = colorsByDataPoint(p),
                fillColor = HintColorUtil.colorWithAlpha(p)
            ),
            tooltipKind = if (isVerticallyOriented) {
                TipLayoutHint.Kind.HORIZONTAL_TOOLTIP
            } else {
                TipLayoutHint.Kind.VERTICAL_TOOLTIP
            }
        )
    }

    internal class ErrorBarLegendKeyElementFactory : LegendKeyElementFactory {

        override fun createKeyElement(p: DataPointAesthetics, size: DoubleVector): SvgGElement {
            val strokeWidth = AesScaling.strokeWidth(p)

            val width = p.width()!! * (size.x - strokeWidth)
            val height = size.y - strokeWidth
            val x = (size.x - width) / 2
            val y = strokeWidth / 2
            return errorBarLegendShape(
                errorBarShapeSegments(DoubleRectangle(x, y, width, height)),
                p
            )
        }
    }

    companion object {
        const val HANDLES_GROUPS = false

        private fun errorBarLegendShape(segments: List<DoubleSegment>, p: DataPointAesthetics): SvgGElement {
            val g = SvgGElement()
            segments.forEach { segment ->
                val shapeLine = SvgLineElement(segment.start.x, segment.start.y, segment.end.x, segment.end.y)
                GeomHelper.decorate(shapeLine, p)
                g.children().add(shapeLine)
            }
            return g
        }

        private fun errorBarShapeSegments(r: DoubleRectangle): List<DoubleSegment> {
            val center = r.left + r.width / 2
            return with(r) {
                listOf(
                    DoubleSegment(DoubleVector(left, top), DoubleVector(right, top)),
                    DoubleSegment(DoubleVector(left, bottom), DoubleVector(right, bottom)),
                    DoubleSegment(DoubleVector(center, top), DoubleVector(center, bottom))
                )
            }
        }

        private fun errorBarShape(
            segments: List<DoubleSegment>,
            p: DataPointAesthetics,
            geomHelper: GeomHelper
        ): SvgGElement {
            val g = SvgGElement()
            val elementHelper = geomHelper.createSvgElementHelper()
            elementHelper.setStrokeAlphaEnabled(true)
            segments.forEach { segment ->
                g.children().add(
                    elementHelper.createLine(segment.start, segment.end, p)!!
                )
            }
            return g
        }
    }
}
