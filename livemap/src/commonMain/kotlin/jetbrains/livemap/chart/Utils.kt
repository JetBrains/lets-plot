/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.chart

import jetbrains.datalore.base.values.Color
import kotlin.math.min

fun changeAlphaWithMin(color: Color, newAlpha: Int?): Color {
    return newAlpha?.let { min(it, color.alpha) }?.let(color::changeAlpha) ?: color
}
