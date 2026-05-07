/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.visualtesting

import org.jetbrains.letsPlot.commons.values.Bitmap
import org.jetbrains.letsPlot.visualtesting.ImageComparer.ComparisonProfile
import kotlin.reflect.KFunction0

abstract class TestSuitBase : VisualTestBase() {
    val name: String = this::class.simpleName ?: "TestSuit"

    private data class RegisteredTest(
        val test: KFunction0<Bitmap>,
        val profile: ComparisonProfile
    )

    private val tests: MutableList<RegisteredTest> = mutableListOf()

    fun registerTest(test: KFunction0<Bitmap>, profile: ComparisonProfile = defaultComparisonProfile) {
        tests.add(RegisteredTest(test, profile))
    }

    fun runTests(): Int {
        var failedTestsCount = 0
        println("'$name' - running ${tests.size} tests...\n")
        for ((test, profile) in tests) {
            failedTestsCount += runTest(test, profile)
        }

        return failedTestsCount
    }

    fun assertTest(test: KFunction0<Bitmap>, profile: ComparisonProfile? = null) {
        val actual = test.invoke()
        assertImage(actual, test.name, profile)
    }

    private fun runTest(test: KFunction0<Bitmap>, profile: ComparisonProfile?): Int {
        val res = runCatching {
            assertTest(test, profile)
        }
        if (res.isFailure) {
            println("[FAILED]: '${test.name}' - ${res.exceptionOrNull()?.message}\n")
            return 1
        } else {
            println("[PASSED]: '${test.name}'\n")
        }
        return 0
    }
}
