package jetbrains.livemap.core

import kotlin.js.Date

actual class SystemTime actual constructor() {

    actual fun getTimeMs(): Long {
        return Date().getMilliseconds().toLong()
    }
}