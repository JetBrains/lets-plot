/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.canvas

import org.jetbrains.letsPlot.commons.intern.async.Async
import org.jetbrains.letsPlot.commons.intern.async.ThreadSafeAsync

interface Dispatcher {
    fun <T> schedule(f: () -> T)
}

fun <T> Dispatcher.scheduleAsync(f: Async<T>): Async<T> {
    val s = ThreadSafeAsync<T>()
    f.onResult(
        { schedule { s.success(it) } },
        { schedule { s.failure(it) } }
    )
    return s
}
