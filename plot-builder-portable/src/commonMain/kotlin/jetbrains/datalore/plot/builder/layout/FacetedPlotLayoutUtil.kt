/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.builder.assemble.PlotFacets
import kotlin.math.max

internal object FacetedPlotLayoutUtil {
    fun countVAxisInFirstRow(
        facetTiles: List<PlotFacets.FacetTileInfo>,
        colCount: Int,
    ): Int {
        return facetTiles.take(colCount).map {
            if (it.hasVAxis) 1 else 0
        }.sum()
    }

    fun countHAxisInFirstCol(
        facetTiles: List<PlotFacets.FacetTileInfo>,
        colCount: Int,
    ): Int {
        val col0Indices = (facetTiles.indices).filter { it % colCount == 0 }
        return col0Indices
            .map { facetTiles[it] }
            .map {
                if (it.hasHAxis) 1 else 0
            }.sum()
    }

    fun tilesAreaSize(
        tileLayoutInfos: List<TileLayoutInfo>,
        facets: PlotFacets,
        addedHSize: Double,
        addedVSize: Double,
    ): DoubleVector {
        // Tiles are enumerated by rows, i.e.
        // the index is computed like: row * nCols + col

        var totalTilesWidth = 0.0
        for (r in 0 until facets.rowCount) {
            var rowWidth = 0.0
            for (c in 0 until facets.colCount) {
                val i = r * facets.colCount + c
                if (i < tileLayoutInfos.size) {
                    val tileLayoutInfo = tileLayoutInfos[i]
                    val addAxisWidth = when (tileLayoutInfo.vAxisShown) {
                        true -> tileLayoutInfo.axisThicknessY()
                        else -> 0.0
                    }
                    rowWidth += (tileLayoutInfo.geomWidth() + addAxisWidth)
                }
            }
            totalTilesWidth = max(totalTilesWidth, rowWidth)
        }

        var totalTilesHeight = 0.0
        for (c in 0 until facets.colCount) {
            var colHeight = 0.0
            for (r in 0 until facets.rowCount) {
                val i = r * facets.colCount + c
                if (i < tileLayoutInfos.size) {
                    val tileLayoutInfo = tileLayoutInfos[i]
                    val addAxisHeight = when (tileLayoutInfo.hAxisShown) {
                        true -> tileLayoutInfo.axisThicknessX()
                        else -> 0.0
                    }
                    colHeight += (tileLayoutInfo.geomHeight() + addAxisHeight)
                }
            }
            totalTilesHeight = max(totalTilesHeight, colHeight)
        }

        val w = totalTilesWidth + addedHSize
        val h = totalTilesHeight + addedVSize
        return DoubleVector(w, h)
    }
}