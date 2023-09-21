/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values

import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.ThemeFlavorUtil.SymbolicColors
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.AXIS
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.AXIS_LINE
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.AXIS_TICKS
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.AXIS_TOOLTIP
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.ELEMENT_BLANK
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.Elem
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.FACET_STRIP_BGR_RECT
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.FLAVOR
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.Flavor
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.PANEL_BKGR_RECT
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.PANEL_GRID
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.PANEL_GRID_MAJOR
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.PANEL_GRID_MINOR

class ThemeValuesRGrey : ThemeValues(VALUES) {

    companion object {
        private val VALUES: Map<String, Any> = ThemeValuesBase() + mapOf(

            // Panel (no border)
            PANEL_BKGR_RECT to mapOf(
                Elem.SIZE to 0.0,
                Elem.FILL to SymbolicColors.GRAY_1,
            ),

            // Grid
            PANEL_GRID to mapOf(
                Elem.COLOR to SymbolicColors.WHITE,
            ),
            PANEL_GRID_MAJOR to mapOf(
                Elem.SIZE to 1.4,
            ),
            PANEL_GRID_MINOR to mapOf(
                Elem.SIZE to 0.5,
            ),

            // Axis
            AXIS to mapOf(
                Elem.COLOR to SymbolicColors.DARK_GRAY_1,
            ),
            AXIS_LINE to ELEMENT_BLANK,
            AXIS_TICKS to mapOf(
                Elem.SIZE to 1.4
            ),

            AXIS_TOOLTIP to mapOf(
                Elem.COLOR to SymbolicColors.WHITE,
                Elem.FILL to SymbolicColors.DARK_GRAY_1,
            ),

            // Facets
            FACET_STRIP_BGR_RECT to mapOf(
                Elem.SIZE to 0.0,
                Elem.FILL to SymbolicColors.GRAY_2,
            ),

            FLAVOR to Flavor.BASE,
        )
    }
}