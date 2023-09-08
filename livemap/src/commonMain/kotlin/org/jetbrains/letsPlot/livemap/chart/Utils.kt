/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.chart

import org.jetbrains.letsPlot.commons.values.Color
import kotlin.math.min

fun alphaScaledColor(color: Color, newAlpha: Int?): Color {
    return when {
        newAlpha == null -> color.alpha
        else -> min(newAlpha, color.alpha)
    }.let(color::changeAlpha)
}

fun scaledPointSize(size: Double, scalingSizeFactor: Double): Double {
    return size * scalingSizeFactor / 2.0
}

fun scaledStrokeWidth(strokeWidth: Double, scalingSizeFactor: Double): Double {
    return strokeWidth * scalingSizeFactor
}

fun scaledLineDash(lineDash: DoubleArray, scalingSizeFactor: Double): DoubleArray {
    return lineDash.map { it * scalingSizeFactor }.toDoubleArray()
}