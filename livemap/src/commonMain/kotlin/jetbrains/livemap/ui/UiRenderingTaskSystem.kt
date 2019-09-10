package jetbrains.livemap.ui

import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponent
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.rendering.layers.DirtyRenderLayerComponent
import jetbrains.livemap.core.rendering.layers.RenderLayerComponent

class UiRenderingTaskSystem(componentManager: EcsComponentManager) : AbstractSystem<LiveMapContext>(componentManager) {

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        val uiLayer = getSingletonEntity(UiLayerComponent::class)

        uiLayer
            .getComponent<RenderLayerComponent>()
            .renderLayer
            .addRenderTask { context2d ->
                for (entity in getEntities(UiRenderComponent::class)) {
                    val renderObject = entity.getComponent<UiRenderComponent>().renderBox
                    context.mapRenderContext.draw(renderObject.origin, renderObject, context2d)
                }
            }

        DirtyRenderLayerComponent.tag(uiLayer)
    }

    class UiLayerComponent : EcsComponent
}