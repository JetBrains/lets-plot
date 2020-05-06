/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.presentation

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.builder.presentation.Defaults.Plot.Axis

object Defaults {
    // HEX colors only (because of using of parseHex())
    const val DARK_GRAY = "#3d3d3d"
    val GRAY = Color.GRAY.toHexColor()
    val LIGHT_GRAY = Color.LIGHT_GRAY.toHexColor()
    val X_LIGHT_GRAY = Color.VERY_LIGHT_GRAY.toHexColor()
    const val XX_LIGHT_GRAY = "#e0e0e0"

    const val BACKDROP_COLOR = "white"

    const val TEXT_COLOR = DARK_GRAY

    const val FONT_LARGE = 16
    const val FONT_MEDIUM = 12
    const val FONT_SMALL = 10
    const val FONT_X_SMALL = 8

    const val FONT_FAMILY_NORMAL = "\"Lucida Grande\", sans-serif"
    const val FONT_FAMILY_MONOSPACED = "\"Courier New\", Courier, monospace"

    class Common {
        object Title {
            const val FONT_SIZE = FONT_LARGE
            val FONT_SIZE_CSS = "" + FONT_SIZE + "px"
        }

        object Legend {
            const val TITLE_FONT_SIZE =
                FONT_MEDIUM
            const val ITEM_FONT_SIZE =
                FONT_SMALL
            val OUTLINE_COLOR = Color.parseHex(XX_LIGHT_GRAY)
        }

        object Tooltip {
            const val MAX_POINTER_FOOTING_LENGTH = 12.0
            const val POINTER_FOOTING_TO_SIDE_LENGTH_RATIO = 0.4

            const val NORMAL_STEM_LENGTH = 12.0
            const val MARGIN_BETWEEN_TOOLTIPS = 5.0
            const val DATA_TOOLTIP_FONT_SIZE = FONT_MEDIUM
            const val LINE_INTERVAL = 3.0
            const val H_CONTENT_PADDING = 4.0
            const val V_CONTENT_PADDING = 4.0

            const val BORDER_WIDTH = 4.0

            val DARK_TEXT_COLOR = Color.BLACK
            val LIGHT_TEXT_COLOR = Color.WHITE

            const val AXIS_STEM_LENGTH = 0.0
            const val AXIS_TOOLTIP_FONT_SIZE = Axis.TICK_FONT_SIZE
            val AXIS_TOOLTIP_COLOR = Axis.LINE_COLOR
            const val AXIS_RADIUS = 1.5 // fix a tooltip border overlapping an axis
        }
    }

    class Table {
        object Head {
            const val FONT_SIZE = FONT_MEDIUM
            const val FONT_SIZE_CSS = "" + FONT_SIZE + "px"
        }

        object Data {
            const val FONT_SIZE = FONT_MEDIUM
            const val FONT_SIZE_CSS = "" + FONT_SIZE + "px"
        }
    }

    class Plot {
        object Axis {
            const val TITLE_FONT_SIZE =
                FONT_MEDIUM
            const val TICK_FONT_SIZE =
                FONT_SMALL
            const val TICK_FONT_SIZE_SMALL =
                FONT_X_SMALL

            val LINE_COLOR = Color.parseHex(DARK_GRAY)
            val TICK_COLOR = Color.parseHex(DARK_GRAY)
            val GRID_LINE_COLOR = Color.parseHex(X_LIGHT_GRAY)

            // Bug in WebKit (?) : combination of style
            //    shape-rendering: crispedges;
            // and stroke-width less than 1
            // makes horizontal line disappear
            /*
      public static final double LINE_WIDTH = 0.8;
      public static final double TICK_LINE_WIDTH = 0.8;
      public static final double GRID_LINE_WIDTH = 0.8;
*/
            const val LINE_WIDTH = 1.0
            const val TICK_LINE_WIDTH = 1.0
            const val GRID_LINE_WIDTH = 1.0
        }
    }
}
