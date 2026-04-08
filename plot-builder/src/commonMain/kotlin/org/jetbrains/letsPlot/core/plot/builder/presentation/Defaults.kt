/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.presentation

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.commons.values.FontFace
import org.jetbrains.letsPlot.core.plot.base.tooltip.render.TooltipRenderDefaults
import org.jetbrains.letsPlot.core.plot.builder.presentation.Defaults.Plot.Axis
import org.jetbrains.letsPlot.datamodel.svg.style.TextStyle

object Defaults {
    // Plot size
    const val ASPECT_RATIO = 3.0 / 2.0
    const val MIN_PLOT_WIDTH = 50.0

    @Suppress("MemberVisibilityCanBePrivate")
    const val DEF_PLOT_WIDTH = 600.0

    @Suppress("MemberVisibilityCanBePrivate")
    const val DEF_LARGE_PLOT_WIDTH = 800.0

    val DEF_PLOT_SIZE = DoubleVector(DEF_PLOT_WIDTH, DEF_PLOT_WIDTH / ASPECT_RATIO)
    val DEF_LARGE_PLOT_SIZE = DoubleVector(DEF_LARGE_PLOT_WIDTH, DEF_LARGE_PLOT_WIDTH / ASPECT_RATIO)

    const val TOOLBAR_HEIGHT = 33

    // HEX colors only (because of using of parseHex())
    const val DARK_GRAY = "#3d3d3d"
    val GRAY = Color.GRAY.toHexColor()

    val TEXT_COLOR = Color.parseHex(DARK_GRAY)

    const val FONT_LARGE = 16.0
    const val FONT_MEDIUM = 15.0
    const val FONT_SMALL = 13.0

    // Note, we don't really support fallback families names in family name (see vis.svgMapper.jfx.SvgTextElementMapper)
    const val FONT_FAMILY_NORMAL = "sans-serif"

    class Common {
        object Title {
            const val FONT_SIZE = FONT_LARGE
            const val FONT_SIZE_CSS = "" + FONT_SIZE + "px"
        }

        object Subtitle {
            const val FONT_SIZE = FONT_MEDIUM
            const val FONT_SIZE_CSS = "" + FONT_SIZE + "px"
        }

        object Caption {
            const val FONT_SIZE = FONT_SMALL
            const val FONT_SIZE_CSS = "" + FONT_SIZE + "px"
        }

        object Tag {
            const val FONT_SIZE = FONT_LARGE
            const val FONT_SIZE_CSS = "" + FONT_SIZE + "px"
        }

        object Legend {
            const val TITLE_FONT_SIZE = FONT_MEDIUM
            const val ITEM_FONT_SIZE = FONT_SMALL
            const val LINES_MAX_LENGTH = 30
            const val LINES_MAX_COUNT = 5
        }

        object Axis {
            const val LABEL_MAX_LENGTH = 20
        }

        object Tooltip {
            const val MAX_POINTER_FOOTING_LENGTH = TooltipRenderDefaults.MAX_POINTER_FOOTING_LENGTH
            const val POINTER_FOOTING_TO_SIDE_LENGTH_RATIO = TooltipRenderDefaults.POINTER_FOOTING_TO_SIDE_LENGTH_RATIO

            const val MARGIN_BETWEEN_TOOLTIPS = TooltipRenderDefaults.MARGIN_BETWEEN_TOOLTIPS
            const val DATA_TOOLTIP_FONT_SIZE = FONT_SMALL
            const val LINE_INTERVAL = TooltipRenderDefaults.LINE_INTERVAL
            const val INTERVAL_BETWEEN_SUBSTRINGS = TooltipRenderDefaults.INTERVAL_BETWEEN_SUBSTRINGS
            const val H_CONTENT_PADDING = TooltipRenderDefaults.H_CONTENT_PADDING
            const val V_CONTENT_PADDING = TooltipRenderDefaults.V_CONTENT_PADDING
            const val CONTENT_EXTENDED_PADDING = TooltipRenderDefaults.CONTENT_EXTENDED_PADDING

            const val LABEL_VALUE_INTERVAL = TooltipRenderDefaults.LABEL_VALUE_INTERVAL
            const val VALUE_LINE_MAX_LENGTH = TooltipRenderDefaults.VALUE_LINE_MAX_LENGTH

            const val LINE_SEPARATOR_WIDTH = TooltipRenderDefaults.LINE_SEPARATOR_WIDTH

            const val BORDER_RADIUS = TooltipRenderDefaults.BORDER_RADIUS
            const val COLOR_BAR_WIDTH = TooltipRenderDefaults.COLOR_BAR_WIDTH
            const val COLOR_BAR_STROKE_WIDTH = TooltipRenderDefaults.COLOR_BAR_STROKE_WIDTH

            val DARK_TEXT_COLOR = TooltipRenderDefaults.DARK_TEXT_COLOR
            val LIGHT_TEXT_COLOR = TooltipRenderDefaults.LIGHT_TEXT_COLOR

            const val AXIS_TOOLTIP_FONT_SIZE = Plot.Axis.TICK_FONT_SIZE
            val AXIS_TOOLTIP_COLOR = Plot.Axis.LINE_COLOR

            // Consider to remove.
            // was 1.5 to fix a tooltip border overlapping an axis, but now TipLayoutHint has stroke property
            const val AXIS_RADIUS = 0.0

            const val ROTATION_ANGLE = TooltipRenderDefaults.ROTATION_ANGLE
        }
    }

    class Plot {
        object Axis {
            const val TITLE_FONT_SIZE = FONT_MEDIUM
            const val TICK_FONT_SIZE = FONT_SMALL

            val LINE_COLOR = Color.parseHex(DARK_GRAY)
        }
    }

    private fun createTextStyle(
        face: FontFace = FontFace.NORMAL,
        size: Double = FONT_MEDIUM,
        color: Color = Color.BLACK
    ) = TextStyle(family = FONT_FAMILY_NORMAL, face, size, color)

    internal val DEFAULT_TEXT_STYLES = mapOf(
        Style.PLOT_TITLE to createTextStyle(size = Common.Title.FONT_SIZE, face = FontFace.BOLD),
        Style.PLOT_SUBTITLE to createTextStyle(size = Common.Subtitle.FONT_SIZE),
        Style.PLOT_CAPTION to createTextStyle(size = Common.Caption.FONT_SIZE),
        Style.PLOT_TAG to createTextStyle(size = Common.Tag.FONT_SIZE),
        Style.LEGEND_TITLE to createTextStyle(size = Common.Legend.TITLE_FONT_SIZE),
        Style.LEGEND_ITEM to createTextStyle(size = Common.Legend.ITEM_FONT_SIZE),
        Style.TOOLTIP_TEXT to createTextStyle(size = Common.Tooltip.DATA_TOOLTIP_FONT_SIZE),
        Style.TOOLTIP_TITLE to createTextStyle(
            size = Common.Tooltip.DATA_TOOLTIP_FONT_SIZE,
            face = FontFace.BOLD
        ),
        Style.TOOLTIP_LABEL to createTextStyle(
            size = Common.Tooltip.DATA_TOOLTIP_FONT_SIZE,
            face = FontFace.BOLD
        ),
        "${Style.AXIS_TITLE}-x" to createTextStyle(size = Axis.TITLE_FONT_SIZE),
        "${Style.AXIS_TITLE}-y" to createTextStyle(size = Axis.TITLE_FONT_SIZE),
        "${Style.AXIS_TEXT}-x" to createTextStyle(size = Axis.TICK_FONT_SIZE),
        "${Style.AXIS_TEXT}-y" to createTextStyle(size = Axis.TICK_FONT_SIZE),
        "${Style.AXIS_TOOLTIP_TEXT}-x" to createTextStyle(
            size = Common.Tooltip.AXIS_TOOLTIP_FONT_SIZE,
            color = Color.WHITE
        ),
        "${Style.AXIS_TOOLTIP_TEXT}-y" to createTextStyle(
            size = Common.Tooltip.AXIS_TOOLTIP_FONT_SIZE,
            color = Color.WHITE
        ),
        "${Style.FACET_STRIP_TEXT}-x" to createTextStyle(size = FONT_MEDIUM),
        "${Style.FACET_STRIP_TEXT}-y" to createTextStyle(size = FONT_MEDIUM)
    )

    object SubplotsGrid {
        const val DEF_HSPACE = 4.0
        const val DEF_VSPACE = 4.0
    }
}
