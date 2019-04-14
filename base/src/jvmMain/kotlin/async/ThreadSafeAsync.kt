package jetbrains.datalore.base.async

import jetbrains.datalore.base.function.Consumer
import jetbrains.datalore.base.function.Function
import jetbrains.datalore.base.registration.Registration

actual class ThreadSafeAsync<ItemT> : ResolvableAsync<ItemT> {
    private val myAsync: SimpleAsync<ItemT> = SimpleAsync()

    override fun onSuccess(successHandler: Consumer<in ItemT>): Registration {
        synchronized(myAsync) {
            return safeReg(myAsync.onSuccess(successHandler))
        }
    }

    override fun onResult(successHandler: Consumer<in ItemT>, failureHandler: Consumer<Throwable>): Registration {
        synchronized(myAsync) {
            return safeReg(myAsync.onResult(successHandler, failureHandler))
        }
    }

    override fun onFailure(failureHandler: Consumer<Throwable>): Registration {
        synchronized(myAsync) {
            return safeReg(myAsync.onFailure(failureHandler))
        }
    }

    override fun <ResultT> map(success: Function<in ItemT, out ResultT>): Async<ResultT> {
        synchronized(myAsync) {
            return Asyncs.map(this, success, ThreadSafeAsync())
        }
    }

    override fun <ResultT> flatMap(success: Function<in ItemT, Async<ResultT>>): Async<ResultT> {
        synchronized(myAsync) {
            return Asyncs.select(this, success, ThreadSafeAsync())
        }
    }

    private fun safeReg(r: Registration): Registration {
        return object : Registration() {
            override fun doRemove() {
                synchronized(myAsync) {
                    r.remove()
                }
            }
        }
    }

    override fun success(result: ItemT) {
        synchronized(myAsync) {
            myAsync.success(result)
        }
    }

    override fun failure(t: Throwable) {
        synchronized(myAsync) {
            myAsync.failure(t)
        }
    }
}