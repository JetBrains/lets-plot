/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.config

import org.jetbrains.letsPlot.core.plot.base.theme.ExponentFormat
import org.jetbrains.letsPlot.core.plot.base.theme.FontFamilyRegistry
import org.jetbrains.letsPlot.core.plot.base.theme.TitlePosition
import org.jetbrains.letsPlot.core.plot.base.theme.Theme
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.ThemeUtil
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.ELEMENT_BLANK
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.LEGEND_MARGIN
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.PANEL_INSET
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.PLOT_CAPTION_POSITION
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.PLOT_INSET
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.PLOT_MARGIN
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.PLOT_TITLE_POSITION
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
            value = convertInset(key, value)
            value = convertExponentFormat(key, value)
            value = convertTitlePosition(key, value)
            LegendThemeConfig.convertValue(key, value)
        }

        theme = ThemeUtil.buildTheme(themeName, userOptions, fontFamilyRegistry)
    }

    companion object {
        private fun convertExponentFormat(key: String, value: Any): Any {
            fun toFormat(value: String): ExponentFormat.NotationType {
                val notationTypes = ExponentFormat.NotationType.entries.map { it.name.lowercase() to it }.toMap()
                return notationTypes[value] ?: throw IllegalArgumentException(
                    "Illegal value: '$value'.\n${ThemeOption.EXPONENT_FORMAT} expected value is a string: ${notationTypes.keys.joinToString("|")}."
                )
            }
            if (key == ThemeOption.EXPONENT_FORMAT) {
                return when (value) {
                    is String -> toFormat(value)
                    is List<*> -> {
                        val format = value[0].let { toFormat(it.toString()) }
                        val minExponent = (value[1] as? Number?)?.toInt()
                        val maxExponent = (value[2] as? Number?)?.toInt()
                        ExponentFormat(format, minExponent, maxExponent)
                    }
                    else -> throw IllegalArgumentException(
                        "Illegal value: '$value'.\n${ThemeOption.EXPONENT_FORMAT} expected value is a string: e|pow|pow_full or tuple (format, min_exp, max_exp)."
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

        private fun toThickness(obj: Any?): List<Double?> {
            val thickness: List<Double?> = when (obj) {
                is Number -> listOf(obj.toDouble())
                is List<*> -> {
                    require(obj.all { it == null || it is Number }) {
                        "The option requires a list of numbers, but was: $obj."
                    }
                    obj.map { (it as? Number)?.toDouble() }
                }
                else -> error("The option should be specified using number or list of numbers, but was: $obj.")
            }

            val top: Double?
            val right: Double?
            val bottom: Double?
            val left: Double?

            when (thickness.size) {
                1 -> {
                    val value = thickness.single()
                    top = value
                    right = value
                    left = value
                    bottom = value
                }
                2 -> {
                    val (v, h) = thickness
                    top = v
                    bottom = v
                    right = h
                    left = h
                }
                3 -> {
                    top = thickness[0]
                    right = thickness[1]
                    left = thickness[1]
                    bottom = thickness[2]
                }
                4 -> {
                    top = thickness[0]
                    right = thickness[1]
                    bottom = thickness[2]
                    left = thickness[3]
                }
                else -> {
                    error("The option accept a number or a list of one, two, three or four numbers, but was: $obj.")
                }
            }

            return listOf(top, right, bottom, left)
        }

        private fun convertMargins(key: String, value: Any): Any {
            fun toMarginSpec(value: Any?): Map<String, Any> {
                val (top, right, bottom, left) = toThickness(value)

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
                key == PLOT_MARGIN || key == LEGEND_MARGIN -> toMarginSpec(value)
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

        private fun convertInset(key: String, value: Any): Any {
            fun toInsetSpec(value: Any?): Map<String, Any> {
                val (top, right, bottom, left) = toThickness(value)

                return mapOf(
                    ThemeOption.Elem.Inset.TOP to top,
                    ThemeOption.Elem.Inset.RIGHT to right,
                    ThemeOption.Elem.Inset.BOTTOM to bottom,
                    ThemeOption.Elem.Inset.LEFT to left
                )
                    .filterValues { it != null }
                    .mapValues { (_, v) -> v as Any }
            }

            return when {
                key == PANEL_INSET || key == PLOT_INSET -> toInsetSpec(value)
                value is Map<*, *> && value.containsKey(ThemeOption.Elem.INSET) -> {
                    val inset = toInsetSpec(value[ThemeOption.Elem.INSET])
                    // to keep other options
                    value - ThemeOption.Elem.INSET + inset
                }
                else -> value
            }
        }

        private fun convertTitlePosition(key: String, value: Any): Any {
            return when (key) {
                PLOT_TITLE_POSITION, PLOT_CAPTION_POSITION -> {
                    when (value) {
                        "panel" -> TitlePosition.PANEL
                        "plot" -> TitlePosition.PLOT
                        else -> throw IllegalArgumentException(
                            "Illegal value: '$value', $key. Expected values are: 'panel' or 'plot'."
                        )
                    }
                }
                else -> value
            }
        }
    }
}
