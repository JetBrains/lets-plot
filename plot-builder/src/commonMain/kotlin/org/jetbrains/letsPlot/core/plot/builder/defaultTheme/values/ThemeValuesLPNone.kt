/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.AXIS_TOOLTIP
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.Elem
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.Geom
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.LINE
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.RECT
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.TEXT

internal class ThemeValuesLPNone : ThemeValues(VALUES) {

    companion object {

        private val VALUES: Map<String, Any> = ThemeValuesBase() + mapOf(
            LINE to mapOf(
                Elem.COLOR to Color.BLUE,
            ),
            RECT to mapOf(
                Elem.COLOR to Color.BLUE,
                Elem.FILL to Color.LIGHT_BLUE,
            ),
            TEXT to mapOf(
                Elem.COLOR to Color.BLUE,
            ),

            AXIS_TOOLTIP to mapOf(
                Elem.COLOR to Color.BLUE,
                Elem.FILL to Color.LIGHT_BLUE,
            ),

            ThemeOption.GEOM to mapOf(
                Geom.PEN to Color.BLUE,
                Geom.PAPER to Color.WHITE,
                Geom.BRUSH to Color.PACIFIC_BLUE,
            ),
        )
    }
}

