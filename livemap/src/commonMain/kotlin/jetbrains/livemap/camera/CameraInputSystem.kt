package jetbrains.livemap.camera

import jetbrains.datalore.base.geometry.Vector
import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.input.MouseInputComponent

class CameraInputSystem(componentManager: EcsComponentManager) : AbstractSystem<LiveMapContext>(componentManager) {

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        val cameraEntity = getSingletonEntity(CameraComponent::class)
        val camera = cameraEntity.get<CameraComponent>()
        val mouseInput = cameraEntity.get<MouseInputComponent>()
        val viewProjection = context.mapRenderContext.viewProjection

        val dragDistance = mouseInput.dragDistance

        if (dragDistance != null && dragDistance != Vector.ZERO) {
            camera.center = viewProjection.getMapCoord(
                viewProjection.viewSize
                    .mul(0.5)
                    .subtract(dragDistance.toDoubleVector())
            )
        }
    }
}