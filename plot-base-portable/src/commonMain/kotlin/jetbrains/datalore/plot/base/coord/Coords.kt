/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.coord

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.CoordinateSystem

object Coords {
    fun create(xRange: ClosedRange<Double>, yRange: ClosedRange<Double>): CoordinateSystem {
        val origin = DoubleVector(
            originX(xRange),
            originY(yRange)
        )
        return create(origin)
    }

    fun create(origin: DoubleVector): CoordinateSystem {
        return DefaultCoordinateSystem(
            toClientOffsetX(origin.x),
            toClientOffsetY(origin.y),
            fromClientOffsetX(origin.x),
            fromClientOffsetY(origin.y)
        )
    }

    fun toClientOffsetX(xRange: ClosedRange<Double>): (Double) -> Double {
        return toClientOffsetX(
            originX(
                xRange
            )
        )
    }

    fun toClientOffsetY(yRange: ClosedRange<Double>): (Double) -> Double {
        return toClientOffsetY(
            originY(
                yRange
            )
        )
    }

    private fun originX(xRange: ClosedRange<Double>): Double {
        return -xRange.lowerEndpoint()
    }

    private fun originY(yRange: ClosedRange<Double>): Double {
        return yRange.upperEndpoint()
    }

    private fun toClientOffsetX(originX: Double): (Double) -> Double {
        return { x -> originX + x }
    }

    private fun fromClientOffsetX(originX: Double): (Double) -> Double {
        return { x -> x - originX }
    }

    private fun toClientOffsetY(originY: Double): (Double) -> Double {
        // y-axis is inverted
        return { y -> originY - y }
    }

    private fun fromClientOffsetY(originY: Double): (Double) -> Double {
        // y-axis is inverted
        return { y -> originY - y }
    }
}
