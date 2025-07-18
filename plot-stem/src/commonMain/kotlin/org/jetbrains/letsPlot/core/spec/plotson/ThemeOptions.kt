/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.plotson

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption
import org.jetbrains.letsPlot.core.spec.Option


class ThemeOptions : Options() {
    var name: ThemeName? by map(Option.Meta.NAME)
    var line: Element? by map(ThemeOption.LINE)
    var axis: Element? by map(ThemeOption.AXIS)
    var axisTitle: Element? by map(ThemeOption.AXIS_TITLE)
    var axisLine: Element? by map(ThemeOption.AXIS_LINE)
    var panelGrid: Element? by map(ThemeOption.PANEL_GRID)
    var axisTicksX: Element? by map(ThemeOption.AXIS_TICKS_X)
    var axisTicksY: Element? by map(ThemeOption.AXIS_TICKS_Y)
    var axisTooltip: Element? by map(ThemeOption.AXIS_TOOLTIP)
    var labelText: Element? by map(ThemeOption.ANNOTATION_TEXT)
    var flavor: Flavor? by map(ThemeOption.FLAVOR)

    enum class ThemeName(val value: String) {
        GREY(ThemeOption.Name.R_GREY),
        LIGHT(ThemeOption.Name.R_LIGHT),
        CLASSIC(ThemeOption.Name.R_CLASSIC),
        MINIMAL(ThemeOption.Name.R_MINIMAL),
        BW(ThemeOption.Name.R_BW),
        MINIMAL2(ThemeOption.Name.LP_MINIMAL),
        NONE(ThemeOption.Name.LP_NONE)
    }

    enum class Flavor(val value: String) {
        DARCULA(ThemeOption.Flavor.DARCULA),
        SOLARIZED_LIGHT(ThemeOption.Flavor.SOLARIZED_LIGHT),
        SOLARIZED_DARK(ThemeOption.Flavor.SOLARIZED_DARK),
        HIGH_CONTRAST_LIGHT(ThemeOption.Flavor.HIGH_CONTRAST_LIGHT),
        HIGH_CONTRAST_DARK(ThemeOption.Flavor.HIGH_CONTRAST_DARK),
    }

    class Element : Options() {
        var blank: Boolean? by map(ThemeOption.Elem.BLANK)
        var fill: Color? by map(ThemeOption.Elem.FILL)
        var color: Color? by map(ThemeOption.Elem.COLOR)
        var size: Double? by map(ThemeOption.Elem.SIZE)

        var family: String? by map(ThemeOption.Elem.FONT_FAMILY)
        var face: String? by map(ThemeOption.Elem.FONT_FACE)
        var angle: Double? by map(ThemeOption.Elem.ANGLE)
        var hjust: Double? by map(ThemeOption.Elem.HJUST)
        var vjust: Double? by map(ThemeOption.Elem.VJUST)

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

fun ThemeOptions.setVoid(): ThemeOptions {
    name = ThemeOptions.ThemeName.CLASSIC
    line = ThemeOptions.Element.BLANK
    axis = ThemeOptions.Element.BLANK
    return this
}