/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.commons.values.FontFace
import org.jetbrains.letsPlot.core.plot.base.guide.LegendDirection
import org.jetbrains.letsPlot.core.plot.base.guide.LegendJustification
import org.jetbrains.letsPlot.core.plot.base.guide.LegendPosition
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.AXIS_ONTOP
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.AXIS_TEXT
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.AXIS_TICKS_LENGTH
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.AXIS_TITLE
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.ELEMENT_BLANK
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.Elem
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.GEOM
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.Geom
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.LEGEND_BKGR_RECT
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.LEGEND_DIRECTION
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.LEGEND_JUSTIFICATION
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.LEGEND_POSITION
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.LEGEND_TITLE
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.LINE
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.PANEL_BORDER_RECT
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.PANEL_GRID_MINOR
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.PLOT_BKGR_RECT
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.PLOT_CAPTION
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.PLOT_SUBTITLE
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.PLOT_TITLE
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.RECT
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.TEXT
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.TITLE
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.TOOLTIP_RECT
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.TOOLTIP_TITLE_TEXT
import org.jetbrains.letsPlot.core.plot.builder.presentation.Defaults

open class ThemeValuesBase : ThemeValues(VALUES) {

    companion object {
        private val DARK_GREY: Color = Color.parseHex("#474747")
        private val LIGHT_GREY: Color = Color.parseHex("#E9E9E9")

        private val VALUES: Map<String, Any> = mapOf(
            LINE to mapOf(
                Elem.COLOR to DARK_GREY,
                Elem.SIZE to 1.0,
            ),
            RECT to mapOf(
                Elem.COLOR to DARK_GREY,
                Elem.FILL to LIGHT_GREY,
                Elem.SIZE to 1.0,
            ),
            TEXT to mapOf(
                Elem.COLOR to DARK_GREY,
                Elem.SIZE to Defaults.FONT_SMALL,
                Elem.FONT_FACE to FontFace.NORMAL,
                Elem.FONT_FAMILY to Defaults.FONT_FAMILY_NORMAL,
//                Elem.FONT_MONOSPACED to Defaults.FONT_FAMILY_NORMAL.monospaced,
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
            PLOT_TITLE to mapOf(
                Elem.SIZE to Defaults.FONT_LARGE
            ),
            PLOT_SUBTITLE to mapOf(
                Elem.SIZE to Defaults.FONT_MEDIUM
            ),
            PLOT_CAPTION to mapOf(
                Elem.HJUST to 1.0,
                Elem.SIZE to Defaults.FONT_SMALL
            ),
//            TEXT_WIDTH_FACTOR to 1.0,
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
//                Elem.Margin.BOTTOM to 0.0,
//                Elem.Margin.LEFT to 0.0
// All around equal default margins because axis text can appear on either side of a plot tile. (See scale x/y "position")
                Elem.Margin.BOTTOM to 3.0,
                Elem.Margin.LEFT to 3.0,
                // Currently 'angle' is supported for AXIS_TEXT only: NaN means no rotation.
                Elem.ANGLE to Double.NaN
            ),

            AXIS_TITLE to mapOf(
                Elem.SIZE to Defaults.FONT_MEDIUM,
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
            LEGEND_TITLE to mapOf(
                Elem.SIZE to Defaults.FONT_MEDIUM
            ),
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
            ),

            // Named colors
            GEOM to mapOf(
                Geom.PEN to DARK_GREY,
                Geom.PAPER to Color.WHITE,
                Geom.BRUSH to Color.PACIFIC_BLUE
            )
        )
    }
}