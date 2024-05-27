/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


// Not thread-safe
fun <T> debounce(
    delayMs: Long,
    scope: CoroutineScope,
    action: (T) -> Unit
): (T) -> Unit {
    var activeJob: Job? = null

    return { v: T ->
        activeJob?.cancel()
        activeJob = scope.launch {
            delay(delayMs)
            action(v)
        }
    }
}
