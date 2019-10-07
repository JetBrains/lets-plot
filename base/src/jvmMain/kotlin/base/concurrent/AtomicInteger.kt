package jetbrains.datalore.base.concurrent

import java.util.concurrent.atomic.AtomicInteger

actual class AtomicInteger actual constructor(int: Int) {
    private val value: AtomicInteger = AtomicInteger(int)

    actual fun decrementAndGet(): Int {
        return value.decrementAndGet()
    }
}