/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.org.jetbrains.letsPlot.base.intern.async.asyncAssert

import org.jetbrains.letsPlot.base.intern.async.Async
import jetbrains.datalore.base.function.Value

internal class AsyncResult<T> private constructor(val state: AsyncState, val value: T?, val error: Throwable?) {

    internal enum class AsyncState {
        UNFINISHED,
        SUCCEEDED,
        FAILED
    }

    companion object {

        fun <T> getResult(async: Async<T>): AsyncResult<T> {
            val resultValue = Value(AsyncResult<T>(AsyncState.UNFINISHED, null, null))
            async.onResult(
                    { value -> resultValue.set(AsyncResult(AsyncState.SUCCEEDED, value, null)) },
                    { value -> resultValue.set(AsyncResult<T>(AsyncState.FAILED, null, value)) }
            )
            return resultValue.get()
        }
    }
}
