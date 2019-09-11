package jetbrains.livemap.entities.scaling

import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.LiveMapSystem
import jetbrains.livemap.camera.ZoomChangedComponent
import jetbrains.livemap.core.ecs.EcsComponentManager
import kotlin.math.pow

class ScaleUpdateSystem(componentManager: EcsComponentManager) : LiveMapSystem(componentManager) {

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        if (camera().isIntegerZoom) {
            for (entity in getEntities(COMPONENT_TYPES)) {
                val scaleComponent = entity.get<ScaleComponent>()
                val scale = 2.0.pow(camera().zoom - scaleComponent.zoom)
                scaleComponent.scale = scale
            }
        }
    }

    companion object {
        private val COMPONENT_TYPES = listOf(
            ZoomChangedComponent::class,
            ScaleComponent::class
        )
    }
}