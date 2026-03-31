/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.visualtesting

import org.jetbrains.letsPlot.commons.values.Bitmap
import org.jetbrains.letsPlot.core.canvas.CanvasPeer
import kotlin.reflect.KFunction0

internal abstract class TestSuit {
    abstract val imageComparer: ImageComparer
    abstract val canvasPeer: CanvasPeer

    val name: String = this::class.simpleName ?: "TestSuit"
    private val tests: MutableList<KFunction0<Bitmap>> = mutableListOf()

    fun registerTest(test: KFunction0<Bitmap>) {
        tests.add(test)
    }

    internal fun runTests(): Int {
        var failedTestsCount = 0
        println("'$name' - running ${tests.size} tests...\n")
        for (test in tests) {
            val res = runCatching {
                val expectedFileName = test.name.replace(" ", "_").replace(".", "_")
                val actual = test.invoke()
                imageComparer.assertBitmapEquals(expectedFileName, actual)
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
