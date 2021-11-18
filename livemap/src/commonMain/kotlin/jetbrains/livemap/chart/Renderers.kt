/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.chart

import jetbrains.datalore.base.function.Consumer
import jetbrains.datalore.base.typedGeometry.MultiPolygon
import jetbrains.datalore.vis.canvas.Context2d
import jetbrains.datalore.vis.canvas.Context2d.LineJoin
import jetbrains.livemap.Client
import jetbrains.livemap.chart.Utils.drawPath
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.geometry.ScaleComponent
import jetbrains.livemap.geometry.ScreenGeometryComponent
import jetbrains.livemap.mapengine.Renderer
import jetbrains.livemap.mapengine.lineTo
import jetbrains.livemap.mapengine.moveTo

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
            val radius = symbolData.size.x * chartElement.scaleFactor / 2.0

            ctx.translate(-radius, -radius)

            ctx.translate(radius, radius)
            ctx.beginPath()
            drawPath(ctx, radius, shape)
            if (chartElement.fillColor != null) {
                ctx.setFillStyle(chartElement.fillColor)
                ctx.fill()
            }
            if (chartElement.strokeColor != null && !chartElement.strokeWidth.isNaN()) {
                ctx.setStrokeStyle(chartElement.strokeColor)
                ctx.setLineWidth(chartElement.strokeWidth)
                ctx.stroke()
            }
        }
    }

    class PolygonRenderer : Renderer {
        override fun render(entity: EcsEntity, ctx: Context2d) {
            if (!entity.contains(ScreenGeometryComponent::class)) {
                return
            }

            ctx.save()

            if (entity.contains(ScaleComponent::class)) {
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
                    c.setFillStyle(chartElement.fillColor)
                    c.fill()
                }

                if (chartElement.strokeColor != null && chartElement.strokeWidth != 0.0) {
                    c.setStrokeStyle(chartElement.strokeColor)
                    c.setLineWidth(chartElement.strokeWidth * chartElement.scaleFactor)
                    c.stroke()
                }
            }

            ctx.restore()
        }
    }

    class PathRenderer : Renderer {
        override fun render(entity: EcsEntity, ctx: Context2d) {
            if (!entity.contains(ScreenGeometryComponent::class)) {
                return
            }

            val chartElement = entity.get<ChartElementComponent>()
            ctx.setLineDash(chartElement.lineDash!!)
            ctx.setStrokeStyle(chartElement.strokeColor)
            ctx.setLineWidth(chartElement.strokeWidth * chartElement.scaleFactor)
            ctx.beginPath()

            drawLines(entity.get<ScreenGeometryComponent>().geometry, ctx) { it.stroke() }
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

