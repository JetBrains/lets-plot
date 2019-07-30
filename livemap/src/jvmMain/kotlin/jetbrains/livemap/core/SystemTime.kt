package jetbrains.livemap.core

actual class SystemTime actual constructor() {

    actual companion object {
        actual fun getTimeMs(): Long {
            return System.currentTimeMillis()
        }
    }
}