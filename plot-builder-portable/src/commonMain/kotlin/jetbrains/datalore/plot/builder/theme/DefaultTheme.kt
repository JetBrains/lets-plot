/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.theme

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.builder.guide.TooltipAnchor


class DefaultTheme : Theme {

    override fun axisX(): AxisTheme {
        return AXIS_THEME
    }

    override fun axisY(): AxisTheme {
        return AXIS_THEME
    }

    override fun legend(): LegendTheme {
        return LEGEND_THEME
    }

    override fun tooltip(): TooltipTheme {
        return TOOLTIP_THEME
    }

    companion object {
        private val AXIS_THEME = DefaultAxisTheme()

        private val LEGEND_THEME: LegendTheme = object :
            LegendTheme {
            override fun keySize(): Double {
                return 23.0
            }

            override fun margin(): Double {
                return 5.0
            }

            override fun padding(): Double {
                return 5.0
            }

            override fun position(): jetbrains.datalore.plot.builder.guide.LegendPosition {
                return jetbrains.datalore.plot.builder.guide.LegendPosition.RIGHT
            }

            override fun justification(): jetbrains.datalore.plot.builder.guide.LegendJustification {
                return jetbrains.datalore.plot.builder.guide.LegendJustification.CENTER
            }

            override fun direction(): jetbrains.datalore.plot.builder.guide.LegendDirection {
                return jetbrains.datalore.plot.builder.guide.LegendDirection.AUTO
            }

            override fun backgroundFill(): Color {
                return Color.WHITE
            }
        }

        private val TOOLTIP_THEME: TooltipTheme = object :
            TooltipTheme {
            override fun isVisible(): Boolean {
                return true
            }

            override fun anchor(): TooltipAnchor? {
                return null
            }
        }
    }
}
