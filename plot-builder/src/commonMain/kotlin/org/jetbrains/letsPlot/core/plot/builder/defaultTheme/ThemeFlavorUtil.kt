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
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.GEOM
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.PAPER
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.PEN

object ThemeFlavorUtil {

    fun Map<String, Any>.applyFlavor(flavorName: String): Map<String, Any> {
        val flavor = forName(flavorName)

        fun chooseNewColor(
            elementName: String,
            elementColor: Any?,
            isFillColor: Boolean,
        ): Color {
            if (elementColor == PEN) return flavor.color
            if (elementColor == PAPER) return flavor.fill

            return flavor.specialColors[elementName]?.get(if (isFillColor) Elem.FILL else Elem.COLOR)
                ?: if (isFillColor) flavor.fill else flavor.color
        }

        // set named colors + apply flavor colors to theme options
        return mapOf(
            GEOM to mapOf(
                ThemeOption.Geom.PEN to flavor.color,
                ThemeOption.Geom.PAPER to flavor.fill,
                ThemeOption.Geom.BRUSH to Color.PACIFIC_BLUE
            )
        ) + mapValues { (key, value) ->
            if (value is Map<*, *>) {
                val updated = value.toMutableMap()
                if (key in ELEM_TO_UPDATE_COLOR) {
                    updated[Elem.COLOR] = chooseNewColor(key, value[Elem.COLOR], isFillColor = false)
                }
                if (key in ELEM_TO_UPDATE_FILL) {
                    updated[Elem.FILL] = chooseNewColor(key, value[Elem.FILL], isFillColor = true)
                }
                if (key == ThemeOption.TOOLTIP_RECT) {
                    flavor.specialColors[key]?.get(Elem.FILL)?.let { updated[Elem.FILL] = it }
                }
                updated
            } else {
                value
            }
        }
    }

    private val ELEM_TO_UPDATE_COLOR = listOf(
        ThemeOption.LINE,
        ThemeOption.RECT,

        ThemeOption.PANEL_GRID,
        ThemeOption.PANEL_BKGR_RECT,
        ThemeOption.PANEL_BORDER_RECT,
        ThemeOption.FACET_STRIP_BGR_RECT,

        ThemeOption.AXIS,

        ThemeOption.AXIS_TOOLTIP,
        ThemeOption.TOOLTIP_RECT,

        ThemeOption.TEXT,
        ThemeOption.TITLE,
    )

    private val ELEM_TO_UPDATE_FILL = listOf(
        ThemeOption.RECT,
        ThemeOption.PLOT_BKGR_RECT,
        ThemeOption.PANEL_BKGR_RECT,
        ThemeOption.FACET_STRIP_BGR_RECT,
        ThemeOption.LEGEND_BKGR_RECT,
        ThemeOption.AXIS_TOOLTIP,
        ThemeOption.TOOLTIP_RECT,
    )

    private class ThemeFlavor(
        val fill: Color,
        val color: Color,
        val specialColors: Map<String, Map<String, Color>>
    )

    private fun forName(flavor: String): ThemeFlavor {
        val DARK_GREY: Color = parseHex("#474747")
        val LIGHT_GREY: Color = parseHex("#E9E9E9")
        val STRIP_BACKGROUND: Color = parseHex("#D9D9D9")

        return when (flavor) {
            Flavor.BASE -> ThemeFlavor(
                fill = Color.WHITE,
                color = DARK_GREY,
                specialColors = mapOf(
                    ThemeOption.RECT to mapOf(
                        Elem.FILL to LIGHT_GREY
                    ),
                    ThemeOption.TOOLTIP_RECT to mapOf(
                        Elem.COLOR to Color.BLACK
                    ),
                )
            )

            Flavor.MINIMAL -> ThemeFlavor(
                fill = Color.WHITE,
                color = DARK_GREY,
                specialColors = mapOf(
                    ThemeOption.RECT to mapOf(
                        Elem.FILL to LIGHT_GREY
                    ),
                    ThemeOption.TOOLTIP_RECT to mapOf(
                        Elem.COLOR to Color.BLACK
                    ),
                    ThemeOption.PANEL_GRID to mapOf(
                        Elem.COLOR to LIGHT_GREY
                    ),
                    ThemeOption.PANEL_BKGR_RECT to mapOf(
                        Elem.FILL to LIGHT_GREY
                    ),
                    ThemeOption.FACET_STRIP_BGR_RECT to mapOf(
                        Elem.FILL to LIGHT_GREY
                    ),
                )
            )

            Flavor.LIGHT -> ThemeFlavor(
                fill = Color.WHITE,
                color = DARK_GREY,
                specialColors = mapOf(
                    ThemeOption.RECT to mapOf(
                        Elem.FILL to LIGHT_GREY
                    ),
                    ThemeOption.TOOLTIP_RECT to mapOf(
                        Elem.COLOR to Color.BLACK
                    ),
                    ThemeOption.AXIS to mapOf(
                        Elem.COLOR to parseHex("#C9C9C9")
                    ),
                    ThemeOption.PANEL_GRID to mapOf(
                        Elem.COLOR to LIGHT_GREY
                    ),
                    ThemeOption.PANEL_BKGR_RECT to mapOf(
                        Elem.COLOR to parseHex("#C9C9C9")
                    ),
                    ThemeOption.FACET_STRIP_BGR_RECT to mapOf(
                        Elem.FILL to STRIP_BACKGROUND
                    ),
                )
            )

            Flavor.GREY -> ThemeFlavor(
                fill = Color.WHITE,
                color = DARK_GREY,
                specialColors = mapOf(
                    ThemeOption.RECT to mapOf(
                        Elem.FILL to LIGHT_GREY
                    ),
                    ThemeOption.TOOLTIP_RECT to mapOf(
                        Elem.COLOR to Color.BLACK
                    ),
                    ThemeOption.AXIS to mapOf(
                        Elem.COLOR to DARK_GREY
                    ),
                    ThemeOption.PANEL_BKGR_RECT to mapOf(
                        Elem.FILL to parseHex("#EBEBEB")
                    ),
                    ThemeOption.FACET_STRIP_BGR_RECT to mapOf(
                        Elem.FILL to STRIP_BACKGROUND
                    ),
                )
            )

            Flavor.BW -> ThemeFlavor(
                fill = Color.WHITE,
                color = DARK_GREY,
                specialColors = mapOf(
                    ThemeOption.RECT to mapOf(
                        Elem.FILL to LIGHT_GREY
                    ),
                    ThemeOption.TOOLTIP_RECT to mapOf(
                        Elem.COLOR to Color.BLACK
                    ),
                    ThemeOption.AXIS to mapOf(
                        Elem.COLOR to parseHex("#333333")
                    ),
                    ThemeOption.PANEL_GRID to mapOf(
                        Elem.COLOR to LIGHT_GREY
                    ),
                    ThemeOption.PANEL_BKGR_RECT to mapOf(
                        Elem.COLOR to parseHex("#333333")
                    ),
                    ThemeOption.FACET_STRIP_BGR_RECT to mapOf(
                        Elem.FILL to STRIP_BACKGROUND,
                        Elem.COLOR to parseHex("#333333"),
                    ),
                )
            )

            Flavor.DARCULA -> ThemeFlavor(
                fill = parseHex("#303030"),
                color = parseHex("#BBBBBB"),
                specialColors = mapOf(
                    ThemeOption.PANEL_BKGR_RECT to mapOf(
                        Elem.FILL to parseHex("#3B3B3B")
                    ),
                    ThemeOption.FACET_STRIP_BGR_RECT to mapOf(
                        Elem.FILL to parseHex("#363636")
                    ),
                    ThemeOption.PANEL_GRID to mapOf(
                        Elem.COLOR to parseHex("#474747")
                    ),
                    ThemeOption.TOOLTIP_RECT to mapOf(
                        Elem.FILL to parseHex("#141414")
                    ),
                    ThemeOption.AXIS_TOOLTIP to mapOf(
                        Elem.FILL to parseHex("#BBBBBB")
                    ),
                )
            )

            Flavor.SOLARIZED_LIGHT -> ThemeFlavor(
                fill = parseHex("#FDF6E3"),
                color = parseHex("#2E4E58"),
                specialColors = mapOf(
                    ThemeOption.PANEL_BKGR_RECT to mapOf(
                        Elem.FILL to parseHex("#EEE8D5")
                    ),
                    ThemeOption.FACET_STRIP_BGR_RECT to mapOf(
                        Elem.FILL to parseHex("#E6DFCA")
                    ),
                    ThemeOption.PANEL_GRID to mapOf(
                        Elem.COLOR to parseHex("#D7D4CB")
                    ),
                    ThemeOption.TOOLTIP_RECT to mapOf(
                        Elem.FILL to parseHex("#FEFBF3")
                    ),
                    ThemeOption.AXIS_TOOLTIP to mapOf(
                        Elem.FILL to parseHex("#2E4E58")
                    ),
                )
            )

            Flavor.SOLARIZED_DARK -> ThemeFlavor(
                fill = parseHex("#0E3C4A"),
                color = parseHex("#A7B6BA"),
                specialColors = mapOf(
                    ThemeOption.PANEL_BKGR_RECT to mapOf(
                        Elem.FILL to parseHex("#1B4854")
                    ),
                    ThemeOption.FACET_STRIP_BGR_RECT to mapOf(
                        Elem.FILL to parseHex("#1F4650")
                    ),
                    ThemeOption.PANEL_GRID to mapOf(
                        Elem.COLOR to parseHex("#455458")
                    ),
                    ThemeOption.TOOLTIP_RECT to mapOf(
                        Elem.FILL to parseHex("#0B2F3A")
                    ),
                    ThemeOption.AXIS_TOOLTIP to mapOf(
                        Elem.FILL to parseHex("#A7B6BA")
                    ),
                )
            )

            Flavor.HIGH_CONTRAST_LIGHT -> ThemeFlavor(
                fill = Color.WHITE,
                color = Color.BLACK,
                specialColors = mapOf(
                    ThemeOption.PANEL_BKGR_RECT to mapOf(
                        Elem.FILL to parseHex("#EBEBEB")
                    ),
                    ThemeOption.FACET_STRIP_BGR_RECT to mapOf(
                        Elem.FILL to parseHex("#D9D9D9")
                    ),
                    ThemeOption.PANEL_GRID to mapOf(
                        Elem.COLOR to parseHex("#E9E9E9")
                    ),
                    ThemeOption.TOOLTIP_RECT to mapOf(
                        Elem.FILL to Color.WHITE
                    ),
                    ThemeOption.AXIS_TOOLTIP to mapOf(
                        Elem.FILL to Color.BLACK
                    ),
                )
            )

            Flavor.HIGH_CONTRAST_DARK -> ThemeFlavor(
                fill = Color.BLACK,
                color = Color.WHITE,
                specialColors = mapOf(
                    ThemeOption.PANEL_BKGR_RECT to mapOf(
                        Elem.FILL to parseHex("#3B3B3B")
                    ),
                    ThemeOption.FACET_STRIP_BGR_RECT to mapOf(
                        Elem.FILL to parseHex("#363636")
                    ),
                    ThemeOption.PANEL_GRID to mapOf(
                        Elem.COLOR to parseHex("#474747")
                    ),
                    ThemeOption.TOOLTIP_RECT to mapOf(
                        Elem.FILL to parseHex("#460073")
                    ),
                    ThemeOption.AXIS_TOOLTIP to mapOf(
                        Elem.FILL to Color.WHITE
                    ),
                )
            )

            else -> throw IllegalArgumentException("Unsupported theme flavor: '$flavor'")
        }
    }
}