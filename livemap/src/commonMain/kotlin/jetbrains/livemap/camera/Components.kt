package jetbrains.livemap.camera

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.livemap.core.ecs.EcsComponent

class ZoomChangedComponent : EcsComponent

class CenterChangedComponent : EcsComponent

class CameraListenerComponent : EcsComponent

class CameraUpdateComponent : EcsComponent {

    var isZoomChanged: Boolean = false
    var isMoved: Boolean = false

    fun nothing(): CameraUpdateComponent {
        isMoved = false
        isZoomChanged = false
        return this
    }
}

class CameraComponent(var zoom: Double, var center: DoubleVector) : EcsComponent

class UpdateViewProjectionComponent : EcsComponent