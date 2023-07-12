/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.assemble

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.data.DataFrameUtil
import jetbrains.datalore.plot.builder.assemble.facet.FacetGrid
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil

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
     *          Tiles are enumerated by rows, i.e.:
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

    companion object {
        const val DEF_ORDER_DIR = 0 // no ordering
        val DEF_FORMATTER: (Any) -> String = { it.toString() }

        fun undefined(): PlotFacets {
            return FacetGrid(null, null, emptyList<Any>(), emptyList<Any>(), 1, 1)
        }

        fun dataByLevelTuple(
            data: DataFrame,
            varNames: List<String>,
            varLevels: List<List<Any>>
        ): List<Pair<List<Any>, DataFrame>> {
            // This also checks invariants.
            val nameLevelTuples = createNameLevelTuples(varNames, varLevels)

            val indicesByVarByLevel = dataIndicesByVarByLevel(data, varNames, varLevels)

            val dataByLevelKey = ArrayList<Pair<List<Any>, DataFrame>>()
            for (nameLevelTuple in nameLevelTuples) {
                val topName = nameLevelTuple.first().first
                val topLevel = nameLevelTuple.first().second
                val indices = ArrayList(indicesByVarByLevel.getValue(topName).getValue(topLevel))
                for (i in 1 until nameLevelTuple.size) {
                    val name = nameLevelTuple[i].first
                    val level = nameLevelTuple[i].second
                    val levelIndices = indicesByVarByLevel.getValue(name).getValue(level)
                    indices.retainAll(HashSet(levelIndices))
                }

                val levelKey = nameLevelTuple.map { it.second }

                // build the data subset
                val levelData = data.slice(indices)
                dataByLevelKey.add(levelKey to levelData)
            }

            return dataByLevelKey
        }

        private fun dataIndicesByVarByLevel(
            data: DataFrame,
            varNames: List<String>,
            varLevels: List<List<Any>>
        ): Map<String, Map<Any, List<Int>>> {

            val indicesByVarByLevel = HashMap<String, Map<Any, List<Int>>>()
            for ((i, varName) in varNames.withIndex()) {
                val levels = varLevels[i]

                val indicesByLevel = HashMap<Any, List<Int>>()
                for (level in levels) {
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
                    indicesByLevel[level] = indices
                }

                indicesByVarByLevel[varName] = indicesByLevel
            }

            return indicesByVarByLevel
        }

        fun createNameLevelTuples(
            varNames: List<String>,
            varLevels: List<List<Any>>
        ): List<List<Pair<String, Any>>> {
            require(varNames.isNotEmpty()) { "Empty list of facet variables." }
            require(varNames.size == varNames.distinct().size) { "Facet variables must be distinct, were: $varNames." }
            check(varNames.size == varLevels.size)
            return createNameLevelTuplesIntern(varNames, varLevels)
        }

        private fun createNameLevelTuplesIntern(
            varNames: List<String>,
            varLevels: List<List<Any>>
        ): List<List<Pair<String, Any>>> {
            val name = varNames.first()
            val levels = varLevels.first()

            val levelKeys = ArrayList<List<Pair<String, Any>>>()
            for (level in levels) {
                if (varNames.size > 1) {
                    val subKeys = createNameLevelTuples(
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

        fun reorderLevels(
            varNames: List<String>,
            varLevels: List<List<Any>>,
            ordering: List<Int>
        ): List<List<Any>> {
            val orderingByFacet = varNames.zip(ordering).toMap()

            val result = ArrayList<List<Any>>()
            for ((i, name) in varNames.withIndex()) {
                if (i >= varLevels.size) break
                result.add(reorderVarLevels(name, varLevels[i], orderingByFacet.getValue(name)))
            }

            return result
        }

        fun reorderVarLevels(
            name: String?,
            levels: List<Any>,
            order: Int
        ): List<Any> {
            if (name == null) return levels

            // We expect either a list of Doubles or a list of Strings.
            @Suppress("UNCHECKED_CAST")
            levels as List<Comparable<Any>>

            return when {
                order <= -1 -> levels.sortedDescending()
                order >= 1 -> levels.sorted()
                else -> levels  // not ordered
            }
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
