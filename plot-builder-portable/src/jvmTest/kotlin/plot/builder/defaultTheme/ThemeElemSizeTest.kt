/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.defaultTheme

import org.jetbrains.letsPlot.core.plot.base.theme.Theme
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.AXIS
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.AXIS_LINE
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.AXIS_LINE_X
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.AXIS_X
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.Elem
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.LINE
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.test.assertEquals

@RunWith(Parameterized::class)
internal class ThemeElemSizeTest(
    private val themeValues: Map<String, Any>,
    private val expected: Double
) {
    private val theme: Theme = DefaultTheme(themeValues)

    @Test
    fun eval() {
        assertEquals(expected, theme.horizontalAxis(flipAxis = false).lineWidth())
    }


    companion object {
        @Suppress("BooleanLiteralArgument")
        @JvmStatic
        @Parameterized.Parameters
        fun params(): Collection<Array<Any?>> {
            return listOf<Array<Any?>>(
                arrayOf(axisXLine(null, null, null, null, 0), 0.0),
                arrayOf(axisXLine(4, null, null, null, 0), 4.0),
                arrayOf(axisXLine(null, 3, null, null, 0), 3.0),
                arrayOf(axisXLine(null, null, 2, null, 0), 2.0),
                arrayOf(axisXLine(null, null, null, 1, 0), 1.0),
            )
        }

        private fun axisXLine(
            axisLineX: Number?,
            axisLine: Number?,
            axisX: Number?,
            axis: Number?,
            line: Number?,
        ): Map<String, Any> {
            val values = HashMap<String, Any>()

            axisLineX?.run { values.put(AXIS_LINE_X, mapOf(Elem.SIZE to this)) }
            axisLine?.run { values.put(AXIS_LINE, mapOf(Elem.SIZE to this)) }
            axisX?.run { values.put(AXIS_X, mapOf(Elem.SIZE to this)) }
            axis?.run { values.put(AXIS, mapOf(Elem.SIZE to this)) }
            line?.run { values.put(LINE, mapOf(Elem.SIZE to this)) }

            return values
        }
    }
}