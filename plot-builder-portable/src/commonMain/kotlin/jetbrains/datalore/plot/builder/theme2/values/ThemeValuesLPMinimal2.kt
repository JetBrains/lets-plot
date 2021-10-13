/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.theme2.values

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.AXIS
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.AXIS_LINE_Y
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.AXIS_TICKS_Y
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.ELEMENT_BLANK
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.Elem
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.FACET_STRIP_BGR_RECT
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.LINE
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.PANEL_BKGR_RECT
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.PANEL_GRID
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.PANEL_GRID_MINOR
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.RECT
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.TEXT
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.TITLE
import jetbrains.datalore.plot.builder.theme2.values.ThemeValuesBase.Companion.PLOT_BACKGROUND

class ThemeValuesLPMinimal2 : ThemeValues(VALUES) {

    companion object {

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
                Elem.COLOR to DARK_GREY
            ),

            TEXT to mapOf(
                Elem.COLOR to DARK_GREY
            ),

            TITLE to mapOf(
                Elem.COLOR to BLACK
            ),

//        LEGEND_TEXT to mapOf(
//            Elem.COLOR to DARK_GREY
//        ),

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

            FACET_STRIP_BGR_RECT to mapOf(
//            Elem.FILL to Colors.lighter(Color.VERY_LIGHT_GRAY, 0.9),
                Elem.FILL to PLOT_BACKGROUND,
                Elem.SIZE to 0.0,
            ),
        )
    }
}