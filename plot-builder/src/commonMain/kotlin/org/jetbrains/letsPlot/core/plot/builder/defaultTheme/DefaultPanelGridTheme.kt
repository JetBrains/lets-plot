/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.defaultTheme

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.theme.FontFamilyRegistry
import org.jetbrains.letsPlot.core.plot.base.theme.PanelGridTheme
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.Elem
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.LINE
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.PANEL_GRID
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.PANEL_GRID_MAJOR
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.PANEL_GRID_MINOR
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.PANEL_GRID_ONTOP

internal class DefaultPanelGridTheme(
    axis: String,
    options: Map<String, Any>,
    fontFamilyRegistry: FontFamilyRegistry
) : ThemeValuesAccess(options, fontFamilyRegistry), PanelGridTheme {

    private val suffix = "_$axis"
    private val ontopKey = listOf(PANEL_GRID_ONTOP + suffix, PANEL_GRID_ONTOP)
    internal val majorLineKey = listOf(PANEL_GRID_MAJOR + suffix, PANEL_GRID_MAJOR, PANEL_GRID + suffix, PANEL_GRID, LINE)
    internal val minorLineKey = listOf(PANEL_GRID_MINOR + suffix, PANEL_GRID_MINOR, PANEL_GRID + suffix, PANEL_GRID, LINE)

    override fun isOntop(): Boolean {
        return getBoolean(ontopKey)
    }

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

    override fun majorLineType() = getLineType(getElemValue(majorLineKey))

    override fun minorLineType() = getLineType(getElemValue(minorLineKey))
}
