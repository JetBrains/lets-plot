/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config.theme

import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(Parameterized::class)
internal class AxisThemeConfigTest(
    private val axisTheme: AxisThemeConfig,
    private val expectedValue: String?
) {

    @Test
    fun hasOption() {
        assertEquals(expectedValue != null, axisTheme.hasApplicable(OPTION_COMMON))
    }

    @Test
    fun hasOptionValue() {
        assertEquals(expectedValue, axisTheme.getApplicable(OPTION_COMMON))
    }

    companion object {
        private const val OPTION_COMMON = "test_option"
        private const val OPTION_AXIS = "test_option_x"
        private const val VALUE_COMMON = "C"
        private const val VALUE_AXIS = "A"

        @Suppress("BooleanLiteralArgument")
        @JvmStatic
        @Parameterized.Parameters
        fun params(): Collection<Array<Any?>> {
            return listOf<Array<Any?>>(
                arrayOf(input(false, false, false, false), null),
                arrayOf(input(false, false, false, true), VALUE_AXIS),
                arrayOf(input(false, false, true, false), VALUE_AXIS),
                arrayOf(input(false, false, true, true), VALUE_AXIS),
                arrayOf(input(false, true, false, false), VALUE_COMMON),
                arrayOf(input(false, true, false, true), VALUE_AXIS),
                arrayOf(input(false, true, true, false), VALUE_AXIS),
                arrayOf(input(false, true, true, true), VALUE_AXIS),
                arrayOf(input(true, false, false, false), VALUE_COMMON),
                arrayOf(input(true, false, false, true), VALUE_COMMON),
                arrayOf(input(true, false, true, false), VALUE_AXIS),
                arrayOf(input(true, false, true, true), VALUE_AXIS),
                arrayOf(input(true, true, false, false), VALUE_COMMON),
                arrayOf(input(true, true, false, true), VALUE_COMMON),
                arrayOf(input(true, true, true, false), VALUE_AXIS),
                arrayOf(input(true, true, true, true), VALUE_AXIS)
            )
        }

        private fun input(commonOwn: Boolean, commonDef: Boolean, axisOwn: Boolean, axisDef: Boolean): AxisThemeConfig {
            val own = HashMap<String, Any>()
            val def = HashMap<String, Any>()
            if (commonOwn) {
                own[OPTION_COMMON] = VALUE_COMMON
            }
            if (commonDef) {
                def[OPTION_COMMON] = VALUE_COMMON
            }
            if (axisOwn) {
                own[OPTION_AXIS] = VALUE_AXIS
            }
            if (axisDef) {
                def[OPTION_AXIS] = VALUE_AXIS
            }
            return AxisThemeConfig.X(own, def)
        }
    }
}