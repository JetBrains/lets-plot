/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.core.animation

class TimeState(
    val duration: Double,
    private val loop: Animation.Loop,
    var direction: Animation.Direction
) {
    var isFinished: Boolean = false
        private set

    internal fun calcTime(time: Double): Double {
        val newTime: Double

        if (time > duration) {
            if (loop === Animation.Loop.DISABLED) {
                newTime = duration
                isFinished = true
            } else {
                newTime = time % duration
                if (loop === Animation.Loop.SWITCH_DIRECTION) {
                    val dir = (direction.ordinal + time / duration).toInt() % 2
                    direction = Animation.Direction.values()[dir]
                }
            }
        } else {
            newTime = time
        }

        return newTime
    }
}