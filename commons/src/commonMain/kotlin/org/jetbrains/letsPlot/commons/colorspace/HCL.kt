/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.colorspace


/**
 * Hue, Chroma, Lightness
 *  h: 0..360
 *  c: 0..100
 *  l: 0..100
 */
data class HCL(
    val h: Double,
    val c: Double,
    val l: Double
)
