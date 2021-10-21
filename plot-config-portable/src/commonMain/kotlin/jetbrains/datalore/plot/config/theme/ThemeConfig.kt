/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config.theme

import jetbrains.datalore.plot.builder.defaultTheme.DefaultTheme
import jetbrains.datalore.plot.builder.defaultTheme.values.*
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.ELEMENT_BLANK
import jetbrains.datalore.plot.builder.theme.Theme
import jetbrains.datalore.plot.config.Option

class ThemeConfig constructor(
    themeSettings: Map<String, Any> = emptyMap()
) {

    val theme: Theme

    init {

        val themeName = themeSettings.getOrElse(Option.Meta.NAME) { ThemeOption.Name.LP_MINIMAL }.toString()
        val baselineValues = THEME_VALUES_BY_NAME.getOrElse(themeName) { ThemeValuesBase() }

        // ToDo: select defaults.
//        val baselineValues = ThemeValuesLPMinimal2()
//            val baselineValues = ThemeValuesRClassic()
//            val baselineValues = ThemeValuesRGrey()

        // Make sure all values are converted to proper objects.
        @Suppress("NAME_SHADOWING")
        val userOptions: Map<String, Any> = themeSettings.mapValues { (key, value) ->
            val value = convertElementBlank(value)
            LegendThemeConfig.convertValue(key, value)
        }

        val effectiveOptions: Map<String, Any> = baselineValues + userOptions
        theme = DefaultTheme(effectiveOptions)
    }

    companion object {
        private val THEME_VALUES_BY_NAME: Map<String, ThemeValues> = mapOf(
            ThemeOption.Name.R_GREY to ThemeValuesRGrey(),
            ThemeOption.Name.R_LIGHT to ThemeValuesRLight(),
            ThemeOption.Name.R_CLASSIC to ThemeValuesRClassic(),
            ThemeOption.Name.R_MINIMAL to ThemeValuesRMinimal(),
            ThemeOption.Name.LP_MINIMAL to ThemeValuesLPMinimal2(),
            ThemeOption.Name.LP_NONE to ThemeValuesLPNone(),
        )

        /**
         * Converts a simple "blank" string to a 'blank element'.
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
