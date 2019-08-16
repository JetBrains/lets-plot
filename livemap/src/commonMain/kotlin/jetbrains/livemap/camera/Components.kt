package jetbrains.livemap.camera

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.livemap.core.ecs.EcsComponent
import jetbrains.livemap.core.ecs.EcsEntity

class ZoomChangedComponent : EcsComponent {
    companion object {

        fun tag(entity: EcsEntity) {
            entity.provideComponent(::ZoomChangedComponent)
        }
    }
}

class CenterChangedComponent : EcsComponent {
    companion object {

        fun tag(entity: EcsEntity) {
            entity.provideComponent(::CenterChangedComponent)
        }
    }
}

class CameraListenerComponent : EcsComponent {
    companion object {
        operator fun get(entity: EcsEntity): CameraListenerComponent {
            return entity.getComponent()
        }

        fun tag(entity: EcsEntity) {
            entity.provideComponent(::CameraListenerComponent)
        }
    }
}

class CameraUpdateComponent : EcsComponent {

    var isZoomChanged: Boolean = false
    var isMoved: Boolean = false

    fun nothing(): CameraUpdateComponent {
        isMoved = false
        isZoomChanged = false
        return this
    }

    companion object {

        operator fun get(entity: EcsEntity): CameraUpdateComponent {
            return entity.getComponent()
        }
    }
}

class CameraComponent(var zoom: Double, var center: DoubleVector) : EcsComponent {

    companion object {
        fun getZoom(entity: EcsEntity): Double {
            return get(entity).zoom
        }

        operator fun get(entity: EcsEntity): CameraComponent {
            return entity.getComponent()
        }
    }
}

class UpdateViewProjectionComponent : EcsComponent {
    companion object {
        fun tag(cellEntity: EcsEntity) {
            cellEntity.provideComponent(::UpdateViewProjectionComponent)
        }
    }
}