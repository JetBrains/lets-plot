/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.chart

import jetbrains.datalore.base.ArrowSpec
import jetbrains.datalore.base.function.Consumer
import jetbrains.datalore.base.typedGeometry.MultiPolygon
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.vis.canvas.Context2d
import jetbrains.datalore.vis.canvas.Context2d.LineJoin
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

object Renderers {

    fun drawLines(geometry: MultiPolygon<Client>, ctx: Context2d, afterPolygon: Consumer<Context2d>) {
        for (polygon in geometry) {
            for (ring in polygon) {
                ring[0].let { ctx.moveTo(it) }
                ring.drop(1).forEach { ctx.lineTo(it) }
            }
        }
        afterPolygon(ctx)
    }

    class PointRenderer(
        private val shape: Int
    ) : Renderer {

        override fun render(entity: EcsEntity, ctx: Context2d) {
            val chartElement = entity.get<ChartElementComponent>()
            val symbolData = entity.get<SymbolComponent>()
            val radius = symbolData.size.x * chartElement.scalingSizeFactor / 2.0

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
            chartElement.arrowSpec?.let { arrowSpec -> drawArrows(arrowSpec, geometry, color, ctx) }
        }

        private fun drawArrows(arrowSpec: ArrowSpec, geometry: MultiPolygon<Client>, color: Color, ctx: Context2d) {

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
                    val (xs, ys) = arrowSpec.createGeometry(polarAngle, end.x, end.y)
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

            ctx.save()

            ctx.rotate(textSpec.angle)

            ctx.setFont(textSpec.font)
            ctx.setFillStyle(chartElementComponent.fillColor)

            ctx.fillText(textSpec.label, textSpec.alignment.x, textSpec.alignment.y)
            ctx.restore()
        }
    }
}
