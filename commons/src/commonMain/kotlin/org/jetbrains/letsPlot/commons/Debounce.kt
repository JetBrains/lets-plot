/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons

import kotlinx.coroutines.*


// TODO: investigate can GlobalScope be replaced with some other scope
@OptIn(DelicateCoroutinesApi::class)
fun <T> debounce(
    delayMs: Long,
    scope: CoroutineScope = GlobalScope,
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
