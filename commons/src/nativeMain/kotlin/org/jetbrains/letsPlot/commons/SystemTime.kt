/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons

import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
actual open class SystemTime actual constructor() {
    actual open fun getTimeMs(): Long {
        return Clock.System.now().toEpochMilliseconds()
    }
}