package jetbrains.datalore.base.concurrent

expect class Lock() {
    fun lock()
    fun unlock()
}

inline fun <R> Lock.execute(f: () -> R): R {
    try {
        lock()
        return f()
    } finally {
        unlock()
    }
}
