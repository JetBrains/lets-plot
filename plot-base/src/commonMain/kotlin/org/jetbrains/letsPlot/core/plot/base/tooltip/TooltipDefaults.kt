/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.tooltip

import org.jetbrains.letsPlot.commons.values.Color

object TooltipDefaults {
    const val MAX_POINTER_FOOTING_LENGTH = 12.0
    const val POINTER_FOOTING_TO_SIDE_LENGTH_RATIO = 0.4

    const val MARGIN_BETWEEN_TOOLTIPS = 5.0
    const val DATA_TOOLTIP_FONT_SIZE = 13.0
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

    const val AXIS_TOOLTIP_FONT_SIZE = 13.0
    val AXIS_TOOLTIP_COLOR = Color.parseHex("#3d3d3d")

    const val ROTATION_ANGLE = 15.0
}
