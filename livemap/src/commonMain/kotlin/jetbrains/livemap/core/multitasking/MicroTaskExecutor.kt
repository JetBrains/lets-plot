package jetbrains.livemap.core.multitasking

interface MicroTaskExecutor {
    fun start()
    fun updateAndGetFinished(tasks: MutableSet<MicroThreadComponent>): Set<MicroThreadComponent>
    fun stop()
}