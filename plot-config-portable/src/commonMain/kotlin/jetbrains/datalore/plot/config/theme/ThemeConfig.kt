/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config.theme

import jetbrains.datalore.plot.builder.theme.*
import jetbrains.datalore.plot.config.Option.Theme.LEGEND_DIRECTION
import jetbrains.datalore.plot.config.Option.Theme.LEGEND_JUSTIFICATION
import jetbrains.datalore.plot.config.Option.Theme.LEGEND_POSITION

class ThemeConfig(options: Map<*, *>) {


    val theme: Theme

    init {
        theme = MyTheme(options, DEF_OPTIONS)
    }

    private class MyTheme internal constructor(options: Map<*, *>, defOptions: Map<*, *>) : Theme {
        private val myAxisXTheme: AxisTheme
        private val myAxisYTheme: AxisTheme
        private val myLegendTheme: LegendTheme
        private val myTooltipTheme: TooltipTheme

        init {
            myAxisXTheme = AxisThemeConfig.X(options, defOptions)
            myAxisYTheme = AxisThemeConfig.Y(options, defOptions)
            myLegendTheme = LegendThemeConfig(options, defOptions)
            myTooltipTheme = TooltipThemeConfig(options, defOptions)
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

        override fun tooltip(): TooltipTheme {
            return myTooltipTheme
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
