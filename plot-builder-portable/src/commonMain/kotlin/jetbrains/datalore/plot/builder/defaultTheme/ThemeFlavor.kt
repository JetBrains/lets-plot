/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.defaultTheme

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.base.values.Color.Companion.parseHex
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption

class ThemeFlavor(
    private val fill: Color,
    private val color: Color,
    private val tooltipBackground: Color? = null,
) {
    constructor(
        fillHex: String,
        colorHex: String,
        tooltipHexColor: String? = null,
    ) : this(
        parseHex(fillHex),
        parseHex(colorHex),
        tooltipHexColor?.let(::parseHex)
    )

    fun updateColors(options: Map<String, Any>): Map<String, Any> {
        return options.mapValues { (key, value) ->
            if (value is Map<*, *> && value != ThemeOption.ELEMENT_BLANK) {
                val updated = value.toMutableMap()
                if (key in ELEM_TO_UPDATE_COLOR) {
                    updated[ThemeOption.Elem.COLOR] = color
                }
                if (key in ELEM_TO_UPDATE_FILL) {
                    updated[ThemeOption.Elem.FILL] = fill
                }
                if (key == ThemeOption.TOOLTIP_RECT && tooltipBackground != null) {
                    updated[ThemeOption.Elem.FILL] = tooltipBackground
                }
                updated
            } else {
                value
            }
        }
    }

    companion object {
        private val ELEM_TO_UPDATE_COLOR = listOf(
            ThemeOption.LINE,
            ThemeOption.RECT,
            ThemeOption.AXIS,
            ThemeOption.AXIS_TICKS,
            ThemeOption.PANEL_GRID,
            ThemeOption.PANEL_BKGR_RECT,

            ThemeOption.AXIS_TOOLTIP,
            ThemeOption.TOOLTIP_RECT,

            ThemeOption.TEXT,
            ThemeOption.TITLE,
        )

        private val ELEM_TO_UPDATE_FILL = listOf(
            ThemeOption.RECT,
            ThemeOption.PLOT_BKGR_RECT,
            ThemeOption.LEGEND_BKGR_RECT,
            ThemeOption.AXIS_TOOLTIP,
            ThemeOption.TOOLTIP_RECT,
        )


        fun forName(flavor: String): ThemeFlavor {
            return when (flavor) {
                ThemeOption.Flavor.DARCULA -> ThemeFlavor(
                    fillHex = "#303030",
                    colorHex = "#BBBBBB",
                    tooltipHexColor = "#141414"
                )
                ThemeOption.Flavor.SOLARIZED_LIGHT -> ThemeFlavor(
                    fillHex = "#EEE8D5",
                    colorHex = "#2E4E58",
                    tooltipHexColor = "#FDF6E3"
                )
                ThemeOption.Flavor.SOLARIZED_DARK -> ThemeFlavor(
                    fillHex = "#0E3C4A",
                    colorHex = "#A7B6BA",
                    tooltipHexColor = "#003841"
                )
                ThemeOption.Flavor.HIGH_CONTRAST_LIGHT -> ThemeFlavor(fillHex = "#FFFFFF", colorHex = "#000000")
                ThemeOption.Flavor.HIGH_CONTRAST_DARK -> ThemeFlavor(
                    fillHex = "#000000",
                    colorHex = "#FFFFFF",
                    tooltipHexColor = "#460073"
                )
                else -> throw IllegalArgumentException("Unsupported theme flavor: '$flavor'")
            }
        }
    }
}