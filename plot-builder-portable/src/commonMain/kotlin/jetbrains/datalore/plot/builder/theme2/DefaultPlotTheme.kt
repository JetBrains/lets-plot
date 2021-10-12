/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.theme2

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.builder.theme.PlotTheme
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption

class DefaultPlotTheme(
    options: Map<String, Any>
) : ThemeValuesAccess(options), PlotTheme {
    private val titleKey = listOf(ThemeOption.PLOT_TITLE, ThemeOption.TITLE, ThemeOption.TEXT)

    override fun titleColor(): Color {
        return getColor(getElemValue(titleKey), ThemeOption.Elem.COLOR)
    }
}