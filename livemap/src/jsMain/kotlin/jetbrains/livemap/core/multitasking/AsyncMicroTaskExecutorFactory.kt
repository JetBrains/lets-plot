package jetbrains.livemap.core.multitasking

actual class AsyncMicroTaskExecutorFactory {

    actual companion object {
        actual fun create(): MicroTaskExecutor? = null
    }
}