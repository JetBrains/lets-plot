package jetbrains.livemap.core.multitasking.coroutine

interface CoroutineTaskExecutor {
    fun execute(coThreads: Sequence<MicroCoThreadComponent>) : Set<MicroCoThreadComponent>
}
