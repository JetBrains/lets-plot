/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.svg.style

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.commons.values.FontFace

class TextStyle(
    val family: String,
    val face: FontFace,
    val size: Double,
    val color: Color
) {
    val isNoneSize: Boolean
        get() = size == NONE_SIZE

    val isNoneFamily: Boolean
        get() = family == NONE_FAMILY

    val isNoneColor: Boolean
        get() = color == NONE_COLOR

    companion object {
        const val NONE_SIZE = -1.0
        const val NONE_FAMILY = ""
        val NONE_COLOR = Color(1, 2, 3, 4)
    }
}