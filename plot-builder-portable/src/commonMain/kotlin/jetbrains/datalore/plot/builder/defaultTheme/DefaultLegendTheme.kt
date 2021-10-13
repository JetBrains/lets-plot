/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.defaultTheme

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.builder.guide.LegendDirection
import jetbrains.datalore.plot.builder.guide.LegendJustification
import jetbrains.datalore.plot.builder.guide.LegendPosition
import jetbrains.datalore.plot.builder.theme.LegendTheme
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.LEGEND_TEXT
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.LEGEND_TITLE
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.TEXT
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.TITLE

class DefaultLegendTheme(
    options: Map<String, Any>
) : ThemeValuesAccess(options), LegendTheme {

    private val titleKey = listOf(LEGEND_TITLE, TITLE, TEXT)
    private val textKey = listOf(LEGEND_TEXT, TEXT)

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

    override fun backgroundFill(): Color {
        return Color.WHITE
    }

    override fun titleColor(): Color {
        return getColor(getElemValue(titleKey), ThemeOption.Elem.COLOR)
    }

    override fun textColor(): Color {
        return getColor(getElemValue(textKey), ThemeOption.Elem.COLOR)
    }
}
