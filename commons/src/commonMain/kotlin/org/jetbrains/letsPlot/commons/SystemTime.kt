/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons

import kotlin.time.TimeSource

open class SystemTime() {
    open fun getTimeMs(): Long {
        return TimeSource.Monotonic.markNow().elapsedNow().inWholeMilliseconds
    }
}