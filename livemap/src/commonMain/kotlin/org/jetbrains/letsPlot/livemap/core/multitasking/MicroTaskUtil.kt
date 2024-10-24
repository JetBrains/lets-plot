/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.core.multitasking

object MicroTaskUtil {
    private val EMPTY_MICRO_TASK: MicroTask<Unit> = object : MicroTask<Unit> {
        override fun getResult() {}

        override fun resume() {}

        override fun alive(): Boolean = false
    }

    internal fun <ItemT, ResultT> map(
        microTask: MicroTask<ItemT>,
        mapFunction: (ItemT) -> ResultT
    ): MicroTask<ResultT> {
        return object : MicroTask<ResultT> {

            private var result: ResultT? = null
            private var transformed = false

            override fun resume() {
                if (microTask.alive()) {
                    microTask.resume()
                } else if (!transformed) {
                    result = mapFunction(microTask.getResult())
                    transformed = true
                }
            }

            override fun alive(): Boolean = microTask.alive() || !transformed

            override fun getResult(): ResultT = result ?: error("")
        }
    }

    internal fun <ItemT, ResultT> flatMap(
        microTask: MicroTask<ItemT>,
        mapFunction: (ItemT) -> MicroTask<ResultT>
    ): MicroTask<ResultT> {
        return object : MicroTask<ResultT> {
            private var transformed = false
            private var result: MicroTask<ResultT>? = null


            override fun resume() {
                if (microTask.alive()) {
                    microTask.resume()
                } else if (!transformed) {
                    result = mapFunction(microTask.getResult())
                    transformed = true
                } else if (result!!.alive()) {
                    result!!.resume()
                }
            }

            override fun alive() = microTask.alive() || !transformed || result!!.alive()

            override fun getResult(): ResultT = result!!.getResult()
        }
    }

    fun create(task: () -> Unit): MicroTask<Unit> {
        return CompositeMicroTask(listOf(task))
    }

    fun create(tasks: Iterable<() -> Unit>): MicroTask<Unit> {
        return CompositeMicroTask(tasks)
    }

    fun join(tasks: Iterable<MicroTask<Unit>>): MicroTask<Unit> {
        return MultiMicroTask(tasks)
    }

    fun <FirstT, SecondT> pair(first: MicroTask<FirstT>, second: MicroTask<SecondT>): MicroTask<Pair<FirstT, SecondT>> {
        return PairMicroTask(first, second)
    }

    private class CompositeMicroTask internal constructor(tasks: Iterable<() -> Unit>) : MicroTask<Unit> {

        private val iterator = tasks.iterator()

        override fun resume() {
            iterator.next()()
        }

        override fun alive(): Boolean = iterator.hasNext()

        override fun getResult() {}
    }

    private class PairMicroTask<FirstT, SecondT> internal constructor(
        private val first: MicroTask<FirstT>,
        private val second: MicroTask<SecondT>
    ) : MicroTask<Pair<FirstT, SecondT>> {
        private var firstResult: FirstT? = null
        private var secondResult: SecondT? = null

        override fun resume() {
            if (firstResult == null) {
                first.resume()
                if (!first.alive()) {
                    firstResult = first.getResult()
                }
            }
            if (secondResult == null) {
                second.resume()
                if (!second.alive()) {
                    secondResult = second.getResult()
                }
            }
        }

        override fun alive(): Boolean = first.alive() || second.alive()

        override fun getResult(): Pair<FirstT, SecondT> {
            return Pair(firstResult!!, secondResult!!)
        }
    }

    private class MultiMicroTask internal constructor(microTasks: Iterable<MicroTask<Unit>>) : MicroTask<Unit> {

        private val tasks: Iterator<MicroTask<Unit>> = microTasks.iterator()
        private var currentMicroTask = EMPTY_MICRO_TASK

        init {
            goToNextAliveMicroTask()
        }

        override fun resume() {
            currentMicroTask.resume()
            goToNextAliveMicroTask()
        }

        override fun alive(): Boolean = currentMicroTask.alive()

        override fun getResult() {}

        private fun goToNextAliveMicroTask() {
            while (!currentMicroTask.alive()) {
                if (tasks.hasNext()) {
                    currentMicroTask = tasks.next()
                } else {
                    return
                }
            }
        }
    }
}