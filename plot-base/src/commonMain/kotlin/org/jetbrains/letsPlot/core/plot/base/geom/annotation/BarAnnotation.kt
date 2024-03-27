/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom.annotation

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.math.toDegrees
import org.jetbrains.letsPlot.core.plot.base.CoordinateSystem
import org.jetbrains.letsPlot.core.plot.base.DataPointAesthetics
import org.jetbrains.letsPlot.core.plot.base.GeomContext
import org.jetbrains.letsPlot.core.plot.base.geom.GeomBase
import org.jetbrains.letsPlot.core.plot.base.geom.util.RectanglesHelper
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import org.jetbrains.letsPlot.core.plot.base.render.svg.MultilineLabel
import org.jetbrains.letsPlot.core.plot.base.render.svg.Text
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2

object BarAnnotation {

    fun build(
        root: SvgRoot,
        rectanglesHelper: RectanglesHelper,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        if (coord.isLinear) {
            linearAnnotations(root, rectanglesHelper, coord, ctx)
        } else {
            nonLinearAnnotations(root, rectanglesHelper, ctx)
        }
    }

    private fun nonLinearAnnotations(
        root: SvgRoot,
        rectanglesHelper: RectanglesHelper,
        ctx: GeomContext
    ) {
        val annotation = ctx.annotation ?: return

        rectanglesHelper.iterateRectangleGeometry { p, rect ->
            val clientBarCenter = rectanglesHelper.toClient(rect.center, p) ?: return@iterateRectangleGeometry

            val barBorder = with(rect.flipIf(ctx.flipped)) {
                listOf(DoubleVector(left, bottom), DoubleVector(right, bottom))
            }.mapNotNull {
                rectanglesHelper.toClient(it.flipIf(ctx.flipped), p)
            }

            if (barBorder.size != 2) return@iterateRectangleGeometry

            val v = barBorder[1].subtract(barBorder[0])
            val angle = atan2(v.y, v.x).let {
                when (abs(it)) {
                    in PI / 2..3 * PI / 2 -> PI - it
                    else -> 2 * PI - it
                }
            }
            val text = annotation.getAnnotationText(p.index(), ctx.plotContext)
            AnnotationUtil.createLabelElement(
                text,
                clientBarCenter,
                textParams = AnnotationUtil.TextParams(
                    style = annotation.textStyle,
                    color = annotation.getTextColor(p.fill()),
                    hjust = "middle",
                    vjust = "center",
                    alpha = 0.0,
                    angle = toDegrees(angle)
                ),
                geomContext = ctx,
            )
                .also(root::add)
        }
    }

    private fun linearAnnotations(
        root: SvgRoot,
        rectanglesHelper: RectanglesHelper,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val annotation = ctx.annotation ?: return
        val viewPort = GeomBase.overallAesBounds(ctx).let(coord::toClient) ?: return

        val padding = annotation.textStyle.size / 2
        val isHorizontallyOriented = ctx.flipped

        val rectangles = mutableListOf<Triple<DataPointAesthetics, DoubleRectangle, Boolean>>()
        rectanglesHelper.iterateRectangleGeometry { p, rect ->
            val clientRect = rectanglesHelper.toClient(rect, p)
                ?.intersect(viewPort)   // use the visible part of bar to place annotation on it
                ?: return@iterateRectangleGeometry

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
                        val text = annotation.getAnnotationText(p.index(), ctx.plotContext)
                        val textSize = AnnotationUtil.textSizeGetter(annotation.textStyle, ctx).invoke(text, p)

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
                                annotation.getTextColor(p.fill())
                            }
                            else -> {
                                alpha = 0.75
                                annotation.getTextColor()
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
                            AnnotationUtil.createLabelElement(
                                line,
                                location,
                                textParams = AnnotationUtil.TextParams(
                                    style = annotation.textStyle,
                                    color = labelColor,
                                    hjust = hAlignment.toString().lowercase(),
                                    vjust = "top",
                                    fill = ctx.backgroundColor,
                                    alpha = alpha
                                ),
                                geomContext = ctx,
                            ).also {
                                location = location.add(DoubleVector(0.0, annotation.textStyle.size))
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
}