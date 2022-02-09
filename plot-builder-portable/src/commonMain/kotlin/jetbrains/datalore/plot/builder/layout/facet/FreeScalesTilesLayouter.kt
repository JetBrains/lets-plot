/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout.facet

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.builder.layout.TileLayoutProvider
import jetbrains.datalore.plot.builder.assemble.PlotFacets
import jetbrains.datalore.plot.builder.coord.CoordProvider
import jetbrains.datalore.plot.builder.layout.TileLayoutInfo
import jetbrains.datalore.plot.builder.theme.AxisTheme

internal object FreeScalesTilesLayouter {
    fun createTileLayoutInfos(
        tilesAreaSize: DoubleVector,
        facetTiles: List<PlotFacets.FacetTileInfo>,
        layoutProviderByTile: List<TileLayoutProvider>,
        facets: PlotFacets,
        addedHSize: Double,
        addedVSize: Double,
        coordProvider: CoordProvider,
        hAxisTheme: AxisTheme,
        vAxisTheme: AxisTheme,
    ): List<TileLayoutInfo> {
        throw IllegalStateException("Not implemented")
//
//        // rough estimate (without axis. The final size will be smaller)
//        val vAxisCount = FacetedPlotLayoutUtil.countVAxisInFirstRow(facetTiles, facets.colCount)
//        val vAxisThickness = PlotAxisLayoutUtil.initialThickness(Orientation.LEFT, vAxisTheme)
//        val geomWidth = (tilesAreaSize.x - addedHSize - vAxisCount * vAxisThickness) / facets.colCount
//        val tileWidth = geomWidth + vAxisThickness
//
//        val hAxisCount = FacetedPlotLayoutUtil.countHAxisInFirstCol(facetTiles, facets.colCount)
//        val hAxisThickness = PlotAxisLayoutUtil.initialThickness(Orientation.BOTTOM, hAxisTheme)
//        val geomHeight = (tilesAreaSize.y - addedVSize - hAxisCount * hAxisThickness) / facets.rowCount
//        val tileHeight = geomHeight + hAxisThickness
//
//        // 1st iteration
//        val layoutedTileInfos = layoutByTile.map {
//            FacetedPlotLayout.layoutTile(tileWidth, tileHeight, it, coordProvider)
//        }
//
//        // adjust geom size
//        val tilesAreaSizeNew = FacetedPlotLayout.tilesAreaSize(
//            layoutedTileInfos,
//            facets,
//            addedHSize,
//            addedVSize
//        )
//        val widthDiff = tilesAreaSize.x - tilesAreaSizeNew.x
//        val heightDiff = tilesAreaSize.y - tilesAreaSizeNew.y
//
//        // Error 1 px per tile is ok.
//        if (abs(widthDiff) <= facets.colCount && abs(heightDiff) <= facets.rowCount) {
//            return layoutedTileInfos
//        }
//
//        // 2nd iteration
//        val geomWidthDelta = widthDiff / facets.colCount
//        val geomHeightDelta = heightDiff / facets.rowCount
//
//        val layoutedTileInfos2 = layoutByTile.mapIndexed { i, layout ->
//            val layoutedTile = layoutedTileInfos[i]
//            val tileWidth2 = layoutedTile.geomWidth() + geomWidthDelta + layoutedTile.axisThicknessY()
//            val tileHeight2 = layoutedTile.geomHeight() + geomHeightDelta + layoutedTile.axisThicknessX()
//            FacetedPlotLayout.layoutTile(tileWidth2, tileHeight2, layout, coordProvider)
//        }
//
//        return layoutedTileInfos2
////            return layoutedTileInfos
    }
}