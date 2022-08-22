/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.defaultTheme.values

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.base.values.FontFace
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.AXIS_ONTOP
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.AXIS_TEXT
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.AXIS_TICKS_LENGTH
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.AXIS_TITLE
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.ELEMENT_BLANK
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.Elem
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.LEGEND_BKGR_RECT
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.LEGEND_DIRECTION
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.LEGEND_JUSTIFICATION
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.LEGEND_POSITION
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.LINE
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.PANEL_BORDER_RECT
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.PANEL_GRID_MINOR
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.PLOT_BKGR_RECT
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.PLOT_CAPTION
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.RECT
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.TEXT
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.TITLE
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.TOOLTIP_RECT
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.TOOLTIP_TITLE_TEXT
import jetbrains.datalore.plot.builder.guide.LegendDirection
import jetbrains.datalore.plot.builder.guide.LegendJustification
import jetbrains.datalore.plot.builder.guide.LegendPosition
import jetbrains.datalore.plot.builder.presentation.Defaults

open class ThemeValuesBase : ThemeValues(VALUES) {

    companion object {

        private val VALUES: Map<String, Any> = mapOf(
            LINE to mapOf(
                Elem.COLOR to Color.BLUE,
                Elem.SIZE to 1.0,
            ),
            RECT to mapOf(
                Elem.COLOR to Color.BLUE,
                Elem.FILL to Color.LIGHT_BLUE,
                Elem.SIZE to 1.0,
            ),
            TEXT to mapOf(
                Elem.COLOR to Color.BLUE,
                Elem.SIZE to Defaults.FONT_SMALL,
                Elem.FONT_FACE to FontFace.NORMAL,
                Elem.FONT_FAMILY to Defaults.FONT_FAMILY_NORMAL,
                Elem.HJUST to 0.0,
                Elem.VJUST to 0.5,
            ),
            TITLE to mapOf(
                Elem.SIZE to Defaults.FONT_MEDIUM,
                Elem.Margin.TOP to 4.0,
                Elem.Margin.RIGHT to 0.0,
                Elem.Margin.BOTTOM to 4.0,
                Elem.Margin.LEFT to 0.0
            ),
            PLOT_CAPTION to mapOf(
                Elem.HJUST to 1.0,
            ),
            PANEL_BORDER_RECT to ELEMENT_BLANK,

            PLOT_BKGR_RECT to mapOf(
                Elem.FILL to Color.WHITE,
                Elem.SIZE to 0.0,
            ),

            LEGEND_BKGR_RECT to mapOf(
                Elem.FILL to Color.WHITE,
                Elem.SIZE to 0.0,
            ),

            AXIS_ONTOP to false,
            AXIS_TICKS_LENGTH to 4.0,

            AXIS_TEXT to mapOf(
                Elem.Margin.TOP to 3.0,
                Elem.Margin.RIGHT to 3.0,
                Elem.Margin.BOTTOM to 0.0,
                Elem.Margin.LEFT to 0.0
            ),

            AXIS_TITLE to mapOf(
                Elem.HJUST to 0.5,
                Elem.Margin.TOP to 4.0,
                Elem.Margin.RIGHT to 4.0,
                Elem.Margin.BOTTOM to 4.0,
                Elem.Margin.LEFT to 4.0
            ),

            PANEL_GRID_MINOR to mapOf(
                Elem.SIZE to 0.5,
            ),

            // Legend
            LEGEND_POSITION to LegendPosition.RIGHT,
            LEGEND_JUSTIFICATION to LegendJustification.CENTER,
            LEGEND_DIRECTION to LegendDirection.AUTO,

            // Tooltip
            TOOLTIP_RECT to mapOf(
                Elem.FILL to Color.WHITE,
                Elem.COLOR to Color.BLACK,
                Elem.SIZE to 1.0,
            ),

            TOOLTIP_TITLE_TEXT to mapOf(
                Elem.FONT_FACE to FontFace.BOLD,
            )
        )
    }
}