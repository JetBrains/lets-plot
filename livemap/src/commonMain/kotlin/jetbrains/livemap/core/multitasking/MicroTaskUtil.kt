package jetbrains.livemap.core.multitasking

object MicroTaskUtil {
    private val EMPTY_MICRO_THREAD: MicroTask<Unit> = object : MicroTask<Unit> {
        override fun getResult(): Unit? {return null}

        override fun resume() {}

        override fun alive(): Boolean = false
    }

    internal fun <ItemT, ResultT> map(
        microTask: MicroTask<ItemT>,
        mapFunction: (ItemT?) -> ResultT
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

            override fun getResult(): ResultT? = result
        }
    }

    internal fun <ItemT, ResultT> flatMap(
        microTask: MicroTask<ItemT>,
        mapFunction: (ItemT?) -> MicroTask<ResultT>
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

            override fun getResult(): ResultT? = result!!.getResult()
        }
    }

    fun create(tasks: Iterable<() -> Unit>): MicroTask<Unit> {
        return CompositeMicroThread(tasks)
    }

    fun join(tasks: Iterable<MicroTask<Unit>>): MicroTask<Unit> {
        return MultiMicroThread(tasks)
    }

    private class CompositeMicroThread internal constructor(tasks: Iterable<() -> Unit>) : MicroTask<Unit> {

        private val myTasks = tasks.iterator()

        override fun resume() {
            myTasks.next()()
        }

        override fun alive(): Boolean = myTasks.hasNext()

        override fun getResult(): Unit? = null
    }

    private class MultiMicroThread internal constructor(microThreads: Iterable<MicroTask<Unit>>) : MicroTask<Unit> {

        private val threads: Iterator<MicroTask<Unit>> = microThreads.iterator()
        private var currentMicroThread = EMPTY_MICRO_THREAD

        init {
            goToNextAliveMicroThread()
        }

        override fun resume() {
            currentMicroThread.resume()
            goToNextAliveMicroThread()
        }

        override fun alive(): Boolean = currentMicroThread.alive()

        override fun getResult(): Unit? = null

        private fun goToNextAliveMicroThread() {
            while (!currentMicroThread.alive()) {
                if (threads.hasNext()) {
                    currentMicroThread = threads.next()
                } else {
                    return
                }
            }
        }
    }
}