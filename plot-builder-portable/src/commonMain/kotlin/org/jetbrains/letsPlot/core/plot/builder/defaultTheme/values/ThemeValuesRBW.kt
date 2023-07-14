/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.AXIS
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.AXIS_LINE
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.AXIS_TOOLTIP
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.ELEMENT_BLANK
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.Elem
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.FACET_STRIP_BGR_RECT
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.LINE
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.PANEL_BKGR_RECT
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.PANEL_GRID
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.RECT
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.TEXT

class ThemeValuesRBW : ThemeValues(VALUES) {

    companion object {
        private val PLOT_BACKGROUND = Color.WHITE

        private val PANEL_BORDER: Color = Color.parseHex("#333333")
        private val STRIP_BACKGROUND: Color = Color.parseHex("#D9D9D9")

        private val DARK_GREY: Color = Color.parseHex("#474747")
        private val LIGHT_GREY: Color = Color.parseHex("#E9E9E9")

        private val VALUES: Map<String, Any> = ThemeValuesBase() + mapOf(

            LINE to mapOf(
                Elem.COLOR to DARK_GREY
            ),

            RECT to mapOf(
                Elem.COLOR to DARK_GREY,
                Elem.FILL to LIGHT_GREY
            ),

            TEXT to mapOf(
                Elem.COLOR to DARK_GREY
            ),

            PANEL_BKGR_RECT to mapOf(
                Elem.FILL to PLOT_BACKGROUND,
                Elem.COLOR to PANEL_BORDER
            ),
            PANEL_GRID to mapOf(
                Elem.COLOR to LIGHT_GREY
            ),
            ThemeOption.PANEL_GRID_MAJOR to mapOf(
                Elem.SIZE to 1.4,
            ),
            ThemeOption.PANEL_GRID_MINOR to mapOf(
                Elem.SIZE to 0.5,
            ),

            AXIS_LINE to ELEMENT_BLANK,
            AXIS to mapOf(
                Elem.COLOR to PANEL_BORDER
            ),

            AXIS_TOOLTIP to mapOf(
                Elem.COLOR to PLOT_BACKGROUND,
                Elem.FILL to DARK_GREY,
            ),

            // Facets
            FACET_STRIP_BGR_RECT to mapOf(
                Elem.FILL to STRIP_BACKGROUND,
                Elem.COLOR to PANEL_BORDER,
            ),
        )
    }
}