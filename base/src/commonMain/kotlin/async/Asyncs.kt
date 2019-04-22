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
                object : Consumer<Any?> {
                    override fun accept(value: Any?) {
                        finished.set(true)
                    }
                },
                object : Consumer<Throwable> {
                    override fun accept(value: Throwable) {
                        finished.set(true)
                    }
                }).remove()
        return finished.get()
    }

    fun <ValueT> constant(value: ValueT): Async<ValueT> {
        return object : Async<ValueT> {
            override fun onSuccess(successHandler: Consumer<in ValueT>): Registration {
                successHandler.accept(value)
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
                    return Asyncs.failure(t)
                }

                //return cannot be moved to try block to avoid catching possible errors from Asyncs.constant call
                return Asyncs.constant(result)
            }

            override fun <ResultT> flatMap(success: (ValueT) -> Async<ResultT>?): Async<ResultT?> {
                val result: Async<ResultT>?
                try {
                    result = success(value)
                } catch (t: Throwable) {
                    return Asyncs.failure(t)
                }

                //return cannot be moved to try block to avoid catching possible errors from Asyncs.constant call
                if (result == null) {
                    return Asyncs.constant<ResultT?>(null)
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
                failureHandler.accept(t)
                return Registration.EMPTY
            }

            override fun <ResultT> map(success: (ValueT) -> ResultT): Async<ResultT> {
                return Asyncs.failure(t)
            }

            override fun <ResultT> flatMap(success: (ValueT) -> Async<ResultT>?): Async<ResultT?> {
                return Asyncs.failure(t)
            }
        }
    }

    fun voidAsync(): Async<Unit?> {
        return Asyncs.constant<Unit?>(null)
    }

//    fun <ResultT> toUnit(async: Async<ResultT>): Async<Unit> {
//        return map<ResultT, Unit, ResultT>(async, { null }, ThreadSafeAsync<Unit>())
//    }

    internal fun <SourceT, TargetT, AsyncResultT : SourceT> map(
            async: Async<AsyncResultT>,
            f: (SourceT) -> TargetT,
            resultAsync: ResolvableAsync<TargetT>):
            Async<TargetT> {

        async.onResult(
                object : Consumer<AsyncResultT> {
                    override fun accept(value: AsyncResultT) {
                        val result: TargetT
                        try {
                            result = f(value)
                        } catch (e: Exception) {
                            resultAsync.failure(e)
                            return
                        }

                        resultAsync.success(result)
                    }
                },
                object : Consumer<Throwable> {
                    override fun accept(value: Throwable) {
                        resultAsync.failure(value)
                    }
                })
        return resultAsync
    }

    internal fun <SourceT, TargetT> select(
            async: Async<SourceT>,
            f: (SourceT) -> Async<TargetT>?,
            resultAsync: ResolvableAsync<TargetT?>):
            Async<TargetT?> {

        async.onResult(
                object : Consumer<SourceT> {
                    override fun accept(value: SourceT) {
                        val async1: Async<TargetT>?
                        try {
                            async1 = f(value)
                        } catch (e: Exception) {
                            resultAsync.failure(e)
                            return
                        }

                        if (async1 == null) {
                            resultAsync.success(null)
                        } else {
                            delegate(async1, resultAsync)
                        }
                    }
                },
                object : Consumer<Throwable> {
                    override fun accept(value: Throwable) {
                        resultAsync.failure(value)
                    }
                })
        return resultAsync
    }

    fun <FirstT, SecondT> seq(first: Async<FirstT>, second: Async<SecondT>): Async<SecondT?> {
        return select(first, { second }, ThreadSafeAsync<SecondT?>())
    }


// When converting `parallel` to Kotlin
// uncomment parallel tests in AsyncsTest

//    fun parallel(vararg asyncs: Async<*>): Async<Unit> {
//        return parallel(Arrays.asList(*asyncs))
//    }

//    fun <ItemT> parallelResult(asyncs: Collection<Async<out ItemT>>): Async<List<ItemT>> {
//        return runParallel(asyncs, true)
//    }

//    @JvmOverloads
//    fun parallel(asyncs: Collection<Async<*>>, alwaysSucceed: Boolean = false): Async<Unit> {
//        return toUnit(runParallel<Any>(asyncs, alwaysSucceed))
//    }

//    private fun <ItemT> runParallel(asyncs: Collection<Async<out ItemT>>,
//                                    alwaysSucceed: Boolean): Async<List<ItemT>> {
//        val result = ThreadSafeAsync()
//        val inProgress = AtomicInteger(asyncs.size)
//        val values = OrderedValues<ItemT>()
//        val exceptions = ThreadSafeThrowables()
//
//        val checkTermination = object : Runnable {
//            override fun run() {
//                if (inProgress.decrementAndGet() <= 0) {
//                    if (!exceptions.isEmpty && !alwaysSucceed) {
//                        result.failure(exceptions.toSingleException())
//                    } else {
//                        result.success(values.get())
//                    }
//                }
//            }
//        }
//
//        var i = 0
//        for (async in asyncs) {
//            val index = i++
//            async.onResult(
//                    object : Consumer<ItemT> {
//                        override fun accept(item: ItemT) {
//                            values[index] = item
//                            checkTermination.run()
//                        }
//                    },
//                    object : Consumer<Throwable> {
//                        override fun accept(failure: Throwable) {
//                            exceptions.add(failure)
//                            checkTermination.run()
//                        }
//                    })
//        }
//
//        if (asyncs.isEmpty()) {
//            checkTermination.run()
//        }
//
//        return result
//    }

    fun <ItemT> composite(asyncs: List<Async<ItemT>>): Async<List<ItemT>> {
        throw IllegalStateException("was not converted to Kotlin")

        // while converting to Kotlin,
        // jetbrains.datalore.base.async.CompositeAsyncTest
        // should also be converted
    }

    fun onAnyResult(async: Async<*>, r: Runnable): Registration {
        return async.onResult(object : Consumer<Any?> {
            override fun accept(value: Any?) {
                r.run()
            }
        }, object : Consumer<Throwable> {
            override fun accept(value: Throwable) {
                r.run()
            }
        })
    }

    fun <ResultT> untilSuccess(s: Supplier<Async<ResultT>>): Async<ResultT> {
        val result = SimpleAsync<ResultT>()
        val async: Async<ResultT>
        val successConsumer = object : Consumer<ResultT> {
            override fun accept(value: ResultT) {
                result.success(value)
            }
        }
        try {
            async = s.get()
        } catch (ignore: Exception) {
            untilSuccess(s).onSuccess(successConsumer)
            return result
        }

        async.onResult(successConsumer,
                object : Consumer<Throwable> {
                    override fun accept(value: Throwable) {
                        untilSuccess(s).onSuccess(successConsumer)
                    }
                })
        return result
    }

    internal fun <ValueT> delegate(from: Async<out ValueT>, to: AsyncResolver<in ValueT>): Registration {
        return from.onResult(
                object : Consumer<ValueT> {
                    override fun accept(value: ValueT) {
                        to.success(value)
                    }
                }, object : Consumer<Throwable> {
            override fun accept(value: Throwable) {
                to.failure(value)
            }
        })
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

//    private class OrderedValues<ValueT> {
//        private val myLock = Any()
//        private val myMap = TreeMap<Int, ValueT>()
//
//        internal operator fun set(index: Int, value: ValueT) {
//            synchronized(myLock) {
//                myMap.put(index, value)
//            }
//        }
//
//        internal fun get(): List<ValueT> {
//            synchronized(myLock) {
//                return ArrayList<ValueT>(myMap.values)
//            }
//        }
//    }

//    private class ThreadSafeThrowables {
//        private val myLock = Any()
//        private val myThrowables = ArrayList<Throwable>(0)
//
//        internal val isEmpty: Boolean
//            get() = synchronized(myLock) {
//                return myThrowables.isEmpty()
//            }
//
//        internal fun add(t: Throwable) {
//            synchronized(myLock) {
//                myThrowables.add(t)
//            }
//        }
//
//        internal fun toSingleException(): Throwable {
//            synchronized(myLock) {
//                if (myThrowables.isEmpty()) {
//                    throw IllegalStateException("Empty collection")
//                }
//                return if (myThrowables.size == 1) {
//                    myThrowables[0]
//                } else ThrowableCollectionException(ArrayList<E>(myThrowables))
//            }
//        }
//    }
}
