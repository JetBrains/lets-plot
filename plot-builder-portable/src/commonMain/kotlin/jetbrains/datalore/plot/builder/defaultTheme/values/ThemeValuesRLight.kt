/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.defaultTheme.values

import org.jetbrains.letsPlot.commons.values.Color
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.AXIS
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.AXIS_LINE
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.AXIS_TICKS
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.AXIS_TOOLTIP
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.ELEMENT_BLANK
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.Elem
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.FACET_STRIP_BGR_RECT
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.LINE
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.PANEL_BKGR_RECT
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.PANEL_GRID
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.RECT
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.TEXT

class ThemeValuesRLight : ThemeValues(VALUES) {

    companion object {

        private val PLOT_BACKGROUND = Color.WHITE

        private val PANEL_BORDER: Color = Color.parseHex("#C9C9C9")
        private val STRIP_BACKGROUND: Color = Color.parseHex("#D9D9D9")

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

            PANEL_BKGR_RECT to mapOf(
                Elem.FILL to PLOT_BACKGROUND,
                Elem.COLOR to PANEL_BORDER
            ),
            PANEL_GRID to mapOf(
                Elem.COLOR to LIGHT_GREY
            ),

            AXIS_LINE to ELEMENT_BLANK,
            AXIS to mapOf(
                Elem.COLOR to PANEL_BORDER
            ),

            AXIS_TOOLTIP to mapOf(
                Elem.COLOR to PLOT_BACKGROUND,
                Elem.FILL to DARK_GREY,
            ),

            FACET_STRIP_BGR_RECT to mapOf(
                Elem.FILL to STRIP_BACKGROUND,
                Elem.SIZE to 0.0,
            ),
        )
    }
}