package jetbrains.datalore.base.edt

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.function.Consumer
import jetbrains.datalore.base.function.Runnable
import jetbrains.datalore.base.function.Supplier
import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.base.unsupported.UNSUPPORTED

actual class RunnableWithAsync<ItemT>
private actual constructor(
        action: Runnable,
        async: SafeAsync<ItemT>) :
        Runnable, Async<ItemT> {
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

    override fun run() {
        UNSUPPORTED()
    }

    actual companion object {
        actual fun fromRunnable(r: Runnable): RunnableWithAsync<Unit> {
            UNSUPPORTED()
        }

        actual fun <ResT> fromSupplier(s: Supplier<ResT>): RunnableWithAsync<ResT> {
            UNSUPPORTED()
        }

        actual fun <ResT> fromAsyncSupplier(s: Supplier<Async<ResT>>): RunnableWithAsync<ResT> {
            UNSUPPORTED()
        }
    }
}