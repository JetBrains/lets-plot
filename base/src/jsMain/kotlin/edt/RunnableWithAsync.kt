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
    override fun onSuccess(successHandler: Consumer<in ItemT>): Registration {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onResult(successHandler: Consumer<in ItemT>, failureHandler: Consumer<Throwable>): Registration {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onFailure(failureHandler: Consumer<Throwable>): Registration {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun <ResultT> map(success: Function<in ItemT, out ResultT>): Async<ResultT> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun <ResultT> flatMap(success: Function<in ItemT, out Async<ResultT>?>): Async<ResultT?> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun run() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual companion object {
        actual fun fromRunnable(r: Runnable): RunnableWithAsync<Unit> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        actual fun <ResT> fromSupplier(s: Supplier<ResT>): RunnableWithAsync<ResT> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        actual fun <ResT> fromAsyncSupplier(s: Supplier<Async<ResT>>): RunnableWithAsync<ResT> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }
}