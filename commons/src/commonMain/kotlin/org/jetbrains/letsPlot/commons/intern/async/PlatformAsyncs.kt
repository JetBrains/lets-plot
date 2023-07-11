/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.async

import org.jetbrains.letsPlot.commons.intern.gcommon.collect.TreeMap
import org.jetbrains.letsPlot.commons.intern.concurrent.AtomicInteger
import org.jetbrains.letsPlot.commons.intern.concurrent.Lock
import org.jetbrains.letsPlot.commons.intern.concurrent.execute

object PlatformAsyncs {

    fun parallel(vararg asyncs: Async<*>): Async<Unit> {
        return parallel(listOf(*asyncs))
    }

    fun <ItemT> parallelResult(asyncs: Collection<Async<out ItemT>>): Async<List<ItemT>> {
        return runParallel(asyncs, true)
    }

    fun <ItemT> parallel(asyncs: Collection<Async<out ItemT>>, alwaysSucceed: Boolean = false): Async<Unit> {
        return Asyncs.toUnit(runParallel(asyncs, alwaysSucceed))
    }

    fun <ItemT> composite(asyncs: List<Async<ItemT>>): Async<List<ItemT>> {
        return runParallel(asyncs, false)
    }

    private fun <ItemT> runParallel(
        asyncs: Collection<Async<out ItemT>>,
        alwaysSucceed: Boolean
    ): Async<List<ItemT>> {
        val result: ResolvableAsync<List<ItemT>> = ThreadSafeAsync()
        val inProgress = AtomicInteger(asyncs.size)
        val values = OrderedValues<ItemT>()
        val exceptions = ThreadSafeThrowables()

        val checkTermination = {
            if (inProgress.decrementAndGet() <= 0) {
                if (!exceptions.isEmpty && !alwaysSucceed) {
                    result.failure(exceptions.toSingleException())
                } else {
                    result.success(values.get())
                }
            }
        }

        for ((i, async) in asyncs.withIndex()) {
            async.onResult(
                { item ->
                    values[i] = item
                    checkTermination()
                },
                { failure ->
                    exceptions.add(failure)
                    checkTermination()
                })
        }

        if (asyncs.isEmpty()) {
            checkTermination()
        }

        return result
    }


    private class OrderedValues<ValueT> {
        private val myLock = Lock()
        private val myMap = TreeMap<Int, ValueT>()

        internal operator fun set(index: Int, value: ValueT) {
            myLock.execute {
                myMap.put(index, value)
            }
        }

        internal fun get(): List<ValueT> {
            myLock.execute {
                return ArrayList(myMap.values)
            }
        }
    }

    private class ThreadSafeThrowables {
        private val myLock = Lock()
        private val myThrowables = ArrayList<Throwable>(0)

        internal val isEmpty: Boolean
            get() = myLock.execute {
                return myThrowables.isEmpty()
            }

        internal fun add(t: Throwable) {
            myLock.execute {
                myThrowables.add(t)
            }
        }

        internal fun toSingleException(): Throwable {
            myLock.execute {
                check(!myThrowables.isEmpty()) { "Empty collection" }

                return if (myThrowables.size == 1) {
                    myThrowables[0]
                } else ThrowableCollectionException(ArrayList(myThrowables))
            }
        }
    }
}