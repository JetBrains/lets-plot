/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.defaultTheme

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.guide.LegendDirection
import org.jetbrains.letsPlot.core.plot.base.guide.LegendJustification
import org.jetbrains.letsPlot.core.plot.base.guide.LegendPosition
import org.jetbrains.letsPlot.core.plot.base.layout.TextJustification
import org.jetbrains.letsPlot.core.plot.base.theme.LegendTheme
import org.jetbrains.letsPlot.core.plot.base.theme.ThemeTextStyle
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.LEGEND_TEXT
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.LEGEND_TITLE
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.TEXT
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.TITLE
import org.jetbrains.letsPlot.core.plot.base.theme.FontFamilyRegistry

internal class DefaultLegendTheme(
    options: Map<String, Any>,
    fontFamilyRegistry: FontFamilyRegistry
) : ThemeValuesAccess(options, fontFamilyRegistry), LegendTheme {

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
        return getColor(getElemValue(backgroundKey), ThemeOption.Elem.COLOR)
    }

    override fun backgroundFill(): Color {
        return getColor(getElemValue(backgroundKey), ThemeOption.Elem.FILL)
    }

    override fun backgroundStrokeWidth(): Double {
        return getNumber(getElemValue(backgroundKey), ThemeOption.Elem.SIZE)
    }
}
