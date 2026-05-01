/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.layout

import org.jetbrains.letsPlot.commons.geometry.DoubleInsets
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.layout.Thickness
import org.jetbrains.letsPlot.core.plot.base.theme.PlotTheme
import org.jetbrains.letsPlot.core.plot.builder.coord.CoordProvider
import org.jetbrains.letsPlot.core.plot.builder.layout.PlotLayoutUtil.plotInsets

internal class SingleTilePlotLayout constructor(
    private val tileLayout: TileLayout,
    private val plotTheme: PlotTheme
) : PlotLayout {

    private val insets: DoubleInsets = plotInsets(plotTheme.plotInset())

    override fun layoutByPlotSize(plotInnerSize: DoubleVector, coordProvider: CoordProvider): PlotLayoutInfo {
        val geomWithAxisSize = plotInnerSize
            .subtract(insets.leftTop)
            .subtract(insets.rightBottom)

        val tileInfo = tileLayout
            .doTopDownLayout(geomWithAxisSize, coordProvider)
            .withOffset(insets.leftTop)

        return tileInfoToPlotInfo(tileInfo)
    }

    override fun layoutByGeomSize(
        geomContentSize: DoubleVector,
        coordProvider: CoordProvider,
        axisSpacer: Thickness
    ): PlotLayoutInfo {
        val tileInfo = tileLayout
            .doInsideOutLayout(geomContentSize, coordProvider, axisSpacer)
            .withOffset(insets.leftTop)
            .withNormalizedOrigin()

        return tileInfoToPlotInfo(tileInfo)
    }

    private fun tileInfoToPlotInfo(tileInfo: TileLayoutInfo): PlotLayoutInfo {
        return PlotLayoutInfo(listOf(tileInfo), insets)
    }
}
