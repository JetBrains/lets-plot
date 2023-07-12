/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.chart.path

import org.jetbrains.letsPlot.commons.intern.typedGeometry.MultiLineString
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Scalar
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.canvas.Context2d
import jetbrains.livemap.World
import jetbrains.livemap.WorldPoint
import jetbrains.livemap.chart.ChartElementComponent
import jetbrains.livemap.chart.changeAlphaWithMin
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.geometry.WorldGeometryComponent
import jetbrains.livemap.mapengine.RenderHelper
import jetbrains.livemap.mapengine.Renderer
import jetbrains.livemap.mapengine.lineTo
import jetbrains.livemap.mapengine.moveTo
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class PathRenderer : Renderer {
    override fun render(entity: EcsEntity, ctx: Context2d, renderHelper: RenderHelper) {
        val geometry = entity.get<WorldGeometryComponent>().geometry.multiLineString
        val chartElement = entity.get<ChartElementComponent>()
        val color = changeAlphaWithMin(chartElement.strokeColor!!, chartElement.scalingAlphaValue)

        ctx.save()
        ctx.scale(renderHelper.zoomFactor)
        ctx.beginPath()

        for (lineString in geometry) {
            lineString[0].let(ctx::moveTo)
            lineString.drop(1).forEach(ctx::lineTo)
        }
        ctx.restore()

        ctx.setStrokeStyle(color)
        ctx.setLineDash(chartElement.lineDash!!.map { it * chartElement.scalingSizeFactor }.toDoubleArray())
        ctx.setLineWidth(chartElement.strokeWidth * chartElement.scalingSizeFactor)
        ctx.stroke()

        chartElement.arrowSpec?.let { arrowSpec ->
            drawArrows(arrowSpec, geometry, color, chartElement.scalingSizeFactor, ctx, renderHelper)
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
        }
    }

    private fun drawArrows(
        arrowSpec: ArrowSpec,
        geometry: MultiLineString<World>,
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

        for (lineString in geometry) {
            if (arrowSpec.isOnFirstEnd) {
                val (start, end) = lineString.take(2).reversed()

                drawArrowAtEnd(start, end, arrowSpec)
            }
            if (arrowSpec.isOnLastEnd) {
                val (start, end) = lineString.takeLast(2)
                drawArrowAtEnd(start, end, arrowSpec)
            }
        }
    }
}