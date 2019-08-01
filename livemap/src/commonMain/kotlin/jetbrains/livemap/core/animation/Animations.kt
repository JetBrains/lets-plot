package jetbrains.livemap.core.animation

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.livemap.core.animation.Animation.*
import jetbrains.livemap.core.animation.Animation.Loop.*

object Animations {
    val LINEAR: EasingFunction = { t -> t }
    val EASE_IN_QUAD: EasingFunction = { t -> t * t }
    val EASE_OUT_QUAD: EasingFunction = { t -> t * (2 - t) }

    class DoubleAnimator(
        private val start: Double,
        private val length: Double,
        private val consumer: (Double) -> Unit
    ) : Animator {

        override fun doAnimation(progress: Double) {
            consumer(start + progress * length)
        }
    }

    class DoubleVectorAnimator(
        private val start: DoubleVector,
        private val length: DoubleVector,
        private val consumer: (DoubleVector) -> Unit
    ) : Animator {

        override fun doAnimation(progress: Double) {
            consumer(start.add(length.mul(progress)))
        }
    }

    class AnimationBuilder(private val duration: Double) {
        private var easingFunction = LINEAR
        private var loop = DISABLED
        private var direction = Direction.FORWARD
        private var animators: ArrayList<Animator> = ArrayList()

        fun setEasingFunction(v: EasingFunction) = apply { easingFunction = v }

        fun setLoop(v: Loop) = apply { loop = v }

        fun setDirection(v: Direction) = apply { direction = v }

        fun setAnimator(v: Animator) = apply { animators = listOf(v) as ArrayList<Animator> }

        fun setAnimators(v: Collection<Animator>) = apply { animators = ArrayList(v) }

        fun addAnimator(v: Animator) = apply { animators.add(v) }

        fun build(): Animation {
            return SimpleAnimation(TimeState(duration, loop, direction), easingFunction, animators)
        }
    }

    internal class SimpleAnimation (
        private val timeState: TimeState,
        private val easingFunction: EasingFunction,
        private val animators: List<Animator>
    ) : Animation {
        override val isFinished: Boolean
            get() = timeState.isFinished

        override val duration: Double
            get() = timeState.duration

        override var time: Double = 0.0
            set(time) {
                field = timeState.calcTime(time)
            }

        override fun animate() {
            val progress = progress
            animators.forEach { animator -> animator.doAnimation(progress) }
        }

        private val progress: Double
            get() {
                if (duration == 0.0) {
                    return 1.0
                }

                val progress = easingFunction(time / duration)
                return if (timeState.direction === Direction.FORWARD) {
                    progress
                } else {
                    1 - progress
                }
            }
    }
}