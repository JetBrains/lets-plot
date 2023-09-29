/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values

import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.ThemeFlavor.Companion.SymbolicColor
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.AXIS
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.AXIS_LINE
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.AXIS_TOOLTIP
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.ELEMENT_BLANK
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.Elem
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.FACET_STRIP_BGR_RECT
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.FLAVOR
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.Flavor
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.LINE
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.PANEL_BKGR_RECT
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.PANEL_GRID
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.PANEL_GRID_MAJOR
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.PANEL_GRID_MINOR
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.RECT
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.TEXT

internal class ThemeValuesRBW : ThemeValues(VALUES) {

    companion object {

        private val VALUES: Map<String, Any> = ThemeValuesBase() + mapOf(
            LINE to mapOf(
                Elem.COLOR to SymbolicColor.BLACK
            ),

            RECT to mapOf(
                Elem.COLOR to SymbolicColor.BLACK,
                Elem.FILL to SymbolicColor.GREY_1
            ),

            TEXT to mapOf(
                Elem.COLOR to SymbolicColor.BLACK
            ),

            PANEL_BKGR_RECT to mapOf(
                Elem.COLOR to SymbolicColor.GREY_4,
                Elem.FILL to SymbolicColor.WHITE,
            ),
            PANEL_GRID to mapOf(
                Elem.COLOR to SymbolicColor.GREY_1,
            ),

            PANEL_GRID_MAJOR to mapOf(
                Elem.SIZE to 1.4,
            ),
            PANEL_GRID_MINOR to mapOf(
                Elem.SIZE to 0.5,
            ),

            AXIS_LINE to ELEMENT_BLANK,
            AXIS to mapOf(
                Elem.COLOR to SymbolicColor.GREY_4,
            ),

            AXIS_TOOLTIP to mapOf(
                Elem.COLOR to SymbolicColor.WHITE,
                Elem.FILL to SymbolicColor.BLACK,
            ),

            // Facets
            FACET_STRIP_BGR_RECT to mapOf(
                Elem.COLOR to SymbolicColor.GREY_4,
                Elem.FILL to SymbolicColor.GREY_2,
            ),

            FLAVOR to Flavor.BW,
        )
    }
}