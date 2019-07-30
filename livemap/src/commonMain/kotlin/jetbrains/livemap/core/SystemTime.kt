package jetbrains.livemap.core

expect class SystemTime() {

    companion object {
        fun getTimeMs(): Long
    }
}