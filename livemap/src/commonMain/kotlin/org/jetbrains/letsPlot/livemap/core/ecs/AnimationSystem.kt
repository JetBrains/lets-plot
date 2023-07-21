/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.core.ecs

import org.jetbrains.letsPlot.livemap.core.animation.Animation.Direction.*
import org.jetbrains.letsPlot.livemap.core.animation.Animation.Loop

class AnimationSystem(componentManager: EcsComponentManager) : AbstractSystem<EcsContext>(componentManager) {

    private fun updateProgress(animation: AnimationComponent) {
        with(animation) {
            progress = when(direction) {
                FORWARD -> progress()
                BACK -> 1 - progress()
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

    override fun updateImpl(context: EcsContext, dt: Double) {
        for (entity in getEntities(AnimationComponent::class)) {
            val animationComponent = entity.get<AnimationComponent>()
            updateTime(animationComponent, dt)
            updateProgress(animationComponent)

            if (animationComponent.finished) {
                runLaterBySystem(entity){ it.remove()}
            }
        }
    }

}