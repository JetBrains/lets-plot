/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.coord

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.commons.unsupported.UNSUPPORTED

internal class MarginalLayerCoordProvider : CoordProviderBase(
    xLim = Pair(null, null),
    yLim = Pair(null, null),
    flipped = false
) {

    override fun with(xLim: Pair<Double?, Double?>, yLim: Pair<Double?, Double?>, flipped: Boolean): CoordProvider {
        UNSUPPORTED("MarginalLayerCoordProvider.with()")
    }

    override fun adjustGeomSize(hDomain: DoubleSpan, vDomain: DoubleSpan, geomSize: DoubleVector): DoubleVector {
        return geomSize
    }
}