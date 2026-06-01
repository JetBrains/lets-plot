/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.svg.style

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.commons.values.FontFace

class TextStyle(
    val family: String,
    val face: FontFace,
    val size: Double,
    color: Color,
    fillOpacity: Double? = null
) {
    /**
     * SVG text style keeps `fill` and `fill-opacity` separately.
     *
     * If `fillOpacity` is specified, it is used as the opacity and any alpha channel
     * in `color` is discarded. Otherwise, alpha from `color` is split into
     * `fillOpacity`.
     */
    val color: Color = color.withAlpha(255)
    val fillOpacity: Double? = fillOpacity?.coerceIn(0.0, 1.0)
        ?: color.alpha.takeIf { it < 255 }?.let { it / 255.0 }

    val isNoneSize: Boolean
        get() = size == NONE_SIZE

    val isNoneFamily: Boolean
        get() = family == NONE_FAMILY

    val isNoneColor: Boolean
        get() = color == NONE_COLOR

    companion object {
        const val NONE_SIZE = -1.0
        const val NONE_FAMILY = ""
        val NONE_COLOR = Color(1, 2, 3)
    }
}
