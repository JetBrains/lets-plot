/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.coord

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.base.unsupported.UNSUPPORTED
import jetbrains.datalore.plot.base.CoordinateSystem

object Coords {
    fun create(
        xRange: DoubleSpan,
        yRange: DoubleSpan,
        coordMapper: CoordinatesMapper,
    ): CoordinateSystem {
        val origin = DoubleVector(
            originX(xRange),
            originY(yRange)
        )
        return create(origin, coordMapper)
    }

    fun create(
        origin: DoubleVector,
        coordMapper: CoordinatesMapper
    ): CoordinateSystem {
        return DefaultCoordinateSystem(
            toClientOffsetX(origin.x),
            toClientOffsetY(origin.y),
            coordMapper
        )
    }

    fun toClientOffsetX(xRange: DoubleSpan): (Double) -> Double {
        return toClientOffsetX(
            originX(
                xRange
            )
        )
    }

    fun toClientOffsetY(yRange: DoubleSpan): (Double) -> Double {
        return toClientOffsetY(
            originY(
                yRange
            )
        )
    }

    private fun originX(xRange: DoubleSpan): Double {
        return -xRange.lowerEnd
    }

    private fun originY(yRange: DoubleSpan): Double {
        return yRange.upperEnd
    }

    private fun toClientOffsetX(originX: Double): (Double) -> Double {
        return { x -> originX + x }
    }

    private fun toClientOffsetY(originY: Double): (Double) -> Double {
        // y-axis is inverted
        return { y -> originY - y }
    }


    // ToDo: Old signature used in demos: ned to update demos.
    fun create(
        xRange: DoubleSpan,
        yRange: DoubleSpan,
    ): CoordinateSystem {
        UNSUPPORTED()
    }

    // ToDo: Old signature used in demos: ned to update demos.
    fun create(
        origin: DoubleVector,
    ): CoordinateSystem {
        UNSUPPORTED()
    }

}
