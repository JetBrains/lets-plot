/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.builder.coord.CoordProvider
import jetbrains.datalore.plot.builder.layout.PlotLayoutUtil.plotInsets
import jetbrains.datalore.plot.builder.layout.util.Insets
import jetbrains.datalore.plot.builder.scale.AxisPosition
import jetbrains.datalore.plot.builder.theme.AxisTheme

internal class SingleTilePlotLayout constructor(
    private val tileLayout: TileLayout,
    hAxisPosition: AxisPosition,
    vAxisPosition: AxisPosition,
    hAxisTheme: AxisTheme,
    vAxisTheme: AxisTheme,
) : PlotLayout {

    private val insets: Insets = plotInsets(
        hAxisPosition, vAxisPosition,
        hAxisTheme, vAxisTheme
    )

    override fun doLayout(preferredSize: DoubleVector, coordProvider: CoordProvider): PlotLayoutInfo {
        return if (tileLayout.insideOut) {
            layoutByGeomSize(preferredSize, coordProvider)
        } else {
            layoutOuterSize(preferredSize, coordProvider)
        }
    }

    private fun layoutOuterSize(outerSize: DoubleVector, coordProvider: CoordProvider): PlotLayoutInfo {
        val tilePreferredSize = outerSize
            .subtract(insets.leftTop)
            .subtract(insets.rightBottom)

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
