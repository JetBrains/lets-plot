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
        UNSUPPORTED("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onResult(successHandler: Consumer<in ItemT>, failureHandler: Consumer<Throwable>): Registration {
        UNSUPPORTED("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onFailure(failureHandler: Consumer<Throwable>): Registration {
        UNSUPPORTED("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun <ResultT> map(success: (ItemT) -> ResultT): Async<ResultT> {
        UNSUPPORTED("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun <ResultT> flatMap(success: (ItemT) -> Async<ResultT>?): Async<ResultT?> {
        UNSUPPORTED("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun run() {
        UNSUPPORTED("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual companion object {
        actual fun fromRunnable(r: Runnable): RunnableWithAsync<Unit> {
            UNSUPPORTED("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        actual fun <ResT> fromSupplier(s: Supplier<ResT>): RunnableWithAsync<ResT> {
            UNSUPPORTED("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        actual fun <ResT> fromAsyncSupplier(s: Supplier<Async<ResT>>): RunnableWithAsync<ResT> {
            UNSUPPORTED("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }
}