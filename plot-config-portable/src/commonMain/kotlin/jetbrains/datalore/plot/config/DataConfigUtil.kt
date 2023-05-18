/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.base.collections.filterNotNullKeys
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.GeomKind
import jetbrains.datalore.plot.base.Stat
import jetbrains.datalore.plot.base.data.DataFrameUtil
import jetbrains.datalore.plot.base.stat.Stats
import jetbrains.datalore.plot.base.util.YOrientationBaseUtil
import jetbrains.datalore.plot.builder.VarBinding
import jetbrains.datalore.plot.builder.data.OrderOptionUtil

internal object DataConfigUtil {
    /**
     * returns DataFrame extended with auto-generated discrete variables
     */
    fun createDataFrame(
        commonDataFrame: DataFrame,
        ownDataFrame: DataFrame,
        combinedDiscreteMappings: Map<String, String>,
        isClientSide: Boolean
    ): DataFrame {
        if (isClientSide) {
            // no new discrete variables, all job was done on server side
            return ownDataFrame
        }

        // server side
        val ownData = DataFrameUtil.toMap(ownDataFrame)
        val combinedData = DataFrameUtil.toMap(commonDataFrame) + ownData

        // copy columns
        val asDiscreteColumns = combinedDiscreteMappings
            .filter { (_, varName) -> combinedData.containsKey(varName) }
            .map { (aes, varName) -> DataMetaUtil.asDiscreteName(aes, varName) to combinedData[varName] }
            .toMap()

        return DataFrameUtil.fromMap(ownData + asDiscreteColumns)
    }

    /**
     * returns original (not encoded) discrete var names from both common and own mappings.
     */
    fun combinedDiscreteMapping(
        commonMappings: Map<String, String>,
        ownMappings: Map<String, String>,
        commonDiscreteAes: Set<String>,
        ownDiscreteAes: Set<String>,
    ): Map<String, String> {

        // own as_discrete variables
        val ownDiscreteMappings = (commonMappings + ownMappings).filter { (aes, _) -> aes in ownDiscreteAes }

        // common names already encoded by PlotConfig. Restore original name.
        val commonDiscreteMappings = commonMappings
            .filterKeys { it in commonDiscreteAes }
            .mapValues { (aes, varName) -> DataMetaUtil.fromAsDiscrete(aes, varName) }

        val ownSimpleMappings = ownMappings - ownDiscreteMappings.keys

        // minus own non-discrete mappings (simple layer var overrides discrete plot var)
        return ownDiscreteMappings + commonDiscreteMappings - ownSimpleMappings.keys
    }

    fun layerMappingsAndCombinedData(
        layerOptions: Map<*, *>,
        geomKind: GeomKind,
        stat: Stat,

        sharedData: DataFrame,
        layerData: DataFrame,

        asDiscreteAesSet: Set<String>,

        consumedAesMappings: Map<*, *>,
        explicitConstantAes: List<Aes<*>>,

        isYOrientation: Boolean,
        clientSide: Boolean,
        isMapPlot: Boolean,
    ): Pair<Map<Aes<*>, DataFrame.Variable>, DataFrame> {

        val isGeoConfigApplicable = GeoConfig.isApplicable(layerOptions, consumedAesMappings, isMapPlot)
        val isDataGeoDF = GeoConfig.isGeoDataframe(layerOptions, Option.PlotBase.DATA)

        // If layer has no mapping then no data is needed.
        val dropData: Boolean = (consumedAesMappings.isEmpty() &&
                // Do not touch GeoDataframe - empty mapping is OK in this case.
                !isDataGeoDF &&
                !isGeoConfigApplicable
                )

        var combinedData = when {
            dropData -> DataFrame.Builder.emptyFrame()
            !(sharedData.isEmpty || layerData.isEmpty) && sharedData.rowCount() == layerData.rowCount() -> {
                DataFrameUtil.appendReplace(sharedData, layerData)
            }

            !layerData.isEmpty -> layerData
            else -> sharedData
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
            aesMappings = ConfigUtil.createAesMapping(combinedData, consumedAesMappings, asDiscreteAesSet)
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

                this
                    .addOrderSpecs(orderSpecs)
                    .addFactorLevels(factorLevelsByVar)
            }

            build()
        }
    }
}