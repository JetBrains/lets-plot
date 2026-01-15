package org.jetbrains.letsPlot.visualtesting

import kotlin.reflect.KFunction0

internal open class TestSuit {
    val name: String = this::class.simpleName ?: "TestSuit"
    private val tests: MutableList<KFunction0<Unit>> = mutableListOf()

    fun registerTest(test: KFunction0<Unit>) {
        tests.add(test)
    }

    internal fun runTests() {
        var failedTestsCount = 0
        println("'$name' - running ${tests.size} tests...")
        for ((i, test) in tests.withIndex()) {
            val res = runCatching { test.invoke() }
            if (res.isFailure) {
                println("[FAILED]: '${test.name}' - ${res.exceptionOrNull()?.message}")
                failedTestsCount++
            } else {
                println("[PASSED]: '${test.name}'")
            }
        }

        if (failedTestsCount > 0) {
            error("$failedTestsCount tests failed!")
        }
    }

}
