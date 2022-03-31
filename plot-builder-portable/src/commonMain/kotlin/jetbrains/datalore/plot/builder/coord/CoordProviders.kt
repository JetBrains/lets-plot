/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.coord

import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.plot.builder.coord.map.MercatorProjectionX
import jetbrains.datalore.plot.builder.coord.map.MercatorProjectionY

object CoordProviders {
    @Suppress("NAME_SHADOWING")
    fun cartesian(
        xLim: DoubleSpan? = null,
        yLim: DoubleSpan? = null,
        flipped: Boolean = false
    ): CoordProvider {
        return CartesianCoordProvider(xLim, yLim, flipped)
    }

    @Suppress("NAME_SHADOWING")
    fun fixed(
        ratio: Double,
        xLim: DoubleSpan? = null,
        yLim: DoubleSpan? = null,
        flipped: Boolean = false
    ): CoordProvider {
        return FixedRatioCoordProvider(ratio, xLim, yLim, flipped)
    }

    @Suppress("NAME_SHADOWING")
    fun map(
        xLim: DoubleSpan? = null,
        yLim: DoubleSpan? = null,
        flipped: Boolean = false
    ): CoordProvider {
        // Only Mercator so far.
        return ProjectionCoordProvider(
            MercatorProjectionX(),
            MercatorProjectionY(),
            xLim,
            yLim,
            flipped
        )
    }
}
