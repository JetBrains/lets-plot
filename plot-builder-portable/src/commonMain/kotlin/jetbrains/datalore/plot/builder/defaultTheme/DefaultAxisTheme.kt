/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.defaultTheme

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.AXIS
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.AXIS_LINE
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.AXIS_ONTOP
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.AXIS_TEXT
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.AXIS_TICKS
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.AXIS_TICKS_LENGTH
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.AXIS_TITLE
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.AXIS_TOOLTIP
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.AXIS_TOOLTIP_TEXT
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.Elem
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.LINE
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.RECT
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.TEXT
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.TITLE
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.TOOLTIP_TEXT
import jetbrains.datalore.plot.builder.layout.TextJustification
import jetbrains.datalore.plot.builder.presentation.FontFamilyRegistry
import jetbrains.datalore.plot.builder.theme.AxisTheme
import jetbrains.datalore.plot.builder.theme.ThemeTextStyle

internal class DefaultAxisTheme(
    override val axis: String,
    options: Map<String, Any>,
    fontFamilyRegistry: FontFamilyRegistry
) : ThemeValuesAccess(options, fontFamilyRegistry), AxisTheme {

    private val suffix = "_$axis"
    internal val ontopKey = listOf(AXIS_ONTOP + suffix, AXIS_ONTOP)
    internal val lineKey = listOf(AXIS_LINE + suffix, AXIS_LINE, AXIS + suffix, AXIS, LINE)
    internal val textKey = listOf(AXIS_TEXT + suffix, AXIS_TEXT, TEXT, AXIS + suffix, AXIS)
    internal val titleKey = listOf(AXIS_TITLE + suffix, AXIS_TITLE, TITLE, TEXT, AXIS + suffix, AXIS)
    internal val tickKey = listOf(AXIS_TICKS + suffix, AXIS_TICKS, AXIS + suffix, AXIS, LINE)
    internal val tickLengthKey = listOf(AXIS_TICKS_LENGTH + suffix, AXIS_TICKS_LENGTH)
    internal val tooltipKey = listOf(AXIS_TOOLTIP + suffix, AXIS_TOOLTIP, RECT)
    internal val tooltipFillKey = tooltipKey + lineKey
    internal val tooltipTextKey = listOf(AXIS_TOOLTIP_TEXT + suffix, AXIS_TOOLTIP_TEXT, TOOLTIP_TEXT, TEXT)

    // Inherits from the tooltip rect stroke color.
    internal val tooltipTextColorKey = (tooltipTextKey - TEXT) + tooltipKey

    override fun isOntop(): Boolean {
        return getBoolean(ontopKey)
    }

    override fun showLine(): Boolean {
        return !isElemBlank(lineKey)
    }

    override fun showTickMarks(): Boolean {
        return !isElemBlank(tickKey)
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

    override fun titleStyle(): ThemeTextStyle {
        return getTextStyle(getElemValue(titleKey))
    }

    override fun titleJustification(): TextJustification {
        return getTextJustification(getElemValue(titleKey))
    }

    override fun titleMargins() = getMargins(getElemValue(titleKey))

    override fun lineWidth(): Double {
        return getNumber(getElemValue(lineKey), Elem.SIZE)
    }

    override fun lineColor(): Color {
        return getColor(getElemValue(lineKey), Elem.COLOR)
    }

    override fun tickMarkWidth(): Double {
        return getNumber(getElemValue(tickKey), Elem.SIZE)
    }

    override fun tickMarkLength(): Double {
        return getNumber(tickLengthKey)
    }

    override fun tickMarkColor(): Color {
        return getColor(getElemValue(tickKey), Elem.COLOR)
    }

    override fun tickLabelMargins() = getMargins(getElemValue(textKey))

    override fun labelStyle(): ThemeTextStyle {
        return getTextStyle(getElemValue(textKey))
    }

    override fun labelAngle(): Double? {
        val values = getElemValue(textKey)
        if (!values.containsKey(Elem.ANGLE)) return null
        return getNumber(values, Elem.ANGLE)
    }

    override fun tooltipFill(): Color {
        return getColor(getElemValue(tooltipFillKey), Elem.FILL)
    }

    override fun tooltipColor(): Color {
        return getColor(getElemValue(tooltipKey), Elem.COLOR)
    }

    override fun tooltipStrokeWidth(): Double {
        return getNumber(getElemValue(tooltipKey), Elem.SIZE)
    }

    override fun tooltipTextStyle(): ThemeTextStyle {
        val tooltipTextColor = getColor(getElemValue(tooltipTextColorKey), Elem.COLOR)
        val textStyle = getTextStyle(getElemValue(tooltipTextKey))
        return textStyle.copy(color = tooltipTextColor)
    }
}
