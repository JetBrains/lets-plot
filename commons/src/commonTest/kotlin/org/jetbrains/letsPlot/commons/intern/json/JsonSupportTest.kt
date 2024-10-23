/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.json

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class JsonSupportTest {

    @Test
    fun parserErrorMessage() {
        val jsonString = """
            |{
            |    "a": ]
            |}
        """.trimMargin()

        val res = kotlin.runCatching { JsonSupport.parse(jsonString) }.exceptionOrNull() ?: fail("Exception expected")

        // TODO: improve error message
        assertEquals("Invalid token: RIGHT_BRACKET", res.message)
    }

    @Test
    fun unknownTokenInTheMiddleOfTheLongLine() {
        // Line is longer than 80 characters
        val jsonString = """
            |{
            |    "a": 1,
            |    "b": 2,
            |    "c": 3,
            |    "ddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd": '4', "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee": 5
            |}
        """.trimMargin()

        val res = kotlin.runCatching { JsonSupport.parse(jsonString) }.exceptionOrNull() ?: fail("Exception expected")
        val expectedErrorText = """
            |Unknown token >'< at 135 (5:98)
            |ddddddddddddddddddddddddddddddddddddd": '4', "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee
            |                                        ^
        """.trimMargin()
        assertEquals(expectedErrorText, res.message)
    }

    @Test
    fun unknownTokenAtTheStartOfTheLongLine() {
        // Line is longer than 80 characters
        val jsonString = """
            |{
            |    "a": 1,
            |    "b": 2,
            |    "c": 3,
            |    "d": '4', "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee": 5, "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff": 6
            |}
        """.trimMargin()

        val res = kotlin.runCatching { JsonSupport.parse(jsonString) }.exceptionOrNull() ?: fail("Exception expected")
        val expectedErrorText = """
            |Unknown token >'< at 47 (5:10)
            |    "d": '4', "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee
            |         ^
        """.trimMargin()
        assertEquals(expectedErrorText, res.message)
    }

    @Test
    fun unknownTokenAtTheEndOfTheLongLine() {
        // Line is longer than 80 characters
        val jsonString = """
            |{
            |    "a": 1,
            |    "b": 2,
            |    "c": 3,
            |    "ddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd": '4', "e": 5, "f": 6
            |}
        """.trimMargin()

        val res = kotlin.runCatching { JsonSupport.parse(jsonString) }.exceptionOrNull() ?: fail("Exception expected")
        val expectedErrorText = """
            |Unknown token >'< at 135 (5:98)
            |ddddddddddddddddddddddddddddddddddddd": '4', "e": 5, "f": 6
            |                                        ^
        """.trimMargin()
        assertEquals(expectedErrorText, res.message)
    }

    @Test
    fun unknownTokenInMultiLineJson() {
        val jsonString = """
            |{
            |    "a": 1,
            |    "b": 2,
            |    "c": '3',
            |    "d": 4
            |}
        """.trimMargin()

        val res = kotlin.runCatching { JsonSupport.parse(jsonString) }.exceptionOrNull() ?: fail("Exception expected")
        val expectedErrorText = """
            |Unknown token >'< at 35 (4:10)
            |    "c": '3',
            |         ^
        """.trimMargin()
        assertEquals(expectedErrorText, res.message)
    }

    @Test
    fun unknownTokenInLastLine() {
        val jsonString = """
            |{
            |    "a": 1,
            |    "b": 2,
            |    "c": 3,
            |    "d": '4' }
        """.trimMargin()

        val res = kotlin.runCatching { JsonSupport.parse(jsonString) }.exceptionOrNull() ?: fail("Exception expected")
        val expectedErrorText = """
            |Unknown token >'< at 47 (5:10)
            |    "d": '4' }
            |         ^
        """.trimMargin()
        assertEquals(expectedErrorText, res.message)
    }

    @Test
    fun unknownTokenInFirstLine() {
        val jsonString = """
            |{ "a": '1',
            |    "b": 2,
            |    "c": 3,
            |    "d": 4
            |}
        """.trimMargin()

        val res = kotlin.runCatching { JsonSupport.parse(jsonString) }.exceptionOrNull() ?: fail("Exception expected")
        val expectedErrorText = """
            |Unknown token >'< at 7 (1:8)
            |{ "a": '1',
            |       ^
        """.trimMargin()
        assertEquals(expectedErrorText, res.message)
    }

    @Test
    fun unknownTokenInSingleLineJson() {
        val jsonString = """
            |{ "a": 1, "b": 2, "c": '3', "d": 4 }
        """.trimMargin()

        val res = kotlin.runCatching { JsonSupport.parse(jsonString) }.exceptionOrNull() ?: fail("Exception expected")
        val expectedErrorText = """
            |Unknown token >'< at 23 (1:24)
            |{ "a": 1, "b": 2, "c": '3', "d": 4 }
            |                       ^
        """.trimMargin()
        assertEquals(expectedErrorText, res.message)
    }



    @Test
    fun parser() {
        val cases = listOf(
            testCase("""[null, 1, "1", {}]""", listOf<Any?>(null, 1.0, "1", emptyMap<Any, Any>())),
            testCase("""[1,null,null,null,2]""", listOf(1.0, null, null,null, 2.0)),
            testCase("""[null]""", listOf(null)),
            testCase("""null""", null),
            testCase("""" """", null),
            testCase("""true""", true),
            testCase("""false""", false),
            testCase("""[false]""", listOf(false)),
            testCase("""[true]""", listOf(true)),
            testCase("""[""]""", listOf("")),
            testCase("""[]""", listOf<Any>()),
            testCase("""{}""", mapOf<String, Any>()),
            testCase("""""""", ""),
            testCase("""42""", 42.0),
            testCase("""-0.1""", -0.1),
            testCase("""[-0]""", listOf(-0.0)),
            testCase(""" [1]""", listOf(1.0)),
            testCase("""[ 4]""", listOf(4.0)),
            testCase("""[-1]""", listOf(-1.0)),
            testCase("""[2] """, listOf(2.0)),
            testCase("""[-123]""", listOf(-123.0)),
            testCase("""[123]""", listOf(123.0)),
            testCase("""[123.456789]""", listOf(123.456789)),
            testCase("""[0e1]""", listOf(0e1)),
            testCase("""[20e1]""", listOf(20e1)),
            testCase("""[123e45]""", listOf(123e45)),
            testCase("""[1E+2]""", listOf(1e+2)),
            testCase("""[1E-2]""", listOf(1e-2)),
            testCase("""[1E22]""", listOf(1e22)),
            testCase("""[0e+1]""", listOf(0e+1)),
            testCase("""[123.456e78]""", listOf(123.456e78)),
            testCase("""[-0]""", listOf(-0.0)),
            testCase("""[-0.000000000000000000000000000000000000000000000000000000000000000000000000000001]""", listOf(-1.0E-78)),
            testCase("""{ "min": -1.0e+28, "max": 1.0e+28 }""", mapOf("min" to -1.0e+28, "max" to 1.0e+28)),
            testCase("""{"x":[{"id": "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"}], "id": "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"}""",
                mapOf("x" to listOf<Any>(mapOf("id" to "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx")), "id" to "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx")),

            testCase(""""asd"""", "asd"),
            testCase(""""\"Hello\""""", "\"Hello\""),

            testCase("""["asd "]""", listOf("asd ")),
            testCase("""[[]   ]""", listOf<Any>(listOf<Any>())),
            testCase(""" [] """, emptyList<Any>()),
            testCase("""[" "]""", listOf(" ")),
            testCase("""["a"]""", listOf("a")),
            testCase("[\"a\"]\n", listOf("a")),
            testCase("""["aa"]""", listOf("aa")),
            testCase("""["asd"]""", listOf("asd")),
            testCase("[1\n]", listOf(1.0)),
            testCase("""["a/*b*/c/*d//e"]""", listOf("a/*b*/c/*d//e")),

            testCase("""{"asd":"sdf", "dfg":"fgh"}""", mapOf("asd" to "sdf", "dfg" to "fgh")),
            testCase("""{"a":"b","a":"b"}""", mapOf("a" to "b")),
            testCase("""{"a":[]}""", mapOf("a" to emptyList<Any>())),
            testCase("""{"a":"b","a":"c"}""", mapOf("a" to "c")),
            testCase("{\n\"a\": \"b\"\n}", mapOf("a" to "b")),
            testCase("""{"":0}""", mapOf("" to 0.0)),

            testCase("""["\\n"]""", listOf("\\n")),
            testCase("""["\\a"]""", listOf("\\a")),
            testCase("""["\""]""", listOf("\"")),
            testCase("""["\"-\\-\/-\b-\f-\n-\r-\t"]""", listOf("\"-\\-/-\b-\u000C-\n-\r-\t")),

            testCase("""["\u0060\u012a\u12AB"]"""),
            testCase("""["π"]""", listOf("π")),
            testCase("""["\u0000"]""", listOf("\u0000")),
            testCase("""["\uFFFF"]""", listOf("\uFFFF")),
            testCase("""["\uDBFF\uDFFF"]""", listOf("\uDBFF\uDFFF")),
            testCase("""["\uD801\udc37"]""", listOf("\uD801\udc37")),
            testCase("""["\uA66D"]""", listOf("\uA66D")),
            testCase("""["\uD83F\uDFFE"]""", listOf("\uD83F\uDFFE")),
            testCase("""["\\u0000"]""", listOf("\\u0000")),
            testCase("""["\u0821"]""", listOf("\u0821")),
            testCase("""["new\u000Aline"]""", listOf("new\u000Aline")),
            testCase("""["\u0022"]""", listOf("\u0022")),
            testCase("""["\u0061\u30af\u30EA\u30b9"]""", listOf("\u0061\u30af\u30EA\u30b9")),
            testCase("""["\u200B"]""", listOf("\u200B")),
            testCase("""["\u0123"]""", listOf("\u0123")),
            testCase("""["\u005C"]""", listOf("\u005C")),
            testCase("""["\u2064"]""", listOf("\u2064")),
            testCase("""["\u0012"]""", listOf("\u0012")),
            testCase("""["\uFDD0"]""", listOf("\uFDD0")),
            testCase("""["\uFFFE"]""", listOf("\uFFFE")),
            testCase("""["\uDBFF\uDFFE"]""", listOf("\uDBFF\uDFFE")),
            testCase("""["€𝄞"]""", listOf("€𝄞")),
            testCase("""["\uD834\uDd1e"]""", listOf("\uD834\uDd1e")),
            testCase("""{"title":"\u041f\u043e\u043b\u0442\u043e\u0440\u0430 \u0417\u0435\u043c\u043b\u0435\u043a\u043e\u043f\u0430" }""",
                mapOf("title" to "\u041f\u043e\u043b\u0442\u043e\u0440\u0430 \u0417\u0435\u043c\u043b\u0435\u043a\u043e\u043f\u0430")),
            testCase("""["￿"]""", listOf("￿")),
            testCase("""{"foo\u0000bar": 42}""", mapOf("foo\u0000bar" to 42.0)),
            testCase("""["\ud83d\ude39\ud83d\udc8d"]""", listOf("\ud83d\ude39\ud83d\udc8d")),
            testCase("""[ "asd"]""", listOf("asd")),
            testCase("""["􏿿"]""", listOf("􏿿")),
            testCase("""["\u002c"]""", listOf("\u002c")),
            testCase("""["⍂㈴⍂"]""", listOf("⍂㈴⍂")),
            testCase("""["𛿿"]""", listOf("𛿿")),
            testCase(""""［］"""", "［］")
            )

        cases
            .drop(0)
            .forEachIndexed {index, (json, expected) ->
            try {
                val result = JsonParser(json).parseJson()
                if (expected != null) {
                    assertEquals(expected, result)
                }

            }
            catch (e: AssertionError) {
                println("$index/${cases.count()}: $json")
                throw e
            }
            catch (e: Exception) {
                println("$index/${cases.count()}: $json")
                throw e
            }
        }

    }

    private fun testCase(json: String, result: Any? = null): Pair<String, Any?> {
        return Pair(json, result)
    }

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
                """{"\"like\" \\this\\":"\"like\" \\this\\"}""",
                mapOf(
                    """"like" \this\""" to """"like" \this\"""
                )
            ),
            TestData(
                """{"str\twith special\ncharacters \"like\" \\this\\":"str\twith special\ncharacters \"like\" \\this\\"}""",
                mapOf(
                    "str\twith special\ncharacters \"like\" \\this\\" to "str\twith special\ncharacters \"like\" \\this\\"
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
                """{"int":1,"double":2.2,"str":"hello","null":null, "bool": true, "obj":{}}""",
                mapOf(
                    "int" to 1.0,
                    "double" to 2.2,
                    "str" to "hello",
                    "null" to null,
                    "bool" to true,
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
