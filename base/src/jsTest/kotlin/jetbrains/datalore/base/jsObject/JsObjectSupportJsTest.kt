package jetbrains.datalore.base.jsObject

import kotlin.test.Test
import kotlin.test.assertEquals

class JsObjectSupportJsTest {

    @Test
    fun runTests() {
        for ((index, datum) in testData.withIndex()) {
            @Suppress("UNCHECKED_CAST")
            oneTest(datum[0], datum[1] as Map<String, *>)
        }

    }

    private fun oneTest(input: dynamic, expected: Map<String, *>) {
        val actual = dynamicObjectToMap(input)
        assertEquals(expected, actual)
    }

    companion object {
        @Suppress("UnsafeCastFromDynamic")
        val testData: List<Array<Any>> = listOf(
            arrayOf(
                js(
                    """
                {
                    key:"val"
                }"""
                ),
                mapOf<String, Any?>(
                    "key" to "val"
                )
            )
        )
    }
}