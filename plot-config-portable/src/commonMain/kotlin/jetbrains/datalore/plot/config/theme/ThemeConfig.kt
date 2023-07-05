/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config.theme

import jetbrains.datalore.plot.builder.defaultTheme.DefaultTheme
import jetbrains.datalore.plot.builder.defaultTheme.ThemeFlavor
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.ELEMENT_BLANK
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeValues
import jetbrains.datalore.plot.builder.presentation.FontFamilyRegistry
import jetbrains.datalore.plot.builder.theme.Theme
import jetbrains.datalore.plot.config.Option
import jetbrains.datalore.plot.config.getString

class ThemeConfig constructor(
    themeSettings: Map<String, Any> = emptyMap(),
    fontFamilyRegistry: FontFamilyRegistry
) {

    val theme: Theme

    init {

        val themeName = themeSettings.getOrElse(Option.Meta.NAME) { ThemeOption.Name.LP_MINIMAL }.toString()
        val baselineValues = ThemeValues.forName(themeName)

        // Make sure all values are converted to proper objects.
        @Suppress("NAME_SHADOWING")
        val userOptions: Map<String, Any> = themeSettings.mapValues { (key, value) ->
            var value = convertElementBlank(value)
            value = convertMargins(value)
            LegendThemeConfig.convertValue(key, value)
        }

        val themeFlavorOptions = baselineValues.values.let {
            val flavorName = themeSettings.getString(Option.Theme.FLAVOR)
            if (flavorName != null) {
                ThemeFlavor.forName(flavorName).updateColors(it)
            } else {
                it
            }
        }

        theme = DefaultTheme(themeFlavorOptions, fontFamilyRegistry, userOptions)
    }

    companion object {

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

        private fun convertMargins(value: Any): Any {
            return if (value is Map<*, *> && ThemeOption.Elem.MARGIN in value) {
                val oldMargins = value[ThemeOption.Elem.MARGIN] as Map<*, *>
                val newMargins = oldMargins.map { (k, v) -> ThemeOption.Elem.MARGIN + "_" + k to v }
                value - ThemeOption.Elem.MARGIN + newMargins
            } else {
                value
            }
        }
    }
}
