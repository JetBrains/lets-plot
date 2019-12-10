/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.json

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class JsonSupportTest {

    @Test
    fun runTestCases() {
        for ((index, datum) in testData.withIndex()) {
            val actualObj: MutableMap<String, Any?>
            try {
                actualObj = JsonSupport.parseJson(datum.input)
                assertEquals(datum.expectedOutput, actualObj, "test case [$index] - parseJson")
            } catch (e: Throwable) {
                fail("test case ${datum.input} failed with exception ${e.message}")
            }
            try {
                val actualJson = JsonSupport.formatJson(actualObj)
                val parsedObject = JsonSupport.parseJson(actualJson)
                assertEquals(datum.expectedOutput, parsedObject, "test case [$index] - formatJson")
            } catch (e: Throwable) {
                fail("test case ${actualObj} failed with exception ${e.message}")
            }
        }
    }

    class TestData(val input: String, val expectedOutput: Map<String, Any?>)

    companion object {
        val testData: List<TestData> = listOf(
            TestData(
                """{"a":"\"like\" \\this\\"}""",
                mapOf(
                    "a" to """"like" \this\"""
                )
            ),
            TestData(
                """{"a":"str\twith special\ncharacters \"like\" \\this\\"}""",
                mapOf(
                    "a" to "str\twith special\ncharacters \"like\" \\this\\"
                )
            ),
            TestData(
                "{}",
                emptyMap()
            ),
            TestData(
                """{"k":"v"}""",
                mapOf("k" to "v")
            ),
            TestData(
                """{"a":null,"b":null}""",
                mapOf("a" to null, "b" to null)
            ),
            TestData(
                """{"array":[]}""",
                mapOf(
                    "array" to emptyList<Any?>()
                )
            ),
            TestData(
                """{"int":1,"double":2.2,"str":"hello","null":null,"obj":{}}""",
                mapOf(
                    "int" to 1.0,
                    "double" to 2.2,
                    "str" to "hello",
                    "null" to null,
                    "obj" to emptyMap<String, Any?>()
                )
            ),
            TestData(
                """{"a":["\"{\"b\":\"c\"}\"","\"{\"d\":\"e\"}\""]}""",
                mapOf(
                    "a" to listOf(""""{"b":"c"}"""", """"{"d":"e"}"""")
                )
            ),
            TestData(
                """{"level_1":{"level_2":{"list":[1,1,null,100.0]}}}""",
                mapOf(
                    "level_1" to mapOf<String, Any?>(
                        "level_2" to mapOf<String, Any?>(
                            "list" to listOf<Any?>(1.0, 1.0, null, 100.00)
                        )
                    )
                )
            ),
            TestData(
                """{"list":["1",2,{"3":5}]}""",
                mapOf(
                    "list" to listOf<Any?>("1", 2.0, mapOf("3" to 5.0))
                )
            )
        )
    }
}
