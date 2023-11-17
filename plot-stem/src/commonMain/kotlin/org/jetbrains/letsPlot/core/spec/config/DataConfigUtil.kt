/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.config

import org.jetbrains.letsPlot.commons.intern.filterNotNullKeys
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.plot.base.Stat
import org.jetbrains.letsPlot.core.plot.base.data.DataFrameUtil
import org.jetbrains.letsPlot.core.plot.base.stat.Stats
import org.jetbrains.letsPlot.core.plot.base.util.YOrientationBaseUtil
import org.jetbrains.letsPlot.core.plot.builder.VarBinding
import org.jetbrains.letsPlot.core.plot.builder.data.OrderOptionUtil
import org.jetbrains.letsPlot.core.spec.Option

internal object DataConfigUtil {

    /**
     * returns discrete var names from both common and own mappings.
     */
    fun combinedDiscreteMapping(
        commonMappings: Map<String, String>,
        ownMappings: Map<String, String>,
        commonDiscreteAes: Set<String>,
        ownDiscreteAes: Set<String>,
    ): Map<String, String> {

        // own as_discrete variables
        val ownDiscreteMappings = ownMappings.filter { (aes, _) -> aes in ownDiscreteAes }
        val ownSimpleMappings = ownMappings - ownDiscreteMappings.keys

        // common names already encoded by PlotConfig. Restore original name.
        val commonDiscreteMappings = commonMappings.filterKeys { it in commonDiscreteAes }

        // minus own non-discrete mappings (simple layer var overrides discrete plot var)
        return ownDiscreteMappings + commonDiscreteMappings - ownSimpleMappings.keys
    }

    private fun appendAsDiscreteData(
        dataFrame: DataFrame,
        discreteMappings: Map<String, String>
    ): DataFrame {
        val data = DataFrameUtil.toMap(dataFrame)
        // Copy columns with new name "aes.var-name"
        val asDiscreteColumns = discreteMappings
            .filter { (_, varName) -> data.containsKey(varName) }
            .map { (aes, varName) -> DataMetaUtil.asDiscreteName(aes, varName) to data[varName] }
            .toMap()
        return DataFrameUtil.fromMap(data + asDiscreteColumns)
    }

    fun layerMappingsAndCombinedData(
        layerOptions: Map<*, *>,
        geomKind: GeomKind,
        stat: Stat,

        sharedData: DataFrame,
        layerData: DataFrame,

        combinedDiscreteMappings: Map<String, String>,

        consumedAesMappings: Map<*, *>,
        explicitConstantAes: List<Aes<*>>,

        isYOrientation: Boolean,
        clientSide: Boolean,
        isMapPlot: Boolean,
    ): Pair<Map<Aes<*>, DataFrame.Variable>, DataFrame> {

        val isGeoConfigApplicable = GeoConfig.isApplicable(layerOptions, consumedAesMappings, isMapPlot)
        val isDataGeoDF = GeoConfig.isGeoDataframe(layerOptions, Option.PlotBase.DATA)

        val keepData: Boolean =
            // Do not drop data on the client: some of stat-vars are mapped by default.
            clientSide && stat != Stats.IDENTITY
                    // Do not touch GeoDataframe - empty mapping is OK in this case.
                    || isDataGeoDF || isGeoConfigApplicable

        // If layer has no mappings then no data is needed.
        val dropData: Boolean = consumedAesMappings.isEmpty() && !keepData

        var combinedData = when {
            dropData -> DataFrame.Builder.emptyFrame()
            !(sharedData.isEmpty || layerData.isEmpty) && sharedData.rowCount() == layerData.rowCount() -> {
                DataFrameUtil.appendReplace(sharedData, layerData)
            }

            !layerData.isEmpty -> layerData
            else -> sharedData
        }

        // Client side : copy variable marked as 'as_discrete' with name "aes.var-name"
        if (clientSide) {
            combinedData = appendAsDiscreteData(combinedData, combinedDiscreteMappings)
        }

        var aesMappings: Map<Aes<*>, DataFrame.Variable>
        if (clientSide && isGeoConfigApplicable) {
            val geoConfig = GeoConfig(
                geomKind,
                combinedData,
                layerOptions,
                consumedAesMappings
            )
            combinedData = geoConfig.dataAndCoordinates
            aesMappings = geoConfig.mappings

        } else {
            aesMappings = ConfigUtil.createAesMapping(combinedData, consumedAesMappings)
        }

        if (clientSide) {
            // add stat default mappings
            val statDefMapping = Stats.defaultMapping(stat).let {
                when (isYOrientation) {
                    true -> YOrientationBaseUtil.flipAesKeys(it)
                    false -> it
                }
            }
            // Only keys (aes) in 'statDefMapping' that are not already present in 'aesMappinds'.
            aesMappings = statDefMapping + aesMappings
        }

        // drop from aes mapping constant that were defined explicitly.
        @Suppress("ConvertArgumentToSet")
        aesMappings = aesMappings - explicitConstantAes

        return Pair(
            first = aesMappings,
            second = combinedData
        )
    }

    fun combinedDataWithDataMeta(
        rawCombinedData: DataFrame,
        varBindings: List<VarBinding>,
        plotDataMeta: Map<*, *>,
        ownDataMeta: Map<String, Any>,
        asDiscreteAesSet: Set<String>,
        orderOptions: List<OrderOptionUtil.OrderOption>,
        aggregateOperation: (List<Double?>) -> Double?,
        combinedMappingOptions: Map<String, String>,
        clientSide: Boolean
    ): DataFrame {

        // 'DateTime' variables
        val dateTimeVariables = DataMetaUtil.getDateTimeColumns(plotDataMeta) +
                DataMetaUtil.getDateTimeColumns(ownDataMeta)
        val variablesToMarkAsDateTime = rawCombinedData.variables().filter { it.name in dateTimeVariables }

        // 'as_discrete' variables
        val asDiscreteVariables = varBindings.filter { it.aes.name in asDiscreteAesSet }.map { it.variable.name }
        val variablesToMarkAsDiscrete = rawCombinedData.variables().filter { it.name in asDiscreteVariables }

        fun DataFrame.Builder.addVariables(
            variables: List<DataFrame.Variable>,
            put: (DataFrame.Builder, DataFrame.Variable, List<*>) -> DataFrame.Builder
        ) : DataFrame.Builder {
            variables.forEach { variable ->
                this.remove(variable)
                put(this, variable, rawCombinedData[variable])
            }
            return this
        }
        return rawCombinedData.builder().run {

            addVariables(variablesToMarkAsDateTime, DataFrame.Builder::putDateTime)
            addVariables(variablesToMarkAsDiscrete, DataFrame.Builder::putDiscrete)

            if (clientSide) {
                val variables = rawCombinedData.variables()
                val orderSpecs = OrderOptionUtil.createOrderSpecs(orderOptions, variables, varBindings, aggregateOperation)
                val factorLevelsByVar = DataMetaUtil.getFactorLevelsByVariable(ownDataMeta)
                    .mapKeys { (varName, _) -> variables.find { it.name == varName } }
                    .filterNotNullKeys()

                val plotFactorLevelsByVar = DataMetaUtil.getFactorLevelsByVariable(plotDataMeta)
                val layerFactorLevelsByVar = DataMetaUtil.getFactorLevelsByVariable(ownDataMeta)
                val factorLevelsByVar = (plotFactorLevelsByVar + layerFactorLevelsByVar)
                    .flatMap { (varName, levels) ->
                        val variablesWithLevels: MutableList<DataFrame.Variable> = combinedMappingOptions
                            .filterValues { it == varName }
                            .keys
                            .mapNotNull { aesName ->
                                varBindings.find { it.aes.name == aesName }?.variable
                            }
                            .toMutableList()
                        if (variablesWithLevels.isEmpty()) {
                            variables.find { it.name == varName }?.let { variablesWithLevels += it }
                        }
                        variablesWithLevels.map { it to levels }
                    }
                    .toMap()

                this
                    .addOrderSpecs(orderSpecs)
                    .addFactorLevels(factorLevelsByVar)
            }

            build()
        }
    }
}