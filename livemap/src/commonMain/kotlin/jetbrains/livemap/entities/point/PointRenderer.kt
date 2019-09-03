package jetbrains.datalore.maps.livemap.entities.point

import jetbrains.datalore.maps.livemap.entities.point.RendererUtils.drawPath
import jetbrains.datalore.maps.livemap.entities.rendering.StyleComponent
import jetbrains.datalore.visualization.base.canvas.Context2d
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.core.rendering.TransformComponent
import jetbrains.livemap.entities.placement.Components.ScreenDimensionComponent
import jetbrains.livemap.entities.rendering.Renderer

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
        renderFeature(entity.get<StyleComponent>(), ctx, radius, entity.get<PointComponent>().shape)
    }
}
