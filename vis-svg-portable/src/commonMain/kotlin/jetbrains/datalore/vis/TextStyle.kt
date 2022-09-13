/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.base.values.FontFace
import jetbrains.datalore.base.values.FontFamily

data class TextStyle(
    val family: FontFamily,
    val face: FontFace,
    val size: Double,
    val monospaced: Boolean,
    val color: Color
)