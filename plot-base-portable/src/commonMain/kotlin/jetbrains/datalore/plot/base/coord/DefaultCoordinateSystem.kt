/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.coord

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.base.spatial.projections.Projection
import jetbrains.datalore.plot.base.CoordinateSystem

internal open class DefaultCoordinateSystem(
    val toClientOffsetX: (Double) -> Double,
    val toClientOffsetY: (Double) -> Double,
    val fromClientOffsetX: (Double) -> Double,
    val fromClientOffsetY: (Double) -> Double,
    val clientLimitsX: DoubleSpan?,
    val clientLimitsY: DoubleSpan?,
    val projection: Projection,
) : CoordinateSystem {
    override fun toClient(p: DoubleVector): DoubleVector {
        val projected = projection.project(p)
            ?: throw IllegalArgumentException("Can't poject $p using projection ${projection::class.simpleName}")
        return DoubleVector(
            toClientOffsetX(projected.x),
            toClientOffsetY(projected.y)
        )
    }

    override fun flip(): CoordinateSystem {
        return FlippedCoordinateSystem(this)
    }
}
