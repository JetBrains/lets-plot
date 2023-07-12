/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector

interface CoordinateSystem {
    fun toClient(p: DoubleVector): DoubleVector?

    fun toClient(r: DoubleRectangle): DoubleRectangle? {
        val leftTop = toClient(r.origin)
        val rightBottom = toClient(r.origin.add(r.dimension))
        return if (leftTop != null && rightBottom != null) {
            DoubleRectangle.span(leftTop, rightBottom)
        } else {
            null
        }
    }

    fun unitSize(p: DoubleVector): DoubleVector

    fun flip(): CoordinateSystem
}
