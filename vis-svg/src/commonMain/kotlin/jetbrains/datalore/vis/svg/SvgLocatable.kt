/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.svg

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector

interface SvgLocatable {

    val bBox: DoubleRectangle
    fun pointToTransformedCoordinates(point: DoubleVector): DoubleVector

    fun pointToAbsoluteCoordinates(point: DoubleVector): DoubleVector
}