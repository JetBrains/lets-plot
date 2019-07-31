package jetbrains.datalore.base.concurrent

import java.util.concurrent.locks.ReentrantLock

actual class Lock actual constructor() {
    private val mutex = ReentrantLock()

    actual fun lock() = mutex.lock()
    actual fun unlock()  = mutex.unlock()
}