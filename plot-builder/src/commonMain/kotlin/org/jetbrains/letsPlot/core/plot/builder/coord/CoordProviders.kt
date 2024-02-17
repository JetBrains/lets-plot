/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.coord

import org.jetbrains.letsPlot.commons.intern.spatial.projections.Projection
import org.jetbrains.letsPlot.commons.intern.spatial.projections.mercator

private val NO_LIM: Pair<Double?, Double?> = Pair(null, null)

object CoordProviders {
    @Suppress("NAME_SHADOWING")
    fun cartesian(
        xLim: Pair<Double?, Double?> = NO_LIM,
        yLim: Pair<Double?, Double?> = NO_LIM,
        flipped: Boolean = false
    ): CoordProvider {
        return CartesianCoordProvider(xLim, yLim, flipped)
    }

    @Suppress("NAME_SHADOWING")
    fun fixed(
        ratio: Double,
        xLim: Pair<Double?, Double?> = NO_LIM,
        yLim: Pair<Double?, Double?> = NO_LIM,
        flipped: Boolean = false
    ): CoordProvider {
        return FixedRatioCoordProvider(ratio, xLim, yLim, flipped)
    }

    @Suppress("NAME_SHADOWING")
    fun map(
        xLim: Pair<Double?, Double?> = NO_LIM,
        yLim: Pair<Double?, Double?> = NO_LIM,
        flipped: Boolean = false,
        projection: Projection = mercator()
    ): CoordProvider {
        return ProjectionCoordProvider(
            projection,
            xLim,
            yLim,
            flipped
        )
    }

    fun polar(
        xLim: Pair<Double?, Double?> = NO_LIM,
        yLim: Pair<Double?, Double?> = NO_LIM,
        flipped: Boolean,
        start: Double,
        clockwise: Boolean
    ): CoordProvider {
        return PolarCoordProvider(xLim, yLim, flipped, start, clockwise)
    }
}
