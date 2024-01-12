/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.config

import org.jetbrains.letsPlot.commons.formatting.string.StringFormat
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.data.DataFrameUtil
import org.jetbrains.letsPlot.core.plot.builder.assemble.PlotFacets
import org.jetbrains.letsPlot.core.plot.builder.assemble.PlotFacets.Companion.DEF_FORMATTER
import org.jetbrains.letsPlot.core.plot.builder.assemble.PlotFacets.Companion.DEF_ORDER_DIR
import org.jetbrains.letsPlot.core.plot.builder.assemble.facet.FacetGrid
import org.jetbrains.letsPlot.core.plot.builder.assemble.facet.FacetScales
import org.jetbrains.letsPlot.core.plot.builder.assemble.facet.FacetWrap
import org.jetbrains.letsPlot.core.spec.Option.Facet
import org.jetbrains.letsPlot.core.spec.Option.Facet.FACETS_FILL_DIR
import org.jetbrains.letsPlot.core.spec.Option.Facet.X_FORMAT
import org.jetbrains.letsPlot.core.spec.Option.Facet.X_ORDER
import org.jetbrains.letsPlot.core.spec.Option.Facet.Y_FORMAT
import org.jetbrains.letsPlot.core.spec.Option.Facet.Y_ORDER

internal class FacetConfig(
    options: Map<String, Any>,
    private val superscriptExponent: Boolean
) : OptionsAccessor(options) {

    fun createFacets(dataByLayer: List<DataFrame>): PlotFacets {
        return when (val name = getStringSafe(Facet.NAME)) {
            Facet.NAME_GRID -> createGrid(dataByLayer)
            Facet.NAME_WRAP -> createWrap(dataByLayer)
            else -> throw IllegalArgumentException("Facet 'grid' or 'wrap' expected but was: `$name`")
        }
    }

    private fun createGrid(
        dataByLayer: List<DataFrame>
    ): PlotFacets {
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

        val scales: FacetScales = getScalesOption()

        return FacetGrid(
            nameX, nameY, ArrayList(levelsX), ArrayList(levelsY),
            getOrderOption(X_ORDER),
            getOrderOption(Y_ORDER),
            getFormatterOption(X_FORMAT),
            getFormatterOption(Y_FORMAT),
            scales
        )
    }

    private fun createWrap(
        dataByLayer: List<DataFrame>
    ): PlotFacets {
        // 'facets' cal be just one name or a list of names.
        val facets = getAsStringList(Facet.FACETS)

        val ncol = getInteger(Facet.NCOL)
        val nrow = getInteger(Facet.NROW)

        val facetLevels = ArrayList<List<Any>>()
        for (name in facets) {
            val levels = LinkedHashSet<Any>()
            for (data in dataByLayer) {
                if (DataFrameUtil.hasVariable(data, name)) {
                    val variable = DataFrameUtil.findVariableOrFail(data, name)
                    levels.addAll(data.distinctValues(variable))
                }
            }
            facetLevels.add(levels.toList())
        }

        // facet ordering
        val orderOption = getAsList(Facet.FACETS_ORDER).map {
            toOrderVal(it)
        }
        // Num of order values must be same as num of factes.
        val ordering = (orderOption + List(facets.size) { DEF_ORDER_DIR }).take(facets.size)

        // facet formatting
        val formatterOption = getAsList(Facet.FACETS_FORMAT).map {
            toFormatterVal(it)
        }
        // Num of formatters must be same as num of factes.
        val formatters = (formatterOption + List(facets.size) { DEF_FORMATTER }).take(facets.size)

        val scales: FacetScales = getScalesOption()

        return FacetWrap(facets, facetLevels, nrow, ncol, getDirOption(), ordering, formatters, scales)
    }


    private fun getOrderOption(optionName: String): Int {
        return toOrderVal(get(optionName))
    }

    private fun toOrderVal(optionVal: Any?): Int {
        return when (optionVal) {
            null -> DEF_ORDER_DIR
            is Number -> optionVal.toInt()
            else -> throw IllegalArgumentException(
                "Unsupported `order` value: $optionVal.\n" +
                        "Use: 1 (natural), -1 (descending) or 0 (no ordering)."
            )
        }
    }

    private fun getDirOption(): FacetWrap.Direction {
        return when (val opt = get(FACETS_FILL_DIR)) {
            null -> FacetWrap.Direction.H
            else -> when (opt.toString().uppercase()) {
                "V" -> FacetWrap.Direction.V
                "H" -> FacetWrap.Direction.H
                else -> throw IllegalArgumentException(
                    "Unsupported `dir` value: $opt.\n" +
                            "Use: 'H' (horizontal) or 'V' (vertical)."
                )
            }
        }
    }

    private fun toFormatterVal(optionVal: Any?): (Any) -> String {
        return when (optionVal) {
            null -> DEF_FORMATTER
            else -> {
                val fmt = StringFormat.forOneArg(optionVal.toString(), superscriptExponent = superscriptExponent)
                return { value: Any -> fmt.format(value) }
            }
        }
    }

    private fun getFormatterOption(optionName: String): (Any) -> String {
        return toFormatterVal(get(optionName))
    }

    private fun getScalesOption(): FacetScales {
        return getString(Facet.SCALES)?.let {
            when (it.lowercase()) {
                Facet.SCALES_FIXED -> FacetScales.FIXED
                Facet.SCALES_FREE -> FacetScales.FREE
                Facet.SCALES_FREE_X -> FacetScales.FREE_X
                Facet.SCALES_FREE_Y -> FacetScales.FREE_Y
                else -> throw IllegalArgumentException(
                    "Unsupported `scales` value: $it.\n" +
                            "Use: ${Facet.SCALES_FIXED}, ${Facet.SCALES_FREE}, " +
                            "${Facet.SCALES_FREE_X} or ${Facet.SCALES_FREE_Y}"
                )
            }
        } ?: FacetScales.FIXED
    }
}
