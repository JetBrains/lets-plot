/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.coord

import org.jetbrains.letsPlot.commons.intern.spatial.projections.Projection
import org.jetbrains.letsPlot.commons.intern.spatial.projections.mercator
import org.jetbrains.letsPlot.commons.interval.DoubleSpan

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
        xLim: DoubleSpan? = null,
        yLim: DoubleSpan? = null,
        flipped: Boolean,
        start: Double,
        clockwise: Boolean
    ): CoordProvider {
        return PolarCoordProvider(xLim, yLim, flipped, start, clockwise)
    }
}
