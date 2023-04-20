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

class ErrorBarGeom : GeomBase() {

    override val legendKeyElementFactory: LegendKeyElementFactory
        get() = ErrorBarLegendKeyElementFactory()

    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val geomHelper = GeomHelper(pos, coord, ctx)
        val colorsByDataPoint = HintColorUtil.createColorMarkerMapper(GeomKind.ERROR_BAR, ctx)

        val vDataPoints = GeomUtil.withDefined(aesthetics.dataPoints(), Aes.X, Aes.YMIN, Aes.YMAX, Aes.WIDTH)
        if (vDataPoints.any()) {
            buildVertical(root, vDataPoints, ctx, geomHelper, colorsByDataPoint)
        } else {
            val hDataPoints = GeomUtil.withDefined(aesthetics.dataPoints(), Aes.Y, Aes.XMIN, Aes.XMAX, Aes.HEIGHT)
            buildHorizontal(root, hDataPoints, ctx, geomHelper, colorsByDataPoint)
        }
    }

    private fun buildVertical(
        root: SvgRoot,
        dataPoints: Iterable<DataPointAesthetics>,
        ctx: GeomContext,
        geomHelper: GeomHelper,
        colorsByDataPoint: (DataPointAesthetics) -> List<Color>
    ) {
        for (p in dataPoints) {
            val x = p.x()!!
            val ymin = p.ymin()!!
            val ymax = p.ymax()!!
            val width = p.width()!! * ctx.getResolution(Aes.X)
            val height = ymax - ymin

            val r = DoubleRectangle(x - width / 2, ymin, width, height)
            val segments = errorBarVerticalShape(r)
            val g = errorBarShape(segments, p, geomHelper)
            root.add(g)

            buildHints(
                DoubleRectangle(r.left, r.center.y, r.width, 0.0),
                p,
                ctx,
                geomHelper,
                colorsByDataPoint,
                isVerticalGeom = true
            )
        }
    }

    private fun buildHorizontal(
        root: SvgRoot,
        dataPoints: Iterable<DataPointAesthetics>,
        ctx: GeomContext,
        geomHelper: GeomHelper,
        colorsByDataPoint: (DataPointAesthetics) -> List<Color>
    ) {
        for (p in dataPoints) {
            val y = p.y()!!
            val xmin = p.xmin()!!
            val xmax = p.xmax()!!
            val height = p.height()!! * ctx.getResolution(Aes.Y)
            val width = xmax - xmin

            val r = DoubleRectangle(xmin, y - height / 2, width, height)
            val segments = errorBarHorizontalShape(r)
            val g = errorBarShape(segments, p, geomHelper)
            root.add(g)

            buildHints(
                DoubleRectangle(r.center.x, r.top, 0.0, r.height),
                p,
                ctx,
                geomHelper,
                colorsByDataPoint,
                isVerticalGeom = false
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

        val lower = if (isVerticalGeom) Aes.YMIN else Aes.XMIN
        val upper = if (isVerticalGeom) Aes.YMAX else Aes.XMAX
        val hints = HintsCollection(p, geomHelper)
            .addHint(hint.create(lower))
            .addHint(hint.create(upper))
            .hints

        ctx.targetCollector.addRectangle(
            p.index(),
            clientRect,
            GeomTargetCollector.TooltipParams(
                tipLayoutHints = hints,
                markerColors = colorsByDataPoint(p)
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
                errorBarVerticalShape(DoubleRectangle(x, y, width, height)),
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

        private fun errorBarVerticalShape(r: DoubleRectangle): List<DoubleSegment> {
            val center = r.left + r.width / 2
            return with(r) {
                listOf(
                    DoubleSegment(DoubleVector(left, top), DoubleVector(right, top)),
                    DoubleSegment(DoubleVector(left, bottom), DoubleVector(right, bottom)),
                    DoubleSegment(DoubleVector(center, top), DoubleVector(center, bottom))
                )
            }
        }

        private fun errorBarHorizontalShape(r: DoubleRectangle): List<DoubleSegment> {
            val center = r.top + r.height / 2
            return with(r) {
                listOf(
                    DoubleSegment(DoubleVector(left, top), DoubleVector(left, bottom)),
                    DoubleSegment(DoubleVector(right, top), DoubleVector(right, bottom)),
                    DoubleSegment(DoubleVector(left, center), DoubleVector(right, center))
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
