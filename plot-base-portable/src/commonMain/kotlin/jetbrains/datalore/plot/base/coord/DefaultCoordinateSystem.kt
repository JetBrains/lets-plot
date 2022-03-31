/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.coord

import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.CoordinateSystem
import kotlin.math.max
import kotlin.math.min

internal open class DefaultCoordinateSystem(
    val toClientOffsetX: (Double) -> Double,
    val toClientOffsetY: (Double) -> Double,
    val fromClientOffsetX: (Double) -> Double,
    val fromClientOffsetY: (Double) -> Double,
    val clientLimitsX: DoubleSpan?,
    val clientLimitsY: DoubleSpan?
) : CoordinateSystem {

    override fun toClient(p: DoubleVector): DoubleVector {
        return DoubleVector(toClientOffsetX(p.x), toClientOffsetY(p.y))
    }

    override fun fromClient(p: DoubleVector): DoubleVector {
        return DoubleVector(fromClientOffsetX(p.x), fromClientOffsetY(p.y))
    }

    override fun applyClientLimits(clientBounds: DoubleRectangle): DoubleRectangle {
//        val hRange = clientLimitsX?.let { lim -> convertRange(lim, toClientOffsetX) }
//            ?: clientBounds.xRange()
//        val vRange = clientLimitsY?.let { lim -> convertRange(lim, toClientOffsetY) }
//            ?: clientBounds.yRange()
//        return DoubleRectangle(hRange, vRange)
        return clientBounds
    }

    override fun flip(): CoordinateSystem {
        return FlippedCoordinateSystem(this)
    }


    companion object {
        private fun convertRange(range: DoubleSpan, offset: (Double) -> Double): DoubleSpan {
            val l = offset(range.lowerEnd)
            val u = offset(range.upperEnd)
            return DoubleSpan(
                min(l, u),
                max(l, u),
            )
        }
    }
}
