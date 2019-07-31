package jetbrains.livemap.core.multitasking

expect class AsyncMicroTaskExecutorFactory {
    companion object {
        fun create(): MicroTaskExecutor?
    }
}