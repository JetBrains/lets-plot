package jetbrains.datalore.base.edt

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.function.Runnable
import jetbrains.datalore.base.function.Supplier

abstract class DefaultAsyncEdt protected constructor() : EventDispatchThread {

    override fun scheduleAsync(r: Runnable): Async<Unit> {
        return scheduleRunnableWithAsync(RunnableWithAsync.fromRunnable(r))
    }

    override fun <ResultT> scheduleAsync(s: Supplier<ResultT>): Async<ResultT> {
        return scheduleRunnableWithAsync(RunnableWithAsync.fromSupplier(s))
    }

    override fun <ResultT> flatScheduleAsync(s: Supplier<Async<ResultT>>): Async<ResultT> {
        return scheduleRunnableWithAsync(RunnableWithAsync.fromAsyncSupplier(s))
    }

    protected fun <ResultT> scheduleRunnableWithAsync(runnableWithAsync: RunnableWithAsync<ResultT>): Async<ResultT> {
        schedule(runnableWithAsync)
        return runnableWithAsync
    }
}
