/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.assemble.facet

import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.builder.assemble.PlotFacets
import kotlin.math.max

class FacetGrid(
    private val xVar: String?,
    private val yVar: String?,
    xLevels: List<Any>,
    yLevels: List<Any>,
    xOrder: Int,
    yOrder: Int
) : PlotFacets() {

    override val isDefined: Boolean = xVar != null || yVar != null
    private val xLevels: List<Any> = reorderVarLevels(xVar, xLevels, xOrder)
    private val yLevels: List<Any> = reorderVarLevels(yVar, yLevels, yOrder)
    override val colCount: Int = max(1, xLevels.size)
    override val rowCount: Int = max(1, yLevels.size)
    override val numTiles = colCount * rowCount
    override val variables: List<String>
        get() = listOfNotNull(xVar, yVar)

    /**
     * @return List of Dataframes, one Dataframe per tile.
     *          Tiles are enumerated by rows, i.e.:
     *          the index is computed like: row * nCols + col
     */
    override fun dataByTile(data: DataFrame): List<DataFrame> {
        require(isDefined) { "dataByTile() called on Undefined plot facets." }

        val dataByLevelTupleList = dataByLevelTuple(
            data,
            listOfNotNull(
                xVar,
                yVar,
            ),
            listOfNotNull(
                xVar?.let { xLevels },
                yVar?.let { yLevels },
            )
        )
        val dataByLevelTuple = dataByLevelTupleList.toMap()

        val colLevels = if (xLevels.isEmpty()) listOf(null) else xLevels
        val rowLevels = if (yLevels.isEmpty()) listOf(null) else yLevels

        val dataByTile: MutableList<DataFrame> = ArrayList()
        // Enumerate tiles by-row.
        for (rowLevel in rowLevels) {
            for (colLevel in colLevels) {
                val levelTuple = listOfNotNull(colLevel, rowLevel)
                val tileData = dataByLevelTuple.getValue(levelTuple)
                dataByTile.add(tileData)
            }
        }

        return dataByTile
    }

    /**
     * @return List of FacetTileInfo.
     *          Tiles are enumerated by rows, i.e.:
     *          the index is computed like: row * nCols + col
     */
    override fun tileInfos(): List<FacetTileInfo> {
        val colLabels = (if (xLevels.isEmpty()) listOf(null) else xLevels).map { it?.toString() }
        val rowLabels = (if (yLevels.isEmpty()) listOf(null) else yLevels).map { it?.toString() }

        val infos = ArrayList<FacetTileInfo>()
        for (row in 0 until rowCount) {
            val addColLab = row == 0
            val hasXAxis = row == rowCount - 1
            for (col in 0 until colCount) {
                val addRowLab = col == colCount - 1
                val hasYAxis = col == 0

                val colLabs = if (addColLab) {
                    colLabels[col]?.let { listOf(it) } ?: emptyList()
                } else {
                    emptyList<String>()
                }

                infos.add(
                    FacetTileInfo(
                        col, row,
                        colLabs,
                        if (addRowLab) rowLabels[row] else null,
                        hasXAxis, hasYAxis
                    )
                )
            }
        }

        return infos
    }
}
