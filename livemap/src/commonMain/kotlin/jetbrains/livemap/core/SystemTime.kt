package jetbrains.livemap.core

expect open class SystemTime() {
    fun getTimeMs(): Long
}