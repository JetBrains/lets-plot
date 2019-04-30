package jetbrains.datalore.base.async

import jetbrains.datalore.base.function.Consumer
import jetbrains.datalore.base.registration.Registration

actual class ThreadSafeAsync<ItemT> : ResolvableAsync<ItemT> {
    override fun onSuccess(successHandler: Consumer<in ItemT>): Registration {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onResult(successHandler: Consumer<in ItemT>, failureHandler: Consumer<Throwable>): Registration {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onFailure(failureHandler: Consumer<Throwable>): Registration {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun <ResultT> map(success: (ItemT) -> ResultT): Async<ResultT> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun <ResultT> flatMap(success: (ItemT) -> Async<ResultT>?): Async<ResultT?> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun success(result: ItemT) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun failure(t: Throwable) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}