/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.svg.dom

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector

interface SvgLocatable {

    val bBox: DoubleRectangle
    fun pointToTransformedCoordinates(point: DoubleVector): DoubleVector

    fun pointToAbsoluteCoordinates(point: DoubleVector): DoubleVector
}