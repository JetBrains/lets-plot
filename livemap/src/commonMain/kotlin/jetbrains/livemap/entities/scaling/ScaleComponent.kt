package jetbrains.livemap.entities.scaling

import jetbrains.livemap.core.ecs.EcsComponent
import jetbrains.livemap.core.ecs.EcsEntity

class ScaleComponent : EcsComponent {

    var scale = 1.0
    var zoom: Int = 0

    companion object {
        operator fun get(entity: EcsEntity): ScaleComponent {
            return entity.getComponent()
        }

        fun setScale(entity: EcsEntity, scale: Double) {
            get(entity).scale = scale
        }

        fun setZoom(entity: EcsEntity, zoom: Int) {
            get(entity).zoom = zoom
        }
    }
}