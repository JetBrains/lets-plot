/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values

object ThemeOption {
    const val NAME = "name"

    // System
    const val EXPONENT_FORMAT = "exponent_format"

    // Common
    const val TITLE = "title"
    const val TEXT = "text"
    const val LINE = "line"
    const val RECT = "rect"

    const val PLOT_BKGR_RECT = "plot_background"
    const val PLOT_TITLE = "plot_title"
    const val PLOT_SUBTITLE = "plot_subtitle"
    const val PLOT_CAPTION = "plot_caption"
    const val PLOT_MESSAGE = "plot_message"
    const val PLOT_MARGIN = "plot_margin"
    const val PLOT_INSET = "plot_inset"

    const val PLOT_TITLE_POSITION = "plot_title_position"
    const val PLOT_CAPTION_POSITION = "plot_caption_position"

    // ToDo: "text_width_scale" is used Violin demo - update.
//    const val TEXT_WIDTH_FACTOR = "text_width_scale"

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
    const val PANEL_INSET = "panel_inset"
    const val PANEL_BKGR_RECT = "panel_background"
    const val PANEL_BORDER_RECT = "panel_border"
    const val PANEL_BORDER_ONTOP = "panel_border_ontop"

    // Panel grid
    const val PANEL_GRID = "panel_grid"  // a line
    const val PANEL_GRID_ONTOP = "panel_grid_ontop"
    const val PANEL_GRID_ONTOP_X = "panel_grid_ontop_x"
    const val PANEL_GRID_ONTOP_Y = "panel_grid_ontop_y"
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

    // Tooltip
    const val TOOLTIP_RECT = "tooltip"
    const val TOOLTIP_TEXT = "tooltip_text"
    const val TOOLTIP_TITLE_TEXT = "tooltip_title_text"

    // Annotation
    const val ANNOTATION_TEXT = "label_text"

    const val GEOM = "geom"
    const val FLAVOR = "flavor"


    // view element
    val ELEMENT_BLANK = mapOf(Elem.BLANK to true)
    const val ELEMENT_BLANK_SHORTHAND = "blank"

    object Elem {
        const val BLANK = "blank"
        const val FILL = "fill"
        const val COLOR = "color"
        const val SIZE = "size"
        const val LINETYPE = "linetype"
        const val ARROW = "arrow"       // ToDo

        // text
        const val FONT_FAMILY = "family"
        const val FONT_FACE = "face"

        //        const val FONT_MONOSPACED = "monospaced"
        const val HJUST = "hjust"
        const val VJUST = "vjust"
        const val ANGLE = "angle"       // ToDo
        const val LINEHEIGHT = "lineheight" // ToDo
        const val MARGIN = "margin"
        const val INSET = "inset"

        object Margin {
            const val TOP = "margin_t"
            const val RIGHT = "margin_r"
            const val BOTTOM = "margin_b"
            const val LEFT = "margin_l"
        }

        object Inset {
            const val TOP = "inset_t"
            const val RIGHT = "inset_r"
            const val BOTTOM = "inset_b"
            const val LEFT = "inset_l"
        }
    }

    object Name {
        // ggplot2 themes
        const val R_GREY = "grey"
        const val R_LIGHT = "light"
        const val R_CLASSIC = "classic"
        const val R_MINIMAL = "minimal"
        const val R_BW = "bw"

        // lets-plot themes
        const val LP_MINIMAL = "minimal2"
        const val LP_NONE = "none"
    }

    object Flavor {
        const val DARCULA = "darcula"
        const val SOLARIZED_LIGHT = "solarized_light"
        const val SOLARIZED_DARK = "solarized_dark"
        const val HIGH_CONTRAST_LIGHT = "high_contrast_light"
        const val HIGH_CONTRAST_DARK = "high_contrast_dark"

        // base theme flavors
        const val BASE = "base"
        const val GREY = "grey"
        const val LIGHT = "light"
        const val BW = "bw"
    }

    object Geom {
        // Named colors
        const val PEN = "pen"
        const val PAPER = "paper"
        const val BRUSH = "brush"
    }

    internal object ForTest {
        val themeNames = listOf(
            Name.R_GREY,
            Name.R_LIGHT,
            Name.R_CLASSIC,
            Name.R_MINIMAL,
            Name.R_BW,
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
            PANEL_BORDER_RECT,
            PANEL_GRID_MAJOR_X,
            PANEL_GRID_MINOR_X,
            PANEL_GRID_MAJOR_Y,
            PANEL_GRID_MINOR_Y,
            FACET_STRIP_BGR_RECT,
            TOOLTIP_RECT
        )

        // Actually, text sizes are defined in "theme values".
        val elemWithColorOnly = listOf(
            PLOT_TITLE,
            PLOT_SUBTITLE,
            PLOT_CAPTION,
            AXIS_TITLE_X,
            AXIS_TITLE_Y,
            AXIS_TEXT_X,
            AXIS_TEXT_Y,
            AXIS_TOOLTIP_TEXT_X,
            AXIS_TOOLTIP_TEXT_Y,
            FACET_STRIP_TEXT,
            LEGEND_TEXT,
            LEGEND_TITLE,
            TOOLTIP_TEXT,
            TOOLTIP_TITLE_TEXT,
            ANNOTATION_TEXT
        )
        val elemWithFill = listOf(
            PLOT_BKGR_RECT,
            LEGEND_BKGR_RECT,
            AXIS_TOOLTIP_X,
            AXIS_TOOLTIP_Y,
            PANEL_BKGR_RECT,
            FACET_STRIP_BGR_RECT,
            TOOLTIP_RECT
        )
        val numericOptions = listOf(
            AXIS_TICKS_LENGTH_X,
            AXIS_TICKS_LENGTH_Y,
        )

        //  font_face
        val elemWithFontOptions = listOf(
            PLOT_TITLE,
            PLOT_SUBTITLE,
            PLOT_CAPTION,
            AXIS_TITLE_X,
            AXIS_TITLE_Y,
            AXIS_TEXT_X,
            AXIS_TEXT_Y,
            AXIS_TOOLTIP_TEXT_X,
            AXIS_TOOLTIP_TEXT_Y,
            FACET_STRIP_TEXT,
            LEGEND_TEXT,
            LEGEND_TITLE,
            TOOLTIP_TEXT,
            TOOLTIP_TITLE_TEXT,
            ANNOTATION_TEXT
        )
    }
}