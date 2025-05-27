/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom.repel

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import kotlin.math.sign

object DoubleVectorExtensions {
    fun DoubleVector.dot(other: DoubleVector) = x * other.x + y * other.y

    fun DoubleVector.getXVector() = DoubleVector(x, 0.0)

    fun DoubleVector.getYVector() = DoubleVector(0.0, y)

    fun DoubleVector.hadamard(other: DoubleVector) = DoubleVector(x * other.x, y * other.y)

    fun DoubleVector.sign() = DoubleVector(x.sign, y.sign)
}