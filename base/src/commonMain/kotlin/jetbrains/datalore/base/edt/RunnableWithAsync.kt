/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.edt

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.function.Runnable
import jetbrains.datalore.base.function.Supplier

expect class RunnableWithAsync<ItemT>
private constructor(
        action: Runnable,
        async: SafeAsync<ItemT>) :
        Runnable, Async<ItemT> {

    companion object {
        fun fromRunnable(r: Runnable): RunnableWithAsync<Unit>
        fun <ResT> fromSupplier(s: Supplier<ResT>): RunnableWithAsync<ResT>
        fun <ResT> fromAsyncSupplier(s: Supplier<Async<ResT>>): RunnableWithAsync<ResT>
    }
}

