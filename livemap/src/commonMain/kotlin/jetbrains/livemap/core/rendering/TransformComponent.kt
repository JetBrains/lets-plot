package jetbrains.livemap.core.rendering

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.livemap.core.ecs.EcsComponent

class TransformComponent : EcsComponent {
    var scale: Double = 0.0
    var position: DoubleVector = DoubleVector.ZERO
}