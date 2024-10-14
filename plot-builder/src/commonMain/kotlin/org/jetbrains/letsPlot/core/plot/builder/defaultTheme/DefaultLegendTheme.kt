/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.defaultTheme

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.guide.LegendArrangement
import org.jetbrains.letsPlot.core.plot.base.guide.LegendBoxJustification
import org.jetbrains.letsPlot.core.plot.base.guide.LegendDirection
import org.jetbrains.letsPlot.core.plot.base.guide.LegendJustification
import org.jetbrains.letsPlot.core.plot.base.guide.LegendPosition
import org.jetbrains.letsPlot.core.plot.base.layout.TextJustification
import org.jetbrains.letsPlot.core.plot.base.theme.FontFamilyRegistry
import org.jetbrains.letsPlot.core.plot.base.theme.LegendTheme
import org.jetbrains.letsPlot.core.plot.base.theme.ThemeTextStyle
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.LEGEND_TEXT
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.LEGEND_TITLE
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.TEXT
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.TITLE
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.Elem
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.LEGEND_BKGR_RECT
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.LEGEND_BOX
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.LEGEND_BOX_JUST
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.LEGEND_BOX_SPACING
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.LEGEND_DIRECTION
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.LEGEND_JUSTIFICATION
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.LEGEND_KEY_RECT
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.LEGEND_KEY_HEIGHT
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.LEGEND_KEY_SIZE
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.LEGEND_KEY_SPACING
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.LEGEND_KEY_SPACING_X
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.LEGEND_KEY_SPACING_Y
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.LEGEND_KEY_WIDTH
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.LEGEND_MARGIN
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.LEGEND_POSITION
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.LEGEND_SPACING
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.LEGEND_SPACING_X
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.LEGEND_SPACING_Y
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.RECT

internal class DefaultLegendTheme(
    options: Map<String, Any>,
    fontFamilyRegistry: FontFamilyRegistry
) : ThemeValuesAccess(options, fontFamilyRegistry), LegendTheme {

    // background underneath legend keys
    internal val keyRectKey = listOf(LEGEND_KEY_RECT, RECT)

    internal val backgroundKey = listOf(LEGEND_BKGR_RECT, RECT)
    internal val titleKey = listOf(LEGEND_TITLE, TITLE, TEXT)
    internal val textKey = listOf(LEGEND_TEXT, TEXT)

    override fun keySize(): DoubleVector {
        val width = getNumber(listOf(LEGEND_KEY_WIDTH, LEGEND_KEY_SIZE))
        val height = getNumber(listOf(LEGEND_KEY_HEIGHT, LEGEND_KEY_SIZE))
        return DoubleVector(width, height)
    }

    override fun showKeyRect() = !isElemBlank(keyRectKey)

    override fun keyRectFill() = getColor(getElemValue(keyRectKey), Elem.FILL)

    override fun keyRectColor() = getColor(getElemValue(keyRectKey), Elem.COLOR)

    override fun keyRectStrokeWidth() = getNumber(getElemValue(keyRectKey), Elem.SIZE)

    override fun keyLineType() = getLineType(getElemValue(keyRectKey))

    override fun keySpacing(): DoubleVector {
        val spacingX = getNumber(listOf(LEGEND_KEY_SPACING_X, LEGEND_KEY_SPACING))
        val spacingY = getNumber(listOf(LEGEND_KEY_SPACING_Y, LEGEND_KEY_SPACING))
        return DoubleVector(spacingX, spacingY)
    }

    override fun margins() = getMargins(getElemValue(listOf(LEGEND_MARGIN)))

    override fun boxArrangement() = getValue(LEGEND_BOX) as LegendArrangement

    override fun boxSpacing(): Double = getNumber(listOf(LEGEND_BOX_SPACING))

    override fun boxJustification() = getValue(LEGEND_BOX_JUST) as LegendBoxJustification

    override fun spacing(): DoubleVector {
        val spacingX = getNumber(listOf(LEGEND_SPACING_X, LEGEND_SPACING))
        val spacingY = getNumber(listOf(LEGEND_SPACING_Y, LEGEND_SPACING))
        return DoubleVector(spacingX, spacingY)
    }

    override fun position(): LegendPosition {
        return getValue(LEGEND_POSITION) as LegendPosition
    }

    override fun justification(): LegendJustification {
        return getValue(LEGEND_JUSTIFICATION) as LegendJustification
    }

    override fun direction(): LegendDirection {
        return getValue(LEGEND_DIRECTION) as LegendDirection
    }

    override fun showTitle(): Boolean {
        return !isElemBlank(titleKey)
    }

    override fun titleStyle(): ThemeTextStyle {
        return getTextStyle(getElemValue(titleKey))
    }

    override fun titleJustification(): TextJustification {
        return getTextJustification(getElemValue(titleKey))
    }

    override fun textStyle(): ThemeTextStyle {
        return getTextStyle(getElemValue(textKey))
    }

    override fun showBackground(): Boolean {
        return !isElemBlank(backgroundKey)
    }

    override fun backgroundColor(): Color {
        return getColor(getElemValue(backgroundKey), Elem.COLOR)
    }

    override fun backgroundFill(): Color {
        return getColor(getElemValue(backgroundKey), Elem.FILL)
    }

    override fun backgroundStrokeWidth(): Double {
        return getNumber(getElemValue(backgroundKey), Elem.SIZE)
    }

    override fun backgroundLineType() = getLineType(getElemValue(backgroundKey))
}
