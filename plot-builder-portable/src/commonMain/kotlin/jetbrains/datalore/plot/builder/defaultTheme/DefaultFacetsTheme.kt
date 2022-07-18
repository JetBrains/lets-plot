/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.defaultTheme

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.Elem
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.FACET_STRIP_BGR_RECT
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.FACET_STRIP_TEXT
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.RECT
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.TEXT
import jetbrains.datalore.plot.builder.theme.FacetsTheme
import jetbrains.datalore.vis.TextStyle

internal class DefaultFacetsTheme(
    options: Map<String, Any>
) : ThemeValuesAccess(options), FacetsTheme {

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

    override fun stripTextStyle(): TextStyle {
        return getTextStyle(getElemValue(textKey))
    }
}