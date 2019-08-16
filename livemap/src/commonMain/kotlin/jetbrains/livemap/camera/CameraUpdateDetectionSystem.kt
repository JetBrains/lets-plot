package jetbrains.livemap.camera

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.entities.Entities

class CameraUpdateDetectionSystem(componentManager: EcsComponentManager) :
    AbstractSystem<LiveMapContext>(componentManager) {
    private var myPreviousCameraZoom: Double = 0.0
    private var myPreviousCameraCenter: DoubleVector = DoubleVector.ZERO

    override fun initImpl(context: LiveMapContext) {
        val viewProjection = context.mapRenderContext.viewProjection
        createEntity("camera")
            .addComponent(CameraUpdateComponent())
            .addComponent(
                CameraComponent(
                    viewProjection.zoom.toDouble(),
                    viewProjection.center
                )
            )
    }

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        val cameraEntity = Entities.camera(componentManager)
        val camera = CameraComponent[cameraEntity]
        val cameraUpdate = CameraUpdateComponent[cameraEntity]

        removeChangedComponents()

        cameraUpdate.isZoomChanged = myPreviousCameraZoom != camera.zoom
        cameraUpdate.isMoved = myPreviousCameraCenter != camera.center

        myPreviousCameraZoom = camera.zoom
        myPreviousCameraCenter = camera.center

        if (cameraUpdate.isZoomChanged || cameraUpdate.isMoved) {
            updateAll(cameraUpdate.isZoomChanged, cameraUpdate.isMoved)
        }
    }

    private fun removeChangedComponents() {
        for (entity in getEntities(ZoomChangedComponent::class)) {
            entity.removeComponent(ZoomChangedComponent::class)
        }

        for (entity in getEntities(CenterChangedComponent::class)) {
            entity.removeComponent(CenterChangedComponent::class)
        }
    }

    private fun updateAll(zoomChanged: Boolean, centerChanged: Boolean) {
        for (entity in getEntities(CameraListenerComponent::class)) {
            if (zoomChanged) {
                ZoomChangedComponent.tag(entity)
                CenterChangedComponent.tag(entity)
            }

            if (centerChanged) {
                CenterChangedComponent.tag(entity)
            }
        }
    }
}