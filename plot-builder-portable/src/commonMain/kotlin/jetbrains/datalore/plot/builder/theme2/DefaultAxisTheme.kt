/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.theme2

import jetbrains.datalore.plot.builder.theme.AxisTheme
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.AXIS
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.AXIS_LINE
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.AXIS_TEXT
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.AXIS_TICKS
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.AXIS_TITLE
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.AXIS_TOOLTIP
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.Elem
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.LINE
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.RECT
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.TEXT
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.TITLE

class DefaultAxisTheme(
    axis: String,
    options: Map<String, Any>
) : ThemeValuesAccess(options), AxisTheme {

    private val suffix = "_$axis"
    private val lineKey = listOf(AXIS_LINE + suffix, AXIS + suffix, AXIS_LINE, AXIS, LINE)
    private val textKey = listOf(AXIS_TEXT + suffix, AXIS + suffix, AXIS_TEXT, AXIS, TEXT)
    private val titleKey = listOf(AXIS_TITLE + suffix, AXIS + suffix, AXIS_TITLE, AXIS, TITLE, TEXT)
    private val tooltipKey = listOf(AXIS_TOOLTIP + suffix, AXIS + suffix, AXIS_TOOLTIP, AXIS, RECT)
    private val tickmarkKey = listOf(AXIS_TICKS + suffix, AXIS + suffix, AXIS_TICKS, AXIS, LINE)

    override fun showLine(): Boolean {
        return !isElemBlank(lineKey)
    }

    override fun showTickMarks(): Boolean {
        return !isElemBlank(tickmarkKey)
    }

    override fun showTickLabels(): Boolean {
        return !isElemBlank(textKey)
    }

    override fun showTitle(): Boolean {
        return !isElemBlank(titleKey)
    }

    override fun showTooltip(): Boolean {
        return !isElemBlank(tooltipKey)
    }

    override fun lineWidth(): Double {
        return getNumber(getElemValue(lineKey), Elem.SIZE)
    }

    override fun tickMarkWidth(): Double {
        return getNumber(getElemValue(tickmarkKey), Elem.SIZE)
    }

    override fun tickMarkLength(): Double {
        return 6.0
    }

    override fun tickMarkPadding(): Double {
        return 3.0
    }
}
