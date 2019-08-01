package jetbrains.livemap.core

import kotlin.math.roundToLong

actual class SystemTime actual constructor() {

    actual fun getTimeMs(): Long {
        return (System.nanoTime() / 1_000_000.0).roundToLong()
    }
}