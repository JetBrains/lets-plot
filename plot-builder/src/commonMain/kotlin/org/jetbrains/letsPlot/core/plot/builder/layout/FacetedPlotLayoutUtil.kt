/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.layout

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.builder.assemble.PlotFacets

internal object FacetedPlotLayoutUtil {
    fun countVAxisInFirstRow(facetTiles: List<PlotFacets.FacetTileInfo>): Int {
        return facetTiles.filter { it.row == 0 }.map {
            if (it.hasVAxis) 1 else 0
        }.sum()
    }

    fun countHAxisInFirstCol(facetTiles: List<PlotFacets.FacetTileInfo>): Int {
        return facetTiles.filter { it.col == 0 }.map {
            if (it.hasHAxis) 1 else 0
        }.sum()
    }

    fun tilesAreaSize(
        layoutInfos: List<TileLayoutInfo>,
        facets: PlotFacets,
        addedHSize: Double,
        addedVSize: Double,
    ): DoubleVector {
        // Tiles are enumerated by rows, i.e.
        // the index is computed like: row * nCols + col

        val facetTiles = facets.tileInfos()

        val addedVAxisWidth = (0 until facets.colCount).sumOf { col ->
            maxVAxisThickness(layoutInfos, facetTiles, col)
        }
        val addedHAxisHeight = (0 until facets.rowCount).sumOf { row ->
            maxHAxisThickness(layoutInfos, facetTiles, row, facets.rowCount)
        }

        val maxRowWidthGeomOnly = (0 until facets.rowCount).map { row ->
            rowIndices(facetTiles, row).sumOf { ind ->
                layoutInfos[ind].geomOuterWidth()
            }
        }.maxOrNull() ?: 0.0

        val maxColHeightGeomOnly = (0 until facets.colCount).map { col ->
            colIndices(facetTiles, col).sumOf { ind ->
                layoutInfos[ind].geomOuterHeight()
            }
        }.maxOrNull() ?: 0.0

        val w = addedVAxisWidth + maxRowWidthGeomOnly + addedHSize
        val h = addedHAxisHeight + maxColHeightGeomOnly + addedVSize
        return DoubleVector(w, h)
    }

    private fun maxVAxisThickness(
        layoutInfos: List<TileLayoutInfo>,
        facetTiles: List<PlotFacets.FacetTileInfo>,
        col: Int
    ): Double {
        val maxThickness = colIndices(facetTiles, col).maxOfOrNull { ind ->
            layoutInfos[ind].let {
                if (it.vAxisShown) {
                    it.axisThicknessY()
                } else {
                    0.0
                }
            }
        }
        return maxThickness ?: 0.0
    }

    private fun maxHAxisThickness(
        layoutInfos: List<TileLayoutInfo>,
        facetTiles: List<PlotFacets.FacetTileInfo>,
        row: Int,
        numRows: Int
    ): Double {
        val maxThickness = rowIndices(facetTiles, row).maxOfOrNull { ind ->
            if (facetTiles[ind].isBottom && row < numRows - 1) {
                // exclude bottom tile in infinished column (i.e. rightmost column).
                0.0
            } else {
                layoutInfos[ind].let {
                    if (it.hAxisShown) {
                        it.axisThicknessX()
                    } else {
                        0.0
                    }
                }
            }
        }

        return maxThickness ?: 0.0
    }

    private fun colIndices(facetTiles: List<PlotFacets.FacetTileInfo>, col: Int): List<Int> {
        return facetTiles.withIndex()
            .filter { (_, tile) ->
                tile.col == col
            }.map { (index, _) ->
                index
            }
    }

    private fun rowIndices(facetTiles: List<PlotFacets.FacetTileInfo>, row: Int): List<Int> {
        return facetTiles.withIndex()
            .filter { (_, tile) ->
                tile.row == row
            }.map { (index, _) ->
                index
            }
    }

    fun geomOffsetsByCol(
        layoutInfos: List<TileLayoutInfo>,
        facetTiles: List<PlotFacets.FacetTileInfo>,
        colSpace: Double,
        numCols: Int
    ): List<Double> {
        val axisWidths = List<Double>(numCols) { col ->
            maxVAxisThickness(layoutInfos, facetTiles, col)
        }
        val spacesBefore = List<Double>(numCols) { col ->
            if (col == 0) 0.0
            else colSpace
        }

        var baseOffset = 0.0
        val offsets = ArrayList<Double>()
        for (i in (0 until numCols)) {
            val currOffset = baseOffset + spacesBefore[i] + axisWidths[i]
            offsets.add(currOffset)
            baseOffset = currOffset + layoutInfos[i].geomOuterWidth()
        }
        return offsets
    }

    fun geomOffsetsByRow(
        layoutInfos: List<TileLayoutInfo>,
        facetTiles: List<PlotFacets.FacetTileInfo>,
        showFacetStrip: Boolean,
        rowSpace: Double,
        numRows: Int,
        facetColHeadHeight: (PlotFacets.FacetTileInfo) -> Double
    ): List<Double> {
        val axisHeights = List<Double>(numRows) { row ->
            maxHAxisThickness(layoutInfos, facetTiles, row, numRows)
        }

        val spacesBefore = List<Double>(numRows) { row ->
            if (row == 0) 0.0
            else rowSpace
        }

        val tileLabelHeights = colIndices(facetTiles, 0).map { i ->
            when {
                i == 0 -> 0.0  // skip first (will be taken in account later)
                showFacetStrip -> facetColHeadHeight(facetTiles[i])
                else -> 0.0
            }
        }

        var baseOffset = 0.0
        val offsets = ArrayList<Double>()
        for (i in (0 until numRows)) {
            val currOffset = baseOffset + spacesBefore[i] + tileLabelHeights[i]
            offsets.add(currOffset)
            baseOffset = currOffset + layoutInfos[i].geomOuterHeight() + axisHeights[i]
        }
        return offsets
    }
}