/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.core.multitasking

import org.jetbrains.letsPlot.livemap.core.ecs.EcsClock

class MicroTaskCooperativeExecutor(
    private val myClock: EcsClock,
    private val myFrameDurationLimit: Long
) : MicroTaskExecutor {

    override fun start() {}

    override fun stop() {}

    override fun updateAndGetFinished(tasks: MutableSet<MicroThreadComponent>): Set<MicroThreadComponent> {
        val finishedTasks = HashSet<MicroThreadComponent>()

        var enoughTime = true
        while (enoughTime && tasks.isNotEmpty()) {
            val taskIterator = tasks.iterator()
            while (taskIterator.hasNext()) {
                if (myClock.frameDurationMs > myFrameDurationLimit) {
                    enoughTime = false
                    break
                }

                taskIterator.next().run {
                    var resumesCountdown = resumesBeforeTimeCheck

                    while (resumesCountdown-- > 0 && microTask.alive()) {
                        microTask.resume()
                    }

                    if (!microTask.alive()) {
                        finishedTasks.add(this)
                        taskIterator.remove()
                    }
                }
            }
        }

        return finishedTasks
    }
}