/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.util

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.builder.presentation.DefaultFontFamilyRegistry
import org.jetbrains.letsPlot.core.spec.Option
import org.jetbrains.letsPlot.core.spec.config.ThemeConfig

object PlotThemeHelper {
    /**
     * Used in "Lets-plot IDEA plugin"
     */
    fun plotBackground(plotSpec: Map<String, Any>): Color {
        val themeOptions = plotSpec[Option.Plot.THEME]?.let {
            @Suppress("UNCHECKED_CAST")
            if (it is Map<*, *>) it as Map<String, Any>
            else emptyMap()
        } ?: emptyMap()
        val theme = ThemeConfig(themeOptions, DefaultFontFamilyRegistry()).theme
        return theme.plot().backgroundFill()
    }
}