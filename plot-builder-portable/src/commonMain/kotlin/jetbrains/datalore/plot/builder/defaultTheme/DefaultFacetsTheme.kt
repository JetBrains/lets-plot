/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.defaultTheme

import org.jetbrains.letsPlot.commons.values.Color
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.Elem
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.FACET_STRIP_BGR_RECT
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.FACET_STRIP_TEXT
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.RECT
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.TEXT
import jetbrains.datalore.plot.builder.presentation.FontFamilyRegistry
import org.jetbrains.letsPlot.core.plot.base.theme.FacetsTheme
import org.jetbrains.letsPlot.core.plot.base.theme.ThemeTextStyle

internal class DefaultFacetsTheme(
    options: Map<String, Any>,
    fontFamilyRegistry: FontFamilyRegistry
) : ThemeValuesAccess(options, fontFamilyRegistry), FacetsTheme {

    internal val rectKey = listOf(FACET_STRIP_BGR_RECT, RECT)
    internal val textKey = listOf(FACET_STRIP_TEXT, TEXT)

    override fun showStrip(): Boolean {
        return !isElemBlank(textKey)
    }

    override fun showStripBackground(): Boolean {
        return showStrip() && !isElemBlank(rectKey)
    }

    override fun stripFill(): Color {
        return getColor(getElemValue(rectKey), Elem.FILL)
    }

    override fun stripColor(): Color {
        return getColor(getElemValue(rectKey), Elem.COLOR)
    }

    override fun stripStrokeWidth(): Double {
        return getNumber(getElemValue(rectKey), Elem.SIZE)
    }

    override fun stripTextStyle(): ThemeTextStyle {
        return getTextStyle(getElemValue(textKey))
    }
}