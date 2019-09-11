package jetbrains.livemap.core.ecs

import jetbrains.livemap.core.animation.Animation.Direction.FORWARD
import jetbrains.livemap.core.animation.Animation.Direction.values
import jetbrains.livemap.core.animation.Animation.Loop

class AnimationSystem(componentManager: EcsComponentManager) : AbstractSystem<EcsContext>(componentManager) {

    private fun updateProgress(animation: AnimationComponent) {
        with(animation) {
            progress = when {
                direction === FORWARD -> progress()
                else -> 1 - progress()
            }
        }
    }

    private fun AnimationComponent.progress() = easingFunction(time / duration)

    private fun updateTime(animation: AnimationComponent, dt: Double) {
        val newTime: Double
        val time = animation.time + dt
        val duration = animation.duration
        val loop = animation.loop

        if (time > duration) {
            if (loop === Loop.DISABLED) {
                newTime = duration
                animation.finished = true
            } else {
                newTime = time % duration
                if (loop === Loop.SWITCH_DIRECTION) {
                    val dir = (animation.direction.ordinal + time / duration).toInt() % 2
                    animation.direction = values()[dir]
                }
            }
        } else {
            newTime = time
        }

        animation.time = newTime
    }

    protected override fun updateImpl(context: EcsContext, dt: Double) {
        for (entity in getEntities(AnimationComponent::class)) {
            val animationComponent = entity.get<AnimationComponent>()
            updateTime(animationComponent, dt)
            updateProgress(animationComponent)
        }
    }

}