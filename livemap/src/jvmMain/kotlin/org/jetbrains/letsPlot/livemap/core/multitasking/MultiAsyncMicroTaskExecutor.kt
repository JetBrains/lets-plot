/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.core.multitasking

import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MultiAsyncMicroTaskExecutor internal constructor() : MicroTaskExecutor {
    private val myExecutorService: ExecutorService
    private val myRunningTasks = HashMap<MicroThreadComponent, Task>()

    init {
        val processors = Runtime.getRuntime().availableProcessors()
        myExecutorService = Executors.newFixedThreadPool(processors)
    }

    override fun start() {}

    override fun updateAndGetFinished(tasks: MutableSet<MicroThreadComponent>): Set<MicroThreadComponent> {
        val finishedMicroThreads = HashSet<MicroThreadComponent>()

        myRunningTasks.entries.removeIf { (microThread, microTask) ->
            when {
                microTask.isDone -> true.also { finishedMicroThreads += microThread }
                microThread !in tasks -> true.also { microTask.cancel() }
                else -> false
            }
        }

        tasks.removeAll(myRunningTasks.keys)

        // On error LiveMapPresenter closed, controller finishing updates, triggering tasks update with closed ExecutorService
        // ToDo: properly handle mid-update systems stop command (break updates cycle, or ignore in systems/controller)
        if (!myExecutorService.isShutdown && !myExecutorService.isTerminated) {
            tasks.forEach { microThreadComponent ->
                val task = Task(microThreadComponent.microTask)
                myExecutorService.submit(task)
                myRunningTasks[microThreadComponent] = task
            }
        }

        return finishedMicroThreads
    }

    override fun stop() {
        myExecutorService.shutdown()
    }

    private class Task internal constructor(private val myMicroTask: MicroTask<Unit>) : Callable<Void> {
        @Volatile
        private var myIsCancelled = false

        internal val isDone: Boolean
            get() = !myMicroTask.alive()

        override fun call(): Void? {
            while (myMicroTask.alive() && !myIsCancelled) {
                myMicroTask.resume()
            }
            return null
        }

        internal fun cancel() {
            if (myMicroTask.alive()) {
                myIsCancelled = true
            }
        }
    }
}
