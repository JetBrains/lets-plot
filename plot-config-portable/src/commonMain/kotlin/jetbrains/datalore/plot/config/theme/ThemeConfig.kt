/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config.theme

import jetbrains.datalore.plot.builder.guide.TooltipAnchor
import jetbrains.datalore.plot.builder.theme.AxisTheme
import jetbrains.datalore.plot.builder.theme.DefaultTheme
import jetbrains.datalore.plot.builder.theme.LegendTheme
import jetbrains.datalore.plot.builder.theme.Theme
import jetbrains.datalore.plot.config.Option
import jetbrains.datalore.plot.config.Option.Theme.LEGEND_DIRECTION
import jetbrains.datalore.plot.config.Option.Theme.LEGEND_JUSTIFICATION
import jetbrains.datalore.plot.config.Option.Theme.LEGEND_POSITION
import jetbrains.datalore.plot.config.OptionsAccessor

class ThemeConfig(options: Map<*, *>) {


    val theme: Theme

    init {
        theme = MyTheme(options, DEF_OPTIONS)
    }

    private class MyTheme internal constructor(options: Map<*, *>, defOptions: Map<*, *>) : Theme {
        private val myAxisXTheme: AxisTheme
        private val myAxisYTheme: AxisTheme
        private val myLegendTheme: LegendTheme
        private val myTooltipAnchor: TooltipAnchor

        init {
            myAxisXTheme = AxisThemeConfig.X(options, defOptions)
            myAxisYTheme = AxisThemeConfig.Y(options, defOptions)
            myLegendTheme = LegendThemeConfig(options, defOptions)
            myTooltipAnchor = getTooltipAnchor(options)
        }

        override fun axisX(): AxisTheme {
            return myAxisXTheme
        }

        override fun axisY(): AxisTheme {
            return myAxisYTheme
        }

        override fun legend(): LegendTheme {
            return myLegendTheme
        }

        override fun tooltipAnchor(): TooltipAnchor {
            return myTooltipAnchor
        }

        private fun getTooltipAnchor(options: Map<*, *>): TooltipAnchor {
            val opts = OptionsAccessor.over(options)
            val positionString = opts.getString(Option.Theme.TOOLTIP_ANCHOR)
            return when (positionString) {
                "top_right" -> TooltipAnchor.TOP_RIGHT
                "top_left"  -> TooltipAnchor.TOP_LEFT
                "bottom_right" -> TooltipAnchor.BOTTOM_RIGHT
                "bottom_left"  -> TooltipAnchor.BOTTOM_LEFT
                else -> error("Undefined tooltip anchor: $positionString")
            }
        }
    }

    companion object {
        internal val DEF: Theme = DefaultTheme()
        private val DEF_OPTIONS = mapOf(
                LEGEND_POSITION to DEF.legend().position(),
                LEGEND_JUSTIFICATION to DEF.legend().justification(),
                LEGEND_DIRECTION to DEF.legend().direction()
        )
    }
}
