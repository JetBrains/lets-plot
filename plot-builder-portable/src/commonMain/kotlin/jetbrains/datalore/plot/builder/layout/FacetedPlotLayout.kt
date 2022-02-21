/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.builder.assemble.PlotFacets
import jetbrains.datalore.plot.builder.coord.CoordProvider
import jetbrains.datalore.plot.builder.layout.FacetedPlotLayoutUtil.colIndices
import jetbrains.datalore.plot.builder.layout.FacetedPlotLayoutUtil.rowIndices
import jetbrains.datalore.plot.builder.layout.facet.FixedScalesTilesLayouter
import jetbrains.datalore.plot.builder.layout.facet.FreeScalesTilesLayouter
import jetbrains.datalore.plot.builder.layout.tile.TileLayoutUtil
import jetbrains.datalore.plot.builder.theme.AxisTheme

internal class FacetedPlotLayout(
    private val facets: PlotFacets,
    private val layoutProviderByTile: List<TileLayoutProvider>,
    private val showFacetStrip: Boolean,
    private val hAxisTheme: AxisTheme,
    private val vAxisTheme: AxisTheme,
) : PlotLayoutBase() {
    private val totalAddedHSize: Double = PANEL_PADDING * (facets.colCount - 1)
    private val totalAddedVSize: Double = PANEL_PADDING * (facets.rowCount - 1)

    init {
        setPadding(10.0, 10.0, 0.0, 0.0)

        require(facets.isDefined) { "Undefined facets." }
    }

    override fun doLayout(preferredSize: DoubleVector, coordProvider: CoordProvider): PlotLayoutInfo {
        var tilesAreaSize = DoubleVector(
            preferredSize.x - (paddingLeft + paddingRight),
            preferredSize.y - (paddingTop + paddingBottom)
        )

        val facetTiles = facets.tileInfos()

        if (showFacetStrip) {
            val tileColLabCounts = facetTiles.firstOrNull { it.colLabs.isNotEmpty() }?.colLabs?.size ?: 0
            val tileWithColLabInRowCount = facetTiles
                .filter { it.colLabs.isNotEmpty() }
                .distinctBy { it.row }.count()

            val totalAddedHeight = facetColHeadHeight(tileColLabCounts) * tileWithColLabInRowCount

            val labsInRow = if (facetTiles.any { it.rowLab != null }) 1 else 0
            val labsTotalDim = DoubleVector(labsInRow * FACET_TAB_HEIGHT, totalAddedHeight)
            tilesAreaSize = tilesAreaSize.subtract(labsTotalDim)
        }

        val tileLayoutInfos: List<TileLayoutInfo> = if (facets.freeHScale || facets.freeVScale) {
            FreeScalesTilesLayouter.createTileLayoutInfos(
                tilesAreaSize,
                facetTiles,
                layoutProviderByTile,
                facets,
                totalAddedHSize,
                totalAddedVSize,
                coordProvider,
                hAxisTheme = hAxisTheme,
                vAxisTheme = vAxisTheme,
            )
        } else {
            FixedScalesTilesLayouter.createTileLayoutInfos(
                tilesAreaSize,
                facetTiles,
                layoutProviderByTile,
                facets,
                totalAddedHSize,
                totalAddedVSize,
                coordProvider,
                hAxisTheme = hAxisTheme,
                vAxisTheme = vAxisTheme,
            )
        }

        // create final plot tiles layout infos

        // Compute tile vwrtical offsets in 1st column.
        val firstColIndices = colIndices(facetTiles, 0)
        val firstColTileHeights = firstColIndices.map {
            Pair(facetTiles[it], tileLayoutInfos[it])
        }.map { (facetTile, layoutInfo) ->
            val geomHeight = layoutInfo.geomHeight()
            val heightWithAxis = if (layoutInfo.hAxisShown) {
                geomHeight + layoutInfo.axisThicknessX()
            } else {
                geomHeight
            }

            val heightWithStrip = if (showFacetStrip) {
                val addedHeight = facetColHeadHeight(facetTile.colLabs.size)
                heightWithAxis + addedHeight
            } else {
                heightWithAxis
            }
            heightWithStrip
        }
        val firstColTileVOffsets = List(facets.rowCount) { i ->
            firstColTileHeights.take(i).sum() + PANEL_PADDING * i
        }

        // Compute tile horizontal offsets in 1st row.
        val firstRowIndices = rowIndices(facetTiles, 0)
        val firstRowTileWidths = firstRowIndices.map {
            Pair(facetTiles[it], tileLayoutInfos[it])
        }.map { (facetTile, layoutInfo) ->
            val geomWidth = layoutInfo.geomWidth()
            val widthWithAxis = if (layoutInfo.vAxisShown) {
                geomWidth + layoutInfo.axisThicknessY()
            } else {
                geomWidth
            }
            val widthWithFacetTab = if (facetTile.rowLab != null && showFacetStrip) {
                widthWithAxis + FACET_TAB_HEIGHT
            } else {
                widthWithAxis
            }
            widthWithFacetTab
        }
        val firstRowTileHOffsets = List(facets.colCount) { i ->
            firstRowTileWidths.take(i).sum() + PANEL_PADDING * i
        }

        var tilesAreaBounds = DoubleRectangle(DoubleVector.ZERO, DoubleVector.ZERO)
        val tilesAreaOffset = DoubleVector(paddingLeft, paddingTop)
        val tileInfos = ArrayList<TileLayoutInfo>()

        for ((index, facetTile) in facetTiles.withIndex()) {
            val tileLayoutInfo = tileLayoutInfos[index]

            val geomX = if (facetTile.hasVAxis) {
                tileLayoutInfo.axisThicknessY()
            } else {
                0.0
            }

            val geomY = if (showFacetStrip) {
                facetColHeadHeight(facetTile.colLabs.size)
            } else {
                0.0
            }

            val geomWidth = tileLayoutInfo.geomWidth()
            val geomHeight = tileLayoutInfo.geomHeight()
            val geomBounds = DoubleRectangle(geomX, geomY, geomWidth, geomHeight)

            val offset = DoubleVector(
                firstRowTileHOffsets[facetTile.col],
                firstColTileVOffsets[facetTile.row]
            )

            val tileWidth = firstRowTileWidths[facetTile.col]
            val tileHeight = firstColTileHeights[facetTile.row]
            val tileBounds = DoubleRectangle(0.0, 0.0, tileWidth, tileHeight)

            var newLayoutInfo = TileLayoutInfo(
                tileBounds,
                geomBounds,
                TileLayoutUtil.clipBounds(geomBounds),
                tileLayoutInfo.hAxisInfo,
                tileLayoutInfo.vAxisInfo,
                hAxisShown = facetTile.hasHAxis,
                vAxisShown = facetTile.hasVAxis,
                trueIndex = facetTile.trueIndex
            )
                .withOffset(tilesAreaOffset.add(offset))

            if (showFacetStrip) {
                newLayoutInfo = newLayoutInfo.withFacetLabels(facetTile.colLabs, facetTile.rowLab)
            }

            tileInfos.add(newLayoutInfo)
            tilesAreaBounds = tilesAreaBounds.union(newLayoutInfo.getAbsoluteBounds(tilesAreaOffset))
        }

        val plotSize = DoubleVector(
            tilesAreaBounds.right + paddingRight,
            tilesAreaBounds.height + paddingBottom
        )

        return PlotLayoutInfo(tileInfos, plotSize)
    }


    companion object {
        const val FACET_TAB_HEIGHT = 30.0
        const val FACET_H_PADDING = 0
        const val FACET_V_PADDING = 6 //5

        private const val PANEL_PADDING = 10.0

        fun facetColLabelSize(colWidth: Double): DoubleVector {
            return DoubleVector(colWidth - FACET_H_PADDING * 2, FACET_TAB_HEIGHT - FACET_V_PADDING * 2.0)
        }

        fun facetColHeadHeight(labCount: Int): Double {
            return if (labCount > 0) {
                facetColLabelSize(0.0).y * labCount + FACET_V_PADDING * 2
            } else {
                0.0
            }
        }
    }
}
