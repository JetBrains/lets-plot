/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values

import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.AXIS_LINE_Y
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.AXIS_TICKS_Y
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.AXIS_TOOLTIP
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.ELEMENT_BLANK
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.Elem
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.FACET_STRIP_BGR_RECT
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.FLAVOR
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.Flavor
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.PANEL_BKGR_RECT
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.PANEL_GRID
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.PANEL_GRID_MINOR
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.PAPER
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.PEN

class ThemeValuesLPMinimal2 : ThemeValues(VALUES) {

    companion object {

        private val VALUES: Map<String, Any> = ThemeValuesBase() + mapOf(

            PANEL_BKGR_RECT to ELEMENT_BLANK,
            PANEL_GRID_MINOR to ELEMENT_BLANK,

            AXIS_LINE_Y to ELEMENT_BLANK,
            AXIS_TICKS_Y to ELEMENT_BLANK,

            FACET_STRIP_BGR_RECT to ELEMENT_BLANK,

            // Flavors

            FLAVOR to Flavor.MINIMAL,

            AXIS_TOOLTIP to mapOf(
                Elem.FILL to PEN,
                Elem.COLOR to PAPER
            ),
            PANEL_GRID to mapOf(
                Elem.SIZE to 1.0
            ),
        )
    }
}