/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.coord

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.base.spatial.projections.identity
import jetbrains.datalore.plot.base.CoordinateSystem

object Coords {
    fun create(
        coordMapper: CoordinatesMapper,
    ): CoordinateSystem {
        return DefaultCoordinateSystem(coordMapper)
    }

    object DemoAndTest {
        fun create(
            xDomain: DoubleSpan,
            yDomain: DoubleSpan,
            clientSize: DoubleVector
        ): CoordinateSystem {
            val mapper = CoordinatesMapper.create(
                adjustedDomain = DoubleRectangle.hvRange(xDomain, yDomain),
                clientSize = clientSize,
                projection = identity(),
                flipAxis = false
            )
            return DefaultCoordinateSystem(mapper)
        }
    }
}
