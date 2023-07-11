/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.presentation

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.commons.values.FontFace
import jetbrains.datalore.plot.builder.presentation.Defaults.Plot.Axis
import org.jetbrains.letsPlot.datamodel.svg.style.TextStyle

object Defaults {
    // Plot size
    const val ASPECT_RATIO = 3.0 / 2.0
    const val MIN_PLOT_WIDTH = 50.0

    @Suppress("MemberVisibilityCanBePrivate")
    const val DEF_PLOT_WIDTH = 600.0

    @Suppress("MemberVisibilityCanBePrivate")
    const val DEF_LIVE_MAP_WIDTH = 800.0
    val DEF_PLOT_SIZE = DoubleVector(DEF_PLOT_WIDTH, DEF_PLOT_WIDTH / ASPECT_RATIO)
    val DEF_LIVE_MAP_SIZE = DoubleVector(DEF_LIVE_MAP_WIDTH, DEF_LIVE_MAP_WIDTH / ASPECT_RATIO)

    // HEX colors only (because of using of parseHex())
    const val DARK_GRAY = "#3d3d3d"
    val GRAY = Color.GRAY.toHexColor()
    val LIGHT_GRAY = Color.LIGHT_GRAY.toHexColor()
    val X_LIGHT_GRAY = Color.VERY_LIGHT_GRAY.toHexColor()
    const val XX_LIGHT_GRAY = "#e0e0e0"

    val TEXT_COLOR = Color.parseHex(DARK_GRAY)

    const val FONT_LARGE = 16.0
    const val FONT_MEDIUM = 15.0
    const val FONT_SMALL = 13.0

    // Note, we don't really support fallback families names in family name (see vis.svgMapper.jfx.SvgTextElementMapper)
    const val FONT_FAMILY_NORMAL = "Lucida Grande, sans-serif"

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

        object Legend {
            const val TITLE_FONT_SIZE = FONT_MEDIUM
            const val ITEM_FONT_SIZE = FONT_SMALL
            const val LINES_MAX_LENGTH = 30
            const val LINES_MAX_COUNT= 5
        }

        object Axis {
            const val LABEL_MAX_LENGTH = 20
        }

        object Tooltip {
            const val MAX_POINTER_FOOTING_LENGTH = 12.0
            const val POINTER_FOOTING_TO_SIDE_LENGTH_RATIO = 0.4

            const val MARGIN_BETWEEN_TOOLTIPS = 5.0
            const val DATA_TOOLTIP_FONT_SIZE = FONT_SMALL
            const val LINE_INTERVAL = 6.0
            const val INTERVAL_BETWEEN_SUBSTRINGS = 3.0
            const val H_CONTENT_PADDING = 6.0
            const val V_CONTENT_PADDING = 6.0
            const val CONTENT_EXTENDED_PADDING = 10.0

            const val LABEL_VALUE_INTERVAL = 8.0
            const val VALUE_LINE_MAX_LENGTH = 30

            const val LINE_SEPARATOR_WIDTH = 0.7

            const val BORDER_RADIUS = 4.0
            const val COLOR_BAR_WIDTH = 4.0
            const val COLOR_BAR_STROKE_WIDTH = 1.5

            val DARK_TEXT_COLOR = Color.BLACK
            val LIGHT_TEXT_COLOR = Color.WHITE

            const val AXIS_TOOLTIP_FONT_SIZE = Plot.Axis.TICK_FONT_SIZE
            val AXIS_TOOLTIP_COLOR = Plot.Axis.LINE_COLOR
            const val AXIS_RADIUS = 1.5 // fix a tooltip border overlapping an axis

            const val ROTATION_ANGLE = 15.0
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
