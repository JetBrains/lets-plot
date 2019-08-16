package jetbrains.livemap.entities.placement

import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.LiveMapSystem
import jetbrains.livemap.camera.CenterChangedComponent
import jetbrains.livemap.core.ecs.EcsComponentManager

class ScreenLoopsUpdateSystem(componentManager: EcsComponentManager) : LiveMapSystem(componentManager) {

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        val viewProjection = context.mapRenderContext.viewProjection

        for (worldEntity in getEntities(COMPONENT_TYPES)) {

            var origin = Components.ScreenOriginComponent.getOrigin(worldEntity)
            val dimension = Components.ScreenDimensionComponent.getDimension(worldEntity)

            if (worldEntity.contains(Components.ScreenOffsetComponent::class)) {
                origin = origin.add(Components.ScreenOffsetComponent.getScreenOffset(worldEntity))
            }

            Components.ScreenLoopComponent[worldEntity].origins = viewProjection.getOrigins(origin, dimension)
        }
    }

    companion object {
        private val COMPONENT_TYPES = listOf(
            CenterChangedComponent::class,
            Components.ScreenOriginComponent::class,
            Components.ScreenDimensionComponent::class,
            Components.ScreenLoopComponent::class
        )
    }
}