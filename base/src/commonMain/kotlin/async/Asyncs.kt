package jetbrains.datalore.base.async

import jetbrains.datalore.base.function.Consumer
import jetbrains.datalore.base.function.Runnable
import jetbrains.datalore.base.function.Supplier
import jetbrains.datalore.base.function.Value
import jetbrains.datalore.base.registration.Registration

object Asyncs {
    fun isFinished(async: Async<*>): Boolean {
        val finished = Value(false)
        async.onResult(
                { finished.set(true) },
                { finished.set(true) }
        ).remove()
        return finished.get()
    }

    fun <ValueT> constant(value: ValueT): Async<ValueT> {
        return object : Async<ValueT> {
            override fun onSuccess(successHandler: Consumer<in ValueT>): Registration {
                successHandler(value)
                return Registration.EMPTY
            }

            override fun onResult(successHandler: Consumer<in ValueT>, failureHandler: Consumer<Throwable>): Registration {
                return onSuccess(successHandler)
            }

            override fun onFailure(failureHandler: Consumer<Throwable>): Registration {
                return Registration.EMPTY
            }

            override fun <ResultT> map(success: (ValueT) -> ResultT): Async<ResultT> {
                val result: ResultT
                try {
                    result = success(value)
                } catch (t: Throwable) {
                    return failure(t)
                }

                //return cannot be moved to try block to avoid catching possible errors from Asyncs.constant call
                return constant(result)
            }

            override fun <ResultT> flatMap(success: (ValueT) -> Async<ResultT>?): Async<ResultT?> {
                val result: Async<ResultT>?
                try {
                    result = success(value)
                } catch (t: Throwable) {
                    return failure(t)
                }

                //return cannot be moved to try block to avoid catching possible errors from Asyncs.constant call
                if (result == null) {
                    return constant(null)
                } else {
                    return result as Async<ResultT?>
                }
            }
        }
    }

    fun <ValueT> failure(t: Throwable): Async<ValueT> {
        return object : Async<ValueT> {
            override fun onSuccess(successHandler: Consumer<in ValueT>): Registration {
                return Registration.EMPTY
            }

            override fun onResult(successHandler: Consumer<in ValueT>, failureHandler: Consumer<Throwable>): Registration {
                return onFailure(failureHandler)
            }

            override fun onFailure(failureHandler: Consumer<Throwable>): Registration {
                failureHandler(t)
                return Registration.EMPTY
            }

            override fun <ResultT> map(success: (ValueT) -> ResultT): Async<ResultT> {
                return failure(t)
            }

            override fun <ResultT> flatMap(success: (ValueT) -> Async<ResultT>?): Async<ResultT?> {
                return failure(t)
            }
        }
    }

    fun voidAsync(): Async<Unit> {
        return constant(Unit)
    }

    fun <ResultT> toUnit(async: Async<ResultT>): Async<Unit> {
        return map(async, {}, ThreadSafeAsync())
    }

    internal fun <SourceT, TargetT, AsyncResultT : SourceT> map(
            async: Async<AsyncResultT>,
            f: (SourceT) -> TargetT,
            resultAsync: ResolvableAsync<TargetT>):
            Async<TargetT> {

        async.onResult(
                { value ->
                    val result: TargetT
                    try {
                        result = f(value)
                    } catch (e: Exception) {
                        resultAsync.failure(e)
                        return@onResult
                    }

                    resultAsync.success(result)
                },
                { value -> resultAsync.failure(value) })
        return resultAsync
    }

    internal fun <SourceT, TargetT> select(
            async: Async<SourceT>,
            f: (SourceT) -> Async<TargetT>?,
            resultAsync: ResolvableAsync<TargetT?>):
            Async<TargetT?> {

        async.onResult(
                { value ->
                    val async1: Async<TargetT>?
                    try {
                        async1 = f(value)
                    } catch (e: Exception) {
                        resultAsync.failure(e)
                        return@onResult
                    }

                    if (async1 == null) {
                        resultAsync.success(null)
                    } else {
                        delegate(async1, resultAsync)
                    }
                },
                { value -> resultAsync.failure(value) })
        return resultAsync
    }

    fun <FirstT, SecondT> seq(first: Async<FirstT>, second: Async<SecondT>): Async<SecondT?> {
        return select(first, { second }, ThreadSafeAsync<SecondT?>())
    }

    fun onAnyResult(async: Async<*>, r: Runnable): Registration {
        return async.onResult({ r.run() }, { r.run() })
    }

    fun <ResultT> untilSuccess(s: Supplier<Async<ResultT>>): Async<ResultT> {
        val result = SimpleAsync<ResultT>()
        val async: Async<ResultT>
        val successConsumer: Consumer<ResultT> = { value -> result.success(value) }
        try {
            async = s.get()
        } catch (ignore: Exception) {
            untilSuccess(s).onSuccess(successConsumer)
            return result
        }

        async.onResult(successConsumer, { untilSuccess(s).onSuccess(successConsumer) })
        return result
    }

    internal fun <ValueT> delegate(from: Async<out ValueT>, to: AsyncResolver<in ValueT>): Registration {
        return from.onResult({ value -> to.success(value) }, { value -> to.failure(value) })
    }

// When converting to Kotlin
// jetbrains.datalore.base.async.AsyncsPairTest
// should also be converted

//    fun <FirstT, SecondT> pair(first: Async<FirstT>, second: Async<SecondT>): Async<Pair<FirstT, SecondT>> {
//        val res = SimpleAsync<Pair<FirstT, SecondT>>()
//        val proxy = SimpleAsync<Unit>()
//        val firstPaired = PairedAsync(first)
//        val secondPaired = PairedAsync(second)
//        proxy.onResult(
//                object : Consumer<Unit> {
//                    override fun accept(item: Unit) {
//                        if (firstPaired.mySucceeded!! && secondPaired.mySucceeded!!) {
//                            res.success(Pair(firstPaired.myItem, secondPaired.myItem))
//                        } else {
//                            res.failure(Throwable("internal error in pair async"))
//                        }
//                    }
//                },
//                object : Consumer<Throwable> {
//                    override fun accept(throwable: Throwable) {
//                        res.failure(throwable)
//                    }
//                })
//        firstPaired.pair(secondPaired, proxy)
//        secondPaired.pair(firstPaired, proxy)
//        return res
//    }

//    private class PairedAsync<ItemT> private constructor(private val myAsync: Async<ItemT>) {
//        private var myItem: ItemT? = null
//        private var mySucceeded: Boolean? = false
//        private var myReg: Registration? = null
//
//        private fun <AnotherItemT> pair(anotherInfo: PairedAsync<AnotherItemT>, async: SimpleAsync<Unit>) {
//            if (async.hasSucceeded() || async.hasFailed()) {
//                return
//            }
//            myReg = myAsync.onResult(
//                    object : Consumer<ItemT> {
//                        override fun accept(item: ItemT) {
//                            myItem = item
//                            mySucceeded = true
//                            if (anotherInfo.mySucceeded!!) {
//                                async.success(null)
//                            }
//                        }
//                    },
//                    object : Consumer<Throwable> {
//                        override fun accept(failure: Throwable) {
//                            //reg == null can happen in case if myAsync fails instantly
//                            if (anotherInfo.myReg != null) {
//                                anotherInfo.myReg!!.remove()
//                            }
//                            async.failure(failure)
//                        }
//                    })
//        }
//    }


}
