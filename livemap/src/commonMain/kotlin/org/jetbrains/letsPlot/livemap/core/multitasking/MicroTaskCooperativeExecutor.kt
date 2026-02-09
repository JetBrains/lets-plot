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
    private val logEnabled = true
    private fun log(message: () -> String) {
        if (logEnabled) {
            println(message())
        }
    }

    override fun start() {}

    override fun stop() {}

    override fun updateAndGetFinished(tasks: MutableSet<MicroThreadComponent>): Set<MicroThreadComponent> {
        val finishedTasks = HashSet<MicroThreadComponent>()

        var enoughTime = true
        while (enoughTime && tasks.isNotEmpty()) {
            val taskIterator = tasks.iterator()
            while (taskIterator.hasNext()) {
                if (myClock.frameDurationMs > myFrameDurationLimit) {
                    log { "MicroTaskCooperativeExecutor: frame time limit exceeded: ${myClock.frameDurationMs}ms > $myFrameDurationLimit ms" }
                    enoughTime = false
                    break
                }

                taskIterator.next().run {
                    var resumesCountdown = resumesBeforeTimeCheck

                    while (resumesCountdown-- > 0 && microTask.alive()) {
                        microTask.resume()
                    }

                    if (!microTask.alive()) {
                        log { "MicroTaskCooperativeExecutor: task finished. Tasks left: ${tasks.size - 1}" }
                        finishedTasks.add(this)
                        taskIterator.remove()
                    } else {
                        log { "MicroTaskCooperativeExecutor: task not finished. Tasks left: ${tasks.size}" }
                    }
                }
            }
        }

        return finishedTasks
    }
}