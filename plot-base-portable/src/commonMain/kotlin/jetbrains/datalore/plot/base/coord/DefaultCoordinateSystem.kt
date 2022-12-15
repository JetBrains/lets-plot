/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.coord

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.CoordinateSystem

internal class DefaultCoordinateSystem(
    private val coordMapper: CoordinatesMapper,
) : CoordinateSystem {

    private val clientLeft = coordMapper.clientBounds.xRange().lowerEnd
    private val clientBottom = coordMapper.clientBounds.yRange().upperEnd

    override fun toClient(p: DoubleVector): DoubleVector? {
        val mapped = coordMapper.toClient(p)
        return if (mapped != null) {
            toScreen(mapped)
        } else {
            null
        }
    }

    override fun unitSize(p: DoubleVector): DoubleVector {
        return coordMapper.unitSize(p)
    }

    override fun flip(): CoordinateSystem {
        return DefaultCoordinateSystem(
            coordMapper.flip()
        )
    }

    private fun toScreen(p: DoubleVector): DoubleVector {
        val x = p.x - clientLeft
        val y = clientBottom - p.y
        return DoubleVector(x, y)
    }
}
