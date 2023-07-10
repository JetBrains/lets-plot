/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.coord

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import jetbrains.datalore.base.unsupported.UNSUPPORTED

internal class MarginalLayerCoordProvider : CoordProviderBase(
    xLim = null,
    yLim = null,
    flipped = false
) {

    override fun with(xLim: DoubleSpan?, yLim: DoubleSpan?, flipped: Boolean): CoordProvider {
        UNSUPPORTED("MarginalLayerCoordProvider.with()")
    }

    override fun adjustGeomSize(hDomain: DoubleSpan, vDomain: DoubleSpan, geomSize: DoubleVector): DoubleVector {
        return geomSize
    }
}