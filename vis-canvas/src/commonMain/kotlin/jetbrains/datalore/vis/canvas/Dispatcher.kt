/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.canvas

import org.jetbrains.letsPlot.base.intern.async.Async
import org.jetbrains.letsPlot.base.intern.async.ThreadSafeAsync

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
