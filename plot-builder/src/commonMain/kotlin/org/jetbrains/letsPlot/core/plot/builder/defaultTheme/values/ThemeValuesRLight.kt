/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values

import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.ThemeFlavorUtil.SymbolicColor
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
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.RECT
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.TEXT

internal class ThemeValuesRLight : ThemeValues(VALUES) {

    companion object {

        private val VALUES: Map<String, Any> = ThemeValuesBase() + mapOf(
            LINE to mapOf(
                Elem.COLOR to SymbolicColor.BLACK
            ),

            RECT to mapOf(
                Elem.COLOR to SymbolicColor.BLACK,
                Elem.FILL to SymbolicColor.GREY_3
            ),

            TEXT to mapOf(
                Elem.COLOR to SymbolicColor.BLACK
            ),

            PANEL_BKGR_RECT to mapOf(
                Elem.FILL to SymbolicColor.WHITE,
                Elem.COLOR to SymbolicColor.GREY_4
            ),
            PANEL_GRID to mapOf(
                Elem.COLOR to SymbolicColor.GREY_1
            ),

            AXIS_LINE to ELEMENT_BLANK,

            AXIS to mapOf(
                Elem.COLOR to SymbolicColor.GREY_4
            ),

            AXIS_TOOLTIP to mapOf(
                Elem.COLOR to SymbolicColor.WHITE,
                Elem.FILL to SymbolicColor.BLACK
            ),

            FACET_STRIP_BGR_RECT to mapOf(
                Elem.FILL to SymbolicColor.GREY_2,
                Elem.SIZE to 0.0,
            ),

            FLAVOR to Flavor.LIGHT,
        )
    }
}