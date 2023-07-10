/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout.facet

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import jetbrains.datalore.plot.builder.assemble.PlotFacets
import jetbrains.datalore.plot.builder.coord.CoordProvider
import jetbrains.datalore.plot.builder.guide.Orientation
import jetbrains.datalore.plot.builder.layout.FacetedPlotLayoutUtil
import jetbrains.datalore.plot.builder.layout.PlotAxisLayoutUtil
import jetbrains.datalore.plot.builder.layout.TileLayoutInfo
import jetbrains.datalore.plot.builder.layout.TileLayoutProvider
import jetbrains.datalore.plot.builder.theme.AxisTheme
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

        // rough estimate (without axis. The final size will be smaller)
        val vAxisCount = FacetedPlotLayoutUtil.countVAxisInFirstRow(facetTiles)
        val vAxisThickness = PlotAxisLayoutUtil.initialThickness(Orientation.LEFT, vAxisTheme)
        val geomWidth = (tilesAreaSize.x - addedHSize - vAxisCount * vAxisThickness) / facets.colCount

        val hAxisCount = FacetedPlotLayoutUtil.countHAxisInFirstCol(facetTiles)
        val hAxisThickness = PlotAxisLayoutUtil.initialThickness(Orientation.BOTTOM, hAxisTheme)
        val geomHeight = (tilesAreaSize.y - addedVSize - hAxisCount * hAxisThickness) / facets.rowCount

        // 1st iteration

        val layoutByTile = layoutProviderByTile.map {
            it.createInsideOutTileLayout()
        }


        val facetTileAndLayout = facetTiles.map {
            Pair(it, layoutByTile[it.trueIndex])
        }

        val layoutInfos = facetTileAndLayout.map { (facetTile, tileLayout) ->
            tileLayout.doLayout(
                DoubleVector(geomWidth, geomHeight),
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

        // 2nd iteration

        val geomWidthDelta = widthDiff / facets.colCount
        val geomHeightDelta = heightDiff / facets.rowCount

        val geomWidth2 = geomWidth + geomWidthDelta
        val geomHeight2 = geomHeight + geomHeightDelta
        val layoutInfos2 = facetTileAndLayout.map { (facetTile, tileLayout) ->
            tileLayout.doLayout(
                DoubleVector(geomWidth2, geomHeight2),
                coordProvider
            ).withAxisShown(
                facetTile.hasHAxis,
                facetTile.hasVAxis
            )
        }

        return layoutInfos2
    }
}