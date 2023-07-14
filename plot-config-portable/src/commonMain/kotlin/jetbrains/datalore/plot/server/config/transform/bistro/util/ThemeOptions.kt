/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.server.config.transform.bistro.util

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption


class ThemeOptions : Options() {
    var axisTitle: Element? by map(ThemeOption.AXIS_TITLE)
    var axisLine: Element? by map(ThemeOption.AXIS_LINE)
    var panelGrid: Element? by map(ThemeOption.PANEL_GRID)
    var axisTicksX: Element? by map(ThemeOption.AXIS_TICKS_X)
    var axisTicksY: Element? by map(ThemeOption.AXIS_TICKS_Y)

    class Element : Options() {
        var blank: Boolean? by map(ThemeOption.Elem.BLANK)
        var fill: Color? by map(ThemeOption.Elem.FILL)
        var color: Color? by map(ThemeOption.Elem.COLOR)
        var size: Double? by map(ThemeOption.Elem.SIZE)

        init {
            blank = false
        }

        companion object {
            val BLANK = Element().apply {
                blank = true
            }

            fun line(size: Double? = null, color: Color? = null) = Element().apply {
                this.size = size
                this.color = color
            }
        }
    }
}

fun theme(block: ThemeOptions.() -> Unit) = ThemeOptions().apply(block)
