/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.defaultTheme

import org.jetbrains.letsPlot.core.plot.base.theme.Theme
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.AXIS
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.AXIS_LINE
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.AXIS_LINE_X
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.AXIS_X
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.Elem
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.LINE
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.test.assertEquals

@RunWith(Parameterized::class)
internal class ThemeElemBlankTest(
    private val themeValues: Map<String, Any>,
    private val isBlank: Boolean
) {
    private val theme: Theme = DefaultTheme(themeValues)

    @Test
    fun eval() {
        assertEquals(isBlank, !theme.horizontalAxis(flipAxis = false).showLine())
    }


    companion object {
        @Suppress("BooleanLiteralArgument")
        @JvmStatic
        @Parameterized.Parameters
        fun params(): Collection<Array<Any?>> {
            return listOf<Array<Any?>>(
                arrayOf(axisXLine(null, null, null, null, null), false),
                arrayOf(axisXLine(true, null, null, null, null), true),
                arrayOf(axisXLine(null, true, null, null, null), true),
                arrayOf(axisXLine(null, null, true, null, null), true),
                arrayOf(axisXLine(null, null, null, true, null), true),
                arrayOf(axisXLine(null, null, null, null, true), true),

                arrayOf(axisXLine(false, null, null, null, true), false),
                arrayOf(axisXLine(null, false, null, null, true), false),
                arrayOf(axisXLine(null, null, false, null, true), false),
                arrayOf(axisXLine(null, null, null, false, true), false),
                arrayOf(axisXLine(null, null, null, null, false), false),

                arrayOf(axisXLine(true, null, null, null, false), true),
                arrayOf(axisXLine(null, true, null, null, false), true),
                arrayOf(axisXLine(null, null, true, null, false), true),
                arrayOf(axisXLine(null, null, null, true, false), true),
            )
        }

        private fun axisXLine(
            axisLineX: Boolean?,
            axisLine: Boolean?,
            axisX: Boolean?,
            axis: Boolean?,
            line: Boolean?,
        ): Map<String, Any> {
            val values = HashMap<String, Any>()

            axisLineX?.run { values.put(AXIS_LINE_X, mapOf(Elem.BLANK to this)) }
            axisLine?.run { values.put(AXIS_LINE, mapOf(Elem.BLANK to this)) }
            axisX?.run { values.put(AXIS_X, mapOf(Elem.BLANK to this)) }
            axis?.run { values.put(AXIS, mapOf(Elem.BLANK to this)) }
            line?.run { values.put(LINE, mapOf(Elem.BLANK to this)) }

            return values
        }
    }
}