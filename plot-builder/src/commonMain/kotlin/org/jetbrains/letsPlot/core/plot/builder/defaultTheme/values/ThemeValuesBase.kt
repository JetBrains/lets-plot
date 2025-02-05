/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values

import org.jetbrains.letsPlot.commons.values.FontFace
import org.jetbrains.letsPlot.core.plot.base.guide.LegendArrangement
import org.jetbrains.letsPlot.core.plot.base.guide.LegendBoxJustification
import org.jetbrains.letsPlot.core.plot.base.guide.LegendDirection
import org.jetbrains.letsPlot.core.plot.base.guide.LegendJustification
import org.jetbrains.letsPlot.core.plot.base.guide.LegendPosition
import org.jetbrains.letsPlot.core.plot.base.render.linetype.NamedLineType
import org.jetbrains.letsPlot.core.plot.base.theme.TitlePosition
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.ThemeFlavor.Companion.SymbolicColor
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.AXIS_ONTOP
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.AXIS_TEXT
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.AXIS_TEXT_X
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.AXIS_TEXT_Y
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.AXIS_TICKS_LENGTH
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.AXIS_TITLE
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.ELEMENT_BLANK
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.Elem
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.FACET_STRIP_TEXT
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.LEGEND_BKGR_RECT
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.LEGEND_BOX
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.LEGEND_BOX_JUST
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.LEGEND_BOX_SPACING
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.LEGEND_DIRECTION
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.LEGEND_JUSTIFICATION
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.LEGEND_KEY_RECT
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.LEGEND_KEY_SIZE
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.LEGEND_KEY_SPACING
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.LEGEND_MARGIN
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.LEGEND_POSITION
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.LEGEND_SPACING
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.LEGEND_TITLE
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.LINE
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.PANEL_BORDER_ONTOP
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.PANEL_BORDER_RECT
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.PANEL_GRID_MINOR
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.PANEL_GRID_ONTOP
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.PANEL_INSET
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.PLOT_BKGR_RECT
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.PLOT_CAPTION
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.PLOT_CAPTION_POSITION
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.PLOT_INSET
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.PLOT_MARGIN
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.PLOT_SUBTITLE
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.PLOT_TITLE
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.PLOT_TITLE_POSITION
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.RECT
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.TEXT
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.TITLE
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.TOOLTIP_RECT
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.TOOLTIP_TITLE_TEXT
import org.jetbrains.letsPlot.core.plot.builder.presentation.Defaults

internal open class ThemeValuesBase : ThemeValues(VALUES) {

    companion object {

        private val VALUES: Map<String, Any> = mapOf(
            LINE to mapOf(
                Elem.SIZE to 1.0,
                Elem.COLOR to SymbolicColor.BLACK,
                Elem.LINETYPE to NamedLineType.SOLID
            ),
            RECT to mapOf(
                Elem.SIZE to 1.0,
                Elem.COLOR to SymbolicColor.BLACK,
                Elem.FILL to SymbolicColor.WHITE,
                Elem.LINETYPE to NamedLineType.SOLID
            ),
            TEXT to mapOf(
                Elem.SIZE to Defaults.FONT_SMALL,
                Elem.FONT_FACE to FontFace.NORMAL,
                Elem.FONT_FAMILY to Defaults.FONT_FAMILY_NORMAL,
//                Elem.FONT_MONOSPACED to Defaults.FONT_FAMILY_NORMAL.monospaced,
                Elem.HJUST to 0.0,
                Elem.VJUST to 0.5,
                Elem.COLOR to SymbolicColor.BLACK,
            ),
            TITLE to mapOf(
                Elem.SIZE to Defaults.FONT_MEDIUM,
                Elem.Margin.TOP to 0.0,
                Elem.Margin.RIGHT to 0.0,
                Elem.Margin.BOTTOM to 0.0,
                Elem.Margin.LEFT to 0.0
            ),
            PLOT_TITLE to mapOf(
                Elem.SIZE to Defaults.FONT_LARGE,
                Elem.Margin.TOP to 4.0,
            ),
            PLOT_SUBTITLE to mapOf(
                Elem.SIZE to Defaults.FONT_MEDIUM,
                Elem.Margin.TOP to 4.0,
            ),
            PLOT_CAPTION to mapOf(
                Elem.HJUST to 1.0,
                Elem.SIZE to Defaults.FONT_SMALL,
                Elem.Margin.BOTTOM to 4.0,
            ),
//            TEXT_WIDTH_FACTOR to 1.0,
            PANEL_BORDER_RECT to ELEMENT_BLANK,
            PANEL_BORDER_ONTOP to true,

            PLOT_BKGR_RECT to mapOf(
                Elem.SIZE to 0.0,
                Elem.FILL to SymbolicColor.WHITE,
            ),

            LEGEND_BKGR_RECT to mapOf(
                Elem.SIZE to 0.0,
                Elem.FILL to SymbolicColor.WHITE,
            ),

            AXIS_ONTOP to true,
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

            AXIS_TEXT_X to mapOf(
                Elem.HJUST to 0.5,
                Elem.VJUST to Double.NaN,
            ),

            AXIS_TEXT_Y to mapOf(
                Elem.HJUST to Double.NaN,
                Elem.VJUST to 0.5,
            ),

            AXIS_TITLE to mapOf(
                Elem.SIZE to Defaults.FONT_MEDIUM,
                Elem.HJUST to 0.5,
                Elem.Margin.TOP to 4.0,
                Elem.Margin.RIGHT to 4.0,
                Elem.Margin.BOTTOM to 4.0,
                Elem.Margin.LEFT to 4.0
            ),

            PANEL_GRID_ONTOP to false,
            PANEL_INSET to mapOf(
                Elem.Inset.TOP to 0.0,
                Elem.Inset.RIGHT to 0.0,
                Elem.Inset.BOTTOM to 0.0,
                Elem.Inset.LEFT to 0.0
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
            LEGEND_SPACING to 10.0,

            LEGEND_KEY_RECT to ELEMENT_BLANK,
            LEGEND_KEY_SIZE to 23.0,
            LEGEND_KEY_SPACING to 0.0,

            LEGEND_MARGIN to mapOf(
                Elem.Margin.TOP to 5.0,
                Elem.Margin.RIGHT to 5.0,
                Elem.Margin.BOTTOM to 5.0,
                Elem.Margin.LEFT to 5.0
            ),

            LEGEND_BOX to LegendArrangement.VERTICAL,
            LEGEND_BOX_JUST to LegendBoxJustification.AUTO,
            LEGEND_BOX_SPACING to 5.0,

            FACET_STRIP_TEXT to mapOf(
                Elem.HJUST to 0.5,
                Elem.Margin.TOP to 3.0,
                Elem.Margin.RIGHT to 3.0,
                Elem.Margin.BOTTOM to 3.0,
                Elem.Margin.LEFT to 3.0,
            ),

            // Tooltip
            TOOLTIP_RECT to mapOf(
                Elem.SIZE to 1.0,
            ),

            TOOLTIP_TITLE_TEXT to mapOf(
                Elem.FONT_FACE to FontFace.BOLD,
            ),

            PLOT_MARGIN to mapOf(
                Elem.Margin.TOP to 0.0,
                Elem.Margin.RIGHT to 0.0,
                Elem.Margin.BOTTOM to 0.0,
                Elem.Margin.LEFT to 0.0
            ),
            PLOT_INSET to mapOf(
                Elem.Inset.TOP to 6.5,
                Elem.Inset.RIGHT to 6.5,
                Elem.Inset.BOTTOM to 6.5,
                Elem.Inset.LEFT to 6.5
            ),

            PLOT_TITLE_POSITION to TitlePosition.PANEL,
            PLOT_CAPTION_POSITION to TitlePosition.PANEL,
        )
    }
}