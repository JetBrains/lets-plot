/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.theme2.values

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.base.values.Colors
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.AXIS
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.AXIS_LINE_Y
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.AXIS_TICKS_Y
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.ELEMENT_BLANK
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.Elem
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.PANEL_GRID
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.PANEL_GRID_MINOR
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.PANEL_RECT
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.TITLE

object ThemeValuesLPMinimal2 {
    //    val BLACK:Color = Color.parseHex("#171717")
//    val DARK_GREY:Color = Color.parseHex("#474747")
//    val LIGHT_GREY:Color = Color.parseHex("#E9E9E9")
    val BLACK: Color = Color.GREEN
    val DARK_GREY: Color = Color.RED
    val LIGHT_GREY: Color = Color.ORANGE

    val values: Map<String, Any> = ThemeValuesBase.values + mapOf(
        TITLE to mapOf(
            Elem.COLOR to BLACK
        ),

        PANEL_RECT to ELEMENT_BLANK,
        PANEL_GRID_MINOR to ELEMENT_BLANK,
        PANEL_GRID to mapOf(
            Elem.COLOR to LIGHT_GREY
        ),

        AXIS_LINE_Y to ELEMENT_BLANK,
        AXIS_TICKS_Y to ELEMENT_BLANK,
        AXIS to mapOf(
            Elem.COLOR to DARK_GREY
        ),

        ThemeOption.FACET_STRIP to mapOf(
            Elem.FILL to Colors.lighter(Color.VERY_LIGHT_GRAY, 0.9),
            Elem.SIZE to 0.0,
        ),
    )
}