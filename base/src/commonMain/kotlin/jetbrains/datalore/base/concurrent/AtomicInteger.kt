package jetbrains.datalore.base.concurrent

expect class AtomicInteger(int: Int) {
    fun decrementAndGet(): Int
}