/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.defaultTheme.values

object ThemeOption {
    // Common
    const val TITLE = "title"
    const val TEXT = "text"
    const val LINE = "line"
    const val RECT = "rect"

    const val PLOT_BKGR_RECT = "plot_background"
    const val PLOT_TITLE = "plot_title"
    const val PLOT_SUBTITLE = "plot_subtitle" // ToDo
    const val PLOT_CAPTION = "plot_caption"   // ToDo

    // Axis
    const val AXIS = "axis"
    const val AXIS_X = "axis_x"
    const val AXIS_Y = "axis_y"

    const val AXIS_ONTOP = "axis_ontop"
    const val AXIS_ONTOP_X = "axis_ontop_x"
    const val AXIS_ONTOP_Y = "axis_ontop_y"

    const val AXIS_TITLE = "axis_title"
    const val AXIS_TEXT = "axis_text"
    const val AXIS_TICKS = "axis_ticks"
    const val AXIS_TICKS_LENGTH = "axis_ticks_length"
    const val AXIS_LINE = "axis_line"
    const val AXIS_TOOLTIP = "axis_tooltip"
    const val AXIS_TOOLTIP_TEXT = "axis_tooltip_text"

    const val AXIS_TITLE_X = "axis_title_x"
    const val AXIS_TITLE_Y = "axis_title_y"
    const val AXIS_TEXT_X = "axis_text_x"
    const val AXIS_TEXT_Y = "axis_text_y"
    const val AXIS_TICKS_X = "axis_ticks_x"
    const val AXIS_TICKS_Y = "axis_ticks_y"
    const val AXIS_TICKS_LENGTH_X = "axis_ticks_length_x"
    const val AXIS_TICKS_LENGTH_Y = "axis_ticks_length_y"
    const val AXIS_LINE_X = "axis_line_x"
    const val AXIS_LINE_Y = "axis_line_y"

    const val AXIS_TOOLTIP_X = "axis_tooltip_x"
    const val AXIS_TOOLTIP_Y = "axis_tooltip_y"
    const val AXIS_TOOLTIP_TEXT_X = "axis_tooltip_text_x"
    const val AXIS_TOOLTIP_TEXT_Y = "axis_tooltip_text_y"

    // Panel
    const val PANEL_BKGR_RECT = "panel_background"

    // Panel grid
    const val PANEL_GRID = "panel_grid"  // a line
    const val PANEL_GRID_MAJOR = "panel_grid_major"
    const val PANEL_GRID_MINOR = "panel_grid_minor"
    const val PANEL_GRID_MAJOR_X = "panel_grid_major_x"
    const val PANEL_GRID_MINOR_X = "panel_grid_minor_x"
    const val PANEL_GRID_MAJOR_Y = "panel_grid_major_y"
    const val PANEL_GRID_MINOR_Y = "panel_grid_minor_y"

    // Facet
    const val FACET_STRIP_BGR_RECT = "strip_background"   // ToDo: x / y
    const val FACET_STRIP_TEXT = "strip_text"   // ToDo: x / y

    // Legend
    const val LEGEND_BKGR_RECT = "legend_background"
    const val LEGEND_TEXT = "legend_text"
    const val LEGEND_TITLE = "legend_title"
    const val LEGEND_POSITION = "legend_position"
    const val LEGEND_JUSTIFICATION = "legend_justification"
    const val LEGEND_DIRECTION = "legend_direction"

    // view element
    val ELEMENT_BLANK = mapOf(Elem.BLANK to true)
    const val ELEMENT_BLANK_SHORTHAND = "blank"

    object Elem {
        const val BLANK = "blank"
        const val FILL = "fill"
        const val COLOR = "color"
        const val SIZE = "size"
        const val LINETYPE = "linetype" // ToDo
        const val ARROW = "arrow"       // ToDo

        // text
        const val FONT_FAMILY = "family"     // ToDo
        const val FONT_FACE = "face"         // ToDo
        const val HJUST = "hjust"       // ToDo
        const val VJUST = "vjust"       // ToDo
        const val ANGLE = "angle"       // ToDo
        const val LINEHEIGHT = "lineheight" // ToDo
        const val MARGIN = "margin"     // ToDo
    }

    object Name {
        // ggplot2 themes
        const val R_GREY = "grey"
        const val R_LIGHT = "light"
        const val R_CLASSIC = "classic"
        const val R_MINIMAL = "minimal"

        // lets-plot themes
        const val LP_MINIMAL = "minimal2"
        const val LP_NONE = "none"
    }

    internal object ForTest {
        val themeNames = listOf(
            Name.R_GREY,
            Name.R_LIGHT,
            Name.R_CLASSIC,
            Name.R_MINIMAL,
            Name.LP_MINIMAL,
            Name.LP_NONE,
        )
        val elemWithColorAndSize = listOf(
            PLOT_BKGR_RECT,
            LEGEND_BKGR_RECT,
            AXIS_TICKS_X,
            AXIS_TICKS_Y,
            AXIS_LINE_X,
            AXIS_LINE_Y,
            AXIS_TOOLTIP_X,
            AXIS_TOOLTIP_Y,
            PANEL_BKGR_RECT,
            PANEL_GRID_MAJOR_X,
            PANEL_GRID_MINOR_X,
            PANEL_GRID_MAJOR_Y,
            PANEL_GRID_MINOR_Y,
            FACET_STRIP_BGR_RECT,
        )

        // Actually, text sizes are defined in "theme values".
        val elemWithColorOnly = listOf(
            PLOT_TITLE,
            AXIS_TITLE_X,
            AXIS_TITLE_Y,
            AXIS_TEXT_X,
            AXIS_TEXT_Y,
            AXIS_TOOLTIP_TEXT_X,
            AXIS_TOOLTIP_TEXT_Y,
            FACET_STRIP_TEXT,
            LEGEND_TEXT,
            LEGEND_TITLE,
        )
        val elemWithFill = listOf(
            PLOT_BKGR_RECT,
            LEGEND_BKGR_RECT,
            AXIS_TOOLTIP_X,
            AXIS_TOOLTIP_Y,
            PANEL_BKGR_RECT,
            FACET_STRIP_BGR_RECT,
        )
        val numericOptions = listOf(
            AXIS_TICKS_LENGTH_X,
            AXIS_TICKS_LENGTH_Y,
        )
    }
}