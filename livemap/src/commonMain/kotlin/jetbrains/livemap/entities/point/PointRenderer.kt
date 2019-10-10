package jetbrains.datalore.maps.livemap.entities.point

import jetbrains.datalore.maps.livemap.entities.point.RendererUtils.drawPath
import jetbrains.datalore.vis.canvas.Context2d
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.core.rendering.TransformComponent
import jetbrains.livemap.entities.placement.ScreenDimensionComponent
import jetbrains.livemap.entities.rendering.Renderer
import jetbrains.livemap.entities.rendering.StyleComponent

class PointRenderer : Renderer {
    private fun renderFeature(style: StyleComponent, ctx: Context2d, radius: Double, shape: Int) {
        ctx.translate(radius, radius)
        ctx.beginPath()
        drawPath(ctx, radius, shape)

        if (style.fillColor != null) {
            ctx.setFillStyle(style.fillColor)
            ctx.fill()
        }

        if (style.strokeColor != null && !style.strokeWidth.isNaN()) {
            ctx.setStrokeStyle(style.strokeColor)
            ctx.setLineWidth(style.strokeWidth)
            ctx.stroke()
        }
    }

    override fun render(entity: EcsEntity, ctx: Context2d) {
        val radius = entity.get<ScreenDimensionComponent>()
            .run { dimension.x }
            .run { div(2) }
            .run { times(entity.tryGet<TransformComponent>()?.scale ?: 1.0) }

        ctx.translate(-radius, -radius)
        renderFeature(entity.get(), ctx, radius, entity.get<PointComponent>().shape)
    }
}
