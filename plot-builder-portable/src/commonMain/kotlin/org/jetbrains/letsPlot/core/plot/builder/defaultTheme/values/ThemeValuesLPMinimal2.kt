/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.AXIS
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.AXIS_LINE_Y
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.AXIS_TICKS_Y
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.AXIS_TOOLTIP
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.ELEMENT_BLANK
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.Elem
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.FACET_STRIP_BGR_RECT
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.LINE
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.PANEL_BKGR_RECT
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.PANEL_GRID
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.PANEL_GRID_MINOR
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.RECT
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.TEXT

class ThemeValuesLPMinimal2 : ThemeValues(VALUES) {

    companion object {

        private val PLOT_BACKGROUND = Color.WHITE

        private val BLACK: Color = Color.parseHex("#171717")
        private val DARK_GREY: Color = Color.parseHex("#474747")
        private val LIGHT_GREY: Color = Color.parseHex("#E9E9E9")

//        private val BLACK: Color = Color.GREEN
//        private val DARK_GREY: Color = Color.RED
//        private val LIGHT_GREY: Color = Color.ORANGE

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

            PANEL_BKGR_RECT to ELEMENT_BLANK,
            PANEL_GRID_MINOR to ELEMENT_BLANK,
            PANEL_GRID to mapOf(
                Elem.COLOR to LIGHT_GREY
            ),

            AXIS_LINE_Y to ELEMENT_BLANK,
            AXIS_TICKS_Y to ELEMENT_BLANK,
            AXIS to mapOf(
                Elem.COLOR to DARK_GREY
            ),

            AXIS_TOOLTIP to mapOf(
                Elem.COLOR to PLOT_BACKGROUND,
                Elem.FILL to DARK_GREY,
            ),

            FACET_STRIP_BGR_RECT to mapOf(
                Elem.BLANK to true,
            ),
        )
    }
}