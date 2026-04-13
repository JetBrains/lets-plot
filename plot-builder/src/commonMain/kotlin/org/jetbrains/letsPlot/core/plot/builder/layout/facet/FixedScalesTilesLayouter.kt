/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.layout.facet

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.theme.AxisTheme
import org.jetbrains.letsPlot.core.plot.builder.assemble.PlotFacets
import org.jetbrains.letsPlot.core.plot.builder.coord.CoordProvider
import org.jetbrains.letsPlot.core.plot.builder.layout.FacetedPlotLayoutUtil.tilesAreaSize
import org.jetbrains.letsPlot.core.plot.builder.layout.TileLayoutInfo
import org.jetbrains.letsPlot.core.plot.builder.layout.TileLayoutProvider
import kotlin.math.abs

internal object FixedScalesTilesLayouter {
    fun createTileLayoutInfos(
        tilesAreaSize: DoubleVector,
        facets: PlotFacets,
        layoutProviderByTile: List<TileLayoutProvider>,
        addedHSize: Double,
        addedVSize: Double,
        coordProvider: CoordProvider,
        hAxisTheme: AxisTheme,
        vAxisTheme: AxisTheme,
    ): List<TileLayoutInfo> {

        val facetTiles = facets.tileInfos()

        // Estimate per-tile panel size.
        val geomWithAxisSizeEstimate = DoubleVector(
            (tilesAreaSize.x - addedHSize) / facets.colCount,
            (tilesAreaSize.y - addedVSize) / facets.rowCount
        )

        // 1st iteration

        // With 'fixed' scales lay out just one tile (because all tiles are identical).
        val tileLayout = layoutProviderByTile[0].createTileLayout()
        val tileLayoutInfo: TileLayoutInfo = tileLayout.doTopDownLayout(
            geomWithAxisSizeEstimate,
            coordProvider
        )

        val tileLayoutInfos = facetTiles.map {
            tileLayoutInfo.withAxisShown(
                it.hasHAxis,
                it.hasVAxis
            )
        }

        // adjust geom size
        val tilesAreaSizeNew = tilesAreaSize(
            tileLayoutInfos,
            facets,
            addedHSize,
            addedVSize
        )

        val widthDiff = tilesAreaSize.x - tilesAreaSizeNew.x
        val heightDiff = tilesAreaSize.y - tilesAreaSizeNew.y

        // Error 1 px per tile is ok.
        if (abs(widthDiff) <= facets.colCount && abs(heightDiff) <= facets.rowCount) {
            return tileLayoutInfos
        }

        // 2nd iteration

        val widthDelta = widthDiff / facets.colCount
        val heightDelta = heightDiff / facets.rowCount

        val geomWithAxisSizeAdjusted = DoubleVector(
            tileLayoutInfo.geomWithAxisBounds.width + widthDelta,
            tileLayoutInfo.geomWithAxisBounds.height + heightDelta
        )
        val tileLayoutInfo2 = tileLayout.doTopDownLayout(
            geomWithAxisSizeAdjusted,
            coordProvider
        )

        return facetTiles.map {
            tileLayoutInfo2.withAxisShown(
                it.hasHAxis,
                it.hasVAxis
            )
        }
    }
}