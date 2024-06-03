/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.platf.w3c.jsObject

import kotlin.test.Test
import kotlin.test.assertEquals

class JsDynamicToAnyTest {

    @Test
    fun runTestCases() {
        for ((index, datum) in testData.withIndex()) {
            val actual = dynamicToAnyQ(datum.input)
            assertEquals(datum.expectedOutput, actual, "test case [$index]")
        }
    }

    class TestData(val input: dynamic, val expectedOutput: Any?)

    companion object {
        @Suppress("UnsafeCastFromDynamic")
        val testData: List<TestData> = listOf(
            TestData(
                js("{}"),
                emptyMap<Any, Any>()
            ),
            TestData(
                js("{a:null,b:null}"),  // null values are dropped
                emptyMap<Any, Any>()
            ),
            TestData(
                js("[]"),
                emptyList<Any?>()
            ),
            TestData(
                js("[1, 2]"),
                listOf(1, 2)
            ),
            TestData(
                js("[1.0, 2.0]"),
                listOf(1.0, 2.0)
            ),
            TestData(
                js("[1.0, null, 'Hello']"),
                listOf(1.0, null, "Hello")
            ),
            TestData(
                js("null"),
                null
            ),
            TestData(
                js("1"),
                1
            ),
            TestData(
                js("1.5"),
                1.5
            ),
            TestData(
                js("'Hello'"),
                "Hello"
            ),
            TestData(
                js(
                    """{
                                'int':1,    
                                'double':2.2,    
                                'str':"hello",    
                                'null':null,    
                                'obj':{}    
                            }
                            """
                ),
                mapOf(
                    "int" to 1,
                    "double" to 2.2,
                    "str" to "hello",
//                    "none" to null,                  // null values are dropped
                    "obj" to emptyMap<String, Any?>()
                )
            ),
            TestData(
                js("{level_1:{level_2:{list:[1,1,null,100.0]}}}"),
                mapOf(
                    "level_1" to mapOf<String, Any?>(
                        "level_2" to mapOf<String, Any?>(
                            "list" to listOf<Any?>(1, 1, null, 100.00)
                        )
                    )
                )
            )
        )
    }
}