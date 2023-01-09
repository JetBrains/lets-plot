/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout.tile

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.geometry.DoubleVector.Companion.ZERO
import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.plot.builder.coord.CoordProvider
import jetbrains.datalore.plot.builder.layout.GeomMarginsLayout
import jetbrains.datalore.plot.builder.layout.util.GeomAreaInsets

internal object TileLayoutUtil {
    fun liveMapGeomBounds(plotSize: DoubleVector): DoubleRectangle {
        return DoubleRectangle(ZERO, plotSize)
    }

    fun geomOuterBounds(
        geomInsets: GeomAreaInsets,
        plotSize: DoubleVector,
        hDomain: DoubleSpan,
        vDomain: DoubleSpan,
        marginsLayout: GeomMarginsLayout,
        coordProvider: CoordProvider
    ): DoubleRectangle {
        val plottingArea = geomInsets.subtractFrom(DoubleRectangle(ZERO, plotSize))
        val geomInnerSize = marginsLayout.toInnerSize(plottingArea.dimension)

        val geomOuterSizeAdjusted = coordProvider.adjustGeomSize(hDomain, vDomain, geomInnerSize).let {
            marginsLayout.toOuterSize(it)
        }
        return DoubleRectangle(plottingArea.origin, geomOuterSizeAdjusted)
    }
}
