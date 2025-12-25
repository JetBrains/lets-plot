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
import org.jetbrains.letsPlot.core.plot.base.geom.annotation.AnnotationUtil.textColorAndLabelAlpha
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomHelper
import org.jetbrains.letsPlot.core.plot.base.geom.util.PathPoint
import org.jetbrains.letsPlot.core.plot.base.geom.util.PolygonData
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import org.jetbrains.letsPlot.core.plot.base.render.svg.Label
import org.jetbrains.letsPlot.core.plot.base.render.svg.Text
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2

object BarAnnotation {

    fun build(
        root: SvgRoot,
        polygons: List<PolygonData>,
        transform: (p: DataPointAesthetics) -> DoubleRectangle?,
        helper: GeomHelper,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        if (coord.isLinear) {
            linearAnnotations(root, polygons, coord, ctx)
        } else {
            nonLinearAnnotations(root, polygons, transform, helper, ctx)
        }
    }

    private fun nonLinearAnnotations(
        root: SvgRoot,
        polygons: List<PolygonData>,
        transform: (p: DataPointAesthetics) -> DoubleRectangle?,
        helper: GeomHelper,
        ctx: GeomContext
    ) {
        val annotation = ctx.annotation ?: return

        polygons.forEach { polygon ->
            val p = polygon.rings[0][0].aes
            val rect = transform(p) ?: return@forEach
            val centroid = helper.toClient(rect.center, p) ?: return@forEach
            val zero = helper.toClient(0.0, 0.0, p) ?: return@forEach

            val v = centroid.subtract(zero).orthogonal()

            val angle = atan2(v.y, v.x).let {
                when (abs(it)) {
                    in PI / 2..3 * PI / 2 -> PI - it
                    else -> 2 * PI - it
                }
            }

            val text = annotation.getAnnotationText(p.index(), ctx.plotContext)
            val (textColor, _) = textColorAndLabelAlpha(
                annotation, p.color(), p.fill(),
                insideGeom = true
            )

            AnnotationUtil.createLabelElement(
                text,
                centroid,
                textParams = AnnotationUtil.TextParams(
                    style = annotation.textStyle,
//                    color = annotation.getTextColor(p.fill()),
                    color = textColor,
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
        polygons: List<PolygonData>,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val annotation = ctx.annotation ?: return
        val viewPort = GeomBase.overallAesBounds(ctx).let(coord::toClient) ?: return

        val padding = annotation.textStyle.size / 2
        val isHorizontallyOriented = ctx.flipped

        val rectangles = mutableListOf<Triple<DataPointAesthetics, DoubleRectangle, Boolean>>()

        polygons.forEach { polygonData ->
            val (p, rect) = recoverRectangleFromPolygonData(polygonData) ?: return@forEach

            val clientRect = rect
                .intersect(viewPort)   // use the visible part of bar to place annotation on it
                ?: return@forEach

            val isUpsideDown = with(p) { y()!! < 0 } // bar with base at the top
            rectangles.add(Triple(p, clientRect, isUpsideDown))
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
                    .forEachIndexed { index, (p, barRect, isUpsideDown) ->
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
                            isUpsideDown
                        )
                            ?: return@forEachIndexed

                        val (textColor, alpha) = textColorAndLabelAlpha(
                            annotation, p.color(), p.fill(),
                            insideGeom = barRect.contains(textRect)
                        )

                        var location = DoubleVector(
                            x = when (hAlignment) {
                                Text.HorizontalAnchor.LEFT -> textRect.left
                                Text.HorizontalAnchor.RIGHT -> textRect.right
                                Text.HorizontalAnchor.MIDDLE -> textRect.center.x
                            },
                            y = textRect.top
                        )

                        // separate label for each line
                        val labels = Label.splitLines(text).map { line ->
                            AnnotationUtil.createLabelElement(
                                line,
                                location,
                                textParams = AnnotationUtil.TextParams(
                                    style = annotation.textStyle,
                                    color = textColor,
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

    private fun placeLabel(
        barRect: DoubleRectangle,
        index: Int,
        barsCount: Int,
        textSize: DoubleVector,
        padding: Double,
        viewPort: DoubleRectangle,
        isHorizontallyOriented: Boolean,
        isUpsideDown: Boolean
    ): Pair<Text.HorizontalAnchor, DoubleRectangle>? {

        val coordSelector: (DoubleVector) -> Double =
            if (isHorizontallyOriented) DoubleVector::x else DoubleVector::y

        var insideBar = when {
            barsCount == 1 -> {
                // use left (for horizontally orientated) or bottom (of the vertical bar)
                (if (isHorizontallyOriented) PlacementInsideBar.MIN else PlacementInsideBar.MAX).let {
                    if (isUpsideDown) it.flip() else it
                }
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
            if (isUpsideDown) insideBar = insideBar.flip()
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

    private fun recoverRectangleFromPolygonData(polygonData: PolygonData): Pair<DataPointAesthetics, DoubleRectangle>? {
        val dp = polygonData.rings[0][0].aes
        val rect = polygonData.rings[0].let { ring ->
            val coords = ring.map(PathPoint::coord)
            if (coords.isEmpty() || coords.size != 5) return null
            val minX = coords.minOf { it.x }
            val minY = coords.minOf { it.y }
            val maxX = coords.maxOf { it.x }
            val maxY = coords.maxOf { it.y }

            DoubleRectangle(
                origin = DoubleVector(minX, minY),
                dimension = DoubleVector(maxX - minX, maxY - minY)
            )
        }

        return dp to rect
    }
}