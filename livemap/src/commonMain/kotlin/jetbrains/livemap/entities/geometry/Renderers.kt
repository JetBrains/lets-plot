/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.entities.geometry

import jetbrains.datalore.base.function.Consumer
import jetbrains.datalore.base.projectionGeometry.MultiPolygon
import jetbrains.datalore.maps.livemap.entities.rendering.Common
import jetbrains.datalore.vis.canvas.Context2d
import jetbrains.datalore.vis.canvas.Context2d.LineJoin
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.entities.placement.ScreenDimensionComponent
import jetbrains.livemap.entities.rendering.Renderer
import jetbrains.livemap.entities.rendering.StyleComponent
import jetbrains.livemap.entities.rendering.lineTo
import jetbrains.livemap.entities.rendering.moveTo
import jetbrains.livemap.entities.scaling.ScaleComponent
import jetbrains.livemap.projections.Client

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

            Common.apply(entity.get<StyleComponent>(), ctx)
            ctx.setLineJoin(LineJoin.ROUND)

            ctx.beginPath()

            drawLines(entity.get<ScreenGeometryComponent>().geometry, ctx) { c ->
                c.closePath()
                c.fill()
                c.stroke()
            }

            ctx.restore()
        }
    }

    class PathRenderer : Renderer {
        override fun render(entity: EcsEntity, ctx: Context2d) {
            if (!entity.contains(ScreenGeometryComponent::class)) {
                return
            }

            val styleComponent = entity.get<StyleComponent>()
            ctx.setLineDash(styleComponent.lineDash!!)
            ctx.setStrokeStyle(styleComponent.strokeColor)
            ctx.setLineWidth(styleComponent.strokeWidth)
            ctx.beginPath()

            drawLines(entity.get<ScreenGeometryComponent>().geometry, ctx) { it.stroke() }
        }
    }

    class BarRenderer : Renderer {
        override fun render(entity: EcsEntity, ctx: Context2d) {
            val style = entity.get<StyleComponent>()
            val dimension = entity.get<ScreenDimensionComponent>().dimension

            if (style.fillColor != null) {
                ctx.setFillStyle(style.fillColor)
                ctx.fillRect(0.0, 0.0, dimension.x, dimension.y)
            }

            if (style.strokeColor != null && style.strokeWidth != 0.0) {
                ctx.setStrokeStyle(style.strokeColor)
                ctx.setLineWidth(style.strokeWidth)
                ctx.strokeRect(0.0, 0.0, dimension.x, dimension.y)
            }
        }
    }

    class PieSectorRenderer : Renderer {

        override fun render(entity: EcsEntity, ctx: Context2d) {
            val style = entity.get<StyleComponent>()
            val sector = entity.get<PieSectorComponent>()

            if (style.strokeColor != null && style.strokeWidth > 0.0) {
                ctx.setStrokeStyle(style.strokeColor)
                ctx.setLineWidth(style.strokeWidth)
                ctx.beginPath()
                ctx.arc(
                    0.0, 0.0,
                    sector.radius + style.strokeWidth / 2,
                    sector.startAngle, sector.endAngle
                )
                ctx.stroke()
            }

            if (style.fillColor != null) {
                ctx.setFillStyle(style.fillColor)
                ctx.beginPath()
                ctx.moveTo(0.0, 0.0)
                ctx.arc(0.0, 0.0, sector.radius, sector.startAngle, sector.endAngle)
                ctx.fill()
            }
        }
    }

    class TextRenderer : Renderer {

        override fun render(entity: EcsEntity, ctx: Context2d) {
            val style = entity.get<StyleComponent>()
            val textSpec = entity.get<TextComponent>().textSpec

            ctx.save()
            ctx.rotate(textSpec.angle)

            ctx.setTextAlign(Context2d.TextAlign.CENTER)
            ctx.setTextBaseline(Context2d.TextBaseline.MIDDLE)
            ctx.setFont(textSpec.font)
            ctx.setFillStyle(style.fillColor)

            ctx.fillText(textSpec.label, textSpec.alignment.x, textSpec.alignment.y)
            ctx.restore()
        }
    }
}

