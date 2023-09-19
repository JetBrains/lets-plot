/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.defaultTheme

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.commons.values.Color.Companion.parseHex
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.Elem
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeValues.Companion.mergeWith

object ThemeFlavorUtil {

    fun Map<String, Any>.applyFlavor(flavorName: String): Map<String, Any> {
        val flavor = createFlavor(flavorName)

        return mapValues { (parameter, options) ->
            if (options is Map<*, *>) {
                options.mapValues { (key, value) ->
                    if (value is SymbolicColor) {
                        val color = flavor.symbolicColors[value]
                        requireNotNull(color) { "Undefined color in flavor scheme = '$flavorName': '$parameter': '${key}' = '${value.name}'" }
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

    enum class SymbolicColor {
        WHITE,

        LIGHT_GRAY_1,  // 'panel_grid' in themes
        LIGHT_GRAY_2,  // 'rect' fill in 'none' theme

        GRAY_1,        // 'panel_background' in 'grey' theme
        GRAY_2,        // facet 'strip_background'
        GRAY_3,        // panel_border and axis in 'light' theme

        DARK_GRAY;
    }

    private class Flavor(
        val symbolicColors: Map<SymbolicColor, Color>,
        val specialColors: Map<String, Map<String, Color>>
    )

    private fun createFlavor(name: String): Flavor {
        return when (name) {
            ThemeOption.Flavor.BASE -> Flavor(
                symbolicColors = mapOf(
                    SymbolicColor.WHITE to Color.WHITE,

                    SymbolicColor.LIGHT_GRAY_1 to parseHex("#E9E9E9"),
                    SymbolicColor.LIGHT_GRAY_2 to parseHex("#E9E9E9"),

                    SymbolicColor.GRAY_1 to parseHex("#EBEBEB"),
                    SymbolicColor.GRAY_2 to parseHex("#D9D9D9"),
                    SymbolicColor.GRAY_3 to parseHex("#C9C9C9"),

                    SymbolicColor.DARK_GRAY to parseHex("#474747"),
                ),
                specialColors = mapOf(
                    ThemeOption.TOOLTIP_RECT to mapOf(
                        Elem.COLOR to Color.BLACK,
                        Elem.FILL to Color.WHITE
                    ),
                )
            )

            ThemeOption.Flavor.DARCULA -> Flavor(
                symbolicColors = mapOf(
                    SymbolicColor.WHITE to parseHex("#303030"),

                    SymbolicColor.LIGHT_GRAY_1 to parseHex("#474747"),
                    SymbolicColor.LIGHT_GRAY_2 to parseHex("#303030"),

                    SymbolicColor.GRAY_1 to parseHex("#3B3B3B"),
                    SymbolicColor.GRAY_2 to parseHex("#363636"),
                    SymbolicColor.GRAY_3 to parseHex("#BBBBBB"),

                    SymbolicColor.DARK_GRAY to parseHex("#BBBBBB"),
                ),
                specialColors = mapOf(
                    ThemeOption.TOOLTIP_RECT to mapOf(
                        Elem.COLOR to parseHex("#BBBBBB"),
                        Elem.FILL to parseHex("#141414")
                    ),
                )
            )

            ThemeOption.Flavor.SOLARIZED_LIGHT -> Flavor(
                symbolicColors = mapOf(
                    SymbolicColor.WHITE to parseHex("#FDF6E3"),

                    SymbolicColor.LIGHT_GRAY_1 to parseHex("#D7D4CB"),
                    SymbolicColor.LIGHT_GRAY_2 to parseHex("#FDF6E3"),

                    SymbolicColor.GRAY_1 to parseHex("#EEE8D5"),
                    SymbolicColor.GRAY_2 to parseHex("#E6DFCA"),
                    SymbolicColor.GRAY_3 to parseHex("#2E4E58"),

                    SymbolicColor.DARK_GRAY to parseHex("#2E4E58"),
                ),
                specialColors = mapOf(
                    ThemeOption.TOOLTIP_RECT to mapOf(
                        Elem.COLOR to parseHex("#2E4E58"),
                        Elem.FILL to parseHex("#FEFBF3")
                    ),
                )
            )

            ThemeOption.Flavor.SOLARIZED_DARK -> Flavor(
                symbolicColors = mapOf(
                    SymbolicColor.WHITE to parseHex("#0E3C4A"),

                    SymbolicColor.LIGHT_GRAY_1 to parseHex("#455458"),
                    SymbolicColor.LIGHT_GRAY_2 to parseHex("#0E3C4A"),

                    SymbolicColor.GRAY_1 to parseHex("#1B4854"),
                    SymbolicColor.GRAY_2 to parseHex("#1F4650"),
                    SymbolicColor.GRAY_3 to parseHex("#A7B6BA"),

                    SymbolicColor.DARK_GRAY to parseHex("#A7B6BA"),
                ),
                specialColors = mapOf(
                    ThemeOption.TOOLTIP_RECT to mapOf(
                        Elem.COLOR to parseHex("#A7B6BA"),
                        Elem.FILL to parseHex("#0B2F3A")
                    ),
                )
            )

            ThemeOption.Flavor.HIGH_CONTRAST_LIGHT -> Flavor(
                symbolicColors = mapOf(
                    SymbolicColor.WHITE to Color.WHITE,

                    SymbolicColor.LIGHT_GRAY_1 to parseHex("#E9E9E9"),
                    SymbolicColor.LIGHT_GRAY_2 to Color.WHITE,

                    SymbolicColor.GRAY_1 to parseHex("#EBEBEB"),
                    SymbolicColor.GRAY_2 to parseHex("#D9D9D9"),
                    SymbolicColor.GRAY_3 to Color.BLACK,

                    SymbolicColor.DARK_GRAY to Color.BLACK,
                ),
                specialColors = mapOf(
                    ThemeOption.TOOLTIP_RECT to mapOf(
                        Elem.COLOR to Color.BLACK,
                        Elem.FILL to Color.WHITE
                    ),
                )
            )

            ThemeOption.Flavor.HIGH_CONTRAST_DARK -> Flavor(
                symbolicColors = mapOf(
                    SymbolicColor.WHITE to Color.BLACK,

                    SymbolicColor.LIGHT_GRAY_1 to parseHex("#474747"),
                    SymbolicColor.LIGHT_GRAY_2 to Color.BLACK,

                    SymbolicColor.GRAY_1 to parseHex("#3B3B3B"),
                    SymbolicColor.GRAY_2 to parseHex("#363636"),
                    SymbolicColor.GRAY_3 to Color.WHITE,

                    SymbolicColor.DARK_GRAY to Color.WHITE,
                ),
                specialColors = mapOf(
                    ThemeOption.TOOLTIP_RECT to mapOf(
                        Elem.COLOR to Color.WHITE,
                        Elem.FILL to parseHex("#460073")
                    ),
                )
            )

            else -> throw IllegalArgumentException("Unsupported theme flavor: '$name'")
        }
    }
}