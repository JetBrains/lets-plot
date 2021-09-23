/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.coord

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.gcommon.collect.Comparables.max
import jetbrains.datalore.base.gcommon.collect.Comparables.min
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.CoordinateSystem

internal open class DefaultCoordinateSystem(
    val toClientOffsetX: (Double) -> Double,
    val toClientOffsetY: (Double) -> Double,
    val fromClientOffsetX: (Double) -> Double,
    val fromClientOffsetY: (Double) -> Double,
    val xLimits: ClosedRange<Double>?,
    val yLimits: ClosedRange<Double>?
) : CoordinateSystem {

    override fun toClient(p: DoubleVector): DoubleVector {
        return DoubleVector(toClientOffsetX(p.x), toClientOffsetY(p.y))
    }

    override fun fromClient(p: DoubleVector): DoubleVector {
        return DoubleVector(fromClientOffsetX(p.x), fromClientOffsetY(p.y))
    }

    override fun applyClientLimits(clientBounds: DoubleRectangle): DoubleRectangle {
        val hRange = xLimits?.let { lim -> convertRange(lim, toClientOffsetX) }
            ?: clientBounds.xRange()
        val vRange = yLimits?.let { lim -> convertRange(lim, toClientOffsetY) }
            ?: clientBounds.yRange()
        return DoubleRectangle(hRange, vRange)
    }

    override fun flip(): CoordinateSystem {
        return FlippedCoordinateSystem(this)
    }


    companion object {
        private fun convertRange(range: ClosedRange<Double>, transform: (Double) -> Double): ClosedRange<Double> {
            val l = transform(range.lowerEnd)
            val u = transform(range.upperEnd)
            return ClosedRange(
                min(l, u),
                max(l, u),
            )
        }
    }
}
