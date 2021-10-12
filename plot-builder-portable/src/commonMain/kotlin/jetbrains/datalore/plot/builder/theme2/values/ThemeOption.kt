/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.theme2.values

object ThemeOption {
    // Common
    const val TITLE = "title"
    const val TEXT = "text"
    const val LINE = "line"
    const val RECT = "rect"

    const val PLOT_TITLE = "plot_title"
    const val PLOT_SUBTITLE = "plot_subtitle" // ToDo
    const val PLOT_CAPTION = "plot_caption"   // ToDo

    // Axis
    const val AXIS = "axis"
    const val AXIS_X = "axis_x"
    const val AXIS_Y = "axis_y"

    const val AXIS_TITLE = "axis_title"
    const val AXIS_TEXT = "axis_text"
    const val AXIS_TICKS = "axis_ticks"
    const val AXIS_LINE = "axis_line"
    const val AXIS_TOOLTIP = "axis_tooltip"

    const val AXIS_TITLE_X = "axis_title_x"
    const val AXIS_TITLE_Y = "axis_title_y"
    const val AXIS_TEXT_X = "axis_text_x"
    const val AXIS_TEXT_Y = "axis_text_y"
    const val AXIS_TICKS_X = "axis_ticks_x"
    const val AXIS_TICKS_Y = "axis_ticks_y"
    const val AXIS_LINE_X = "axis_line_x"
    const val AXIS_LINE_Y = "axis_line_y"
    const val AXIS_TOOLTIP_X = "axis_tooltip_x"
    const val AXIS_TOOLTIP_Y = "axis_tooltip_y"

    // Panel
    const val PANEL_RECT = "panel_rect"

    // Panel grid
    const val PANEL_GRID = "panel_grid"  // a line
    const val PANEL_GRID_MAJOR = "panel_grid_major"
    const val PANEL_GRID_MINOR = "panel_grid_minor"
    const val PANEL_GRID_MAJOR_X = "panel_grid_major_x"
    const val PANEL_GRID_MINOR_X = "panel_grid_minor_x"
    const val PANEL_GRID_MAJOR_Y = "panel_grid_major_y"
    const val PANEL_GRID_MINOR_Y = "panel_grid_minor_y"

    // Facet
    const val FACET_STRIP = "facet_strip"
    // ToDo: strip-x / strip-y

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
        const val FONT_FACE = "face"
        const val HJUST = "hjust"       // ToDo
        const val VJUST = "vjust"       // ToDo
        const val ANGLE = "angle"       // ToDo
        const val LINEHEIGHT = "lineheight" // ToDo
        const val MARGIN = "margin"     // ToDo
    }

    object Name {
        // ggplot2 themes
        const val R_CLASSIC = "classic"
        const val R_GREY = "grey"

        // lets-plot themes
        const val LP_MINIMAL2 = "lp_minimal2"
    }
}