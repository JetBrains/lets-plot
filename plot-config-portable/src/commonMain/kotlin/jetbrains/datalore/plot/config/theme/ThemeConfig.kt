/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config.theme

import jetbrains.datalore.plot.builder.theme.*
import jetbrains.datalore.plot.config.Option.Theme.LEGEND_DIRECTION
import jetbrains.datalore.plot.config.Option.Theme.LEGEND_JUSTIFICATION
import jetbrains.datalore.plot.config.Option.Theme.LEGEND_POSITION

class ThemeConfig(options: Map<String, Any>) {

    val theme: Theme = MyTheme(options, DEF_OPTIONS)

    private class MyTheme internal constructor(options: Map<String, Any>, defOptions: Map<String, Any>) : Theme {
        private val axisXTheme: AxisTheme
        private val axisYTheme: AxisTheme
        private val legendTheme: LegendTheme

        init {
            axisXTheme = AxisThemeConfig.X(options, defOptions)
            axisYTheme = AxisThemeConfig.Y(options, defOptions)
            legendTheme = LegendThemeConfig(options, defOptions)
        }

        override fun axisX(): AxisTheme {
            return axisXTheme
        }

        override fun axisY(): AxisTheme {
            return axisYTheme
        }

        override fun legend(): LegendTheme {
            return legendTheme
        }

        override fun facets(): FacetsTheme {
            // ToDo: configurable
            return DEF.facets()
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
