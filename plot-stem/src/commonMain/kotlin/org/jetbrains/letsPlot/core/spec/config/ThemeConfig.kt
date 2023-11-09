/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.config

import org.jetbrains.letsPlot.core.plot.base.theme.Theme
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.ThemeUtil
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.ELEMENT_BLANK
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.PLOT_MARGIN
import org.jetbrains.letsPlot.core.plot.base.theme.FontFamilyRegistry
import org.jetbrains.letsPlot.core.spec.Option

class ThemeConfig constructor(
    themeSettings: Map<String, Any> = emptyMap(),
    fontFamilyRegistry: FontFamilyRegistry
) {

    val theme: Theme

    init {

        val themeName = themeSettings.getOrElse(Option.Meta.NAME) { ThemeOption.Name.LP_MINIMAL }.toString()

        // Make sure all values are converted to proper objects.
        @Suppress("NAME_SHADOWING")
        val userOptions: Map<String, Any> = themeSettings.mapValues { (key, value) ->
            var value = convertElementBlank(value)
            value = convertMargins(key, value)
            LegendThemeConfig.convertValue(key, value)
        }

        theme = ThemeUtil.buildTheme(themeName, userOptions, fontFamilyRegistry)
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

        private fun convertMargins(key: String, value: Any): Any {
            fun toMargin(value: Any?) = when (value) {
                    "t" -> ThemeOption.Elem.Margin.TOP
                    "r" -> ThemeOption.Elem.Margin.RIGHT
                    "b" -> ThemeOption.Elem.Margin.BOTTOM
                    "l" -> ThemeOption.Elem.Margin.LEFT
                    else -> throw IllegalArgumentException(
                        "Illegal value: '$value'.\n${ThemeOption.Elem.MARGIN} " +
                                "Expected values are: value is either a string: t|r|b|l."
                    )
                }

            return when {
                key == PLOT_MARGIN && value is Map<*, *> -> {
                    value.map { (k, v) -> toMargin(k) to v }.toMap()
                }
                value is Map<*, *> && value.containsKey(ThemeOption.Elem.MARGIN) -> {
                    val oldMargins = value[ThemeOption.Elem.MARGIN] as Map<*, *>
                    val newMargins = oldMargins.map { (k, v) -> toMargin(k) to v }
                    value - ThemeOption.Elem.MARGIN + newMargins
                }
                else -> {
                    value
                }
            }
        }
    }
}
