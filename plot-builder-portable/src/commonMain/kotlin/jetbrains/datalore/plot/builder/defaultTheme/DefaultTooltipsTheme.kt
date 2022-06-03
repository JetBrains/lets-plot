/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.defaultTheme

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.Elem
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.RECT
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.TEXT
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.TOOLTIP_RECT
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.TOOLTIP_TEXT
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.TOOLTIP_TITLE_TEXT
import jetbrains.datalore.plot.builder.theme.TooltipsTheme

internal class DefaultTooltipsTheme(
    options: Map<String, Any>
) : ThemeValuesAccess(options), TooltipsTheme {

    internal val tooltipKey = listOf(TOOLTIP_RECT, RECT)

    internal val textKey = listOf(TOOLTIP_TEXT, TEXT)
    internal val titleTextKey = listOf(TOOLTIP_TITLE_TEXT) + textKey

    override fun tooltipColor(): Color = getColor(getElemValue(tooltipKey), Elem.COLOR)

    override fun tooltipFill(): Color = getColor(getElemValue(tooltipKey), Elem.FILL)

    override fun tooltipStrokeWidth(): Double = getNumber(getElemValue(tooltipKey), Elem.SIZE)

    override fun textColor(): Color = getColor(getElemValue(textKey), Elem.COLOR)

    override fun titleTextColor(): Color = getColor(getElemValue(titleTextKey), Elem.COLOR)
}