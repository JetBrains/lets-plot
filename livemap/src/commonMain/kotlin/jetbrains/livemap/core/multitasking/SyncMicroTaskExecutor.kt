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
            val it = tasks.iterator()
            while (it.hasNext()) {
                if (myClock.systemTime.getTimeMs() - myClock.updateStartTime > myUpdateTimeLimit) {
                    enoughTime = false
                    break
                }

                val task = it.next()
                var iterations = task.quantumIterations
                val microThread = task.microThread

                while (iterations-- > 0 && microThread.alive()) {
                    microThread.resume()
                }

                if (!microThread.alive()) {
                    finishedTasks.add(task)
                    it.remove()
                }
            }
        }

        return finishedTasks
    }
}