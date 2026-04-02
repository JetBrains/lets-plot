/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.visualtesting

import org.jetbrains.letsPlot.commons.values.Bitmap
import org.jetbrains.letsPlot.core.canvas.CanvasPeer
import org.jetbrains.letsPlot.visualtesting.ImageComparer.ComparisonProfile
import kotlin.reflect.KClass
import kotlin.reflect.KFunction1

abstract class TestSuit<T : TestSuit<T>> {
    abstract val imageComparer: ImageComparer
    abstract val canvasPeer: CanvasPeer
    open val defaultComparisonProfile: ComparisonProfile = ComparisonProfile.Strict

    val name: String = this::class.simpleName ?: "TestSuit"
    private val testSuiteClass: KClass<out TestSuit<*>> = this::class
    private data class RegisteredTest<T>(
        val test: KFunction1<T, Bitmap>,
        val profile: ComparisonProfile
    )

    private val tests: MutableList<RegisteredTest<T>> = mutableListOf()

    fun registerTest(test: KFunction1<T, Bitmap>, profile: ComparisonProfile = defaultComparisonProfile) {
        tests.add(RegisteredTest(test, profile))
    }

    fun runTests(): Int {
        var failedTestsCount = 0
        println("'$name' - running ${tests.size} tests...\n")
        @Suppress("UNCHECKED_CAST")
        val self = this as T
        for ((test, profile) in tests) {
            val res = runCatching {
                val expectedFileName = test.name.replace(" ", "_").replace(".", "_")
                val actual = test.invoke(self)
                imageComparer.assertBitmapEquals(expectedFileName, actual, profile, testSuiteClass, test)
            }
            if (res.isFailure) {
                println("[FAILED]: '${test.name}' - ${res.exceptionOrNull()?.message}\n")
                failedTestsCount++
            } else {
                println("[PASSED]: '${test.name}'\n")
            }
        }

        return failedTestsCount
    }
}
