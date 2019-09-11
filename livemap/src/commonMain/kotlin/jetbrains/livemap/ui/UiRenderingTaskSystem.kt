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
            .get<RenderLayerComponent>()
            .renderLayer
            .addRenderTask { context2d ->
                getEntities(UiRenderComponent::class).forEach {
                    val renderObject = it.get<UiRenderComponent>().renderBox
                    context.mapRenderContext.draw(context2d, renderObject.origin, renderObject)
                }
            }

        uiLayer.tag(::DirtyRenderLayerComponent)
    }

    class UiLayerComponent : EcsComponent
}