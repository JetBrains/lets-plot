/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.config

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.theme.Theme
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.DefaultTheme
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.ThemeFlavorUtil.applyFlavor
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.ELEMENT_BLANK
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.FLAVOR
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeValues
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeValues.Companion.mergeWith
import org.jetbrains.letsPlot.core.plot.builder.presentation.FontFamilyRegistry
import org.jetbrains.letsPlot.core.spec.Option
import org.jetbrains.letsPlot.core.spec.getString

class ThemeConfig constructor(
    themeSettings: Map<String, Any> = emptyMap(),
    fontFamilyRegistry: FontFamilyRegistry
) {

    val theme: Theme

    init {

        val themeName = themeSettings.getOrElse(Option.Meta.NAME) { ThemeOption.Name.LP_MINIMAL }.toString()
        val baselineValues = ThemeValues.forName(themeName).values

        // Make sure all values are converted to proper objects.
        @Suppress("NAME_SHADOWING")
        val userOptions: Map<String, Any> = themeSettings.mapValues { (key, value) ->
            var value = convertElementBlank(value)
            value = convertMargins(value)
            LegendThemeConfig.convertValue(key, value)
        }
        val flavorName = themeSettings.getString(FLAVOR)
            ?: baselineValues.getString(FLAVOR)
            ?: ThemeOption.Flavor.BASE

        val effectiveOptions = applyFlavor(baselineValues, flavorName)
            .mergeWith(userOptions)

        theme = DefaultTheme(effectiveOptions, fontFamilyRegistry)
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
