/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.coord

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.plot.builder.coord.map.MercatorProjectionX
import jetbrains.datalore.plot.builder.coord.map.MercatorProjectionY

object CoordProviders {
    fun cartesian(xLim: ClosedRange<Double>? = null, yLim: ClosedRange<Double>? = null): CoordProvider {
        return CartesianCoordProvider(xLim, yLim)
    }

    fun fixed(
        ratio: Double,
        xLim: ClosedRange<Double>? = null,
        yLim: ClosedRange<Double>? = null
    ): CoordProvider {
        return FixedRatioCoordProvider(ratio, xLim, yLim)
    }

    fun map(
        xLim: ClosedRange<Double>? = null,
        yLim: ClosedRange<Double>? = null
    ): CoordProvider {
        // Only Mercator so far.
        return ProjectionCoordProvider(
            MercatorProjectionX(),
            MercatorProjectionY(),
            xLim,
            yLim
        )
    }
}
