/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil.finiteOrNull
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.geom.util.AnnotationsUtil
import org.jetbrains.letsPlot.core.plot.base.geom.util.RectangleTooltipHelper
import org.jetbrains.letsPlot.core.plot.base.geom.util.RectanglesHelper
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import org.jetbrains.letsPlot.core.plot.base.render.svg.MultilineLabel
import org.jetbrains.letsPlot.core.plot.base.render.svg.Text
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgNode

open class BarGeom : GeomBase() {

    override fun rangeIncludesZero(aes: Aes<*>): Boolean = (aes == Aes.Y)

    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val helper = RectanglesHelper(aesthetics, pos, coord, ctx, clientRectByDataPoint(ctx))
        val tooltipHelper = RectangleTooltipHelper(pos, coord, ctx)
        val rectangles = mutableListOf<SvgNode>()
        if (coord.isLinear) {
            helper.createRectangles { aes, svgNode, rect ->
                rectangles.add(svgNode)
                tooltipHelper.addTarget(aes, rect)
            }
        } else {
            helper.createNonLinearRectangles { aes, svgNode, polygon ->
                rectangles.add(svgNode)
                tooltipHelper.addTarget(aes, polygon)
            }
        }
        rectangles.reverse() // TODO: why reverse?
        rectangles.forEach(root::add)

        if (coord.isLinear) {
            ctx.annotations?.let { buildAnnotations(root, helper, coord, ctx) }
        }
    }

    private fun buildAnnotations(
        root: SvgRoot,
        rectanglesHelper: RectanglesHelper,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val annotations = ctx.annotations ?: return
        val viewPort = overallAesBounds(ctx).let(coord::toClient) ?: return

        val padding = annotations.textStyle.size / 2
        val isHorizontallyOriented = ctx.flipped

        val rectangles = mutableListOf<Triple<DataPointAesthetics, DoubleRectangle, Boolean>>()
        rectanglesHelper.iterateRectangleGeometry { p, rect ->
            val clientRect = rectanglesHelper.toClient(rect, p) ?: return@iterateRectangleGeometry

            val isNegative = rect.dimension.y < 0
            rectangles.add(Triple(p, clientRect, isNegative))
        }

        rectangles
            .groupBy { (_, rect) ->
                if (isHorizontallyOriented) rect.center.y else rect.center.x
            }
            .forEach { (_, bars) ->
                val barsCount = bars.size
                bars
                    .sortedBy { (_, rect) ->
                        if (isHorizontallyOriented) rect.center.x else rect.center.y
                    }
                    .forEachIndexed { index, (p, barRect, isNegative) ->
                        val text = annotations.getAnnotationText(p.index())
                        val textSize = AnnotationsUtil.textSizeGetter(annotations.textStyle, ctx).invoke(text, p)

                        val (hAlignment, textRect) = placeLabel(
                            barRect,
                            index,
                            barsCount,
                            textSize,
                            padding,
                            viewPort,
                            isHorizontallyOriented,
                            isNegative
                        )
                            ?: return@forEachIndexed

                        val alpha: Double
                        val labelColor = when {
                            barRect.contains(textRect) -> {
                                alpha = 0.0
                                AnnotationsUtil.chooseColor(p.fill()!!)
                            }
                            else -> {
                                alpha = 0.75
                                ctx.penColor
                            }
                        }

                        var location = DoubleVector(
                            x = when (hAlignment) {
                                Text.HorizontalAnchor.LEFT -> textRect.left
                                Text.HorizontalAnchor.RIGHT -> textRect.right
                                Text.HorizontalAnchor.MIDDLE -> textRect.center.x
                            },
                            y = textRect.top
                        )

                        // separate label for each line
                        val labels = MultilineLabel.splitLines(text).map { line ->
                            AnnotationsUtil.createLabelElement(
                                line,
                                location,
                                textParams = AnnotationsUtil.TextParams(
                                    style = annotations.textStyle,
                                    color = labelColor,
                                    hjust = hAlignment.toString().lowercase(),
                                    vjust = "top",
                                    fill = ctx.backgroundColor,
                                    alpha = alpha
                                ),
                                geomContext = ctx,
                                boundsCenter = viewPort.center
                            ).also {
                                location = location.add(DoubleVector(0.0, annotations.textStyle.size))
                            }
                        }
                        labels.forEach(root::add)
                    }
            }
    }

    internal fun DoubleRectangle.contains(other: DoubleRectangle): Boolean {
        return other.xRange() in xRange() && other.yRange() in yRange()
    }

    private fun placeLabel(
        barRect: DoubleRectangle,
        index: Int,
        barsCount: Int,
        textSize: DoubleVector,
        padding: Double,
        viewPort: DoubleRectangle,
        isHorizontallyOriented: Boolean,
        isNegative: Boolean
    ): Pair<Text.HorizontalAnchor, DoubleRectangle>? {

        val coordSelector: (DoubleVector) -> Double =
            if (isHorizontallyOriented) DoubleVector::x else DoubleVector::y

        var insideBar = when {
            barsCount == 1 -> {
                // use left (for horizontally orientated) or bottom (of the vertical bar)
                if (isHorizontallyOriented) PlacementInsideBar.MIN else PlacementInsideBar.MAX
            }
            index == 0 -> PlacementInsideBar.MIN
            index == barsCount - 1 -> PlacementInsideBar.MAX
            else -> PlacementInsideBar.MIDDLE
        }

        fun place(placement: PlacementInsideBar): Pair<Text.HorizontalAnchor, DoubleRectangle> {
            val textRect = createLabelRect(
                hPlacement = if (isHorizontallyOriented) placement else PlacementInsideBar.MIDDLE,
                vPlacement = if (isHorizontallyOriented) PlacementInsideBar.MIDDLE else placement,
                barRect,
                textSize,
                padding
            )
            val hAlignment = if (isHorizontallyOriented) {
                when (placement) {
                    PlacementInsideBar.MIN -> Text.HorizontalAnchor.LEFT
                    PlacementInsideBar.MAX -> Text.HorizontalAnchor.RIGHT
                    PlacementInsideBar.MIDDLE -> Text.HorizontalAnchor.MIDDLE
                }
            } else {
                Text.HorizontalAnchor.MIDDLE
            }
            return hAlignment to textRect
        }

        var (hAlignment, textRect) = place(insideBar)
        if (barRect.contains(textRect)) {
            return hAlignment to textRect
        } else if (index != 0 && index != barsCount - 1) {
            return null
        }

        // try to move outside the bar

        if (barsCount == 1) {
            // move to the right (for horizontally orientated) or to the top (of the vertical bar)
            insideBar = if (isHorizontallyOriented) PlacementInsideBar.MAX else PlacementInsideBar.MIN
            if (isNegative) insideBar = insideBar.flip()
        }

        fun DoubleRectangle.moveTo(value: Double): DoubleRectangle {
            val newOrigin =
                if (isHorizontallyOriented) DoubleVector(value, origin.y) else DoubleVector(origin.x, value)
            return DoubleRectangle(newOrigin, this.dimension)
        }
        textRect = if (insideBar == PlacementInsideBar.MAX) {
            if (isHorizontallyOriented) hAlignment = Text.HorizontalAnchor.LEFT
            val pos = coordSelector(barRect.origin) + coordSelector(barRect.dimension) + padding / 2
            textRect.moveTo(pos)
        } else {
            if (isHorizontallyOriented) hAlignment = Text.HorizontalAnchor.RIGHT
            val pos = coordSelector(barRect.origin) - coordSelector(textSize) - padding / 2
            textRect.moveTo(pos)
        }

        if (viewPort.contains(textRect)) {
            return hAlignment to textRect
        } else if (coordSelector(textSize) + padding > coordSelector(barRect.dimension)) {
            return null
        }

        // move it a little inward
        return place(insideBar)
    }

    private enum class PlacementInsideBar {
        MIN, MAX, MIDDLE;

        fun flip() = when (this) {
            MIN -> MAX
            MAX -> MIN
            MIDDLE -> MIDDLE
        }
    }

    private fun createLabelRect(
        hPlacement: PlacementInsideBar,
        vPlacement: PlacementInsideBar,
        barRect: DoubleRectangle,
        textSize: DoubleVector,
        padding: Double
    ): DoubleRectangle {

        fun getCoord(coordSelector: (DoubleVector) -> Double, align: PlacementInsideBar): Double {
            return when (align) {
                PlacementInsideBar.MIN -> coordSelector(barRect.origin) + padding
                PlacementInsideBar.MAX -> coordSelector(barRect.origin) + coordSelector(barRect.dimension) -
                        coordSelector(textSize) - padding

                PlacementInsideBar.MIDDLE -> coordSelector(barRect.center) - coordSelector(textSize) / 2
            }
        }

        val originX = getCoord(DoubleVector::x, hPlacement)
        val originY = getCoord(DoubleVector::y, vPlacement)
        return DoubleRectangle(originX, originY, textSize.x, textSize.y)
    }

    companion object {
        const val HANDLES_GROUPS = false

        private fun clientRectByDataPoint(ctx: GeomContext): (DataPointAesthetics) -> DoubleRectangle? {
            fun factory(p: DataPointAesthetics): DoubleRectangle? {
                val x = finiteOrNull(p.x()) ?: return null
                val y = finiteOrNull(p.y()) ?: return null
                val w = finiteOrNull(p.width()) ?: return null

                val width = w * ctx.getResolution(Aes.X)
                val origin = DoubleVector(x - width / 2, 0.0)
                val dimension = DoubleVector(width, y)
                return DoubleRectangle(origin, dimension)
            }

            return ::factory
        }
    }
}
