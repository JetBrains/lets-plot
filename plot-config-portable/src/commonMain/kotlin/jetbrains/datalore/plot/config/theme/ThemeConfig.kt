/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config.theme

import jetbrains.datalore.plot.builder.theme.*
import jetbrains.datalore.plot.config.Option.Theme.AXIS_LINE
import jetbrains.datalore.plot.config.Option.Theme.ELEMENT_BLANK
import jetbrains.datalore.plot.config.Option.Theme.LEGEND_DIRECTION
import jetbrains.datalore.plot.config.Option.Theme.LEGEND_JUSTIFICATION
import jetbrains.datalore.plot.config.Option.Theme.LEGEND_POSITION

class ThemeConfig(options: Map<String, Any>) {

    val theme: Theme = OneTileTheme(options)

    private abstract class ConfiguredTheme(
        private val options: Map<String, Any>,
        defOptions: Map<String, Any>
    ) : Theme {

        private val axisXTheme: AxisTheme = AxisThemeConfig.X(options, defOptions)
        private val axisYTheme: AxisTheme = AxisThemeConfig.Y(options, defOptions)
        private val legendTheme: LegendTheme = LegendThemeConfig(options, defOptions)

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

        override fun plot(): PlotTheme {
            // ToDo: configurable
            return DEF.plot()
        }

        override fun multiTile(): Theme {
            return MultiTileTheme(options)
        }
    }

    private class OneTileTheme(options: Map<String, Any>) :
        ConfiguredTheme(options, DEF_OPTIONS)

    private class MultiTileTheme(options: Map<String, Any>) :
        ConfiguredTheme(options, DEF_OPTIONS_MULTI_TILE) {

        override fun plot(): PlotTheme {
            return DEF.multiTile().plot()
        }
    }

    companion object {
        internal val DEF: Theme = DefaultTheme()
        private val DEF_OPTIONS = mapOf(
            LEGEND_POSITION to DEF.legend().position(),
            LEGEND_JUSTIFICATION to DEF.legend().justification(),
            LEGEND_DIRECTION to DEF.legend().direction()
        )

        private val DEF_OPTIONS_MULTI_TILE = DEF_OPTIONS + mapOf(
            "${AXIS_LINE}_x" to ELEMENT_BLANK,      // replaced by inner frame
            "${AXIS_LINE}_y" to ELEMENT_BLANK,      // replaced by inner frame
        )
    }
}
