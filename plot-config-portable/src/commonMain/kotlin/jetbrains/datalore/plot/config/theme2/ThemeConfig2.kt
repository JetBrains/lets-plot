/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config.theme2

import jetbrains.datalore.plot.builder.theme.*
import jetbrains.datalore.plot.builder.theme2.DefaultTheme2
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption
import jetbrains.datalore.plot.builder.theme2.values.ThemeOption.ELEMENT_BLANK
import jetbrains.datalore.plot.builder.theme2.values.ThemeValuesRClassic

class ThemeConfig2(themeSettings: Map<String, Any>) {

    val theme: Theme = OneTileTheme(themeSettings)

    private abstract class ConfiguredTheme(
        private val options: Map<String, Any>,
    ) : Theme {


        private val theme2: Theme
        private val axisXTheme: AxisTheme
        private val axisYTheme: AxisTheme
        private val legendTheme: LegendTheme
        private val panelTheme: PanelTheme
        private val facetsTheme: FacetsTheme

        init {
            // ToDo: select defaults.
//            val baselineOptions = ThemeValuesLPLight.values
            val baselineOptions = ThemeValuesRClassic.values

            // Make sure all values are converted to proper objects.
            @Suppress("NAME_SHADOWING")
            val userOptions: Map<String, Any> = options.mapValues { (key, value) ->
                val value = convertElementBlank(value)
                LegendThemeConfig2.convertValue(key, value)
            }

            // Merge baseline and user options.
            val effectiveOptions: MutableMap<String, Any> = HashMap<String, Any>(baselineOptions)
            for ((key, userValue) in userOptions) {
                if (userValue is Map<*, *>) {
                    // Merge values
                    val baseValue = effectiveOptions.getOrPut(key) { userValue } as Map<*, *>
                    effectiveOptions[key] = baseValue + userValue
                } else {
                    // Override value
                    effectiveOptions[key] = userValue
                }
            }

            theme2 = DefaultTheme2(effectiveOptions)
            axisXTheme = theme2.axisX()
            axisYTheme = theme2.axisY()
            legendTheme = theme2.legend()
            panelTheme = theme2.panel()
            facetsTheme = theme2.facets()
        }

        override fun axisX(): AxisTheme = axisXTheme

        override fun axisY(): AxisTheme = axisYTheme

        override fun legend(): LegendTheme = legendTheme

        override fun panel(): PanelTheme = panelTheme

        override fun facets(): FacetsTheme = facetsTheme

        override fun plot(): PlotTheme {
            // ToDo: configurable
            return DEF.plot()
        }

        override fun multiTile(): Theme {
            return MultiTileTheme(options)
        }
    }

    private class OneTileTheme(options: Map<String, Any>) : ConfiguredTheme(options)

    private class MultiTileTheme(options: Map<String, Any>) : ConfiguredTheme(options) {

        override fun plot(): PlotTheme {
            return DEF.multiTile().plot()
        }
    }

    companion object {
        internal val DEF: Theme = DefaultTheme()
//        private val DEF_OPTIONS = mapOf(
//            LEGEND_POSITION to DEF.legend().position(),
//            LEGEND_JUSTIFICATION to DEF.legend().justification(),
//            LEGEND_DIRECTION to DEF.legend().direction()
//        )

//        private val DEF_OPTIONS_MULTI_TILE = DEF_OPTIONS + mapOf(
//            "${AXIS_LINE}_x" to ELEMENT_BLANK,      // replaced by inner frame
//            "${AXIS_LINE}_y" to ELEMENT_BLANK,      // replaced by inner frame
//        )

        /**
         * Converts old theme 'blank' element to new format
         *
         * ToDo: remove after some sensible period of time. (Added Oct1, 2021)
         */
        private fun convertElementBlank(value: Any): Any {
            if (value is String && value == ThemeOption.ELEMENT_BLANK_SHORTHAND) {
                return ELEMENT_BLANK
            }
            if (value is Map<*, *> && value["name"] == "blank") {
                return ELEMENT_BLANK
            }
            return value
        }
    }
}
