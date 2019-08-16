package jetbrains.livemap.entities.placement

import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.LiveMapSystem
import jetbrains.livemap.camera.CenterChangedComponent
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.rendering.layers.ParentLayerComponent

class WorldOrigin2ScreenUpdateSystem(componentManager: EcsComponentManager) : LiveMapSystem(componentManager) {

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        val viewProjection = context.mapRenderContext.viewProjection

        for (worldEntity in getEntities(COMPONENT_TYPES)) {
            val worldOrigin = Components.WorldOriginComponent.getOrigin(worldEntity)
            val screenOrigin = viewProjection.getViewCoord(worldOrigin)

            Components.ScreenOriginComponent.provide(worldEntity).origin = screenOrigin
            ParentLayerComponent.tagDirtyParentLayer(worldEntity)
        }
    }

    companion object {

        private val COMPONENT_TYPES = listOf(
            CenterChangedComponent::class,
            Components.WorldOriginComponent::class,
            ParentLayerComponent::class
        )
    }
}