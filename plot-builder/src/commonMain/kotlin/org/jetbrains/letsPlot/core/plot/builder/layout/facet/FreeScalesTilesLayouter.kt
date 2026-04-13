/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.layout.facet

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.theme.AxisTheme
import org.jetbrains.letsPlot.core.plot.builder.assemble.PlotFacets
import org.jetbrains.letsPlot.core.plot.builder.coord.CoordProvider
import org.jetbrains.letsPlot.core.plot.builder.layout.FacetedPlotLayoutUtil
import org.jetbrains.letsPlot.core.plot.builder.layout.TileLayoutInfo
import org.jetbrains.letsPlot.core.plot.builder.layout.TileLayoutProvider
import kotlin.math.abs

internal object FreeScalesTilesLayouter {
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

        val layoutByTile = layoutProviderByTile.map {
            it.createTileLayout()
        }

        val facetTileAndLayout = facetTiles.map {
            Pair(it, layoutByTile[it.trueIndex])
        }

        // Estimate per-tile panel size.
        val geomWithAxisSizeEstimate = DoubleVector(
            (tilesAreaSize.x - addedHSize) / facets.colCount,
            (tilesAreaSize.y - addedVSize) / facets.rowCount
        )

        // Estimate geom content size.
        val geomContentSizeEstimate = layoutByTile[0]
            .doTopDownLayout(geomWithAxisSizeEstimate, coordProvider)
            .geomContentBounds.dimension

        // 1st iteration

        val layoutInfos = facetTileAndLayout.map { (facetTile, tileLayout) ->
            tileLayout.doInsideOutLayout(
                geomContentSizeEstimate,
                coordProvider
            ).withAxisShown(
                facetTile.hasHAxis,
                facetTile.hasVAxis
            )
        }

        // adjust geom size
        val tilesAreaSizeNew = FacetedPlotLayoutUtil.tilesAreaSize(
            layoutInfos,
            facets,
            addedHSize,
            addedVSize
        )

        val widthDiff = tilesAreaSize.x - tilesAreaSizeNew.x
        val heightDiff = tilesAreaSize.y - tilesAreaSizeNew.y

        // Error 1 px per tile is ok.
        if (abs(widthDiff) <= facets.colCount && abs(heightDiff) <= facets.rowCount) {
            return layoutInfos
        }

        // 2nd iteration: adjust geom content.

        val geomContentSizeAdjusted = DoubleVector(
            geomContentSizeEstimate.x + widthDiff / facets.colCount,
            geomContentSizeEstimate.y + heightDiff / facets.rowCount
        )

        val layoutInfos2 = facetTileAndLayout.map { (facetTile, tileLayout) ->
            tileLayout.doInsideOutLayout(
                geomContentSizeAdjusted,
                coordProvider
            ).withAxisShown(
                facetTile.hasHAxis,
                facetTile.hasVAxis
            )
        }

        return layoutInfos2
    }
}