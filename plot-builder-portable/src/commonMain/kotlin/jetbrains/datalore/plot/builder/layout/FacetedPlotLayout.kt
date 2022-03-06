/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.builder.assemble.PlotFacets
import jetbrains.datalore.plot.builder.coord.CoordProvider
import jetbrains.datalore.plot.builder.layout.FacetedPlotLayoutUtil.geomOffsetsByCol
import jetbrains.datalore.plot.builder.layout.FacetedPlotLayoutUtil.geomOffsetsByRow
import jetbrains.datalore.plot.builder.layout.facet.FixedScalesTilesLayouter
import jetbrains.datalore.plot.builder.layout.facet.FreeScalesTilesLayouter
import jetbrains.datalore.plot.builder.layout.tile.TileLayoutUtil
import jetbrains.datalore.plot.builder.theme.AxisTheme

internal class FacetedPlotLayout constructor(
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

        val layoutInfos: List<TileLayoutInfo> = if (facets.freeHScale || facets.freeVScale) {
            FreeScalesTilesLayouter.createTileLayoutInfos(
                tilesAreaSize,
                facets,
                layoutProviderByTile,
                totalAddedHSize,
                totalAddedVSize,
                coordProvider,
                hAxisTheme = hAxisTheme,
                vAxisTheme = vAxisTheme,
            )
        } else {
            FixedScalesTilesLayouter.createTileLayoutInfos(
                tilesAreaSize,
                facets,
                layoutProviderByTile,
                totalAddedHSize,
                totalAddedVSize,
                coordProvider,
                hAxisTheme = hAxisTheme,
                vAxisTheme = vAxisTheme,
            )
        }

        // Create final plot tiles layout infos.

        val plotOrigin = DoubleVector(paddingLeft, paddingTop)

        // Align geom areas of tiles.

        // absolute offsets of tile gom areas.
        val geomOffsetByCol = geomOffsetsByCol(layoutInfos, facetTiles, PANEL_PADDING, facets.colCount)
        val geomOffsetByRow = geomOffsetsByRow(
            layoutInfos, facetTiles, showFacetStrip,
            PANEL_PADDING, facets.rowCount
        )

        val tileBoundsList = ArrayList<DoubleRectangle>()
        val geomBoundsList = ArrayList<DoubleRectangle>()
        for ((index, facetTile) in facetTiles.withIndex()) {
            val layoutInfo = layoutInfos[index]

            val col = facetTile.col
            val row = facetTile.row
            val geomX = geomOffsetByCol[col]
            val geomY = geomOffsetByRow[row]
            val geomSize = layoutInfo.geomBounds.dimension

            // Tile width
            val tileLabelWidth = if (facetTile.rowLab != null && showFacetStrip) {
                FACET_TAB_HEIGHT  // one label on the left side.
            } else {
                0.0
            }

            val axisWidth = if (facetTile.hasVAxis) {
                layoutInfo.axisThicknessY()
            } else {
                0.0
            }

            val tileX = geomX - axisWidth
            val tileWidth = geomSize.x + axisWidth + tileLabelWidth

            // Tile height
            val tileLabelHeight = if (showFacetStrip) {
                facetColHeadHeight(facetTile.colLabs.size)
            } else {
                0.0
            }

            val axisHeight = if (facetTile.hasHAxis) {
                layoutInfo.axisThicknessX()
            } else {
                0.0
            }

            val tileY = geomY - tileLabelHeight
            val tileHeight = geomSize.y + tileLabelHeight + axisHeight

//            if (col == 0) {
//                println("[$row][$tileY] $tileHeight = ${geomSize.y} + $tileLabelHeight + $axisHeight")
//            }

            // Absolute bounds...
            val tileBounds = DoubleRectangle(
                DoubleVector(tileX, tileY),
                DoubleVector(tileWidth, tileHeight)
            )
            val geomBounds = DoubleRectangle(
                DoubleVector(geomX, geomY),
                layoutInfo.geomBounds.dimension
            )

            tileBoundsList.add(tileBounds)
            geomBoundsList.add(geomBounds)
        }

        val tilesAreaOrigin = tileBoundsList
            .reduce { b0, b1 -> b0.union(b1) }
            .origin

        // Normalize origin of tilesAreaBounds.
        val originDelta = tilesAreaOrigin.negate()

        val finalLayoutInfos = ArrayList<TileLayoutInfo>()
        for ((index, facetTile) in facetTiles.withIndex()) {
            val layoutInfo = layoutInfos[index]
            val tileBounds = tileBoundsList[index]
            val geomBounds = geomBoundsList[index]

            val newLayoutInfo = TileLayoutInfo(
                tileBounds.add(originDelta),
                geomBounds.add(originDelta),
                TileLayoutUtil.clipBounds(geomBounds),
                layoutInfo.hAxisInfo,
                layoutInfo.vAxisInfo,
                hAxisShown = facetTile.hasHAxis,
                vAxisShown = facetTile.hasVAxis,
                trueIndex = facetTile.trueIndex
            ).withOffset(plotOrigin)

            finalLayoutInfos.add(
                if (showFacetStrip) {
                    newLayoutInfo.withFacetLabels(facetTile.colLabs, facetTile.rowLab)
                } else {
                    newLayoutInfo
                }
            )
        }

        val combinedTilesSize = finalLayoutInfos.map { it.bounds }.reduce { b0, b1 -> b0.union(b1) }.dimension
        val plotSize = combinedTilesSize
            .add(plotOrigin)
            .add(DoubleVector(paddingRight, paddingBottom))

        return PlotLayoutInfo(finalLayoutInfos, plotSize)
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
