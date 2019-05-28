package jetbrains.datalore.base.async

import kotlin.jvm.JvmOverloads

expect object PlatformAsyncs {
    fun parallel(vararg asyncs: Async<*>): Async<Unit>

    fun <ItemT> parallelResult(asyncs: Collection<Async<out ItemT>>): Async<List<ItemT>>

    @JvmOverloads
    fun <ItemT> parallel(asyncs: Collection<Async<out ItemT>>, alwaysSucceed: Boolean = false): Async<Unit>

    fun <ItemT> composite(asyncs: List<Async<ItemT>>): Async<List<ItemT>>
}