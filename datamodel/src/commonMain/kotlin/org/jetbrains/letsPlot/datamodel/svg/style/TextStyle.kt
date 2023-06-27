/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.svg.style

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.base.values.FontFace

data class TextStyle(
    val family: String,
    val face: FontFace,
    val size: Double,
    val color: Color
)