package jetbrains.datalore.vis.canvas

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.async.ThreadSafeAsync

interface Dispatcher {
    fun <T> schedule(f: () -> T)
}

fun <T> Dispatcher.scheduleAsync(f: Async<T>): Async<T> {
    val s = ThreadSafeAsync<T>()
    f.onResult(
        { schedule { s.success(it) } },
        { schedule { s.failure(it) } }
    )
    return s
}
