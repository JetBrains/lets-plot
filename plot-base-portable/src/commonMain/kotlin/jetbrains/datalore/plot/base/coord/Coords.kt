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
        coordMapper: CoordinatesMapper,
    ): CoordinateSystem {
        return DefaultCoordinateSystem(coordMapper)
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
