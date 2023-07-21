/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.chart

import org.jetbrains.letsPlot.commons.values.Color
import kotlin.math.min

fun changeAlphaWithMin(color: Color, newAlpha: Int?): Color {
    return newAlpha?.let { min(it, color.alpha) }?.let(color::changeAlpha) ?: color
}
