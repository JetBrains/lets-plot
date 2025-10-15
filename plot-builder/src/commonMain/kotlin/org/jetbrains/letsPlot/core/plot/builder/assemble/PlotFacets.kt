/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.assemble

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.data.DataFrameUtil
import org.jetbrains.letsPlot.core.plot.builder.assemble.facet.FacetGrid

abstract class PlotFacets {

    abstract val isDefined: Boolean
    abstract val colCount: Int
    abstract val rowCount: Int
    abstract val numTiles: Int
    abstract val variables: List<String>
    abstract val freeHScale: Boolean
    abstract val freeVScale: Boolean

    fun isFacettable(data: DataFrame): Boolean {
        return !data.isEmpty
                && data.rowCount() > 0
                && variables.any {
            DataFrameUtil.hasVariable(data, it)
        }
    }

    /**
     * @return List of Dataframes, one Dataframe per tile.
     *          Tiles are enumerated by rows, i.e.:
     *          the index is computed like: row * nCols + col
     */
    abstract fun dataByTile(data: DataFrame): List<DataFrame>


    /**
     * @return List of FacetTileInfo.
     *          Tiles enumerated by rows, i.e.:
     *          the index is computed like: row * nCols + col
     */
    abstract fun tileInfos(): List<FacetTileInfo>

    /**
     * @param domains Transformed X-mapped data ranges by tile.
     */
    open fun adjustHDomains(domains: List<DoubleSpan?>): List<DoubleSpan?> = domains

    /**
     * @param domains Transformed Y-mapped data ranges by tile.
     */
    open fun adjustVDomains(domains: List<DoubleSpan?>): List<DoubleSpan?> = domains

    abstract fun adjustFreeDisctereHDomainsByTile(
        domainBeforeFacets: List<Any>,
        domainByTile: List<Collection<Any>>
    ): List<List<Any>>

    abstract fun adjustFreeDisctereVDomainsByTile(
        domainBeforeFacets: List<Any>,
        domainByTile: List<Collection<Any>>
    ): List<List<Any>>

    companion object {
        const val DEF_ORDER_DIR = 0 // no ordering
        const val DEF_LAB_WIDTH = -1

        val NO_FORMATTER: (Any) -> String = { throw IllegalStateException("Illegal use of 'no formatter'.") }

        val UNDEFINED: PlotFacets = FacetGrid(
            xVar = null,
            yVar = null,
            xLevels = emptyList<Any>(),
            yLevels = emptyList<Any>(),
            xFormatter = NO_FORMATTER,
            yFormatter = NO_FORMATTER
        )

        fun levelTupleAndDataPairs(
            data: DataFrame,
            varNameAndLevelPairsByTile: List<List<Pair<String, Any>>>,
        ): List<Pair<List<Any>, DataFrame>> {

            val indicesByTile: List<List<Int>> = dataIndicesByTile(data, varNameAndLevelPairsByTile)
            val dataByTile = indicesByTile.map { indices ->
                data.slice(indices)
            }
            return varNameAndLevelPairsByTile
                .map { nameLevelPairs -> nameLevelPairs.map { it.second } } // to a list of levels
                .zip(dataByTile)
        }

        fun dataIndicesByTile(
            data: DataFrame,
            varNameAndLevelPairsByTile: List<List<Pair<String, Any>>>,
        ): List<List<Int>> {
            val indicesByVarByLevel = dataIndicesByVarByLevel(data, varNameAndLevelPairsByTile)
            val indicesByTile = ArrayList<List<Int>>()
            for (nameAndLevelPairs: List<Pair<String, Any>> in varNameAndLevelPairsByTile) {
                val topName = nameAndLevelPairs.first().first
                val topLevel = nameAndLevelPairs.first().second
                val indices = ArrayList(indicesByVarByLevel.getValue(topName).getValue(topLevel))
                for (i in 1 until nameAndLevelPairs.size) {
                    val name = nameAndLevelPairs[i].first
                    val level = nameAndLevelPairs[i].second
                    val levelIndices = indicesByVarByLevel.getValue(name).getValue(level)
                    indices.retainAll(HashSet(levelIndices))
                }

                indicesByTile.add(indices)
            }

            return indicesByTile
        }

        private fun dataIndicesByVarByLevel(
            data: DataFrame,
            varNameAndLevelPairsByTile: List<List<Pair<String, Any>>>,
        ): Map<String, Map<Any, List<Int>>> {

            val indicesByVarByLevel = HashMap<String, Map<Any, List<Int>>>()
            val varNameAndLevelPairs = varNameAndLevelPairsByTile.flatten().toSet()
            for ((varName, level) in varNameAndLevelPairs) {
                val indices = when {
                    // 'empty' data in layers with no aes mapping (only constants)
                    data.isEmpty -> emptyList()
                    DataFrameUtil.hasVariable(data, varName) -> {
                        val variable = DataFrameUtil.findVariableOrFail(data, varName)
                        SeriesUtil.matchingIndices(data[variable], level)
                    }

                    else -> {
                        // 'data' has no column 'varName' -> the entire data should be shown in each facet.
                        (0 until data.rowCount()).toList()
                    }
                }

                (indicesByVarByLevel
                    .getOrPut(varName) { HashMap<Any, List<Int>>() } as MutableMap<Any, List<Int>>)
                    .put(level, indices)

            }
            return indicesByVarByLevel
        }

        fun varNameAndLevelPairsByTile(
            varNames: List<String>,
            varLevels: List<List<Any>>
        ): List<List<Pair<String, Any>>> {
            require(varNames.isNotEmpty()) { "Empty list of facet variables." }
            require(varNames.size == varNames.distinct().size) { "Facet variables must be distinct, were: $varNames." }
            check(varNames.size == varLevels.size)

            val name = varNames.first()
            val levels = varLevels.first()
            val levelKeys = ArrayList<List<Pair<String, Any>>>()
            for (level in levels) {
                if (varNames.size > 1) {
                    val subKeys = varNameAndLevelPairsByTile(
                        varNames.subList(1, varNames.size),
                        varLevels.subList(1, varLevels.size)
                    )
                    for (subKey in subKeys) {
                        levelKeys.add(listOf(name to level) + subKey)
                    }
                } else {
                    // exit
                    levelKeys.add(listOf(name to level))
                }
            }
            return levelKeys
        }
    }

    class FacetTileInfo constructor(
        val col: Int,
        val row: Int,
        val colLabs: List<String>,
        val rowLab: String?,
        val hasHAxis: Boolean,
        val hasVAxis: Boolean,
        val isBottom: Boolean,  // true is the tile is the last one in its respective column.
        val trueIndex: Int     // tile index before re-ordering (in facet wrap)
    ) {
        override fun toString(): String {
            return "FacetTileInfo(col=$col, row=$row, colLabs=$colLabs, rowLab=$rowLab)"
        }
    }
}
