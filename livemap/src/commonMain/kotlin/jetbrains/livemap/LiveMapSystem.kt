package jetbrains.livemap

import jetbrains.livemap.camera.CameraComponent
import jetbrains.livemap.camera.CameraUpdateComponent
import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.EcsEntity

abstract class LiveMapSystem protected constructor(componentManager: EcsComponentManager) :
    AbstractSystem<LiveMapContext>(componentManager) {

    private val myCamera = Camera()

    protected fun camera(): Camera {
        return myCamera
    }

    protected inner class Camera {

        val isUpdated: Boolean
            get() = entity.contains(CameraUpdateComponent::class)

        val entity: EcsEntity
            get() = getSingletonEntity(CameraComponent::class)

        val zoom: Double
            get() = entity.getComponent<CameraComponent>().zoom

        val isIntegerZoom: Boolean
            get() = myCamera.zoom % CAMERA_STEP == 0.0

        fun ifZoomChanged(consumer: (CameraUpdateComponent) -> Unit) {
            val cameraUpdate = CameraUpdateComponent[entity]

            if (cameraUpdate.isZoomChanged) {
                consumer(cameraUpdate)
            }
        }
    }

    companion object {
        private const val CAMERA_STEP = 1.0
    }
}