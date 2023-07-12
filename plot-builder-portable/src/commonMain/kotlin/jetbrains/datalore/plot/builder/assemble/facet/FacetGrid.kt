/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.assemble.facet

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.builder.assemble.PlotFacets
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil
import kotlin.math.max

class FacetGrid constructor(
    private val xVar: String?,
    private val yVar: String?,
    xLevels: List<Any>,
    yLevels: List<Any>,
    xOrder: Int,
    yOrder: Int,
    private val xFormatter: (Any) -> String = DEF_FORMATTER,
    private val yFormatter: (Any) -> String = DEF_FORMATTER,
    scales: FacetScales = FacetScales.FIXED
) : PlotFacets() {

    override val isDefined: Boolean = xVar != null || yVar != null
    override val colCount: Int = max(1, xLevels.size)
    override val rowCount: Int = max(1, yLevels.size)
    override val numTiles = colCount * rowCount
    override val variables: List<String>
        get() = listOfNotNull(xVar, yVar)

    override val freeHScale: Boolean =
        (scales == FacetScales.FREE || scales == FacetScales.FREE_X) && xVar != null

    override val freeVScale: Boolean =
        (scales == FacetScales.FREE || scales == FacetScales.FREE_Y) && yVar != null

    private val xLevels: List<Any> = reorderVarLevels(xVar, xLevels, xOrder)
    private val yLevels: List<Any> = reorderVarLevels(yVar, yLevels, yOrder)

    private val colLevels: List<Any?> get() = xLevels.ifEmpty { listOf(null) }
    private val rowLevels: List<Any?> get() = yLevels.ifEmpty { listOf(null) }


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
        val colLabels = (colLevels).map {
            it?.let { xFormatter(it) }
        }
        val rowLabels = (rowLevels).map {
            it?.let { yFormatter(it) }
        }

        val infos = ArrayList<FacetTileInfo>()
        for (row in 0 until rowCount) {
            val addColLab = row == 0
            val hasHAxis = row == rowCount - 1
            for (col in 0 until colCount) {
                val addRowLab = col == colCount - 1
                val hasVAxis = col == 0

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
                        hasHAxis = hasHAxis,
                        hasVAxis = hasVAxis,
                        isBottom = row == rowCount - 1,
                        trueIndex = infos.size  // no reordering
                    )
                )
            }
        }

        return infos
    }

    override fun adjustHDomains(domains: List<DoubleSpan?>): List<DoubleSpan?> {
        fun colIndices(col: Int): List<Int> {
            return (rowLevels.indices).map { it * colLevels.size + col }.toList()
        }

        return if (freeHScale) {
            // same domain for all tiles in a column.
            val adjusted = MutableList<DoubleSpan?>(domains.size) { null }
            for (col in colLevels.indices) {
                val indices = colIndices(col)
                val union = indices.map { domains[it] }.reduce { d0, d1 -> SeriesUtil.span(d0, d1) }
                indices.forEach {
                    adjusted[it] = union
                }
            }
            adjusted
        } else {
            domains
        }
    }

    override fun adjustVDomains(domains: List<DoubleSpan?>): List<DoubleSpan?> {
        fun rowIndices(row: Int): List<Int> {
            val start = row * colLevels.size
            return (start until start + colLevels.size).toList()
        }

        return if (freeVScale) {
            // same domain for all tiles in a row.
            val adjusted = MutableList<DoubleSpan?>(domains.size) { null }
            for (row in rowLevels.indices) {
                val indices = rowIndices(row)
                val union = indices.map { domains[it] }.reduce { d0, d1 -> SeriesUtil.span(d0, d1) }
                indices.forEach {
                    adjusted[it] = union
                }
            }
            adjusted
        } else {
            domains
        }
    }
}
