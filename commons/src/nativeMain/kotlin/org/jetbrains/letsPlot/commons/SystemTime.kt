/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons

import kotlin.time.TimeSource

actual open class SystemTime actual constructor() {

    actual open fun getTimeMs(): Long {
        val timeSource = TimeSource.Monotonic
        val startTime = timeSource.markNow()
        return startTime.elapsedNow().inWholeMilliseconds
    }
}