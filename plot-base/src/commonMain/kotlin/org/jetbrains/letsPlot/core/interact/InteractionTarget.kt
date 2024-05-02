/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.interact

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector

interface InteractionTarget {
    fun zoom(geomBounds: DoubleRectangle)
    fun pan(offset: DoubleVector)

    val geomBounds: DoubleRectangle
}