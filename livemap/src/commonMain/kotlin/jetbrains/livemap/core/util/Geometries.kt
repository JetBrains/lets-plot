/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.core.util

import org.jetbrains.letsPlot.commons.geometry.DoubleVector

object Geometries {
    operator fun DoubleVector.minus(doubleVector: DoubleVector): DoubleVector = subtract(doubleVector)
    operator fun DoubleVector.plus(doubleVector: DoubleVector): DoubleVector = add(doubleVector)

    fun inside(x: Number, y: Number, rectOrigin: DoubleVector, rectDimension: DoubleVector): Boolean {
        if (x.toDouble() < rectOrigin.x) return false
        if (y.toDouble() < rectOrigin.y) return false
        if (x.toDouble() > rectOrigin.x + rectDimension.x) return false
        if (y.toDouble() > rectOrigin.y + rectDimension.y) return false

        return true
    }
}
