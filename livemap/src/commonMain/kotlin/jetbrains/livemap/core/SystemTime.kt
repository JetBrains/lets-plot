package jetbrains.livemap.core

expect class SystemTime() {
    fun getTimeMs(): Long
}