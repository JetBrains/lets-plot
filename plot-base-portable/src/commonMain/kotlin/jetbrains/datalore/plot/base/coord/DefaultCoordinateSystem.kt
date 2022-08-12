/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.coord

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.CoordinateSystem

internal open class DefaultCoordinateSystem(
    val toClientOffsetX: (Double) -> Double,
    val toClientOffsetY: (Double) -> Double,
    val coordMapper: CoordinatesMapper,
) : CoordinateSystem {
    override fun toClient(p: DoubleVector): DoubleVector {
        val mapped = coordMapper.toClient(p)
        return if (mapped == null) {
            // ToDo: male `toClient` nullable
            throw IllegalStateException("Can't poject $p using projection ${coordMapper.projection::class.simpleName}")
        } else {
            DoubleVector(
                toClientOffsetX(mapped.x),
                toClientOffsetY(mapped.y)
            )
        }
    }

    override fun flip(): CoordinateSystem {
        return FlippedCoordinateSystem(this)
    }
}
