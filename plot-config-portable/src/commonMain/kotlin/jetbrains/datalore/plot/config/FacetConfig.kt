/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.data.DataFrameUtil
import jetbrains.datalore.plot.builder.assemble.PlotFacets
import jetbrains.datalore.plot.builder.assemble.facet.FacetGrid
import jetbrains.datalore.plot.builder.assemble.facet.FacetWrap
import jetbrains.datalore.plot.config.Option.Facet

internal class FacetConfig(options: Map<String, Any>) : OptionsAccessor(options) {

    fun createFacets(dataByLayer: List<DataFrame>): PlotFacets {
        val levelOrderingByFacet = HashMap<String, PlotFacets.Order>()
        if (has(Facet.LEVEL_ORDERING)) {
            val levelOrdering = getMap(Facet.LEVEL_ORDERING)
            for ((varName, dir) in levelOrdering) {
                val order = when (dir.toString()) {
                    Facet.LEVEL_ORDERING_ASC -> PlotFacets.Order.ASC
                    Facet.LEVEL_ORDERING_DESC -> PlotFacets.Order.DESC
                    else -> {
                        throw IllegalArgumentException("Ordering direction expected: `asc` or `desc`, but was: $varName : $dir ")
                    }
                }
                levelOrderingByFacet[varName] = order
            }
        }

        return when (val name = getStringSafe(Facet.NAME)) {
            Facet.NAME_GRID -> createGrid(dataByLayer, levelOrderingByFacet)
            Facet.NAME_WRAP -> createWrap(dataByLayer, levelOrderingByFacet)
            else -> throw IllegalArgumentException("Facet 'grid' or 'wrap' expected but was: `$name`")
        }
    }

    private fun createGrid(
        dataByLayer: List<DataFrame>,
        levelOrderingByFacet: HashMap<String, PlotFacets.Order>
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

        return FacetGrid(nameX, nameY, ArrayList(levelsX), ArrayList(levelsY), levelOrderingByFacet)
    }

    private fun createWrap(
        dataByLayer: List<DataFrame>,
        levelOrderingByFacet: HashMap<String, PlotFacets.Order>
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

        return FacetWrap(facets, facetLevels, nrow, ncol, FacetWrap.Direction.H, levelOrderingByFacet)
    }
}
