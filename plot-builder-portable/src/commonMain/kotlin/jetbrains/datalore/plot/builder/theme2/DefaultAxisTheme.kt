/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.theme2

import jetbrains.datalore.base.values.Color
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
    private val lineKey = listOf(AXIS_LINE + suffix, AXIS_LINE, AXIS + suffix, AXIS, LINE)
    private val textKey = listOf(AXIS_TEXT + suffix, AXIS_TEXT, AXIS + suffix, AXIS, TEXT)
    private val titleKey =
        listOf(AXIS_TITLE + suffix, AXIS_TITLE, AXIS_TEXT + suffix, AXIS_TEXT, AXIS + suffix, AXIS, TITLE, TEXT)
    private val tooltipKey = listOf(AXIS_TOOLTIP + suffix, AXIS_TOOLTIP, RECT)
    private val tickmarkKey = listOf(AXIS_TICKS + suffix, AXIS_TICKS, AXIS + suffix, AXIS, LINE)

    override fun showLine(): Boolean {
        return !isElemBlank(lineKey)
    }

    override fun showTickMarks(): Boolean {
        return !isElemBlank(tickmarkKey)
    }

    override fun showLabels(): Boolean {
        return !isElemBlank(textKey)
    }

    override fun showTitle(): Boolean {
        return !isElemBlank(titleKey)
    }

    override fun showTooltip(): Boolean {
        return !isElemBlank(tooltipKey)
    }

    override fun titleColor(): Color {
        return getColor(getElemValue(titleKey), Elem.COLOR)
    }

    override fun lineWidth(): Double {
        return getNumber(getElemValue(lineKey), Elem.SIZE)
    }

    override fun lineColor(): Color {
        return getColor(getElemValue(lineKey), Elem.COLOR)
    }

    override fun tickMarkWidth(): Double {
        return getNumber(getElemValue(tickmarkKey), Elem.SIZE)
    }

    override fun tickMarkColor(): Color {
        return getColor(getElemValue(tickmarkKey), Elem.COLOR)
    }

    override fun labelColor(): Color {
        return getColor(getElemValue(textKey), Elem.COLOR)
    }
}
