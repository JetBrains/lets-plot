/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.defaultTheme

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.builder.theme.PlotTheme
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.Elem
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.PLOT_TITLE
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.TEXT
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.TITLE

class DefaultPlotTheme(
    options: Map<String, Any>
) : ThemeValuesAccess(options), PlotTheme {

    private val titleKey = listOf(PLOT_TITLE, TITLE, TEXT)

    override fun titleColor(): Color {
        return getColor(getElemValue(titleKey), Elem.COLOR)
    }
}