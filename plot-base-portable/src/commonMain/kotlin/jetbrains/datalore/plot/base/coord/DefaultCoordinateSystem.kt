/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.coord

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.CoordinateSystem

internal class DefaultCoordinateSystem(
    val toClientOffsetX: (Double) -> Double,
    val toClientOffsetY: (Double) -> Double,
    private val coordMapper: CoordinatesMapper,
) : CoordinateSystem {
    override fun toClient(p: DoubleVector): DoubleVector? {
        val mapped = coordMapper.toClient(p)
        return if (mapped != null) {
            DoubleVector(
                toClientOffsetX(mapped.x),
                toClientOffsetY(mapped.y)
            )
        } else {
            null
        }
    }

    override fun unitSize(p: DoubleVector): DoubleVector {
        return coordMapper.unitSize(p)
    }

    override fun flip(): CoordinateSystem {
        return DefaultCoordinateSystem(
            toClientOffsetX,
            toClientOffsetY,
            coordMapper.flip()
        )
    }
}
