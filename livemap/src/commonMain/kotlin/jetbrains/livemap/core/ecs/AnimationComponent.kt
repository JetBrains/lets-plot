package jetbrains.livemap.core.ecs

import jetbrains.livemap.core.animation.Animation.Direction
import jetbrains.livemap.core.animation.Animation.Loop
import jetbrains.livemap.core.animation.EasingFunction


class AnimationComponent : EcsComponent {
    var time: Double = 0.0
    var duration: Double = 0.0
    var finished: Boolean = false
    var progress: Double = 0.0
    lateinit var easingFunction: EasingFunction
    lateinit var loop: Loop
    lateinit var direction: Direction
}
