package jetbrains.livemap.core

import kotlin.js.Date

actual open class SystemTime actual constructor() {

    actual open fun getTimeMs(): Long {
        return Date().getMilliseconds().toLong()
    }
}