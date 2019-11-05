/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import kotlin.test.*
import jetbrains.datalore.base.assertion.assertEquals as assertDoubleEquals

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
}
