/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.chart.path

import org.jetbrains.letsPlot.commons.intern.math.distance
import org.jetbrains.letsPlot.commons.intern.math.distance2
import org.jetbrains.letsPlot.commons.intern.math.pointOnLine
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Scalar
import org.jetbrains.letsPlot.commons.intern.typedGeometry.toVec
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.canvas.Context2d
import org.jetbrains.letsPlot.livemap.World
import org.jetbrains.letsPlot.livemap.WorldPoint
import org.jetbrains.letsPlot.livemap.chart.ChartElementComponent
import org.jetbrains.letsPlot.livemap.core.ecs.EcsEntity
import org.jetbrains.letsPlot.livemap.geometry.WorldGeometryComponent
import org.jetbrains.letsPlot.livemap.mapengine.RenderHelper
import org.jetbrains.letsPlot.livemap.mapengine.Renderer
import kotlin.math.*

class PathRenderer : Renderer {
    override fun render(entity: EcsEntity, ctx: Context2d, renderHelper: RenderHelper) {
        val geometry = entity.get<WorldGeometryComponent>().geometry.multiLineString
        val chartElement = entity.get<ChartElementComponent>()
        val color = chartElement.scaledStrokeColor()

        ctx.save()
        ctx.scale(renderHelper.zoomFactor)

        val startPadding = renderHelper.dimToWorld(chartElement.scaledStartPadding()).value
        val endPadding = renderHelper.dimToWorld(chartElement.scaledEndPadding()).value

        for (lineString in geometry) {
            val adjustedGeometry = padLineString(lineString, startPadding, endPadding)

            ctx.beginPath()

            adjustedGeometry[0].let { ctx.moveTo(it.x, it.y) }
            adjustedGeometry.drop(1).forEach { ctx.lineTo(it.x, it.y) }

            ctx.restore()

            ctx.setStrokeStyle(color)
            ctx.setLineDash(chartElement.scaledLineDash())
            ctx.setLineWidth(chartElement.scaledStrokeWidth())
            ctx.stroke()

            chartElement.arrowSpec?.let {
                drawArrows(it, adjustedGeometry, color, chartElement.scalingSizeFactor, ctx, renderHelper)
            }
        }
    }

    class ArrowSpec private constructor(
        val angle: Double,
        val length: Double,
        val end: End,
        val type: Type
    ) {
        val isOnFirstEnd: Boolean
            get() = end == End.FIRST || end == End.BOTH

        val isOnLastEnd: Boolean
            get() = end == End.LAST || end == End.BOTH

        fun createGeometry(
            polarAngle: Double,
            x: Double,
            y: Double,
            l: Scalar<World>,
            scalingFactor: Double
        ): Pair<DoubleArray, DoubleArray> {
            val xs = doubleArrayOf(
                x - l.value * scalingFactor * cos(polarAngle - angle),
                x,
                x - l.value * scalingFactor * cos(polarAngle + angle)
            )
            val ys = doubleArrayOf(
                y - l.value * scalingFactor * sin(polarAngle - angle),
                y,
                y - l.value * scalingFactor * sin(polarAngle + angle)
            )
            return xs to ys
        }

        enum class End {
            LAST, FIRST, BOTH
        }

        enum class Type {
            OPEN, CLOSED
        }

        companion object {
            fun create(
                arrowAngle: Double?,
                arrowLength: Double?,
                arrowAtEnds: String?,
                arrowType: String?
            ): ArrowSpec? {
                if (arrowAngle == null || arrowLength == null) {
                    return null
                }
                val ends = when (arrowAtEnds) {
                    "last" -> End.LAST
                    "first" -> End.FIRST
                    "both" -> End.BOTH
                    else -> throw IllegalArgumentException("Expected: first|last|both")
                }
                val type = when (arrowType) {
                    "open" -> Type.OPEN
                    "closed" -> Type.CLOSED
                    else -> throw IllegalArgumentException("Expected: open|closed")
                }
                return ArrowSpec(arrowAngle, arrowLength, ends, type)
            }

            fun miterLength(headAngle: Double, strokeWidth: Double): Double {
                return strokeWidth / sin(headAngle / 2)
            }
        }
    }

    private fun drawArrows(
        arrowSpec: ArrowSpec,
        geometry: List<WorldPoint>,
        color: Color,
        scalingSizeFactor: Double,
        ctx: Context2d,
        renderHelper: RenderHelper
    ) {

        fun drawArrowAtEnd(start: WorldPoint, end: WorldPoint, arrowSpec: ArrowSpec) {
            val abscissa = end.x - start.x
            val ordinate = end.y - start.y
            if (abscissa != 0.0 || ordinate != 0.0) {
                val polarAngle = atan2(ordinate, abscissa)
                val worldLength = renderHelper.dimToWorld(arrowSpec.length)
                val (xs, ys) = arrowSpec.createGeometry(polarAngle, end.x, end.y, worldLength, scalingSizeFactor)

                ctx.save()
                ctx.scale(renderHelper.zoomFactor)
                ctx.beginPath()
                ctx.moveTo(xs[0], ys[0])
                for (i in 1..2) {
                    ctx.lineTo(xs[i], ys[i])
                }
                ctx.restore()

                ctx.setLineDash(doubleArrayOf())
                if (arrowSpec.type == ArrowSpec.Type.CLOSED) {
                    ctx.closePath()
                    ctx.setFillStyle(color)
                    ctx.fill()
                }
                ctx.stroke()
            }
        }

        if (arrowSpec.isOnFirstEnd) {
            val (start, end) = geometry.take(2).reversed()
            drawArrowAtEnd(start, end, arrowSpec)
        }
        if (arrowSpec.isOnLastEnd) {
            val (start, end) = geometry.takeLast(2)
            drawArrowAtEnd(start, end, arrowSpec)
        }
    }

    companion object {
        // TODO: fix duplication from padLineString(List<DoubleVector>)
        private fun padLineString(
            lineString: List<WorldPoint>,
            startPadding: Double,
            endPadding: Double
        ): List<WorldPoint> {
            val startPadded = padStart(lineString, startPadding)
            return padEnd(startPadded, endPadding)
        }

        private fun pad(lineString: List<WorldPoint>, padding: Double): Pair<Int, WorldPoint>? {
            if (lineString.size < 2) {
                return null
            }

            val padding2 = padding * padding
            val indexOutsidePadding = lineString.indexOfFirst {
                distance2(lineString.first().x, lineString.first().y, it.x, it.y) >= padding2
            }
            if (indexOutsidePadding < 1) { // not found or first points already satisfy the padding
                return null
            }

            val adjustedStartPoint = run {
                val insidePadding = lineString[indexOutsidePadding - 1]
                val outsidePadding = lineString[indexOutsidePadding]
                val overPadding = distance(
                    lineString.first().x, lineString.first().y,
                    outsidePadding.x, outsidePadding.y
                ) - padding

                pointOnLine(outsidePadding.x, outsidePadding.y, insidePadding.x, insidePadding.y, overPadding)
            }

            return indexOutsidePadding to adjustedStartPoint.toVec()
        }

        private fun padStart(lineString: List<WorldPoint>, padding: Double): List<WorldPoint> {
            val (index, adjustedStartPoint) = pad(lineString, padding) ?: return lineString
            return listOf(adjustedStartPoint) + lineString.subList(index, lineString.size)
        }

        private fun padEnd(lineString: List<WorldPoint>, padding: Double): List<WorldPoint> {
            val (index, adjustedEndPoint) = pad(lineString.asReversed(), padding) ?: return lineString
            return lineString.subList(0, lineString.size - index) + adjustedEndPoint
        }
    }
}