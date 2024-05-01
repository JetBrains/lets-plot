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
            val jsObject = dynamicObjectFromMap(datum.input)
            // transform back for comparison to work
            val actual = dynamicObjectToMap(jsObject) // this is supposed to be well tested
            assertEquals(expected = datum.input, actual, "test case [$index]")
        }
    }

//    class TestData(val input: Map<String, Any>, val expectedOutput: dynamic)
    class TestData(val input: Map<String, Any>)

    companion object {
        @Suppress("UnsafeCastFromDynamic")
        val testData: List<TestData> = listOf(
            TestData(
                emptyMap(),
//                js("{}"),
            ),
//            TestData(
//                emptyMap(),
//                js("{a:null,b:null}"),  // null values are dropped
//            ),
            TestData(
                mapOf(
                    "array" to emptyList<Any?>()
                ),
//                js("{'array':[]}"),
            ),
            TestData(
                mapOf(
                    "int" to 1,
                    "double" to 2.2,
                    "str" to "hello",
                    "obj" to emptyMap<String, Any?>()
                ),
//                js(
//                    """{
//                                'int':1,
//                                'double':2.2,
//                                'str':"hello",
//                                'obj':{}
//                            }
//                            """
//                ),
            ),
            TestData(
                mapOf(
                    "level_1" to mapOf<String, Any?>(
                        "level_2" to mapOf<String, Any?>(
                            "list" to listOf<Any?>(1, 1, null, 100.00)
                        )
                    )
                ),
//                js("{level_1:{level_2:{list:[1,1,null,100.0]}}}"),
            )
        )
    }
}