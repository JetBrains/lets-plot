package jetbrains.datalore.base.async

import jetbrains.datalore.base.function.Runnable

actual object PlatformAsyncs {
    actual fun parallel(vararg asyncs: Async<*>): Async<Unit> {
        return parallel(listOf(*asyncs))
    }

    actual fun <ItemT> parallelResult(asyncs: Collection<Async<out ItemT>>): Async<List<ItemT>> {
        return runParallel(asyncs, true)
    }

    actual fun <ItemT> parallel(asyncs: Collection<Async<out ItemT>>, alwaysSucceed: Boolean): Async<Unit> {
        return Asyncs.toUnit(runParallel(asyncs, alwaysSucceed))
    }

    actual fun <ItemT> composite(asyncs: List<Async<ItemT>>): Async<List<ItemT>> {
        return runParallel(asyncs, false)
    }

    private fun <ItemT> runParallel(asyncs: Collection<Async<out ItemT>>,
                                    alwaysSucceed: Boolean): Async<List<ItemT>> {
        val result: ResolvableAsync<List<ItemT>> = ThreadSafeAsync()
        var inProgress = asyncs.size
        val values = OrderedValues<ItemT>()
        val exceptions = ThreadSafeThrowables()

        val checkTermination = object : Runnable {
            override fun run() {
                if (inProgress-- <= 0) {
                    if (!exceptions.isEmpty && !alwaysSucceed) {
                        result.failure(exceptions.toSingleException())
                    } else {
                        result.success(values.get())
                    }
                }
            }
        }

        for ((i, async) in asyncs.withIndex()) {
            async.onResult(
                { item ->
                    values[i] = item
                    checkTermination.run()
                },
                { failure ->
                    exceptions.add(failure)
                    checkTermination.run()
                })
        }

        if (asyncs.isEmpty()) {
            checkTermination.run()
        }

        return result
    }


    private class OrderedValues<ValueT> {
        private val myLock = Any()
        private val myMap = HashMap<Int, ValueT>()

        internal operator fun set(index: Int, value: ValueT) {
            synchronized(myLock) {
                myMap.put(index, value)
            }
        }

        internal fun get(): List<ValueT> {
            synchronized(myLock) {
                return ArrayList(myMap.values)
            }
        }
    }

    private class ThreadSafeThrowables {
        private val myLock = Any()
        private val myThrowables = ArrayList<Throwable>(0)

        internal val isEmpty: Boolean
            get() = synchronized(myLock) {
                return myThrowables.isEmpty()
            }

        internal fun add(t: Throwable) {
            synchronized(myLock) {
                myThrowables.add(t)
            }
        }

        internal fun toSingleException(): Throwable {
            synchronized(myLock) {
                if (myThrowables.isEmpty()) {
                    throw IllegalStateException("Empty collection")
                }
                return if (myThrowables.size == 1) {
                    myThrowables[0]
                } else ThrowableCollectionException(ArrayList(myThrowables))
            }
        }
    }
}