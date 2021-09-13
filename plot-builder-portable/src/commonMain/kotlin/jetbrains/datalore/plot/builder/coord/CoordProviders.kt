/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.coord

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.plot.FeatureSwitch.FLIP_AXIS
import jetbrains.datalore.plot.builder.coord.map.MercatorProjectionX
import jetbrains.datalore.plot.builder.coord.map.MercatorProjectionY

object CoordProviders {
    @Suppress("NAME_SHADOWING")
    fun cartesian(xLim: ClosedRange<Double>? = null, yLim: ClosedRange<Double>? = null): CoordProvider {
        val (xLim, yLim) = when {
            FLIP_AXIS -> Pair(yLim, xLim)
            else -> Pair(xLim, yLim)
        }
        return CartesianCoordProvider(xLim, yLim)
    }

    @Suppress("NAME_SHADOWING")
    fun fixed(
        ratio: Double,
        xLim: ClosedRange<Double>? = null,
        yLim: ClosedRange<Double>? = null
    ): CoordProvider {
        val (xLim, yLim) = when {
            FLIP_AXIS -> Pair(yLim, xLim)
            else -> Pair(xLim, yLim)
        }
        return FixedRatioCoordProvider(ratio, xLim, yLim)
    }

    @Suppress("NAME_SHADOWING")
    fun map(
        xLim: ClosedRange<Double>? = null,
        yLim: ClosedRange<Double>? = null
    ): CoordProvider {
        val (xLim, yLim) = when {
            FLIP_AXIS -> Pair(yLim, xLim)
            else -> Pair(xLim, yLim)
        }
        // Only Mercator so far.
        return ProjectionCoordProvider(
            MercatorProjectionX(),
            MercatorProjectionY(),
            xLim,
            yLim
        )
    }
}
