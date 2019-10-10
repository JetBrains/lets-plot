package jetbrains.livemap.entities.regions

import jetbrains.datalore.maps.livemap.entities.geometry.Renderers
import jetbrains.datalore.maps.livemap.entities.geometry.ScreenGeometryComponent
import jetbrains.datalore.maps.livemap.entities.rendering.Common
import jetbrains.datalore.vis.canvas.Context2d
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.entities.placement.ScreenLoopComponent
import jetbrains.livemap.entities.rendering.Renderer
import jetbrains.livemap.entities.rendering.StyleComponent
import jetbrains.livemap.entities.scaling.ScaleComponent

class RegionRenderer : Renderer {
    override fun render(entity: EcsEntity, ctx: Context2d) {

        val fragments = entity.get<RegionComponent>().fragments
        if (fragments.isEmpty()) {
            return
        }

        for (fragment in fragments) {
            if (fragment.tryGet<ScreenGeometryComponent>() == null || fragment.tryGet<ScreenLoopComponent>() == null) {
                return
            }
        }

        Common.apply(entity.get<StyleComponent>(), ctx)

        ctx.beginPath()

        val scale = fragments.first().get<ScaleComponent>().scale

        for (fragment in fragments) {
            val screenGeometry = fragment.tryGet<ScreenGeometryComponent>() ?: error("")
            val screenLoop = fragment.tryGet<ScreenLoopComponent>() ?: error("")

            for (origin in screenLoop.origins) {
                ctx.save()
                ctx.translate(origin.x, origin.y)
                ctx.scale(scale, scale)
                Renderers.drawLines(
                    screenGeometry.geometry,
                    ctx
                ) { context2d: Context2d -> nop(context2d) }
                ctx.restore()
            }
        }

        ctx.fill()
    }

    private fun nop(context2d: Context2d) {

    }
}