/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.coord

import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.base.spatial.projections.azimuthalEqualArea
import jetbrains.datalore.base.spatial.projections.conicEqualArea
import jetbrains.datalore.base.spatial.projections.mercator

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
        flipped: Boolean = false,
        projection: String = "mercator"
    ): CoordProvider {
        // Only Mercator so far.
        val mapProjection = when (projection) {
            "mercator" -> mercator()
            "conic" -> conicEqualArea()
            "azimuthal" -> azimuthalEqualArea()
            else -> mercator()
        }
        return ProjectionCoordProvider(
            mapProjection,
            xLim,
            yLim,
            flipped
        )
    }
}
