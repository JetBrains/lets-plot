/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.platf.w3c.jsObject

import kotlin.test.Test
import kotlin.test.assertEquals

class JsObjectSupportJsTest {

    @Test
    fun runTestCases() {
        for ((index, datum) in testData.withIndex()) {
            val actual = dynamicObjectToMap(datum.input)
            assertEquals(datum.expectedOutput, actual, "test case [$index]")
        }
    }

    class TestData(val input: dynamic, val expectedOutput: Map<String, Any?>)

    companion object {
        @Suppress("UnsafeCastFromDynamic")
        val testData: List<TestData> = listOf(
            TestData(
                js("{}"),
                emptyMap()
            ),
            TestData(
                js("{a:null,b:null}"),  // null values are dropped
                emptyMap()
            ),
            TestData(
                js("{'array':[]}"),
                mapOf(
                    "array" to emptyList<Any?>()
                )
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