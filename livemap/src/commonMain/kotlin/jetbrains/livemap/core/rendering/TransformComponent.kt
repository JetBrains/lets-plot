package jetbrains.livemap.core.rendering

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.livemap.core.ecs.EcsComponent
import jetbrains.livemap.core.ecs.EcsEntity

class TransformComponent : EcsComponent {

    var scale: Double = 0.0
    var position: DoubleVector = DoubleVector.ZERO

    companion object {
        fun orNull(entity: EcsEntity): TransformComponent? {
            return entity.getComponent<TransformComponent>()
                .takeIf{ entity.contains(TransformComponent::class) }

        }

        operator fun get(entity: EcsEntity): TransformComponent {
            return entity.getComponent()
        }

        fun setScale(entity: EcsEntity, scale: Double) {
            get(entity).scale = scale
        }

        fun provide(entity: EcsEntity): TransformComponent {
            return entity.provide(::TransformComponent)
        }
    }
}