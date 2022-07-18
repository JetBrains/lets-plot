/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.defaultTheme

import jetbrains.datalore.base.values.FontFace
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.Elem
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.RECT
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.TEXT
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.TOOLTIP_RECT
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.TOOLTIP_TEXT
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.TOOLTIP_TITLE_TEXT
import jetbrains.datalore.plot.builder.theme.TooltipsTheme
import jetbrains.datalore.vis.TextStyle

internal class DefaultTooltipsTheme(
    options: Map<String, Any>
) : ThemeValuesAccess(options), TooltipsTheme {

    internal val tooltipKey = listOf(TOOLTIP_RECT, RECT)

    internal val textKey = listOf(TOOLTIP_TEXT, TEXT)
    internal val titleTextKey = listOf(TOOLTIP_TITLE_TEXT, TOOLTIP_TEXT, TEXT)

    override fun tooltipColor() = getColor(getElemValue(tooltipKey), Elem.COLOR)

    override fun tooltipFill() = getColor(getElemValue(tooltipKey), Elem.FILL)

    override fun tooltipStrokeWidth() = getNumber(getElemValue(tooltipKey), Elem.SIZE)

    override fun textStyle(): TextStyle = getTextStyle(getElemValue(textKey))

    override fun titleTextStyle(): TextStyle {
        val titleStyle = getTextStyle(getElemValue(titleTextKey))
        val textFontFace = getFontFace(getElemValue(textKey))
        return titleStyle.copy(face = titleStyle.face + textFontFace)
    }

    override fun labelTextStyle(): TextStyle {
        return with (textStyle()) {
            TextStyle(family, FontFace.BOLD + face, size, color)
        }
    }
}