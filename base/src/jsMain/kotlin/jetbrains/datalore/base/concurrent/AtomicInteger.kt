package jetbrains.datalore.base.concurrent

actual class AtomicInteger actual constructor(int: Int) {
    private var value: Int = int
    actual fun decrementAndGet(): Int {
        return --value
    }
}