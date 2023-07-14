/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.defaultTheme

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.theme.PanelGridTheme
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.Elem
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.LINE
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.PANEL_GRID
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.PANEL_GRID_MAJOR
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.PANEL_GRID_MINOR
import jetbrains.datalore.plot.builder.presentation.FontFamilyRegistry

internal class DefaultPanelGridTheme(
    axis: String,
    options: Map<String, Any>,
    fontFamilyRegistry: FontFamilyRegistry
) : ThemeValuesAccess(options, fontFamilyRegistry), PanelGridTheme {

    private val suffix = "_$axis"
    internal val majorLineKey =
        listOf(PANEL_GRID_MAJOR + suffix, PANEL_GRID_MAJOR, PANEL_GRID + suffix, PANEL_GRID, LINE)
    internal val minorLineKey =
        listOf(PANEL_GRID_MINOR + suffix, PANEL_GRID_MINOR, PANEL_GRID + suffix, PANEL_GRID, LINE)

    override fun showMajor(): Boolean {
        return !isElemBlank(majorLineKey)
    }

    override fun showMinor(): Boolean {
        return !isElemBlank(minorLineKey)
    }

    override fun majorLineWidth(): Double {
        return getNumber(getElemValue(majorLineKey), Elem.SIZE)
    }

    override fun minorLineWidth(): Double {
        return getNumber(getElemValue(minorLineKey), Elem.SIZE)
    }

    override fun majorLineColor(): Color {
        return getColor(getElemValue(majorLineKey), Elem.COLOR)
    }

    override fun minorLineColor(): Color {
        return getColor(getElemValue(minorLineKey), Elem.COLOR)
    }
}
