/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.defaultTheme

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.base.values.FontFace
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.Elem
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.TEXT
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.TOOLTIP_LABEL
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.TOOLTIP_TEXT
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.TOOLTIP_TITLE
import jetbrains.datalore.plot.builder.theme.TooltipsTheme

internal class DefaultTooltipsTheme(
    options: Map<String, Any>
) : ThemeValuesAccess(options), TooltipsTheme {

    internal val textKey = listOf(TOOLTIP_TEXT)
    internal val titleKey = listOf(TOOLTIP_TITLE) + textKey
    internal val labelKey = listOf(TOOLTIP_LABEL) + textKey

    // Inherits from the 'text' color.
    internal val textColorKey = textKey + TEXT
    internal val titleColorKey = listOf(TOOLTIP_TITLE) + textColorKey
    internal val labelColorKey = listOf(TOOLTIP_LABEL) + textColorKey

    override fun textColor(): Color {
        return getColor(getElemValue(textColorKey), Elem.COLOR)
    }

    override fun textFontFace(): FontFace {
        return getFontFace(getElemValue(textKey))
    }

    override fun titleColor(): Color {
        return getColor(getElemValue(titleColorKey), Elem.COLOR)
    }

    override fun titleFontFace(): FontFace {
        return getFontFace(getElemValue(titleKey))
    }

    override fun labelColor(): Color {
        return getColor(getElemValue(labelColorKey), Elem.COLOR)
    }

    override fun labelFontFace(): FontFace {
        return getFontFace(getElemValue(labelKey))
    }
}