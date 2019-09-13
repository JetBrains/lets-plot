package jetbrains.datalore.maps.livemap.entities.geometry

import jetbrains.datalore.base.function.Consumer
import jetbrains.datalore.maps.livemap.entities.rendering.Common
import jetbrains.datalore.visualization.base.canvas.Context2d
import jetbrains.datalore.visualization.base.canvas.Context2d.LineJoin
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.entities.geometry.ClientGeometry
import jetbrains.livemap.entities.rendering.Renderer
import jetbrains.livemap.entities.rendering.StyleComponent
import jetbrains.livemap.entities.rendering.lineTo
import jetbrains.livemap.entities.rendering.moveTo
import jetbrains.livemap.entities.scaling.ScaleComponent

object Renderers {

    fun drawLines(geometry: ClientGeometry, ctx: Context2d, afterPolygon: Consumer<Context2d>) {
        for (polygon in geometry.asMultipolygon()) {
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
}

