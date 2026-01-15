package org.jetbrains.letsPlot.visualtesting

import kotlin.reflect.KFunction0

internal open class TestSuit {
    val name: String = this::class.simpleName ?: "TestSuit"
    private val tests: MutableList<KFunction0<Unit>> = mutableListOf()

    fun registerTest(test: KFunction0<Unit>) {
        tests.add(test)
    }

    internal fun runTests(): Int {
        var failedTestsCount = 0
        println("'$name' - running ${tests.size} tests...")
        for (test in tests) {
            val res = runCatching { test.invoke() }
            if (res.isFailure) {
                println("[FAILED]: '${test.name}' - ${res.exceptionOrNull()?.message}")
                failedTestsCount++
            } else {
                println("[PASSED]: '${test.name}'")
            }
        }

        return failedTestsCount
    }
}
