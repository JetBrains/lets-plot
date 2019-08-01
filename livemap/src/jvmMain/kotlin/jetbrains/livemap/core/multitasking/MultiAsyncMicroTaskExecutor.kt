package jetbrains.livemap.core.multitasking

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
        val finishedTasks = HashSet<MicroThreadComponent>()

        myRunningTasks.entries.removeIf {
            if (it.value.isDone) {
                finishedTasks.add(it.key)
            }

            if (!tasks.contains(it.key)) {
                it.value.cancel()
            }

            false
        }

        tasks.removeAll(myRunningTasks.keys)
        tasks.forEach { microThreadComponent ->
            val task = Task(microThreadComponent.microThread)
            myExecutorService.submit(task)
            myRunningTasks[microThreadComponent] = task
        }

        return finishedTasks
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