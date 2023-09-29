/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.defaultTheme

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.commons.values.Color.Companion.parseHex
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.Elem

internal class ThemeFlavor private constructor(
    val symbolicColors: Map<SymbolicColor, Color>,
    val specialColors: Map<String, Map<String, Color>>,
    val pen: Color,
    val brush: Color,
    val paper: Color,
) {
    companion object {

        // symbolic colors
        enum class SymbolicColor {
            WHITE,
            BLACK,

            GREY_1,  // 'panel_grid' in themes
            GREY_2,  // facet 'strip_background'
            GREY_3,  // 'panel_background'
            GREY_4,  // borders in 'light' and 'bw' theme
            ;
        }

        private val DARK_GREY = parseHex("#474747")
        private val LIGHT_GREY = parseHex("#E9E9E9")
        private val GREY85 = parseHex("#D9D9D9")

        internal fun forName(name: String): ThemeFlavor {
            return when (name) {
                ThemeOption.Flavor.BASE -> ThemeFlavor(
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
                            Elem.COLOR to DARK_GREY,
                            Elem.FILL to Color.WHITE
                        ),
                        ThemeOption.RECT to mapOf(
                            Elem.FILL to LIGHT_GREY
                        ),
                    ),
                    pen = DARK_GREY,
                    brush = Color.PACIFIC_BLUE,
                    paper = Color.WHITE
                )

                ThemeOption.Flavor.GREY -> ThemeFlavor(
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
                            Elem.COLOR to DARK_GREY,
                            Elem.FILL to Color.WHITE
                        )
                    ),
                    pen = DARK_GREY,
                    brush = Color.PACIFIC_BLUE,
                    paper = Color.WHITE
                )

                ThemeOption.Flavor.LIGHT -> ThemeFlavor(
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
                            Elem.COLOR to DARK_GREY,
                            Elem.FILL to Color.WHITE
                        )
                    ),
                    pen = DARK_GREY,
                    brush = Color.PACIFIC_BLUE,
                    paper = Color.WHITE
                )

                ThemeOption.Flavor.BW -> ThemeFlavor(
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
                            Elem.COLOR to DARK_GREY,
                            Elem.FILL to Color.WHITE
                        )
                    ),
                    pen = DARK_GREY,
                    brush = Color.PACIFIC_BLUE,
                    paper = Color.WHITE
                )

                ThemeOption.Flavor.DARCULA -> ThemeFlavor(
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
                        )
                    ),
                    pen = parseHex("#BBBBBB"),
                    brush = Color.PACIFIC_BLUE,
                    paper = parseHex("#303030")
                )

                ThemeOption.Flavor.SOLARIZED_LIGHT -> ThemeFlavor(
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
                        )
                    ),
                    pen = parseHex("#2E4E58"),
                    brush = Color.PACIFIC_BLUE,
                    paper = parseHex("#FDF6E3")
                )

                ThemeOption.Flavor.SOLARIZED_DARK -> ThemeFlavor(
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
                        )
                    ),
                    pen = parseHex("#A7B6BA"),
                    brush = Color.PACIFIC_BLUE,
                    paper = parseHex("#0E3C4A")
                )

                ThemeOption.Flavor.HIGH_CONTRAST_LIGHT -> ThemeFlavor(
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
                        )
                    ),
                    pen = Color.BLACK,
                    brush = Color.PACIFIC_BLUE,
                    paper = Color.WHITE
                )

                ThemeOption.Flavor.HIGH_CONTRAST_DARK -> ThemeFlavor(
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
                        )
                    ),
                    pen = Color.WHITE,
                    brush = Color.PACIFIC_BLUE,
                    paper = Color.BLACK
                )

                else -> throw IllegalArgumentException("Unsupported theme flavor: '$name'")
            }
        }
    }
}