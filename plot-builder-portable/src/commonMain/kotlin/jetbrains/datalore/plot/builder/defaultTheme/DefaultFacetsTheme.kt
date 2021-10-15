/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.defaultTheme

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.builder.theme.FacetsTheme
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.Elem
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.FACET_STRIP_BGR_RECT
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.FACET_STRIP_TEXT
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.RECT
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.TEXT

internal class DefaultFacetsTheme(
    options: Map<String, Any>
) : ThemeValuesAccess(options), FacetsTheme {

    private val rectKey = listOf(FACET_STRIP_BGR_RECT, RECT)
    private val textKey = listOf(FACET_STRIP_TEXT, TEXT)

    override fun stripFill(): Color {
        return getColor(getElemValue(rectKey), Elem.FILL)
    }

    override fun stripColor(): Color {
        return getColor(getElemValue(rectKey), Elem.COLOR)
    }

    override fun stripStrokeWidth(): Double {
        return getNumber(getElemValue(rectKey), Elem.SIZE)
    }

    override fun stripTextColor(): Color {
        return getColor(getElemValue(textKey), Elem.COLOR)
    }
}