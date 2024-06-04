/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.platf.w3c.jsObject

import kotlin.test.Test
import kotlin.test.assertEquals

class JsObjectFromMapTest {

    @Test
    fun runTestCases() {
        for ((index, datum) in testData.withIndex()) {
            val jsObject = dynamicFromAnyQ(datum.input)
            // transform back for comparison to work
            val actual = dynamicToAnyQ(jsObject) // this is supposed to be well tested
            assertEquals(expected = datum.input, actual, "test case [$index]")
        }
    }

    class TestData(val input: Any?)

    companion object {
        @Suppress("UnsafeCastFromDynamic")
        val testData: List<TestData> = listOf(
            TestData(
                null,
            ),
            TestData(
                "Hello!",
            ),
            TestData(
                10,
            ),
            TestData(
                10.5,
            ),
            TestData(
                listOf(1, 2, 3),
            ),
            TestData(
                listOf(1.2, 2.5, 3.8),
            ),
            TestData(
                listOf("Hello!", null, 3.0),
            ),
            TestData(
                emptyMap<Any, Any>(),
            ),
            TestData(
                mapOf(
                    "array" to emptyList<Any?>()
                ),
            ),
            TestData(
                mapOf(
                    "int" to 1,
                    "double" to 2.2,
                    "str" to "hello",
                    "obj" to emptyMap<String, Any?>()
                ),
            ),
            TestData(
                mapOf(
                    "level_1" to mapOf<String, Any?>(
                        "level_2" to mapOf<String, Any?>(
                            "list" to listOf<Any?>(1, 1, null, 100.00)
                        )
                    )
                ),
            )
        )
    }
}