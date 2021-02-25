/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.CoordinateSystem

internal class BogusCoordinateSystem : CoordinateSystem {
    override fun toClient(p: DoubleVector): DoubleVector {
        throw IllegalStateException("Bogus coordinate system is not supposed to be used.")
    }

    override fun fromClient(p: DoubleVector): DoubleVector {
        throw IllegalStateException("Bogus coordinate system is not supposed to be used.")
    }

    override fun isPointInLimits(p: DoubleVector): Boolean {
        throw IllegalStateException("Bogus coordinate system is not supposed to be used.")
    }

    override fun isRectInLimits(rect: DoubleRectangle): Boolean {
        throw IllegalStateException("Bogus coordinate system is not supposed to be used.")
    }

    override fun isPathInLimits(path: List<DoubleVector>): Boolean {
        throw IllegalStateException("Bogus coordinate system is not supposed to be used.")
    }

    override fun isPolygonInLimits(polygon: List<DoubleVector>): Boolean {
        throw IllegalStateException("Bogus coordinate system is not supposed to be used.")
    }

    override val xLimitRange: ClosedRange<Double>
        get() = throw IllegalStateException("Bogus coordinate system is not supposed to be used.")

    override val yLimitRange: ClosedRange<Double>
        get() = throw IllegalStateException("Bogus coordinate system is not supposed to be used.")
}
