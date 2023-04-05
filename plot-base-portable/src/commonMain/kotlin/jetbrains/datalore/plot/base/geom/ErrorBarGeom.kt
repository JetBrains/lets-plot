/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleSegment
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.aes.AesScaling
import jetbrains.datalore.plot.base.geom.util.GeomHelper
import jetbrains.datalore.plot.base.geom.util.GeomUtil
import jetbrains.datalore.plot.base.geom.util.HintColorUtil
import jetbrains.datalore.plot.base.geom.util.HintsCollection
import jetbrains.datalore.plot.base.geom.util.HintsCollection.HintConfigFactory
import jetbrains.datalore.plot.base.interact.GeomTargetCollector
import jetbrains.datalore.plot.base.interact.TipLayoutHint
import jetbrains.datalore.plot.base.render.LegendKeyElementFactory
import jetbrains.datalore.plot.base.render.SvgRoot
import jetbrains.datalore.vis.svg.SvgGElement
import jetbrains.datalore.vis.svg.SvgLineElement

open class ErrorBarGeom : GeomBase() {

    override val legendKeyElementFactory: LegendKeyElementFactory
        get() = ErrorBarLegendKeyElementFactory(::errorBarShapeSegments)

    protected open fun dataPoints(aesthetics: Aesthetics): Iterable<DataPointAesthetics> {
        return GeomUtil.withDefined(
            aesthetics.dataPoints(),
            Aes.X,
            Aes.YMIN,
            Aes.YMAX,
            Aes.WIDTH
        )
    }

    protected open fun errorBarRectangle(p: DataPointAesthetics, ctx: GeomContext): DoubleRectangle {
        val x = p.x()!!
        val ymin = p.ymin()!!
        val ymax = p.ymax()!!
        val width = p.width()!! * ctx.getResolution(Aes.X)
        val height = ymax - ymin
        return DoubleRectangle(x - width / 2, ymin, width, height)
    }

    protected open fun errorBarShapeSegments(r: DoubleRectangle): List<DoubleSegment> {
        val left = r.left
        val top = r.top
        val right = r.right
        val bottom = r.bottom
        val center = left + r.width / 2
        return listOf(
            DoubleSegment(DoubleVector(left, top), DoubleVector(right, top)),
            DoubleSegment(DoubleVector(left, bottom), DoubleVector(right, bottom)),
            DoubleSegment(DoubleVector(center, top), DoubleVector(center, bottom))
        )
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

        for (p in dataPoints(aesthetics)) {
            val rect = errorBarRectangle(p, ctx)
            val g = errorBarShape(errorBarShapeSegments(rect), p, geomHelper)
            root.add(g)

            buildHints(p, ctx, geomHelper, colorsByDataPoint, root)
        }
    }

    protected open fun buildHints(
        p: DataPointAesthetics,
        ctx: GeomContext,
        geomHelper: GeomHelper,
        colorsByDataPoint: (DataPointAesthetics) -> List<Color>,
        root: SvgRoot
    ) {
        val rect = with(errorBarRectangle(p, ctx)) {
            DoubleRectangle(left, center.y, width, 0.0)
        }
        val clientRect = geomHelper.toClient(rect, p)
        val objectRadius = clientRect?.run {
            if (ctx.flipped) {
                height / 2.0
            } else {
                width / 2.0
            }
        }!!

        val hint = HintConfigFactory()
            .defaultObjectRadius(objectRadius)
            .defaultCoord(p.x()!!)
            .defaultKind(
                if (ctx.flipped) {
                    TipLayoutHint.Kind.ROTATED_TOOLTIP
                } else {
                    TipLayoutHint.Kind.HORIZONTAL_TOOLTIP
                }
            )

        val hints = HintsCollection(p, geomHelper)
            .addHint(hint.create(Aes.YMAX))
            .addHint(hint.create(Aes.YMIN))
            .hints

        ctx.targetCollector.addRectangle(
            p.index(),
            clientRect,
            GeomTargetCollector.TooltipParams(
                tipLayoutHints = hints,
                markerColors = colorsByDataPoint(p)
            ),
            tooltipKind = if (ctx.flipped) {
                TipLayoutHint.Kind.VERTICAL_TOOLTIP
            } else {
                TipLayoutHint.Kind.HORIZONTAL_TOOLTIP
            }
        )
    }

    private class ErrorBarLegendKeyElementFactory(private val shapeFactory: (DoubleRectangle) -> List<DoubleSegment>) :
        LegendKeyElementFactory {

        override fun createKeyElement(p: DataPointAesthetics, size: DoubleVector): SvgGElement {
            val strokeWidth = AesScaling.strokeWidth(p)

            val width = p.width()!! * (size.x - strokeWidth)
            val height = size.y - strokeWidth
            val x = (size.x - width) / 2
            val y = strokeWidth / 2
            return errorBarLegendShape(
                shapeFactory(DoubleRectangle(x, y, width, height)),
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

        private fun errorBarShape(segments: List<DoubleSegment>, p: DataPointAesthetics, geomHelper: GeomHelper) : SvgGElement {
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
