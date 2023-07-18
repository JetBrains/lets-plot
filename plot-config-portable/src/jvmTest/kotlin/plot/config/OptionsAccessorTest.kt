/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import kotlin.test.*
import demoAndTestShared.assertEquals as assertDoubleEquals

class OptionsAccessorTest {

    @Ignore
    @Test
    fun unsafeGetList() {
        val options = HashMap<String, Any>()
        val integersKey = "integers"
        options[integersKey] = listOf(0, 1, 2)
        val opts = OptionsAccessor.over(options)
        val firstString = opts.getList(integersKey)[0] as String
        assertTrue(firstString.isNotEmpty())
    }

    @Test
    fun booleanOptionWithDefault() {
        val options = HashMap<String, Any>()

        // test absent
        assertTrue(OptionsAccessor.over(options).getBoolean("not present", true))
        assertTrue(!OptionsAccessor.over(options).getBoolean("not present"))

        options["T"] = true
        options["F"] = false
        options["NaB"] = "hey!"
        assertTrue(OptionsAccessor.over(options).getBoolean("T", true))
        assertTrue(!OptionsAccessor.over(options).getBoolean("F", true))
        assertTrue(OptionsAccessor.over(options).getBoolean("NaB", true))

        assertTrue(OptionsAccessor.over(options).getBoolean("T"))
        assertTrue(!OptionsAccessor.over(options).getBoolean("F"))
        assertTrue(!OptionsAccessor.over(options).getBoolean("NaB"))
    }

    @Test
    fun numericOptions() {
        val options = HashMap<String, Any>()
        options["int"] = 0
        options["dint"] = 1.0
        options["long"] = 2L
        options["double"] = 3.0

        assertNull(OptionsAccessor.over(options).getInteger("not present"))
        assertNull(OptionsAccessor.over(options).getLong("not present"))
        assertNull(OptionsAccessor.over(options).getDouble("not present"))

        val keys = listOf("int", "dint", "long", "double")
        for (i in keys.indices) {
            val option = keys[i]
            assertEquals(Integer.valueOf(i), OptionsAccessor.over(options).getInteger(option))
            assertEquals(i.toLong(), OptionsAccessor.over(options).getLong(option))
            assertDoubleEquals(i.toDouble(), OptionsAccessor.over(options).getDouble(option), 0.0)
        }
    }

    @Test
    fun numPair() {
        val options = mapOf(
            "a" to listOf(1, 2, 3)
        )
        assertEquals(Pair(1, 2), OptionsAccessor.over(options).getNumPair("a"))
    }

    @Test
    fun numQPair() {
        val options = mapOf(
            "a" to listOf(1, null, 3)
        )
        assertEquals(Pair(1, null), OptionsAccessor.over(options).getNumQPair("a"))
    }

    @Test
    fun intRangeTest() {
        val options = mapOf(
            "a" to listOf(1, 2)
        )

        assertEquals(DoubleSpan(1.0, 2.0), OptionsAccessor.over(options).getRangeOrNull("a"))
    }
}
