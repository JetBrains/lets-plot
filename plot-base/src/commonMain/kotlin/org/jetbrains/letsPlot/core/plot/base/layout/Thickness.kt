/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.layout

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector

class Thickness(
    val top: Double = 0.0,
    val right: Double = 0.0,
    val bottom: Double = 0.0,
    val left: Double = 0.0
) {

    val leftTop = DoubleVector(left, top)
    val rightBottom = DoubleVector(right, bottom)

    fun width() = left + right
    fun height() = top + bottom

    val size = DoubleVector(width(), height())

    fun inflateRect(r: DoubleRectangle): DoubleRectangle {
        return DoubleRectangle(
            r.origin.subtract(leftTop),
            r.dimension.add(size)
        )
    }

    fun shrinkRect(r: DoubleRectangle): DoubleRectangle {
        return DoubleRectangle(
            r.origin.add(leftTop),
            r.dimension.subtract(size)
        )
    }

    fun inflateSize(size: DoubleVector): DoubleVector = size.add(this.size)
    fun shrinkSize(size: DoubleVector): DoubleVector = size.subtract(this.size)

    override fun toString(): String {
        return "Thickness(top=$top, right=$right, bottom=$bottom, left=$left)"
    }

    companion object {
        val ZERO = Thickness()
    }
}