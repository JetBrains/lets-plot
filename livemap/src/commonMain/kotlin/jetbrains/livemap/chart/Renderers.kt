/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.chart

import jetbrains.datalore.base.function.Consumer
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.typedGeometry.MultiPolygon
import jetbrains.datalore.base.typedGeometry.Vec
import jetbrains.datalore.base.typedGeometry.explicitVec
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.vis.canvas.Context2d
import jetbrains.datalore.vis.canvas.LineJoin
import jetbrains.livemap.Client
import jetbrains.livemap.ClientPoint
import jetbrains.livemap.chart.Utils.changeAlphaWithMin
import jetbrains.livemap.chart.Utils.drawPath
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.geometry.ScaleComponent
import jetbrains.livemap.geometry.ScreenGeometryComponent
import jetbrains.livemap.mapengine.Renderer
import jetbrains.livemap.mapengine.lineTo
import jetbrains.livemap.mapengine.moveTo
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

object Renderers {

    fun drawLines(geometry: MultiPolygon<Client>, ctx: Context2d, afterPolygon: Consumer<Context2d>) {
        for (polygon in geometry) {
            for (ring in polygon) {
                ring[0].let(ctx::moveTo)
                ring.drop(1).forEach(ctx::lineTo)
            }
        }
        afterPolygon(ctx)
    }

    class PointRenderer(
        private val shape: Int
    ) : Renderer {

        override fun render(entity: EcsEntity, ctx: Context2d) {
            val chartElement = entity.get<ChartElementComponent>()
            val pointData = entity.get<PointComponent>()
            val radius = pointData.size * chartElement.scalingSizeFactor / 2.0

            ctx.beginPath()
            drawPath(ctx, radius, shape)
            if (chartElement.fillColor != null) {
                ctx.setFillStyle(changeAlphaWithMin(chartElement.fillColor!!, chartElement.scalingAlphaValue))
                ctx.fill()
            }
            if (chartElement.strokeColor != null && !chartElement.strokeWidth.isNaN()) {
                ctx.setStrokeStyle(changeAlphaWithMin(chartElement.strokeColor!!, chartElement.scalingAlphaValue))
                ctx.setLineWidth(chartElement.strokeWidth)
                ctx.stroke()
            }
        }
    }

    class PolygonRenderer : Renderer {
        override fun render(entity: EcsEntity, ctx: Context2d) {
            if (!entity.contains<ScreenGeometryComponent>()) {
                return
            }

            ctx.save()

            if (entity.contains<ScaleComponent>()) {
                val scale = entity.get<ScaleComponent>().scale
                if (scale != 1.0) {
                    ctx.scale(scale, scale)
                }
            }

            val chartElement = entity.get<ChartElementComponent>()

            ctx.setLineJoin(LineJoin.ROUND)

            ctx.beginPath()

            drawLines(entity.get<ScreenGeometryComponent>().geometry, ctx) { c ->
                c.closePath()

                if (chartElement.fillColor != null) {
                    c.setFillStyle(changeAlphaWithMin(chartElement.fillColor!!, chartElement.scalingAlphaValue))
                    c.fill()
                }

                if (chartElement.strokeColor != null && chartElement.strokeWidth != 0.0) {
                    c.setStrokeStyle(changeAlphaWithMin(chartElement.strokeColor!!, chartElement.scalingAlphaValue))
                    c.setLineWidth(chartElement.strokeWidth * chartElement.scalingSizeFactor)
                    c.stroke()
                }
            }

            ctx.restore()
        }
    }

    class PathRenderer : Renderer {
        override fun render(entity: EcsEntity, ctx: Context2d) {
            if (!entity.contains<ScreenGeometryComponent>()) {
                return
            }
            val geometry = entity.get<ScreenGeometryComponent>().geometry
            val chartElement = entity.get<ChartElementComponent>()
            val color = changeAlphaWithMin(chartElement.strokeColor!!, chartElement.scalingAlphaValue)
            ctx.setStrokeStyle(color)
            ctx.setLineDash(chartElement.lineDash!!.map { it * chartElement.scalingSizeFactor }.toDoubleArray())
            ctx.setLineWidth(chartElement.strokeWidth * chartElement.scalingSizeFactor)
            ctx.beginPath()

            drawLines(geometry, ctx, Context2d::stroke)
            chartElement.arrowSpec?.let { arrowSpec ->
                drawArrows(arrowSpec, geometry, color, chartElement.scalingSizeFactor, ctx)
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

            fun createGeometry(polarAngle: Double, x: Double, y: Double, scalingFactor: Double): Pair<DoubleArray, DoubleArray> {
                val xs = doubleArrayOf(
                    x - length * scalingFactor * cos(polarAngle - angle),
                    x,
                    x - length * scalingFactor * cos(polarAngle + angle)
                )
                val ys = doubleArrayOf(
                    y - length * scalingFactor * sin(polarAngle - angle),
                    y,
                    y - length * scalingFactor * sin(polarAngle + angle)
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
            geometry: MultiPolygon<Client>,
            color: Color,
            scalingSizeFactor: Double,
            ctx: Context2d
        ) {

            fun drawArrowAtEnd(points: List<ClientPoint>, arrowSpec: ArrowSpec) {
                if (points.size < 2) {
                    return
                }
                val start = points[0]
                val end = points[1]
                val abscissa = end.x - start.x
                val ordinate = end.y - start.y
                if (abscissa != 0.0 || ordinate != 0.0) {
                    ctx.beginPath()
                    ctx.setLineDash(doubleArrayOf())

                    val polarAngle = atan2(ordinate, abscissa)
                    val (xs, ys) = arrowSpec.createGeometry(polarAngle, end.x, end.y, scalingSizeFactor)
                    ctx.moveTo(xs[0], ys[0])
                    for (i in 1..2) {
                        ctx.lineTo(xs[i], ys[i])
                    }
                    if (arrowSpec.type == ArrowSpec.Type.CLOSED) {
                        ctx.closePath()
                        ctx.setFillStyle(color)
                        ctx.fill()
                    }
                    ctx.stroke()
                }
            }

            for (polygon in geometry) {
                for (ring in polygon) {
                    if (arrowSpec.isOnFirstEnd) {
                        val segment = ring.take(2).reversed()
                        drawArrowAtEnd(segment, arrowSpec)
                    }
                    if (arrowSpec.isOnLastEnd) {
                        val segment = ring.takeLast(2)
                        drawArrowAtEnd(segment, arrowSpec)
                    }
                }
            }
        }
    }

    class TextRenderer : Renderer {
        override fun render(entity: EcsEntity, ctx: Context2d) {
            val chartElementComponent = entity.get<ChartElementComponent>()
            val textSpec = entity.get<TextSpecComponent>().textSpec

            val textPosition: Vec<Client>

            ctx.save()
            ctx.rotate(textSpec.angle)

            if (textSpec.drawBorder) {
                val rectangle = textSpec.rectangle
                drawRoundedRectangle(rectangle, textSpec.labelRadius * rectangle.height, ctx)

                if (chartElementComponent.fillColor != null) {
                    ctx.setFillStyle(
                        changeAlphaWithMin(chartElementComponent.fillColor!!, chartElementComponent.scalingAlphaValue)
                    )
                    ctx.fill()
                }
                if (chartElementComponent.strokeColor != null && textSpec.labelSize != 0.0) {
                    ctx.setStrokeStyle(chartElementComponent.strokeColor)
                    ctx.setLineWidth(textSpec.labelSize)
                    ctx.stroke()
                }

                val xPosition = when (textSpec.hjust) {
                    0.0 -> textSpec.padding
                    1.0 -> -textSpec.padding
                    else -> 0.0
                }
                textPosition = explicitVec(
                    xPosition,
                    rectangle.origin.y + textSpec.padding + textSpec.font.fontSize * 0.8 // top-align the first line
                )
            } else {
                val yPosition = with(textSpec) {
                    when (vjust) {
                        1.0 -> font.fontSize * 0.7
                        0.0 -> -textSize.y + font.fontSize
                        else -> -textSize.y / 2 + font.fontSize * 0.8
                    }
                }
                textPosition = explicitVec(0.0, yPosition)
            }

            ctx.setFont(textSpec.font)
            ctx.setFillStyle(chartElementComponent.strokeColor)

            ctx.setTextAlign(textSpec.textAlign)
            textSpec.lines.forEachIndexed { index, line ->
                ctx.fillText(line, textPosition.x, textPosition.y + textSpec.lineHeight * index)
            }

            ctx.restore()
        }

        private fun drawRoundedRectangle(rect: DoubleRectangle, radius: Double, ctx: Context2d) {
            ctx.apply {
                beginPath()
                with(rect) {
                    // Ensure normal radius
                    val r = minOf(radius, rect.width / 2, rect.height / 2)

                    moveTo(right - r, bottom)
                    bezierCurveTo(
                        right - r, bottom,
                        right, bottom,
                        right, bottom - r
                    )

                    lineTo(right, top + r)
                    bezierCurveTo(
                        right, top + r,
                        right, top,
                        right - r, top
                    )

                    lineTo(left + r, top)
                    bezierCurveTo(
                        left + r, top,
                        left, top,
                        left, top + r
                    )

                    lineTo(left, bottom - r)
                    bezierCurveTo(
                        left, bottom - r,
                        left, bottom,
                        left + r, bottom
                    )
                }
                closePath()
            }
        }
    }
}
