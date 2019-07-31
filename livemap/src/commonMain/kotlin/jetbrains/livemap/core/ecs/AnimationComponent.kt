package jetbrains.livemap.core.ecs

import jetbrains.livemap.core.animation.Animation.*
import jetbrains.livemap.core.animation.EasingFunction


class AnimationComponent : EcsComponent {

    var time: Double = 0.toDouble()
    var duration: Double = 0.toDouble()
    var finished: Boolean = false
    var progress: Double = 0.toDouble()
    lateinit var easingFunction: EasingFunction
    lateinit var loop: Loop
    lateinit var direction: Direction

    companion object {
        operator fun get(entity: EcsEntity): AnimationComponent {
            return entity.getComponent()
        }
    }
}
