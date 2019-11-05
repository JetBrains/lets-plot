/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.edt

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.function.Consumer
import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.base.unsupported.UNSUPPORTED

actual class SafeAsync<ItemT> actual constructor() : Async<ItemT> {
    override fun onSuccess(successHandler: Consumer<in ItemT>): Registration {
        UNSUPPORTED()
    }

    override fun onResult(successHandler: Consumer<in ItemT>, failureHandler: Consumer<Throwable>): Registration {
        UNSUPPORTED()
    }

    override fun onFailure(failureHandler: Consumer<Throwable>): Registration {
        UNSUPPORTED()
    }

    override fun <ResultT> map(success: (ItemT) -> ResultT): Async<ResultT> {
        UNSUPPORTED()
    }

    override fun <ResultT> flatMap(success: (ItemT) -> Async<ResultT>?): Async<ResultT?> {
        UNSUPPORTED()
    }
}