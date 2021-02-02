/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.assemble

import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.data.DataFrameUtil
import jetbrains.datalore.plot.builder.assemble.facet.FacetGrid
import jetbrains.datalore.plot.common.data.SeriesUtil

abstract class PlotFacets {

    abstract val isDefined: Boolean
    abstract val colCount: Int
    abstract val rowCount: Int
    abstract val numTiles: Int
    abstract val variables: List<String>

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


    companion object {
        const val DEF_ORDER_DIR = 1
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

            val vars = varNames.map { DataFrameUtil.findVariableOrFail(data, it) }

            val indicesByVarByLevel = HashMap<String, Map<Any, List<Int>>>()
            for ((i, variable) in vars.withIndex()) {
                val levels = varLevels[i]

                val indicesByLevel = HashMap<Any, List<Int>>()
                for (level in levels) {
                    val indices = SeriesUtil.matchingIndices(data[variable], level)
                    indicesByLevel[level] = indices
                }

                indicesByVarByLevel[variable.name] = indicesByLevel
            }

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
                val b = DataFrame.Builder()
                val variables = data.variables()
                for (variable in variables) {
                    val source = data[variable]
                    val target = SeriesUtil.pickAtIndices(source, indices)
                    b.put(variable, target)
                }

                val levelData = b.build()
                dataByLevelKey.add(levelKey to levelData)
            }

            return dataByLevelKey
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
            @Suppress("UNCHECKED_CAST", "NAME_SHADOWING")
            levels as List<Comparable<Any>>

            return when {
                order < 0 -> levels.sortedDescending()
                else -> levels.sorted()
            }
        }
    }

    class FacetTileInfo(
        val col: Int,
        val row: Int,
        val colLabs: List<String>,
        val rowLab: String?,
        val xAxis: Boolean,
        val yAxis: Boolean,
        val trueIndex: Int     // tile index before re-ordering (in facet wrap)
    ) {
        override fun toString(): String {
            return "FacetTileInfo(col=$col, row=$row, colLabs=$colLabs, rowLab=$rowLab)"
        }
    }
}
