package jetbrains.livemap.entities.placement

import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.LiveMapSystem
import jetbrains.livemap.camera.CenterChangedComponent
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.rendering.layers.ParentLayerComponent

class WorldOrigin2ScreenUpdateSystem(componentManager: EcsComponentManager) : LiveMapSystem(componentManager) {

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        val viewport = context.mapRenderContext.viewport

        for (entity in getEntities(COMPONENT_TYPES)) {
            entity.get<WorldOriginComponent>()
                .origin
                .let(viewport::getViewCoord)
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