/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.config

import org.jetbrains.letsPlot.commons.formatting.string.StringFormat
import org.jetbrains.letsPlot.commons.intern.datetime.TimeZone
import org.jetbrains.letsPlot.core.commons.data.DataType
import org.jetbrains.letsPlot.core.commons.data.DataType.UNKNOWN
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.FormatterUtil.byDataType
import org.jetbrains.letsPlot.core.plot.base.data.DataFrameUtil
import org.jetbrains.letsPlot.core.plot.base.theme.ExponentFormat
import org.jetbrains.letsPlot.core.plot.builder.assemble.PlotAssembler
import org.jetbrains.letsPlot.core.plot.builder.assemble.PlotFacets
import org.jetbrains.letsPlot.core.plot.builder.assemble.PlotFacets.Companion.DEF_LAB_WIDTH
import org.jetbrains.letsPlot.core.plot.builder.assemble.PlotFacets.Companion.DEF_ORDER_DIR
import org.jetbrains.letsPlot.core.plot.builder.assemble.PlotFacets.Companion.dataIndicesByTile
import org.jetbrains.letsPlot.core.plot.builder.assemble.PlotFacets.Companion.varNameAndLevelPairsByTile
import org.jetbrains.letsPlot.core.plot.builder.assemble.facet.FacetGrid
import org.jetbrains.letsPlot.core.plot.builder.assemble.facet.FacetScales
import org.jetbrains.letsPlot.core.plot.builder.assemble.facet.FacetWrap
import org.jetbrains.letsPlot.core.spec.Option.Facet
import org.jetbrains.letsPlot.core.spec.Option.Facet.FACETS_FILL_DIR
import org.jetbrains.letsPlot.core.spec.Option.Facet.X_LABWIDTH
import org.jetbrains.letsPlot.core.spec.Option.Facet.X_ORDER
import org.jetbrains.letsPlot.core.spec.Option.Facet.Y_LABWIDTH
import org.jetbrains.letsPlot.core.spec.Option.Facet.Y_ORDER

internal class FacetConfig(
    options: Map<String, Any>,
    private val expFormat: ExponentFormat,
    private val tz: TimeZone?,
    private val dtypeByVarName: Map<String, DataType>,
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
            requireVariableExistsInData(nameX, dataByLayer)
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
            requireVariableExistsInData(nameY, dataByLayer)
            for (data in dataByLayer) {
                if (DataFrameUtil.hasVariable(data, nameY)) {
                    val variable = DataFrameUtil.findVariableOrFail(data, nameY)
                    levelsY.addAll(data.distinctValues(variable))
                }
            }
        }

        val scales: FacetScales = getScalesOption()

        val xOrder = getOrderOption(X_ORDER)
        val yOrder = getOrderOption(Y_ORDER)
        val xLevels: List<Any> = reorderVarLevels(nameX, levelsX.toList(), xOrder)
        val yLevels: List<Any> = reorderVarLevels(nameY, levelsY.toList(), yOrder)
        val expFormat = PlotAssembler.extractExponentFormat(expFormat)
        val xFormatter = if (nameX == null) PlotFacets.NO_FORMATTER else {
            createFormatter(
                varName = nameX,
                dataType = dtypeByVarName[nameX] ?: UNKNOWN,
                providedFormat = getString(Facet.X_FORMAT),
                expFormat = expFormat,
                tz = tz
            )
        }
        val yFormatter = if (nameY == null) PlotFacets.NO_FORMATTER else {
            createFormatter(
                varName = nameY,
                dataType = dtypeByVarName[nameY] ?: UNKNOWN,
                providedFormat = getString(Facet.Y_FORMAT),
                expFormat = expFormat,
                tz = tz
            )
        }

        return FacetGrid(
            nameX, nameY,
            xLevels, yLevels,
            xFormatter, yFormatter,
            scales,
            getLabWidthOption(X_LABWIDTH),
            getLabWidthOption(Y_LABWIDTH)
        )
    }

    private fun createWrap(
        dataByLayer: List<DataFrame>
    ): PlotFacets {
        // 'facets' can be just one name or a list of names.
        val facets = getAsStringList(Facet.FACETS)

        val ncol = getInteger(Facet.NCOL)
        val nrow = getInteger(Facet.NROW)

        val facetLevels = ArrayList<List<Any>>()
        for (name in facets) {
            requireVariableExistsInData(name, dataByLayer)
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
        val facetOrdering = (orderOption + List(facets.size) { DEF_ORDER_DIR }).take(facets.size)

        // facet formatting
        val formatsProvided = (getAsListQQ(Facet.FACETS_FORMAT)?.map {
            when (it) {
                is String -> it
                else -> null
            }
        } ?: emptyList())
            .let {
                // Pad with nulls to match the number of facet variables
                it + List(facets.size) { null }.take(facets.size)
            }

        val expFormat = PlotAssembler.extractExponentFormat(expFormat)
        val formatters = facets.zip(formatsProvided).map { (varName, providedFormat) ->
            createFormatter(
                varName = varName,
                dataType = dtypeByVarName[varName] ?: UNKNOWN,
                providedFormat = providedFormat,
                expFormat = expFormat,
                tz = tz
            )
        }

        // Label length limit
        val labWidthOption = getAsList(Facet.FACETS_LABWIDTH).map { (it as? Number)?.toInt() ?: DEF_LAB_WIDTH }
        val labWidths = (labWidthOption + List(facets.size) { DEF_LAB_WIDTH }).take(facets.size)

        val scales: FacetScales = getScalesOption()

        val orderedFacetLevels: List<List<Any>> = reorderLevels(facets, facetLevels, facetOrdering)
        val varNameAndLevelPairsByTileRaw: List<List<Pair<String, Any>>> = varNameAndLevelPairsByTile(
            facets,
            orderedFacetLevels
        )

        val dropUnusedLevels = getBoolean(Facet.DROP_UNUSED_LEVELS, true)
        val varNameAndLevelPairsByTile: List<List<Pair<String, Any>>> =
            if (dropUnusedLevels) {
                filterUnusedLevels(dataByLayer, varNameAndLevelPairsByTileRaw)
            } else {
                varNameAndLevelPairsByTileRaw
            }

        return FacetWrap(
            facets,
            varNameAndLevelPairsByTile,
            nrow, ncol,
            direction = getDirOption(),
            facetFormatters = formatters,
            scales = scales,
            labWidths = labWidths
        )
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

    private fun getLabWidthOption(optionName: String) = getIntegerDef(optionName, DEF_LAB_WIDTH)

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

    private companion object {
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

        fun filterUnusedLevels(
            dataByLayer: List<DataFrame>,
            varNameAndLevelPairsByTileRaw: List<List<Pair<String, Any>>>,
        ): List<List<Pair<String, Any>>> {
            // Drop empty facets (if any)
            val dataSizeByTile: List<Int> =
                MutableList<Int>(varNameAndLevelPairsByTileRaw.size) { 0 }.apply {
                    dataByLayer.forEach { data ->
                        val indicesByTile = dataIndicesByTile(data, varNameAndLevelPairsByTileRaw)

                        // Update totals
                        indicesByTile.forEachIndexed { tileIndex, indices ->
                            this[tileIndex] = this[tileIndex] + indices.size
                        }
                    }
                }


            return varNameAndLevelPairsByTileRaw.filterIndexed { index, _ ->
                dataSizeByTile[index] > 0
            }
        }

        fun requireVariableExistsInData(varName: String, dataFrames: List<DataFrame>) {
            require(dataFrames.isEmpty() || dataFrames.any { data ->
                DataFrameUtil.hasVariable(data, varName)
            }) {
                dataFrames.joinToString(separator = "\n") {
                    it.undefinedVariableErrorMessage(varName)
                }
            }
        }

        fun createFormatter(
            varName: String,
            dataType: DataType,
            providedFormat: String?,
            expFormat: StringFormat.ExponentFormat,
            tz: TimeZone?,
        ): (Any) -> String {
            return when (providedFormat) {
                null -> {
                    byDataType(dataType, expFormat, tz)
                }

                else -> {
                    val sf = StringFormat.forOneArg(
                        pattern = providedFormat,
                        type = null, // ?
                        formatFor = varName,
                        expFormat = expFormat,
                        tz = tz
                    );

                    { value: Any -> sf.format(value) }
                }
            }
        }
    }
}
