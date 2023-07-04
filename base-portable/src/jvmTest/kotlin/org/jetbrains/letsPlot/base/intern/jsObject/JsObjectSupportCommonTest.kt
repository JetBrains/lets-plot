/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.org.jetbrains.letsPlot.base.intern.jsObject

import org.jetbrains.letsPlot.base.intern.jsObject.JsObjectSupportCommon.mapToJsObjectInitializer
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.test.Test
import kotlin.test.assertEquals


@RunWith(Parameterized::class)
class JsObjectSupportCommonTest(private val input: Map<String, Any?>, private val expected: String) {

    @Test
    fun `map to JSObject initializer`() {
        val actual = mapToJsObjectInitializer(input)
        assertEquals(clean(expected), clean(actual))
    }

    private fun clean(s: String): String = s.replace("[\\s\\n]".toRegex(), "")

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data() = listOf(
            arrayOf(emptyMap<String, Any?>(), "{}"),
            arrayOf(
                mapOf<String, Any?>(
                    "array" to emptyArray<Any?>()
                ), "{\"array\":[]}"
            ),
            arrayOf(
                mapOf<String, Any?>(
                    "list" to emptyList<Any?>()
                ), "{\"list\":[]}"
            ),
            arrayOf(
                mapOf(
                    "int" to 1,
                    "double" to 2.2,
                    "str" to "hello",
                    "null" to null,
                    "obj" to emptyMap<String, Any?>()
                ), """
                {
                    "int":1,    
                    "double":2.2,    
                    "str":"hello",    
                    "null":null,    
                    "obj":{}    
                }
            """
            ),
            arrayOf(
                mapOf<String, Any?>(
                    "not identifier!!" to "hello"
                ), """{"not identifier!!":"hello"}"""
            ),
            arrayOf(
                mapOf<String, Any?>(
                    "level 1" to mapOf<String, Any?>(
                        "level 2" to mapOf<String, Any?>(
                            "list" to listOf<Any?>(1, 1, null, 100.00)
                        )
                    )
                ), """{"level 1":{"level 2":{"list":[1,1,null,100.0]}}}"""
            ),
            arrayOf(
                mapOf(
                    """"like" \this\""" to """"like" \this\"""
                ),
                """{"\"like\" \\this\\":"\"like\" \\this\\"}""",
            )
        )
    }
}