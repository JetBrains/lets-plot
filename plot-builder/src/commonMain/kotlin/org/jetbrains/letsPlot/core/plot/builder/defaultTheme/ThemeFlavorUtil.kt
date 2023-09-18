/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.defaultTheme

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.commons.values.Color.Companion.parseHex
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.Elem
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.Flavor
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeValues.Companion.mergeWith

object ThemeFlavorUtil {

    fun Map<String, Any>.applyFlavor(flavorName: String): Map<String, Any> {
        val flavor = forName(flavorName)

        return mapValues { (parameter, options) ->
            if (options is Map<*, *>) {
                options.mapValues { (key, value) ->
                    if (value is SymbolicColors) {
                        val color = flavor.symbolicColors[value]
                        require(color != null) { "Undefined color: '$parameter': '${key}' = '${value.name}'" }
                        color
                    } else {
                        value
                    }
                }
            } else {
                options
            }
        }
            .mergeWith(flavor.specialColors)
    }

    enum class SymbolicColors {
        WHITE,
        BLACK,

        LIGHT_GRAY_1,  // 'panel_grid' in themes
        LIGHT_GRAY_2,  // 'rect' fill in 'none' theme

        GRAY_1,        // 'panel_background' in 'grey' theme
        GRAY_2,        // facet 'strip_background'
        GRAY_3,        // panel_border and axis in 'light' theme

        DARK_GRAY_1,   // base DARK_GRAY
        DARK_GRAY_2;   // base color in 'bw' theme
    }

    private class ThemeFlavor(
        val symbolicColors: Map<SymbolicColors, Color>,
        val specialColors: Map<String, Map<String, Color>>
    )

    private fun forName(flavor: String): ThemeFlavor {
        return when (flavor) {
            Flavor.BASE -> ThemeFlavor(
                symbolicColors = mapOf(
                    SymbolicColors.WHITE to Color.WHITE,
                    SymbolicColors.BLACK to Color.BLACK,

                    SymbolicColors.LIGHT_GRAY_1 to parseHex("#E9E9E9"),
                    SymbolicColors.LIGHT_GRAY_2 to parseHex("#E9E9E9"),

                    SymbolicColors.GRAY_1 to parseHex("#EBEBEB"),   // Gray92
                    SymbolicColors.GRAY_2 to parseHex("#D9D9D9"),   // Gray85
                    SymbolicColors.GRAY_3 to parseHex("#C9C9C9"),   // Gray79

                    SymbolicColors.DARK_GRAY_1 to parseHex("#474747"), // Gray28
                    SymbolicColors.DARK_GRAY_2 to parseHex("#333333")  // Gray20
                ),
                specialColors = emptyMap()
            )

            Flavor.DARCULA -> ThemeFlavor(
                symbolicColors = mapOf(
                    SymbolicColors.WHITE to parseHex("#303030"),
                    SymbolicColors.BLACK to parseHex("#BBBBBB"),

                    SymbolicColors.LIGHT_GRAY_1 to parseHex("#474747"),
                    SymbolicColors.LIGHT_GRAY_2 to parseHex("#3B3B3B"),

                    SymbolicColors.GRAY_1 to parseHex("#3B3B3B"),
                    SymbolicColors.GRAY_2 to parseHex("#363636"),
                    SymbolicColors.GRAY_3 to parseHex("#BBBBBB"),

                    SymbolicColors.DARK_GRAY_1 to parseHex("#BBBBBB"),
                    SymbolicColors.DARK_GRAY_2 to parseHex("#BBBBBB"),
                ),
                specialColors = mapOf(
                    ThemeOption.TOOLTIP_RECT to mapOf(
                        Elem.FILL to parseHex("#141414")
                    ),
                )
            )

            Flavor.SOLARIZED_LIGHT -> ThemeFlavor(
                symbolicColors = mapOf(
                    SymbolicColors.WHITE to parseHex("#FDF6E3"),
                    SymbolicColors.BLACK to parseHex("#2E4E58"),

                    SymbolicColors.LIGHT_GRAY_1 to parseHex("#D7D4CB"),
                    SymbolicColors.LIGHT_GRAY_2 to parseHex("#EEE8D5"),

                    SymbolicColors.GRAY_1 to parseHex("#EEE8D5"),
                    SymbolicColors.GRAY_2 to parseHex("#E6DFCA"),
                    SymbolicColors.GRAY_3 to parseHex("#2E4E58"),

                    SymbolicColors.DARK_GRAY_1 to parseHex("#2E4E58"),
                    SymbolicColors.DARK_GRAY_2 to parseHex("#2E4E58"),
                ),
                specialColors = mapOf(
                    ThemeOption.TOOLTIP_RECT to mapOf(
                        Elem.FILL to parseHex("#FEFBF3")
                    ),
                )
            )

            Flavor.SOLARIZED_DARK -> ThemeFlavor(
                symbolicColors = mapOf(
                    SymbolicColors.WHITE to parseHex("#0E3C4A"),
                    SymbolicColors.BLACK to parseHex("#A7B6BA"),

                    SymbolicColors.LIGHT_GRAY_1 to parseHex("#455458"),
                    SymbolicColors.LIGHT_GRAY_2 to parseHex("#1B4854"),

                    SymbolicColors.GRAY_1 to parseHex("#1B4854"),
                    SymbolicColors.GRAY_2 to parseHex("#1F4650"),
                    SymbolicColors.GRAY_3 to parseHex("#A7B6BA"),

                    SymbolicColors.DARK_GRAY_1 to parseHex("#A7B6BA"),
                    SymbolicColors.DARK_GRAY_2 to parseHex("#A7B6BA"),
                ),
                specialColors = mapOf(
                    ThemeOption.TOOLTIP_RECT to mapOf(
                        Elem.FILL to parseHex("#0B2F3A")
                    ),
                )
            )

            Flavor.HIGH_CONTRAST_LIGHT -> ThemeFlavor(
                symbolicColors = mapOf(
                    SymbolicColors.WHITE to Color.WHITE,
                    SymbolicColors.BLACK to Color.BLACK,

                    SymbolicColors.LIGHT_GRAY_1 to parseHex("#E9E9E9"),
                    SymbolicColors.LIGHT_GRAY_2 to parseHex("#EBEBEB"),

                    SymbolicColors.GRAY_1 to parseHex("#EBEBEB"),
                    SymbolicColors.GRAY_2 to parseHex("#D9D9D9"),
                    SymbolicColors.GRAY_3 to Color.BLACK,

                    SymbolicColors.DARK_GRAY_1 to Color.BLACK,
                    SymbolicColors.DARK_GRAY_2 to Color.BLACK,
                ),
                specialColors = mapOf(
                    ThemeOption.TOOLTIP_RECT to mapOf(
                        Elem.FILL to Color.WHITE
                    ),
                )
            )

            Flavor.HIGH_CONTRAST_DARK -> ThemeFlavor(
                symbolicColors = mapOf(
                    SymbolicColors.WHITE to Color.BLACK,
                    SymbolicColors.BLACK to Color.WHITE,

                    SymbolicColors.LIGHT_GRAY_1 to parseHex("#474747"),
                    SymbolicColors.LIGHT_GRAY_2 to parseHex("#3B3B3B"),

                    SymbolicColors.GRAY_1 to parseHex("#3B3B3B"),
                    SymbolicColors.GRAY_2 to parseHex("#363636"),
                    SymbolicColors.GRAY_3 to Color.WHITE,

                    SymbolicColors.DARK_GRAY_1 to Color.WHITE,
                    SymbolicColors.DARK_GRAY_2 to Color.WHITE,
                ),
                specialColors = mapOf(
                    ThemeOption.TOOLTIP_RECT to mapOf(
                        Elem.FILL to parseHex("#460073")
                    ),
                )
            )

            else -> throw IllegalArgumentException("Unsupported theme flavor: '$flavor'")
        }
    }
}