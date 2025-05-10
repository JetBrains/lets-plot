/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons

import kotlinx.cinterop.*
import platform.posix.CLOCK_REALTIME
import platform.posix.clock_gettime
import platform.posix.timespec

actual open class SystemTime actual constructor() {

    @OptIn(ExperimentalForeignApi::class)
    actual open fun getTimeMs(): Long {
        memScoped {
            val timeSpec = alloc<timespec>()
            clock_gettime(CLOCK_REALTIME.convert(), timeSpec.ptr)
            return timeSpec.tv_sec * 1_000L + timeSpec.tv_nsec / 1_000_000L
        }
    }
}