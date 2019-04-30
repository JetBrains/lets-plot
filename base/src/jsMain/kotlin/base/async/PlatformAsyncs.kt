package jetbrains.datalore.base.async

actual object PlatformAsyncs {
    actual fun parallel(vararg asyncs: Async<*>): Async<Unit> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun <ItemT> parallelResult(asyncs: Collection<Async<out ItemT>>): Async<List<ItemT>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun <ItemT> parallel(asyncs: Collection<Async<out ItemT>>, alwaysSucceed: Boolean): Async<Unit> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun <ItemT> composite(asyncs: List<Async<ItemT>>): Async<List<ItemT>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}