package jetbrains.livemap.entities.rendering

import jetbrains.datalore.visualization.base.canvas.Context2d
import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.camera.CameraComponent
import jetbrains.livemap.camera.CameraScale.CameraScaleEffectComponent
import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.core.rendering.layers.DirtyRenderLayerComponent
import jetbrains.livemap.core.rendering.layers.RenderLayerComponent
import jetbrains.livemap.core.rendering.primitives.RenderObject
import jetbrains.livemap.entities.placement.ScreenLoopComponent

class EntitiesRenderingTaskSystem(componentManager: EcsComponentManager) :
    AbstractSystem<LiveMapContext>(componentManager) {

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        val scaleEffect =
            getSingletonEntity(CameraComponent::class)
                .tryGet<CameraScaleEffectComponent>()

        for (layer in getEntities(DIRTY_LAYERS)) {
            layer.get<RenderLayerComponent>().renderLayer.addRenderTask { layerCtx ->
                layerCtx.save()

                scaleEffect?.apply {
                    layerCtx.translate(scaleOrigin.x, scaleOrigin.y)
                    layerCtx.scale(currentScale, currentScale)
                    layerCtx.translate(-scaleOrigin.x, -scaleOrigin.y)
                }
                    ?: layerCtx.scale(1.0, 1.0)

                for (layerEntity in getLayerEntities(layer)) {
                    val renderer = layerEntity.get<RendererComponent>().renderer
                    layerEntity.get<ScreenLoopComponent>().origins.forEach { origin ->
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
        fun getLayerEntities(entity: EcsEntity): Iterable<EcsEntity> {
            return entity.componentManager.getEntitiesById(entity.get<LayerEntitiesComponent>().entities)
        }

        private val DIRTY_LAYERS = listOf(
            DirtyRenderLayerComponent::class,
            LayerEntitiesComponent::class,
            RenderLayerComponent::class
        )
    }
}