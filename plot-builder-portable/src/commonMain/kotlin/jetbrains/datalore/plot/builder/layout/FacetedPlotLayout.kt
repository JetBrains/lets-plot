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
import jetbrains.datalore.plot.builder.layout.PlotLayoutUtil.plotInsets
import jetbrains.datalore.plot.builder.layout.facet.FixedScalesTilesLayouter
import jetbrains.datalore.plot.builder.layout.facet.FreeScalesTilesLayouter
import jetbrains.datalore.plot.builder.layout.util.Insets
import jetbrains.datalore.plot.builder.scale.AxisPosition
import jetbrains.datalore.plot.builder.theme.AxisTheme

internal class FacetedPlotLayout(
    private val facets: PlotFacets,
    private val layoutProviderByTile: List<TileLayoutProvider>,
    private val showFacetStrip: Boolean,
    hAxisOrientation: AxisPosition,
    vAxisOrientation: AxisPosition,
    private val hAxisTheme: AxisTheme,
    private val vAxisTheme: AxisTheme,
) : PlotLayout {
    private val totalAddedHSize: Double = PANEL_PADDING * (facets.colCount - 1)
    private val totalAddedVSize: Double = PANEL_PADDING * (facets.rowCount - 1)

    private val insets: Insets = plotInsets(
        hAxisOrientation, vAxisOrientation,
        hAxisTheme, vAxisTheme
    )

    init {
        require(facets.isDefined) { "Undefined facets." }
    }

    override fun doLayout(preferredSize: DoubleVector, coordProvider: CoordProvider): PlotLayoutInfo {
        var tilesAreaSize = DoubleVector(
            preferredSize.x - (insets.left + insets.right),
            preferredSize.y - (insets.top + insets.bottom)
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


        // Align geom areas of tiles.

        // absolute offsets of tile gom areas.
        val geomOffsetByCol = geomOffsetsByCol(layoutInfos, facetTiles, PANEL_PADDING, facets.colCount)
        val geomOffsetByRow = geomOffsetsByRow(
            layoutInfos, facetTiles, showFacetStrip,
            PANEL_PADDING, facets.rowCount
        )

        val tileBoundsList = ArrayList<DoubleRectangle>()
        val geomOuterBoundsList = ArrayList<DoubleRectangle>()
        for ((index, facetTile) in facetTiles.withIndex()) {
            val layoutInfo = layoutInfos[index]

            val col = facetTile.col
            val row = facetTile.row
            val geomX = geomOffsetByCol[col]
            val geomY = geomOffsetByRow[row]
            val outerGeomSize = layoutInfo.geomOuterBounds.dimension

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
            val tileWidth = outerGeomSize.x + axisWidth + tileLabelWidth

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
            val tileHeight = outerGeomSize.y + tileLabelHeight + axisHeight

//            if (col == 0) {
//                println("[$row][$tileY] $tileHeight = ${geomSize.y} + $tileLabelHeight + $axisHeight")
//            }

            // Absolute bounds...
            val tileBounds = DoubleRectangle(
                DoubleVector(tileX, tileY),
                DoubleVector(tileWidth, tileHeight)
            )
            val geomOuterBounds = DoubleRectangle(
                DoubleVector(geomX, geomY),
                outerGeomSize
            )

            tileBoundsList.add(tileBounds)
            geomOuterBoundsList.add(geomOuterBounds)
        }

        val tilesAreaOrigin = tileBoundsList
            .reduce { b0, b1 -> b0.union(b1) }
            .origin

        // Normalize origin of tilesAreaBounds.
        val originDelta = tilesAreaOrigin.negate()
        val tilesPaddingLeftTop = insets.leftTop

        val finalLayoutInfos = ArrayList<TileLayoutInfo>()
        for ((index, facetTile) in facetTiles.withIndex()) {
            val layoutInfo = layoutInfos[index]
            val geomInnerBoundsOffset = layoutInfo.geomInnerBounds.origin
                .subtract(layoutInfo.geomOuterBounds.origin)

            val tileBounds = tileBoundsList[index]
            val geomOuterBounds = geomOuterBoundsList[index]
            val geomInnerBounds = DoubleRectangle(
                geomOuterBounds.origin.add(geomInnerBoundsOffset),
                layoutInfo.geomInnerBounds.dimension
            )

            val newLayoutInfo = TileLayoutInfo(
                tilesPaddingLeftTop,
                geomWithAxisBounds = tileBounds.add(originDelta),
                geomOuterBounds = geomOuterBounds.add(originDelta),
                geomInnerBounds = geomInnerBounds.add(originDelta),
                layoutInfo.axisInfos,
                hAxisShown = facetTile.hasHAxis,
                vAxisShown = facetTile.hasVAxis,
                trueIndex = facetTile.trueIndex
            )

            finalLayoutInfos.add(
                if (showFacetStrip) {
                    newLayoutInfo.withFacetLabels(facetTile.colLabs, facetTile.rowLab)
                } else {
                    newLayoutInfo
                }
            )
        }

        val combinedTilesSize =
            finalLayoutInfos.map { it.geomWithAxisBounds }.reduce { b0, b1 -> b0.union(b1) }.dimension
        val plotSize = combinedTilesSize
            .add(tilesPaddingLeftTop)
            .add(insets.rightBottom)

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
