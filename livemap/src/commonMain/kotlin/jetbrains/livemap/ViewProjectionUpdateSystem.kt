package jetbrains.livemap

import jetbrains.livemap.camera.CameraComponent
import jetbrains.livemap.camera.UpdateViewProjectionComponent
import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import kotlin.math.roundToInt

class ViewProjectionUpdateSystem(componentManager: EcsComponentManager) :
    AbstractSystem<LiveMapContext>(componentManager) {

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        val cameraEntity = getSingletonEntity(CameraComponent::class)
        val camera = cameraEntity.getComponent<CameraComponent>()
        val viewProjection = context.mapRenderContext.viewProjection

        if (viewProjection.center != camera.center) {
            viewProjection.center = camera.center
        }

        if (cameraEntity.contains(UpdateViewProjectionComponent::class)) {
            viewProjection.zoom = camera.zoom.roundToInt()
            cameraEntity.removeComponent(UpdateViewProjectionComponent::class)
        }
    }
}
