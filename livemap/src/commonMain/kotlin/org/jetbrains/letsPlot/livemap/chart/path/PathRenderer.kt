/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.chart.path

import org.jetbrains.letsPlot.commons.intern.math.distance
import org.jetbrains.letsPlot.commons.intern.math.distance2
import org.jetbrains.letsPlot.commons.intern.math.pointOnLine
import org.jetbrains.letsPlot.commons.intern.math.rotateAround
import org.jetbrains.letsPlot.commons.intern.typedGeometry.*
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.canvas.Context2d
import org.jetbrains.letsPlot.livemap.Client
import org.jetbrains.letsPlot.livemap.Client.Companion.px
import org.jetbrains.letsPlot.livemap.World
import org.jetbrains.letsPlot.livemap.WorldPoint
import org.jetbrains.letsPlot.livemap.chart.ChartElementComponent
import org.jetbrains.letsPlot.livemap.core.ecs.EcsEntity
import org.jetbrains.letsPlot.livemap.geometry.WorldGeometryComponent
import org.jetbrains.letsPlot.livemap.mapengine.RenderHelper
import org.jetbrains.letsPlot.livemap.mapengine.Renderer
import org.jetbrains.letsPlot.livemap.mapengine.lineTo
import org.jetbrains.letsPlot.livemap.mapengine.moveTo
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.sin


open class PathRenderer : Renderer {
    override fun render(entity: EcsEntity, ctx: Context2d, renderHelper: RenderHelper) {
        val geometry = entity.get<WorldGeometryComponent>().geometry.multiLineString
        val chartElement = entity.get<ChartElementComponent>()
        val color = chartElement.scaledStrokeColor()

        ctx.save()
        ctx.scale(renderHelper.zoomFactor)

        val startPadding = renderHelper.dimToWorld(chartElement.scaledStartPadding())
        val endPadding = renderHelper.dimToWorld(chartElement.scaledEndPadding())

        for (lineString in geometry) {
            val adjustedGeometry = padLineString(lineString, startPadding, endPadding)

            ctx.beginPath()
            drawPath(adjustedGeometry, ctx)
            ctx.restore()

            ctx.setStrokeStyle(color)
            ctx.setLineDash(chartElement.scaledLineDash())
            ctx.setLineWidth(chartElement.scaledStrokeWidth())
            ctx.stroke()

            chartElement.arrowSpec?.let {
                // set attribute `stroke-miterlimit` to avoid a bevelled corner
                val miterLimit = ArrowSpec.miterLength(it.angle * 2, chartElement.scaledStrokeWidth())
                ctx.setStrokeMiterLimit(abs(miterLimit))
                val (startHead, endHead) = createArrowHeads(adjustedGeometry, it, chartElement.scalingSizeFactor, renderHelper)
                val startHeadSvg = renderArrowHead(startHead, it, color, ctx, renderHelper)
                val endHeadSvg = renderArrowHead(endHead, it, color, ctx, renderHelper)
                listOfNotNull(startHeadSvg, endHeadSvg)
            }
        }
    }

    private fun renderArrowHead(
        points: List<Vec<World>>,
        arrowSpec: ArrowSpec,
        color: Color,
        ctx: Context2d,
        renderHelper: RenderHelper
    ) {
        if (points.size < 2) return

        ctx.save()
        ctx.scale(renderHelper.zoomFactor)
        ctx.beginPath()
        ctx.moveTo(points[0])
        points.asSequence().drop(1).forEach(ctx::lineTo)
        ctx.restore()

        ctx.setLineDash(doubleArrayOf())
        if (arrowSpec.type == ArrowSpec.Type.CLOSED) {
            ctx.closePath()
            ctx.setFillStyle(color)
            ctx.fill()
        }
        ctx.stroke()
    }

    open fun drawPath(points: List<WorldPoint>, ctx: Context2d) {
        points[0].let(ctx::moveTo)
        points.drop(1).forEach(ctx::lineTo)
    }

    class ArrowSpec constructor(
        val angle: Double,
        val length: Scalar<Client>,
        val end: End,
        val type: Type
    ) {
        val isOnFirstEnd: Boolean
            get() = end == End.FIRST || end == End.BOTH

        val isOnLastEnd: Boolean
            get() = end == End.LAST || end == End.BOTH

        enum class End {
            LAST, FIRST, BOTH
        }

        enum class Type {
            OPEN, CLOSED
        }

        companion object {

            fun miterLength(headAngle: Double, strokeWidth: Double): Double {
                return strokeWidth / sin(headAngle / 2)
            }

            fun create(
                arrowAngle: Double?,
                arrowLength: Scalar<Client>?,
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
        }
    }

    companion object {
        private val MIN_TAIL_LENGTH = 10.px
        private val MIN_HEAD_LENGTH = 5.px

        // TODO: fix duplication from padLineString(List<DoubleVector>)
        private fun padLineString(
            lineString: List<WorldPoint>,
            startPadding: Scalar<World>,
            endPadding: Scalar<World>
        ): List<WorldPoint> {
            val startPadded = padStart(lineString, startPadding)
            return padEnd(startPadded, endPadding)
        }

        private fun pad(lineString: List<WorldPoint>, padding: Scalar<World>): Pair<Int, WorldPoint>? {
            @Suppress("NAME_SHADOWING")
            val padding = padding.value

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

        private fun padStart(lineString: List<WorldPoint>, padding: Scalar<World>): List<WorldPoint> {
            val (index, adjustedStartPoint) = pad(lineString, padding) ?: return lineString
            return listOf(adjustedStartPoint) + lineString.subList(index, lineString.size)
        }

        private fun padEnd(lineString: List<WorldPoint>, padding: Scalar<World>): List<WorldPoint> {
            val (index, adjustedEndPoint) = pad(lineString.asReversed(), padding) ?: return lineString
            return lineString.subList(0, lineString.size - index) + adjustedEndPoint
        }

        private fun adjustArrowHeadLength(lineLength: Scalar<Client>, arrowSpec: ArrowSpec, scalingFactor: Double): Scalar<Client> {
            val headsCount = listOf(arrowSpec.isOnFirstEnd, arrowSpec.isOnLastEnd).count { it }
            val headsLength = arrowSpec.length * headsCount * scalingFactor
            val tailLength = lineLength - headsLength

            return when (tailLength < MIN_TAIL_LENGTH) {
                true -> (lineLength - MIN_TAIL_LENGTH) / headsCount
                false -> arrowSpec.length * scalingFactor
            }
        }

        fun createArrowHeads(
            geometry: List<WorldPoint>,
            arrowSpec: ArrowSpec,
            scalingFactor: Double,
            renderHelper: RenderHelper
        ): Pair<List<Vec<World>>, List<Vec<World>>> {
            val startHead = when (arrowSpec.isOnFirstEnd) {
                true -> createArrowHeadGeometry(arrowSpec, geometry.asReversed(), scalingFactor, renderHelper)
                false -> emptyList()
            }

            val endHead = when (arrowSpec.isOnLastEnd) {
                true -> createArrowHeadGeometry(arrowSpec, geometry, scalingFactor, renderHelper)
                false -> emptyList()
            }

            return startHead to endHead
        }

        private fun createArrowHeadGeometry(
            arrowSpec: ArrowSpec,
            geometry: List<WorldPoint>,
            scalingFactor: Double,
            renderHelper: RenderHelper
        ): List<Vec<World>> {
            if (geometry.size < 2) return emptyList()

            val lineLength = Scalar<World>(geometry.windowed(2).sumOf { (a, b) -> distance(a.x, a.y, b.x, b.y) })
            val arrowHeadLength = adjustArrowHeadLength(renderHelper.dimToClient(lineLength), arrowSpec, scalingFactor)
            val headLength = renderHelper.dimToWorld(maxOf(arrowHeadLength, MIN_HEAD_LENGTH))

            // basePoint affects direction of the arrow head. Important for curves.
            val basePoint = when (geometry.size) {
                0, 1 -> error("Invalid geometry")
                2 -> geometry.first()
                else -> geometry[pointIndexAtDistance(geometry, distanceFromEnd = headLength)]
            }

            val tipPoint = geometry.last()

            val abscissa = tipPoint.x - basePoint.x
            val ordinate = tipPoint.y - basePoint.y
            if (abscissa == 0.0 && ordinate == 0.0) return emptyList()

            // Compute the angle that the vector defined by this segment makes with the
            // X-axis (radians)
            val polarAngle = atan2(ordinate, abscissa)

            val length = tipPoint - newVec(headLength, Scalar(0))

            val leftSide = rotateAround(length.x, length.y, tipPoint.x, tipPoint.y, polarAngle - arrowSpec.angle).toVec<World>()
            val rightSide = rotateAround(length.x, length.y, tipPoint.x, tipPoint.y, polarAngle + arrowSpec.angle).toVec<World>()

            return when (arrowSpec.type) {
                ArrowSpec.Type.CLOSED -> listOf(leftSide, tipPoint, rightSide, leftSide)
                ArrowSpec.Type.OPEN -> listOf(leftSide, tipPoint, rightSide)
            }
        }

        private fun pointIndexAtDistance(curve: List<Vec<World>>, distanceFromEnd: Scalar<World>): Int {
            var length = 0.0
            var i = curve.lastIndex

            while (i > 0 && length < distanceFromEnd.value) {
                val cur = curve[i]
                val prev = curve[--i]
                length += distance(cur.x, cur.y, prev.x, prev.y)
            }
            return i
        }
    }
}

class CurveRenderer : PathRenderer() {
    override fun drawPath(points: List<WorldPoint>, ctx: Context2d) {
        if (points.size < 3) {
            // linear
            super.drawPath(points, ctx)
        } else {
            ctx.drawBezierCurve(points)
        }
    }
}