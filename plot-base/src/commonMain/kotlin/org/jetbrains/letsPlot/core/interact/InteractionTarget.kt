/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.interact

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector

interface InteractionTarget {
    fun zoom(offset: DoubleVector, scale: DoubleVector)
    fun pan(from: DoubleVector, to: DoubleVector): DoubleVector?
    fun panEnd(from: DoubleVector, to: DoubleVector): DoubleVector?

    val geomBounds: DoubleRectangle
}