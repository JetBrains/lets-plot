package jetbrains.livemap.core

import kotlin.js.Date

actual class SystemTime actual constructor() {

    actual companion object {
        actual fun getTimeMs(): Long {
            return Date().getMilliseconds().toLong()
        }
    }
}