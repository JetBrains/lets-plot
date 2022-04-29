/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.defaultTheme.values

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.base.values.FontFamily

data class FontProperties(
    val family: FontFamily,
    val face: FontFace,
    val size: Double,
    val color: Color
)