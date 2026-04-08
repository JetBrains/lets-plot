package org.jetbrains.letsPlot.commons.intern.concurrent

actual class AtomicInteger actual constructor(initialValue: Int) {
    private var value: Int = initialValue
    actual fun decrementAndGet(): Int {
        return --value
    }

    actual fun incrementAndGet(): Int {
        return ++value
    }
}