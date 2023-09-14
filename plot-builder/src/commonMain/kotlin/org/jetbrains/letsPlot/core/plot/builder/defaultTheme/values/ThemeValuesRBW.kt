/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values

import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.AXIS_LINE
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.ELEMENT_BLANK
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.Elem
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.FLAVOR
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.Flavor
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.PANEL_GRID
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.PANEL_GRID_MAJOR
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.PANEL_GRID_MINOR
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.PAPER
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.PEN

class ThemeValuesRBW : ThemeValues(VALUES) {

    companion object {

        private val VALUES: Map<String, Any> = ThemeValuesBase() + mapOf(

            PANEL_GRID_MAJOR to mapOf(
                Elem.SIZE to 1.4,
            ),
            PANEL_GRID_MINOR to mapOf(
                Elem.SIZE to 0.5,
            ),

            AXIS_LINE to ELEMENT_BLANK,

            // Flavors

            FLAVOR to Flavor.BW,

            ThemeOption.FACET_STRIP_BGR_RECT to mapOf(
                Elem.SIZE to 1.0
            ),
            ThemeOption.AXIS_TOOLTIP to mapOf(
                Elem.FILL to PEN,
                Elem.COLOR to PAPER
            ),

            ThemeOption.PANEL_BKGR_RECT to mapOf(
                Elem.SIZE to 1.0,
                Elem.FILL to PAPER
            ),
            PANEL_GRID to mapOf(
                Elem.SIZE to 1.0
            ),
        )
    }
}