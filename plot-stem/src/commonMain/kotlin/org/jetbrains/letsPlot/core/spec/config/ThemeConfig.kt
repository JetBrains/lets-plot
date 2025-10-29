/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.config

import org.jetbrains.letsPlot.commons.intern.filterNotNullValues
import org.jetbrains.letsPlot.core.plot.base.theme.ExponentFormat
import org.jetbrains.letsPlot.core.plot.base.theme.FontFamilyRegistry
import org.jetbrains.letsPlot.core.plot.base.theme.Theme
import org.jetbrains.letsPlot.core.plot.base.theme.TitlePosition
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.DefaultTheme
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.ThemeUtil
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.Elem.BLANK
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.Elem.FILL
import org.jetbrains.letsPlot.core.spec.Option

class ThemeConfig constructor(
    themeOptions: Map<String, Any>,
    containerTheme: Theme?,
    fontFamilyRegistry: FontFamilyRegistry
) {

    val theme: Theme

    init {
        val userOptions: Map<String, Any> = mergeUserOptions(
            themeOptions.mapValues { (key, value) ->
                standardizeThemeOptionValue(key, value)
            },
            containerTheme
        )
        val themeName = userOptions.getOrElse(Option.Meta.NAME) { ThemeOption.Name.LP_MINIMAL }.toString()
        theme = ThemeUtil.buildTheme(themeName, userOptions, fontFamilyRegistry)
    }

    companion object {
        private fun standardizeThemeOptionValue(key: String, value: Any): Any {
            var value = convertElementBlank(value)
            value = convertMargins(key, value)
            value = convertInset(key, value)
            value = convertExponentFormat(key, value)
            value = convertTitlePosition(key, value)
            return LegendThemeConfig.convertValue(key, value)
        }

        private fun convertExponentFormat(key: String, value: Any): Any {
            fun toFormat(value: String): ExponentFormat.NotationType {
                val notationTypes = ExponentFormat.NotationType.entries.map { it.name.lowercase() to it }.toMap()
                return notationTypes[value] ?: throw IllegalArgumentException(
                    "Illegal value: '$value'.\n${ThemeOption.EXPONENT_FORMAT} expected value is a string: ${
                        notationTypes.keys.joinToString(
                            "|"
                        )
                    }."
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
                return ThemeOption.ELEMENT_BLANK
            }
            if (value is Map<*, *> && value["name"] == "blank") {
                return ThemeOption.ELEMENT_BLANK
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
                key in setOf(ThemeOption.PLOT_MARGIN, ThemeOption.LEGEND_MARGIN) -> toMarginSpec(value)
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
                key == ThemeOption.PANEL_INSET || key == ThemeOption.PLOT_INSET -> toInsetSpec(value)
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
                ThemeOption.PLOT_TITLE_POSITION, ThemeOption.PLOT_CAPTION_POSITION -> {
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

        private fun mergeUserOptions(
            ownThemeUserOptions: Map<String, Any>,
            containerTheme: Theme?
        ): Map<String, Any> {

            val containerThemeOptions = if (containerTheme is DefaultTheme) {
                when {
                    ownThemeUserOptions.isEmpty() -> {
                        // No own opinions - take all container options.
                        containerTheme.options
                    }

                    else -> {
                        // Take only a few container options.
                        containerTheme.options.filterKeys { key ->
                            key in setOf(
                                Option.Meta.NAME,                  // a name of a predefined theme.
                                ThemeOption.LEGEND_POSITION,       // for 'guide collect' in the container feature.
                                ThemeOption.LEGEND_JUSTIFICATION,
                                ThemeOption.LEGEND_DIRECTION,
                                ThemeOption.LEGEND_BOX_JUST,
                            )
                        }
                    }
                }

            } else {
                emptyMap()
            }

            // Plot background: preserve own border settings (color, size, linetype),
            // Keep the container's 'fill' color (if specified in container but not specified in own options).
            @Suppress("UNCHECKED_CAST")
            val ownBackgroundOptions =
                ownThemeUserOptions[ThemeOption.PLOT_BKGR_RECT] as? Map<String, Any> ?: emptyMap()
            val mergedBackgroundOptions: Map<String, Any?> =
                if (ownThemeUserOptions.containsKey(ThemeOption.FLAVOR)) {
                    // Keep own plot background.
                    ownBackgroundOptions
                } else if (ownThemeUserOptions.containsKey(ThemeOption.PLOT_BKGR_RECT)) {
                    // Keep own plot background color if specified.
                    mapOf(
                        BLANK to containerTheme?.let { !it.plot().showBackground() },
                        FILL to containerTheme?.plot()?.backgroundFill()
                    ) + ownBackgroundOptions
                } else if (containerTheme != null) {
                    // Inherit plot background color from the container.
                    mapOf(
                        BLANK to !containerTheme.plot().showBackground(),
                        FILL to containerTheme.plot().backgroundFill()
                    )
                } else {
                    emptyMap()
                }

            val mergedThemeOpotions = containerThemeOptions + ownThemeUserOptions + mapOf(
                ThemeOption.PLOT_BKGR_RECT to mergedBackgroundOptions.filterNotNullValues().ifEmpty { null },
            )


            return mergedThemeOpotions.filterNotNullValues()
        }
    }
}
