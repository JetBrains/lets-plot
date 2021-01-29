/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.data.DataFrameUtil
import jetbrains.datalore.plot.builder.assemble.PlotFacets
import jetbrains.datalore.plot.builder.assemble.PlotFacets.Companion.DEF_LEVEL_ORDER
import jetbrains.datalore.plot.builder.assemble.facet.FacetGrid
import jetbrains.datalore.plot.builder.assemble.facet.FacetWrap
import jetbrains.datalore.plot.config.Option.Facet

internal class FacetConfig(options: Map<String, Any>) : OptionsAccessor(options) {

    fun createFacets(dataByLayer: List<DataFrame>): PlotFacets {
        fun toOrderDir(dir: Any?): PlotFacets.Order {
            return when (dir) {
                null -> DEF_LEVEL_ORDER
                else -> when (dir.toString().toLowerCase()) {
                    Facet.LEVEL_ORDERING_ASC -> PlotFacets.Order.ASC
                    Facet.LEVEL_ORDERING_DESC -> PlotFacets.Order.DESC
                    else -> {
                        throw IllegalArgumentException("Ordering direction expected: `asc` or `desc`, but was: `$dir`")
                    }
                }
            }
        }

        val levelOrderingList = if (has(Facet.LEVEL_ORDERING)) {
            when (val levelOrdering = get(Facet.LEVEL_ORDERING)) {
                is List<*> -> levelOrdering.map { toOrderDir(it) }
                else -> listOf(toOrderDir(levelOrdering))
            }
        } else {
            emptyList()
        }

        return when (val name = getStringSafe(Facet.NAME)) {
            Facet.NAME_GRID -> createGrid(dataByLayer, levelOrderingList)
            Facet.NAME_WRAP -> createWrap(dataByLayer, levelOrderingList)
            else -> throw IllegalArgumentException("Facet 'grid' or 'wrap' expected but was: `$name`")
        }
    }

    private fun createGrid(
        dataByLayer: List<DataFrame>,
        levelOrdering: List<PlotFacets.Order>
    ): FacetGrid {
        var nameX: String? = null
        val levelsX = LinkedHashSet<Any>()
        if (has(Facet.X)) {
            nameX = getStringSafe(Facet.X)
            for (data in dataByLayer) {
                if (DataFrameUtil.hasVariable(data, nameX)) {
                    val variable = DataFrameUtil.findVariableOrFail(data, nameX)
                    levelsX.addAll(data.distinctValues(variable))
                }
            }
        }

        var nameY: String? = null
        val levelsY = LinkedHashSet<Any>()
        if (has(Facet.Y)) {
            nameY = getStringSafe(Facet.Y)
            for (data in dataByLayer) {
                if (DataFrameUtil.hasVariable(data, nameY)) {
                    val variable = DataFrameUtil.findVariableOrFail(data, nameY)
                    levelsY.addAll(data.distinctValues(variable))
                }
            }
        }

        val xOrder = nameX?.let {
            if (levelOrdering.isNotEmpty()) levelOrdering[0]
            else DEF_LEVEL_ORDER
        } ?: DEF_LEVEL_ORDER
        val yOrder = nameY?.let {
            if (levelOrdering.size > 1) levelOrdering[1]
            else DEF_LEVEL_ORDER
        } ?: DEF_LEVEL_ORDER
        return FacetGrid(nameX, nameY, ArrayList(levelsX), ArrayList(levelsY), xOrder, yOrder)
    }

    private fun createWrap(
        dataByLayer: List<DataFrame>,
        levelOrdering: List<PlotFacets.Order>
    ): FacetWrap {
        // 'facets' cal be just one name or a list of names.
        val facets = getAsStringList(Facet.FACETS)

        val ncol = getInteger(Facet.NCOL)
        val nrow = getInteger(Facet.NROW)

        val facetLevels = ArrayList<List<Any>>()
        for (name in facets) {
            val levels = HashSet<Any>()
            for (data in dataByLayer) {
                if (DataFrameUtil.hasVariable(data, name)) {
                    val variable = DataFrameUtil.findVariableOrFail(data, name)
                    levels.addAll(data.get(variable).filterNotNull())
                }
            }
            facetLevels.add(levels.toList())
        }

        val levelOrderingFull = ArrayList<PlotFacets.Order>()
        for (i in facets.indices) {
            levelOrderingFull.add(
                if (i < levelOrdering.size) levelOrdering[i]
                else DEF_LEVEL_ORDER
            )
        }
        return FacetWrap(facets, facetLevels, nrow, ncol, FacetWrap.Direction.H, levelOrderingFull)
    }
}
