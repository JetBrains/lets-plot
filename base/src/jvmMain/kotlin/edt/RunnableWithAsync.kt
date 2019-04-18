package jetbrains.datalore.base.edt

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.function.Consumer
import jetbrains.datalore.base.function.Function
import jetbrains.datalore.base.function.Runnable
import jetbrains.datalore.base.function.Supplier
import jetbrains.datalore.base.registration.Registration

actual class RunnableWithAsync<ItemT>
private actual constructor(
        action: Runnable,
        async: SafeAsync<ItemT>) :
        Runnable, Async<ItemT> {

    actual companion object {
        actual fun fromRunnable(r: Runnable): RunnableWithAsync<Unit> {
            val s = object : Supplier<Unit> {
                override fun get(): Unit {
                    r.run()
//                    return null
                }
            }
            return fromSupplier<Unit>(s)
        }

        actual fun <ResT> fromSupplier(s: Supplier<ResT>): RunnableWithAsync<ResT> {
            val safeAsync = SafeAsync<ResT>()
            return RunnableWithAsync(successFromPlain(s, safeAsync), safeAsync)
        }

        actual fun <ResT> fromAsyncSupplier(s: Supplier<Async<ResT>>): RunnableWithAsync<ResT> {
            val safeAsync = SafeAsync<ResT>()
            return RunnableWithAsync(successFromAsync(s, safeAsync), safeAsync)
        }

        private fun <T> successFromPlain(s: Supplier<T>, safeAsync: SafeAsync<T>): Runnable {
            return object : Runnable {
                override fun run() {
                    safeAsync.success(s.get())
                }
            }
        }

        private fun <T> successFromAsync(s: Supplier<Async<T>>, safeAsync: SafeAsync<T>): Runnable {
            return object : Runnable {
                override fun run() {
                    safeAsync.delegate(s.get())
                }
            }
        }
    }

    private val myAction = action
    private val myAsync = async

    override fun run() {
        try {
            myAction.run()
        } catch (t: Throwable) {
            myAsync.fail(t)
            throw t
        }
    }

    override fun onSuccess(successHandler: Consumer<in ItemT>): Registration {
        return myAsync.onSuccess(successHandler)
    }

    override fun onResult(successHandler: Consumer<in ItemT>, failureHandler: Consumer<Throwable>): Registration {
        return myAsync.onResult(successHandler, failureHandler)
    }

    override fun onFailure(failureHandler: Consumer<Throwable>): Registration {
        return myAsync.onFailure(failureHandler)
    }

    override fun <ResultT> map(success: Function<in ItemT, out ResultT>): Async<ResultT> {
        return myAsync.map(success)
    }

    override fun <ResultT> flatMap(success: Function<in ItemT, out Async<ResultT>?>): Async<ResultT?> {
        return myAsync.flatMap(success)
    }

    fun fail() {
        myAsync.fail(EdtException(RuntimeException("Intentionally failed")))
    }
}
