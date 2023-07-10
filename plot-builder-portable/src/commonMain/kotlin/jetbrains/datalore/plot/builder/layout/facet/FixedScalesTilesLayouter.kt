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
import jetbrains.datalore.plot.builder.layout.FacetedPlotLayoutUtil.tilesAreaSize
import jetbrains.datalore.plot.builder.layout.PlotAxisLayoutUtil
import jetbrains.datalore.plot.builder.layout.TileLayoutInfo
import jetbrains.datalore.plot.builder.layout.TileLayoutProvider
import jetbrains.datalore.plot.builder.theme.AxisTheme
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

        // rough estimate (without axis. The final size will be smaller)
        val vAxisCount = FacetedPlotLayoutUtil.countVAxisInFirstRow(facetTiles)
        val vAxisThickness = PlotAxisLayoutUtil.initialThickness(Orientation.LEFT, vAxisTheme)
        val geomWidth = (tilesAreaSize.x - addedHSize - vAxisCount * vAxisThickness) / facets.colCount
        val tileWidth = geomWidth + vAxisThickness

        val hAxisCount = FacetedPlotLayoutUtil.countHAxisInFirstCol(facetTiles)
        val hAxisThickness = PlotAxisLayoutUtil.initialThickness(Orientation.BOTTOM, hAxisTheme)
        val geomHeight = (tilesAreaSize.y - addedVSize - hAxisCount * hAxisThickness) / facets.rowCount
        val tileHeight = geomHeight + hAxisThickness

        // 1st iteration

        // With 'fixed' scales lets layout just one tile (because all tiles are identical).
        val tileLayout = layoutProviderByTile[0].createTopDownTileLayout()
        val tileLayoutInfo: TileLayoutInfo = tileLayout.doLayout(
            DoubleVector(tileWidth, tileHeight),
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

        val geomWidthDelta = widthDiff / facets.colCount
        val geomHeightDelta = heightDiff / facets.rowCount

        val tileWidth2 = tileLayoutInfo.geomOuterWidth() + geomWidthDelta + tileLayoutInfo.axisThicknessY()
        val tileHeight2 = tileLayoutInfo.geomOuterHeight() + geomHeightDelta + tileLayoutInfo.axisThicknessX()
        val tileLayoutInfo2 = tileLayout.doLayout(
            DoubleVector(tileWidth2, tileHeight2),
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