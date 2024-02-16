/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.layout.tile

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.geometry.DoubleVector.Companion.ZERO
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.layout.Thickness
import org.jetbrains.letsPlot.core.plot.builder.coord.CoordProvider
import org.jetbrains.letsPlot.core.plot.builder.layout.GeomMarginsLayout
import org.jetbrains.letsPlot.core.plot.builder.layout.util.GeomAreaInsets

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
        panelPadding: Thickness,
        coordProvider: CoordProvider
    ): DoubleRectangle {
        val plottingArea = geomInsets.subtractFrom(DoubleRectangle(ZERO, plotSize))
        val panelSize = marginsLayout.toInnerSize(plottingArea.dimension)
        val contentSize = panelPadding.shrinkSize(panelSize)

        val geomOuterSizeAdjusted = coordProvider.adjustGeomSize(hDomain, vDomain, contentSize)
            .let(panelPadding::inflateSize)
            .let(marginsLayout::toOuterSize)

        return DoubleRectangle(plottingArea.origin, geomOuterSizeAdjusted)
    }
}
