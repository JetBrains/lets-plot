/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.config

import org.jetbrains.letsPlot.core.plot.base.theme.ExponentFormat
import org.jetbrains.letsPlot.core.plot.base.theme.FontFamilyRegistry
import org.jetbrains.letsPlot.core.plot.base.theme.Theme
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.ThemeUtil
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.ELEMENT_BLANK
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.PLOT_MARGIN
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
            value = convertExponentFormat(key, value)
            LegendThemeConfig.convertValue(key, value)
        }

        theme = ThemeUtil.buildTheme(themeName, userOptions, fontFamilyRegistry)
    }

    companion object {
        private fun convertExponentFormat(key: String, value: Any): Any {
            if (key == ThemeOption.EXPONENT_FORMAT) {
                return when (value.toString().lowercase()) {
                    "e" -> ExponentFormat.E
                    "pow" -> ExponentFormat.POW
                    else -> throw IllegalArgumentException(
                        "Illegal value: '$value'.\n${ThemeOption.EXPONENT_FORMAT} expected value is a string: e|pow."
                    )
                }
            }
            return value
        }

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

            fun toMarginSpec(value: Any?): Map<String, Any> {
                val margins: List<Double?> = when (value) {
                    is Number -> listOf(value.toDouble())
                    is List<*> -> {
                        require(value.all { it == null || it is Number }) {
                            "The margins option requires a list of numbers, but was: $value."
                        }
                        value.map { (it as? Number)?.toDouble() }
                    }
                    else -> error("The margins option should be specified using number or list of numbers, but was: $value.")
                }

                val top: Double?
                val right: Double?
                val bottom: Double?
                val left: Double?

                when (margins.size) {
                    1 -> {
                        val margin = margins.single()
                        top = margin
                        right = margin
                        left = margin
                        bottom = margin
                    }
                    2 -> {
                        val (vMargin, hMargin) = margins
                        top = vMargin
                        bottom = vMargin
                        right = hMargin
                        left = hMargin
                    }
                    3 -> {
                        top = margins[0]
                        right = margins[1]
                        left = margins[1]
                        bottom = margins[2]
                    }
                    4 -> {
                        top = margins[0]
                        right = margins[1]
                        bottom = margins[2]
                        left = margins[3]
                    }
                    else -> {
                        error("The margins accept a number or a list of one, two, three or four numbers, but was: $value.")
                    }
                }
                return mapOf(
                    ThemeOption.Elem.Margin.TOP to top,
                    ThemeOption.Elem.Margin.RIGHT to right,
                    ThemeOption.Elem.Margin.BOTTOM to bottom,
                    ThemeOption.Elem.Margin.LEFT to left
                )
                    .filterValues { it != null }
                    .mapValues { (_, v) -> v as Any }
            }

            return when {
                key == PLOT_MARGIN -> toMarginSpec(value)
                value is Map<*, *> && value.containsKey(ThemeOption.Elem.MARGIN) -> {
                    val margins = toMarginSpec(value[ThemeOption.Elem.MARGIN])
                    // to keep other options
                    value - ThemeOption.Elem.MARGIN + margins
                }
                else -> {
                    value
                }
            }
        }
    }
}
