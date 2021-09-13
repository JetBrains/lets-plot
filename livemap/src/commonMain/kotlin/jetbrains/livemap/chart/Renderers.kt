/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.chart

import jetbrains.datalore.base.function.Consumer
import jetbrains.datalore.base.typedGeometry.MultiPolygon
import jetbrains.datalore.base.typedGeometry.times
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.vis.canvas.Context2d
import jetbrains.datalore.vis.canvas.Context2d.LineJoin
import jetbrains.livemap.Client
import jetbrains.livemap.chart.Utils.drawPath
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.geometry.ScaleComponent
import jetbrains.livemap.geometry.ScreenGeometryComponent
import jetbrains.livemap.mapengine.Renderer
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.max

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

    class DonutRenderer : Renderer {
        override fun render(entity: EcsEntity, ctx: Context2d) {
            val chartElement = entity.get<ChartElementComponent>()
            val symbol = entity.get<SymbolComponent>()
            val radius = symbol.size.x * chartElement.scaleFactor / 2.0

            var currentAngle = - PI / 2
            symbol.values.zip(symbol.colors).forEach { (angle, color) ->
                val startAngle = currentAngle
                val endAngle = currentAngle + angle

                renderSector(chartElement, ctx, radius, startAngle, endAngle, color)

                currentAngle = endAngle
            }
        }

        private fun renderSector(
            chartElement: ChartElementComponent,
            ctx: Context2d,
            sectorRadius: Double,
            sectorStartAngle: Double,
            sectorEndAngle: Double,
            sectorColor: Color
        ) {
            val holeRadius = floor(sectorRadius * 0.55)

            if (chartElement.strokeColor != null && chartElement.strokeWidth > 0.0) {

                ctx.setStrokeStyle(chartElement.strokeColor)
                ctx.setLineWidth(chartElement.strokeWidth)

                // draw inner arc
                ctx.beginPath()
                ctx.arc(
                    x = 0.0, y = 0.0,
                    radius = max(0.0, holeRadius - chartElement.strokeWidth / 2),
                    startAngle = sectorStartAngle,
                    endAngle = sectorEndAngle
                )
                ctx.stroke()

                // draw outer arc
                ctx.beginPath()
                ctx.arc(
                    x = 0.0, y = 0.0,
                    radius = sectorRadius + chartElement.strokeWidth / 2,
                    startAngle = sectorStartAngle,
                    endAngle = sectorEndAngle
                )
                ctx.stroke()
            }

            // fill sector
            ctx.setFillStyle(sectorColor)
            ctx.beginPath()
            ctx.arc(0.0, 0.0, holeRadius, sectorStartAngle, sectorEndAngle)
            ctx.arc(0.0, 0.0, sectorRadius, sectorEndAngle, sectorStartAngle, anticlockwise = true)
            ctx.fill()
        }
    }

    class BarRenderer : Renderer {
        override fun render(entity: EcsEntity, ctx: Context2d) {
            val chartElement = entity.get<ChartElementComponent>()
            val symbol = entity.get<SymbolComponent>()

            val chartDimension = symbol.size * chartElement.scaleFactor
            val centerOffset = chartDimension.x / 2

            val columnWidth = chartDimension.x / symbol.values.size
            symbol.values.zip(symbol.colors).forEachIndexed { index, (height, color) ->
                ctx.setFillStyle(color)

                val columnHeight = chartDimension.y * abs(height)
                val columnX = columnWidth * index - centerOffset
                val columnY = if (height > 0) -columnHeight else 0.0
                ctx.fillRect(columnX, columnY, columnWidth, columnHeight)

                if (chartElement.strokeColor != null && chartElement.strokeWidth != 0.0) {
                    ctx.setStrokeStyle(chartElement.strokeColor)
                    ctx.setLineWidth(chartElement.strokeWidth)
                    ctx.strokeRect(columnX, columnY, columnWidth, columnHeight)
                }
            }
        }
    }

}

