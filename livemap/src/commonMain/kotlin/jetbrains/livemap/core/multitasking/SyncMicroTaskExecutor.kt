/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.core.multitasking

import jetbrains.livemap.core.ecs.EcsClock

class SyncMicroTaskExecutor(
    private val myClock: EcsClock,
    private val myUpdateTimeLimit: Long
) : MicroTaskExecutor {

    override fun start() {}

    override fun stop() {}

    override fun updateAndGetFinished(tasks: MutableSet<MicroThreadComponent>): Set<MicroThreadComponent> {
        val finishedTasks = HashSet<MicroThreadComponent>()

        var enoughTime = true
        while (enoughTime && tasks.isNotEmpty()) {
            val taskIterator = tasks.iterator()
            while (taskIterator.hasNext()) {
                if (myClock.systemTime.getTimeMs() - myClock.updateStartTime > myUpdateTimeLimit) {
                    enoughTime = false
                    break
                }

                taskIterator.next().run {
                    var iterations = quantumIterations

                    while (iterations-- > 0 && microThread.alive()) {
                        microThread.resume()
                    }

                    if (!microThread.alive()) {
                        finishedTasks.add(this)
                        taskIterator.remove()
                    }
                }
            }
        }

        return finishedTasks
    }
}