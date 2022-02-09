/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.builder.assemble.PlotFacets
import jetbrains.datalore.plot.builder.coord.CoordProvider
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

        var tilesAreaBounds = DoubleRectangle(DoubleVector.ZERO, DoubleVector.ZERO)
        val tilesAreaOffset = DoubleVector(paddingLeft, paddingTop)
        val tileInfos = ArrayList<TileLayoutInfo>()

        var offsetX = 0.0
        var offsetY = 0.0
        var currRow = 0
        var prevHeight = 0.0

        for ((index, facetTile) in facetTiles.withIndex()) {
            val tileLayoutInfo = tileLayoutInfos[index]
            val geomWidth = tileLayoutInfo.geomWidth()
            val geomHeight = tileLayoutInfo.geomHeight()

            val axisThicknessX = tileLayoutInfo.axisThicknessX()
            val axisThicknessY = tileLayoutInfo.axisThicknessY()

            var width = geomWidth
            var geomX = 0.0
            if (facetTile.hasVAxis) {
                width += axisThicknessY
                geomX = axisThicknessY
            }
            if (facetTile.rowLab != null && showFacetStrip) {
                width += FACET_TAB_HEIGHT
            }

            var height = geomHeight
            if (facetTile.hasHAxis && facetTile.row == facets.rowCount - 1) {   // bottom row only
                height += axisThicknessX
            }

            var geomY = 0.0
            if (showFacetStrip) {
                val addedHeight = facetColHeadHeight(facetTile.colLabs.size)
                height += addedHeight
                geomY = addedHeight
            }

            val bounds = DoubleRectangle(0.0, 0.0, width, height)
            val geomBounds = DoubleRectangle(geomX, geomY, geomWidth, geomHeight)

            val row = facetTile.row
            if (row > currRow) {
                currRow = row
                offsetY += prevHeight + PANEL_PADDING
            }
            prevHeight = height

            val col = facetTile.col
            if (col == 0) {
                offsetX = 0.0
            }

            val offset = DoubleVector(offsetX, offsetY)
            offsetX += width + PANEL_PADDING

            var info = TileLayoutInfo(
                bounds,
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
                info = info.withFacetLabels(facetTile.colLabs, facetTile.rowLab)
            }

            tileInfos.add(info)

            tilesAreaBounds = tilesAreaBounds.union(info.getAbsoluteBounds(tilesAreaOffset))
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
