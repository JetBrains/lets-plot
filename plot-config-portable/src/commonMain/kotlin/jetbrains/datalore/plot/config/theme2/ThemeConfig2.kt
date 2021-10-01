/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config.theme2

import jetbrains.datalore.plot.builder.theme.*
import jetbrains.datalore.plot.builder.theme2.DefaultTheme2
import jetbrains.datalore.plot.builder.theme2.values.ThemeValuesLPLight
import jetbrains.datalore.plot.config.Option.Theme.AXIS_LINE
import jetbrains.datalore.plot.config.Option.Theme.ELEMENT_BLANK
import jetbrains.datalore.plot.config.Option.Theme.LEGEND_DIRECTION
import jetbrains.datalore.plot.config.Option.Theme.LEGEND_JUSTIFICATION
import jetbrains.datalore.plot.config.Option.Theme.LEGEND_POSITION
import jetbrains.datalore.plot.config.theme.LegendThemeConfig

class ThemeConfig2(themeSettings: Map<String, Any>) {

    val theme: Theme = OneTileTheme(themeSettings)

    private abstract class ConfiguredTheme(
        private val options: Map<String, Any>,
        defOptions: Map<String, Any>
    ) : Theme {


        private val theme2: Theme

        //        private val axisXTheme: AxisTheme = AxisThemeConfig.X(options, defOptions)
//        private val axisYTheme: AxisTheme = AxisThemeConfig.Y(options, defOptions)
        private val axisXTheme: AxisTheme
        private val axisYTheme: AxisTheme

        private val legendTheme: LegendTheme = LegendThemeConfig(options, defOptions)

        init {
            // ToDo: select defaults.
            val baseValues = ThemeValuesLPLight.values

            // Take care of merging 'view objects'.
            val base: MutableMap<String, Any> = HashMap<String, Any>(baseValues)
            for ((key, value) in options) {
                val baseValue = base.getOrPut(key) { value }
                if (baseValue is Map<*, *>) {
                    // merge
                    base[key] = baseValue + value as Map<*, *>
                }
            }

            theme2 = DefaultTheme2(base)
            axisXTheme = theme2.axisX()
            axisYTheme = theme2.axisY()
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
