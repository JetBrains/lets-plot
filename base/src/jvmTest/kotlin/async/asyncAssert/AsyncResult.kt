package jetbrains.datalore.base.async.asyncAssert

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.function.Consumer
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
                    object : Consumer<T> {
                        override fun accept(value: T) {
                            resultValue.set(AsyncResult(AsyncState.SUCCEEDED, value, null))
                        }
                    },
                    object : Consumer<Throwable> {
                        override fun accept(value: Throwable) {
                            resultValue.set(AsyncResult<T>(AsyncState.FAILED, null, value))
                        }
                    })
            return resultValue.get()
        }
    }
}
