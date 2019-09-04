package jetbrains.datalore.base.async

import jetbrains.datalore.base.concurrent.Lock
import jetbrains.datalore.base.concurrent.execute
import jetbrains.datalore.base.function.Consumer
import jetbrains.datalore.base.registration.Registration

class ThreadSafeAsync<ItemT>() : ResolvableAsync<ItemT> {
    private val myAsync: SimpleAsync<ItemT> = SimpleAsync()
    private val lock = Lock()

    override fun onSuccess(successHandler: Consumer<in ItemT>): Registration {
        lock.execute {
            return safeReg(myAsync.onSuccess(successHandler))
        }
    }

    override fun onResult(successHandler: Consumer<in ItemT>, failureHandler: Consumer<Throwable>): Registration {
        lock.execute {
            return safeReg(myAsync.onResult(successHandler, failureHandler))
        }
    }

    override fun onFailure(failureHandler: Consumer<Throwable>): Registration {
        lock.execute {
            return safeReg(myAsync.onFailure(failureHandler))
        }
    }

    override fun <ResultT> map(success: (ItemT) -> ResultT): Async<ResultT> {
        lock.execute {
            return Asyncs.map(this, success, ThreadSafeAsync())
        }
    }

    override fun <ResultT> flatMap(success: (ItemT) -> Async<ResultT>?): Async<ResultT?> {
        lock.execute {
            return Asyncs.select(this, success, ThreadSafeAsync<ResultT?>())
        }
    }

    private fun safeReg(r: Registration): Registration {
        return object : Registration() {
            override fun doRemove() {
                lock.execute {
                    r.remove()
                }
            }
        }
    }

    override fun success(result: ItemT) {
        lock.execute {
            myAsync.success(result)
        }
    }

    override fun failure(t: Throwable) {
        lock.execute {
            myAsync.failure(t)
        }
    }
}