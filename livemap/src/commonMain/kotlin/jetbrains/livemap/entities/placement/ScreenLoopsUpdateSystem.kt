package jetbrains.livemap.entities.placement

import jetbrains.datalore.base.projectionGeometry.plus
import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.LiveMapSystem
import jetbrains.livemap.camera.CenterChangedComponent
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.projections.Coordinates.Companion.ZERO_CLIENT_POINT

class ScreenLoopsUpdateSystem(componentManager: EcsComponentManager) : LiveMapSystem(componentManager) {

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        val viewProjection = context.mapRenderContext.viewProjection

        getEntities(COMPONENT_TYPES).forEach { entity ->
            val origin = entity
                .run { tryGet<ScreenOffsetComponent>()?.offset ?: ZERO_CLIENT_POINT }
                .run { entity.get<ScreenOriginComponent>().origin + this }

            val dimension = entity.get<ScreenDimensionComponent>().dimension

            entity.get<ScreenLoopComponent>().origins = viewProjection.getOrigins(origin, dimension)
        }
    }

    companion object {
        private val COMPONENT_TYPES = listOf(
            CenterChangedComponent::class,
            ScreenOriginComponent::class,
            ScreenDimensionComponent::class,
            ScreenLoopComponent::class
        )
    }
}