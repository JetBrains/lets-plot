/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.edt

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.function.Runnable
import jetbrains.datalore.base.function.Supplier
import jetbrains.datalore.base.registration.Registration

interface EventDispatchThread {

    val currentTimeMillis: Long

    fun schedule(r: Runnable)
    fun scheduleAsync(r: Runnable): Async<Unit>
    fun <ResultT> scheduleAsync(s: Supplier<ResultT>): Async<ResultT>
    fun <ResultT> flatScheduleAsync(s: Supplier<Async<ResultT>>): Async<ResultT>
    fun schedule(delay: Int, r: Runnable): Registration
    fun scheduleRepeating(period: Int, r: Runnable): Registration
}
