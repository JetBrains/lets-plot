/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.defaultTheme

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.base.values.FontFace
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.Elem
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.TEXT
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.TOOLTIP_TEXT
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.TOOLTIP_TITLE_TEXT
import jetbrains.datalore.plot.builder.theme.TooltipsTheme

internal class DefaultTooltipsTheme(
    options: Map<String, Any>
) : ThemeValuesAccess(options), TooltipsTheme {

    internal val tooltipKey = listOf(ThemeOption.TOOLTIP_RECT, ThemeOption.RECT)

    internal val textKey = listOf(TOOLTIP_TEXT)
    internal val titleTextKey = listOf(TOOLTIP_TITLE_TEXT) + textKey

    // Inherits from the 'text' color.
    internal val textColorKey = textKey + TEXT
    internal val titleTextColorKey = listOf(TOOLTIP_TITLE_TEXT) + textColorKey

    override fun textColor(): Color {
        return getColor(getElemValue(textColorKey), Elem.COLOR)
    }

    override fun textFontFace(): FontFace {
        return getFontFace(getElemValue(textKey))
    }

    override fun titleTextColor(): Color {
        return getColor(getElemValue(titleTextColorKey), Elem.COLOR)
    }

    override fun titleTextFontFace(): FontFace {
        return getFontFace(getElemValue(titleTextKey)) + textFontFace()
    }

    override fun labelTextColor(): Color {
        return textColor()
    }

    override fun labelTextFontFace(): FontFace {
        return FontFace.BOLD + textFontFace()
    }

    override fun tooltipColor(): Color {
        return getColor(getElemValue(tooltipKey), Elem.COLOR)
    }

    override fun tooltipFill(): Color {
        return getColor(getElemValue(tooltipKey), Elem.FILL)
    }

    override fun tooltipStrokeWidth(): Double {
       return getNumber(getElemValue(tooltipKey), Elem.SIZE)
    }
}