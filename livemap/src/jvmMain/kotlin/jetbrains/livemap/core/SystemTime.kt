package jetbrains.livemap.core

import kotlin.math.roundToLong

actual open class SystemTime actual constructor() {

    actual open fun getTimeMs(): Long {
        return (System.nanoTime() / 1_000_000.0).roundToLong()
    }
}