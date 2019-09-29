package jetbrains.livemap.core.multitasking.coroutine

import jetbrains.livemap.core.multitasking.coroutine.CooperativeCoroutineTaskExecutor.ExecutionLimitPolicy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.yield
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue


class CooperativeCoroutineTaskExecutorTest {
    private lateinit var coroutineTaskExecutor: CooperativeCoroutineTaskExecutor
    private lateinit var executionLimitPolicy: ExecuteTasksOnlyOnce

    private lateinit var dispatcher: CooperativeCoroutineDispatcher

    @BeforeTest
    fun setUp() {
        dispatcher = CooperativeCoroutineDispatcher()
        executionLimitPolicy = ExecuteTasksOnlyOnce()
        coroutineTaskExecutor = CooperativeCoroutineTaskExecutor(dispatcher, executionLimitPolicy)
    }

    @Test
    fun noYield() {
        val task = microCoThread { }

        execute(task) // task: done

        assertTrue { task.isCompleted }
    }

    @Test
    fun simple() {

        val task = microCoThread {
            yield() // 2
            yield() // 1
        }

        execute(task) // task: start (2 left)
        execute(task) // task: 1 left
        execute(task) // task: done

        assertTrue { task.isCompleted }
    }

    @Test
    fun withQuantum() {

        val task = microCoThread {
            quantum = 2
            println("4 left")
            yield()
            println("3 left")
            yield()

            println("2 left")
            yield()
            println("1 left")
            yield()
            println("Done")
        }

        execute(task) // task: start (4 left)
        execute(task) // task: 2 left
        execute(task) // task: done

        assertTrue { task.isCompleted }
    }

    @Test
    fun quantumOverlapping() {

        val task = microCoThread {
            quantum = 2
            yield()
            quantum = 1 // ignored untill next resume
            yield()

            yield()
            yield()
        }

        execute(task) // task: start (4 left)
        execute(task) // task: 2 left
        execute(task) // task: 1 left
        execute(task) // task: done

        assertTrue { task.isCompleted }
    }


    @Test
    fun twoSimpleTasks() {
        val task1 = microCoThread {
            yield() // 2
            yield() // 1
        }

        val task2 = microCoThread {
            yield() // 2
            yield() // 1
        }

        execute(task1, task2) // task1: start (2 left)
        execute(task1, task2) // task2: start (2 left)
        execute(task1, task2) // task1: 1 left
        execute(task1, task2) // task2: 1 left
        execute(task1, task2) // task1: done
        execute(task1, task2) // task2: done

        assertTrue { task1.isCompleted }
        assertTrue { task2.isCompleted }
    }

    @Test
    fun twoUnevenTasks() {
        val task1 = microCoThread {
            quantum = 1
            pyield("t1: 4 left") // 4
            pyield("t1: 3 left") // 3
            pyield("t1: 2 left") // 2
            pyield("t1: 1 left") // 1
            println("t1: done")
        }

        val task2 = microCoThread {
            quantum = 2
            pyield("t2: 4 left") // 4
            pyield("t2: 3 left")
            pyield("t2: 2 left") // 2
            pyield("t2: 1 left")
            println("t2: done")
        }

        execute(task1, task2)
        println("execute() task1: start (4 left), task2: start (4 left)")
        execute(task1, task2) // task1: 3 left
        println("execute() task1: 3 left, task2: 2 left")
        execute(task1, task2) // task2: 2 left
        println("execute() task1: 2 left, task2: Done")

        assertFalse { task1.isCompleted }
        assertTrue { task2.isCompleted }

        execute(task1, task2)
        println("execute() task1: 1 left, task2: Done")
        execute(task1, task2)
        println("execute() task1: Done, task2: Done")

        assertTrue { task1.isCompleted }
        assertTrue { task2.isCompleted }
    }


    private fun <T> microCoThread(block: suspend CoroutineScope.() -> T): MicroCoThreadComponent {
        val context = dispatcher
        return microCoThread(context, block)
    }

    private fun execute(vararg tasks: MicroCoThreadComponent) {
        executionLimitPolicy.tasksCount = tasks.size
        coroutineTaskExecutor.execute(tasks.asSequence())
    }

    private suspend fun pyield(s: String) {
        println(s)
        yield()
    }

    /**
     * Allows to execute every task only once.
     * To do so we expect that [allowed] will be called on task switch, not on task reapeat
     */
    class ExecuteTasksOnlyOnce : ExecutionLimitPolicy {
        var tasksCount: Int = 0

        override fun allowed(): Boolean {
            return when (tasksCount--) {
                0 -> false
                else -> true
            }
        }
    }
}

