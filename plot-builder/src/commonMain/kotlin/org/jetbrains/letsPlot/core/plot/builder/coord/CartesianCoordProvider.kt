/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.coord

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.interval.DoubleSpan

internal class CartesianCoordProvider(
    xLim: Pair<Double?, Double?>,
    yLim: Pair<Double?, Double?>,
    flipped: Boolean = false
) : CoordProviderBase(xLim, yLim, flipped) {

    override fun with(
        xLim: Pair<Double?, Double?>,
        yLim: Pair<Double?, Double?>,
        flipped: Boolean
    ): CoordProvider {
        return CartesianCoordProvider(xLim, yLim, flipped)
    }

    override fun adjustGeomSize(
        hDomain: DoubleSpan,
        vDomain: DoubleSpan,
        geomSize: DoubleVector
    ): DoubleVector {
        // No adjustment needed.
        return geomSize
    }
}