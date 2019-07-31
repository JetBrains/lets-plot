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

    private var myZoomChanged: Boolean = false
    private var myMoved: Boolean = false

    fun nothing(): CameraUpdateComponent {
        myMoved = false
        myZoomChanged = false
        return this
    }

    fun isZoomChanged(): Boolean {
        return myZoomChanged
    }

    fun setMoved(isTrue: Boolean): CameraUpdateComponent {
        myMoved = isTrue
        return this
    }

    fun setZoomChanged(isTrue: Boolean): CameraUpdateComponent {
        myZoomChanged = isTrue
        return this
    }

    fun isMoved(): Boolean {
        return myMoved
    }

    companion object {

        operator fun get(entity: EcsEntity): CameraUpdateComponent {
            return entity.getComponent()
        }
    }
}

class CameraComponent : EcsComponent {

    private var myZoom: Double = 0.toDouble()
    private var myCenter: DoubleVector? = null

    fun getZoom(): Double {
        return myZoom
    }

    fun setZoom(zoom: Double): CameraComponent {
        myZoom = zoom
        return this
    }

    fun getCenter(): DoubleVector? {
        return myCenter
    }

    fun setCenter(center: DoubleVector): CameraComponent {
        myCenter = center
        return this
    }

    companion object {
        fun getZoom(entity: EcsEntity): Double {
            return get(entity).getZoom()
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