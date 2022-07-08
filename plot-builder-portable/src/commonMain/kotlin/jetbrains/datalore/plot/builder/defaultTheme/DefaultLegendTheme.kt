/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.defaultTheme

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.LEGEND_TEXT
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.LEGEND_TITLE
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.TEXT
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.TITLE
import jetbrains.datalore.plot.builder.guide.LegendDirection
import jetbrains.datalore.plot.builder.guide.LegendJustification
import jetbrains.datalore.plot.builder.guide.LegendPosition
import jetbrains.datalore.plot.builder.theme.LegendTheme
import jetbrains.datalore.vis.TextStyle

internal class DefaultLegendTheme(
    options: Map<String, Any>
) : ThemeValuesAccess(options), LegendTheme {

    internal val backgroundKey = listOf(ThemeOption.LEGEND_BKGR_RECT, ThemeOption.RECT)
    internal val titleKey = listOf(LEGEND_TITLE, TITLE, TEXT)
    internal val textKey = listOf(LEGEND_TEXT, TEXT)

    override fun keySize(): Double {
        return 23.0
    }

    override fun margin(): Double {
        return 5.0
    }

    override fun padding(): Double {
        return 5.0
    }

    override fun position(): LegendPosition {
        return getValue(ThemeOption.LEGEND_POSITION) as LegendPosition
    }

    override fun justification(): LegendJustification {
        return getValue(ThemeOption.LEGEND_JUSTIFICATION) as LegendJustification
    }

    override fun direction(): LegendDirection {
        return getValue(ThemeOption.LEGEND_DIRECTION) as LegendDirection
    }

    override fun titleTextStyle(): TextStyle {
        return getTextStyle(getElemValue(titleKey))
    }

    override fun textTextStyle(): TextStyle {
        return getTextStyle(getElemValue(textKey))
    }

    override fun showBackground(): Boolean {
        return !isElemBlank(backgroundKey)
    }

    override fun backgroundColor(): Color {
        return getColor(getElemValue(backgroundKey), ThemeOption.Elem.COLOR)
    }

    override fun backgroundFill(): Color {
        return getColor(getElemValue(backgroundKey), ThemeOption.Elem.FILL)
    }

    override fun backgroundStrokeWidth(): Double {
        return getNumber(getElemValue(backgroundKey), ThemeOption.Elem.SIZE)
    }
}
