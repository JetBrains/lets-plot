package jetbrains.livemap.core.multitasking.coroutine

import jetbrains.livemap.core.ecs.EcsClock

class CooperativeCoroutineTaskExecutor(
    private val dispatcher: CooperativeCoroutineDispatcher,
    private val executionLimitPolicy: ExecutionLimitPolicy
) : CoroutineTaskExecutor {

    interface ExecutionLimitPolicy {
        fun allowed(): Boolean
    }

    /**
     * Returns false when time from the start of systems update frame exceeds myUpdateTimeLimit.
     */
    class TotalUpdateTimeLimit(
        private val myClock: EcsClock,
        private val myUpdateTimeLimit: Long
    ) : ExecutionLimitPolicy {
        override fun allowed(): Boolean {
            val spent = myClock.systemTime.getTimeMs() - myClock.updateStartTime
            return (spent < myUpdateTimeLimit)
                .also { if (it == false) println("not allowed: $spent")}
        }
    }

    override fun execute(coThreads: Sequence<MicroCoThreadComponent>): Set<MicroCoThreadComponent> {
        if (!coThreads.any()) {
            return emptySet()
        }

        val completed = HashSet<MicroCoThreadComponent>()

        coThreads
            .filterNot(completed::contains)
            .forEach { coThread ->
                if (!executionLimitPolicy.allowed()) {
                    return completed
                }

                repeat(coThread.quantum) {
                    if (!dispatcher.resume(coThread)) {
                        return@repeat // No continuations for this coThread or coThread is completed
                    }
                }

                if (coThread.isCompleted) {
                    completed.add(coThread)
                }
            }

        return completed
    }
}
