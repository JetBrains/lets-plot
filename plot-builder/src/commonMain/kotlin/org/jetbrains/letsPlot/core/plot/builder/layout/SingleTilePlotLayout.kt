/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.layout

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.layout.Thickness
import org.jetbrains.letsPlot.core.plot.base.theme.AxisTheme
import org.jetbrains.letsPlot.core.plot.base.theme.PanelTheme
import org.jetbrains.letsPlot.core.plot.builder.coord.CoordProvider
import org.jetbrains.letsPlot.core.plot.builder.layout.PlotLayoutUtil.plotInsets
import org.jetbrains.letsPlot.core.plot.builder.layout.util.Insets
import org.jetbrains.letsPlot.core.plot.builder.scale.AxisPosition

internal class SingleTilePlotLayout(
    private val tileLayout: TileLayout,
    hAxisPosition: AxisPosition,
    vAxisPosition: AxisPosition,
    hAxisTheme: AxisTheme,
    vAxisTheme: AxisTheme,
    panelTheme: PanelTheme,
) : PlotLayout {

    private val insets: Insets = plotInsets(
        hAxisPosition, vAxisPosition,
        hAxisTheme, vAxisTheme
    )

    private val panelPadding = panelTheme.padding()

    override fun doLayout(preferredSize: DoubleVector, coordProvider: CoordProvider, plotMargins: Thickness): PlotLayoutInfo {
        return if (tileLayout.insideOut) {
            layoutByGeomSize(preferredSize, coordProvider)
        } else {
            layoutOuterSize(preferredSize, coordProvider, plotMargins)
        }
    }

    private fun layoutOuterSize(outerSize: DoubleVector, coordProvider: CoordProvider, plotMargins: Thickness): PlotLayoutInfo {
        val tilePreferredSize = outerSize
            .subtract(insets.leftTop)
            .subtract(insets.rightBottom)
            .subtract(DoubleVector(plotMargins.width(), plotMargins.height()))

        val tileInfo = tileLayout
            .doLayout(tilePreferredSize, coordProvider)
            .withOffset(insets.leftTop)

        return tileInfoToPlotInfo(tileInfo)
    }

    private fun layoutByGeomSize(geomSize: DoubleVector, coordProvider: CoordProvider): PlotLayoutInfo {
        val tileInfo = tileLayout
            .doLayout(geomSize, coordProvider)
            .withOffset(insets.leftTop)
            .withNormalizedOrigin()

        return tileInfoToPlotInfo(tileInfo)
    }

    private fun tileInfoToPlotInfo(tileInfo: TileLayoutInfo): PlotLayoutInfo {
        return PlotLayoutInfo(listOf(tileInfo), insets)
    }
}
