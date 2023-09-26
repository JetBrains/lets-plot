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

    fun applyFlavor(themeSettings: Map<String, Any>, flavorName: String): Map<String, Any> {
        val flavor = createFlavor(flavorName)

        return themeSettings.mapValues { (parameter, options) ->
            if (options !is Map<*, *>) {
                return@mapValues options
            }
            options.mapValues { (key, value) ->
                when (value) {
                    !is SymbolicColor -> value
                    else -> flavor.symbolicColors[value]
                        ?: error("Undefined color in flavor scheme = '$flavorName': '$parameter': '${key}' = '${value.name}'")

                }
            }
        }
            .mergeWith(flavor.specialColors)
    }

    enum class SymbolicColor {
        WHITE,
        BLACK,

        GREY_1,  // 'panel_grid' in themes
        GREY_2,  // facet 'strip_background'
        GREY_3,  // 'panel_background'
        GREY_4,  // borders in 'light' and 'bw' theme
        ;
    }

    private class Flavor(
        val symbolicColors: Map<SymbolicColor, Color>,
        val specialColors: Map<String, Map<String, Color>>
    )

    private val DARK_GREY = parseHex("#474747")
    private val LIGHT_GREY = parseHex("#E9E9E9")
    private val GREY85 = parseHex("#D9D9D9")

    private fun createFlavor(name: String): Flavor {
        return when (name) {
            ThemeOption.Flavor.BASE -> Flavor(
                symbolicColors = mapOf(
                    SymbolicColor.WHITE to Color.WHITE,
                    SymbolicColor.BLACK to DARK_GREY,
                    SymbolicColor.GREY_1 to LIGHT_GREY,
                    SymbolicColor.GREY_2 to LIGHT_GREY,
                    SymbolicColor.GREY_3 to LIGHT_GREY,
                    SymbolicColor.GREY_4 to LIGHT_GREY,
                ),
                specialColors = mapOf(
                    ThemeOption.TOOLTIP_RECT to mapOf(
                        Elem.COLOR to Color.BLACK,
                        Elem.FILL to Color.WHITE
                    ),
                    ThemeOption.RECT to mapOf(
                        Elem.FILL to LIGHT_GREY
                    ),
                )
            )

            ThemeOption.Flavor.GREY -> Flavor(
                symbolicColors = mapOf(
                    SymbolicColor.WHITE to Color.WHITE,
                    SymbolicColor.BLACK to DARK_GREY,

                    SymbolicColor.GREY_1 to LIGHT_GREY,
                    SymbolicColor.GREY_2 to GREY85,
                    SymbolicColor.GREY_3 to parseHex("#EBEBEB"),
                    SymbolicColor.GREY_4 to DARK_GREY,
                ),
                specialColors = mapOf(
                    ThemeOption.TOOLTIP_RECT to mapOf(
                        Elem.COLOR to Color.BLACK,
                        Elem.FILL to Color.WHITE
                    ),
                )
            )

            ThemeOption.Flavor.LIGHT -> Flavor(
                symbolicColors = mapOf(
                    SymbolicColor.WHITE to Color.WHITE,
                    SymbolicColor.BLACK to DARK_GREY,

                    SymbolicColor.GREY_1 to LIGHT_GREY,
                    SymbolicColor.GREY_2 to GREY85,
                    SymbolicColor.GREY_3 to Color.WHITE,
                    SymbolicColor.GREY_4 to parseHex("#C9C9C9"),
                ),
                specialColors = mapOf(
                    ThemeOption.TOOLTIP_RECT to mapOf(
                        Elem.COLOR to Color.BLACK,
                        Elem.FILL to Color.WHITE
                    ),
                    ThemeOption.AXIS to mapOf(
                        Elem.COLOR to parseHex("#C9C9C9")
                    ),
                    ThemeOption.PANEL_BKGR_RECT to mapOf(
                        Elem.COLOR to parseHex("#C9C9C9")
                    ),
                )
            )

            ThemeOption.Flavor.BW -> Flavor(
                symbolicColors = mapOf(
                    SymbolicColor.WHITE to Color.WHITE,
                    SymbolicColor.BLACK to DARK_GREY,

                    SymbolicColor.GREY_1 to LIGHT_GREY,
                    SymbolicColor.GREY_2 to GREY85,
                    SymbolicColor.GREY_3 to Color.WHITE,
                    SymbolicColor.GREY_4 to parseHex("#333333"),
                ),
                specialColors = mapOf(
                    ThemeOption.TOOLTIP_RECT to mapOf(
                        Elem.COLOR to Color.BLACK,
                        Elem.FILL to Color.WHITE
                    ),
                    ThemeOption.PANEL_BKGR_RECT to mapOf(
                        Elem.COLOR to parseHex("#333333")
                    ),
                    ThemeOption.AXIS to mapOf(
                        Elem.COLOR to parseHex("#333333")
                    ),
                    ThemeOption.FACET_STRIP_BGR_RECT to mapOf(
                        Elem.COLOR to parseHex("#333333")
                    ),
                )
            )

            ThemeOption.Flavor.DARCULA -> Flavor(
                symbolicColors = mapOf(
                    SymbolicColor.WHITE to parseHex("#303030"),
                    SymbolicColor.BLACK to parseHex("#BBBBBB"),
                    SymbolicColor.GREY_1 to parseHex("#474747"),
                    SymbolicColor.GREY_2 to parseHex("#363636"),
                    SymbolicColor.GREY_3 to parseHex("#3B3B3B"),
                    SymbolicColor.GREY_4 to parseHex("#BBBBBB"),
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
                    SymbolicColor.BLACK to parseHex("#2E4E58"),
                    SymbolicColor.GREY_1 to parseHex("#D7D4CB"),
                    SymbolicColor.GREY_2 to parseHex("#E6DFCA"),
                    SymbolicColor.GREY_3 to parseHex("#EEE8D5"),
                    SymbolicColor.GREY_4 to parseHex("#2E4E58"),
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
                    SymbolicColor.BLACK to parseHex("#A7B6BA"),
                    SymbolicColor.GREY_1 to parseHex("#455458"),
                    SymbolicColor.GREY_2 to parseHex("#1F4650"),
                    SymbolicColor.GREY_3 to parseHex("#1B4854"),
                    SymbolicColor.GREY_4 to parseHex("#A7B6BA"),
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
                    SymbolicColor.BLACK to Color.BLACK,
                    SymbolicColor.GREY_1 to parseHex("#E9E9E9"),
                    SymbolicColor.GREY_2 to parseHex("#D9D9D9"),
                    SymbolicColor.GREY_3 to parseHex("#EBEBEB"),
                    SymbolicColor.GREY_4 to Color.BLACK,
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
                    SymbolicColor.BLACK to Color.WHITE,
                    SymbolicColor.GREY_1 to parseHex("#474747"),
                    SymbolicColor.GREY_2 to parseHex("#363636"),
                    SymbolicColor.GREY_3 to parseHex("#3B3B3B"),
                    SymbolicColor.GREY_4 to Color.WHITE,
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