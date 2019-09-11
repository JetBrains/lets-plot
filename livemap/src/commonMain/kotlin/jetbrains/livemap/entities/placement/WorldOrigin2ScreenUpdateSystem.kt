package jetbrains.livemap.entities.placement

import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.LiveMapSystem
import jetbrains.livemap.camera.CenterChangedComponent
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.rendering.layers.ParentLayerComponent
import jetbrains.livemap.entities.placement.Components.ScreenOriginComponent
import jetbrains.livemap.entities.placement.Components.WorldOriginComponent

class WorldOrigin2ScreenUpdateSystem(componentManager: EcsComponentManager) : LiveMapSystem(componentManager) {

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        val viewProjection = context.mapRenderContext.viewProjection

        for (entity in getEntities(COMPONENT_TYPES)) {
            entity.get<WorldOriginComponent>()
                .origin
                .let(viewProjection::getViewCoord)
                .let { entity.provide(::ScreenOriginComponent).origin = it }

            ParentLayerComponent.tagDirtyParentLayer(entity)
        }
    }

    companion object {

        private val COMPONENT_TYPES = listOf(
            CenterChangedComponent::class,
            WorldOriginComponent::class,
            ParentLayerComponent::class
        )
    }
}