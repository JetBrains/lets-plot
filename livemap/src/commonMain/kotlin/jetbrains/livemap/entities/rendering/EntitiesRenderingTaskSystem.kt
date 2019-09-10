package jetbrains.livemap.entities.rendering

import jetbrains.datalore.visualization.base.canvas.Context2d
import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.camera.CameraComponent
import jetbrains.livemap.camera.CameraScale.CameraScaleEffectComponent
import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.rendering.layers.DirtyRenderLayerComponent
import jetbrains.livemap.core.rendering.layers.RenderLayerComponent
import jetbrains.livemap.core.rendering.primitives.RenderObject
import jetbrains.livemap.entities.placement.Components

class EntitiesRenderingTaskSystem(componentManager: EcsComponentManager) :
    AbstractSystem<LiveMapContext>(componentManager) {

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        val scaleEffect =
            getSingletonEntity(CameraComponent::class)
                .tryGet<CameraScaleEffectComponent>()

        for (layer in getEntities(DIRTY_LAYERS)) {
            RenderLayerComponent.getRenderLayer(layer).addRenderTask { layerCtx ->
                layerCtx.save()
                if (scaleEffect != null) {
                    val scaleOrigin = scaleEffect.scaleOrigin
                    val scale = scaleEffect.currentScale

                    layerCtx.translate(scaleOrigin.x, scaleOrigin.y)
                    layerCtx.scale(scale, scale)
                    layerCtx.translate(-scaleOrigin.x, -scaleOrigin.y)
                } else {
                    layerCtx.scale(1.0, 1.0)
                }

                for (layerEntity in LayerEntitiesComponent.getEntities(layer)) {
                    val renderer = RendererComponent.getRenderer(layerEntity)
                    val origins = Components.ScreenLoopComponent.getOrigins(layerEntity)
                    for (origin in origins) {
                        context.mapRenderContext.draw(
                            layerCtx,
                            origin,
                            object : RenderObject {
                                override fun render(ctx: Context2d) {
                                    renderer.render(layerEntity, ctx)
                                }
                            }
                        )
                    }
                }

                layerCtx.restore()
            }
        }
    }

    companion object {
        private val DIRTY_LAYERS = listOf(
            DirtyRenderLayerComponent::class,
            LayerEntitiesComponent::class,
            RenderLayerComponent::class
        )
    }
}