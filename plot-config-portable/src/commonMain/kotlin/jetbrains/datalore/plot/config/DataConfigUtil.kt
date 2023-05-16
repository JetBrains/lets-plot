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

    fun layerMappingsAndCombinedData(
        layerOptions: Map<*, *>,
        geomKind: GeomKind,
        stat: Stat,

        sharedData: DataFrame,
        layerData: DataFrame,

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
        orderOptions: List<OrderOptionUtil.OrderOption>,
        aggregateOperation: ((List<Double?>) -> Double?),
        clientSide: Boolean
    ): DataFrame {

        fun DataFrame.Builder.addVariables(
            variable: DataFrame.Variable,
            vars: Set<String>,
            put: (DataFrame.Builder, DataFrame.Variable, List<*>) -> DataFrame.Builder
        ) : DataFrame.Builder {
            if (variable.name in vars) {
                remove(variable)
                put(this, variable, rawCombinedData[variable])
            }
            return this
        }

        // 'DateTime' variables
        val dateTimeVariables = DataMetaUtil.getDateTimeColumns(plotDataMeta) +
                DataMetaUtil.getDateTimeColumns(ownDataMeta)

        // 'as_discrete' variables
        val asDiscreteAesSet = DataMetaUtil.getAsDiscreteAesSet(plotDataMeta) +
                DataMetaUtil.getAsDiscreteAesSet(ownDataMeta)
        val asDiscreteVariables = varBindings.filter { it.aes.name in asDiscreteAesSet }.map { it.variable.name }.toSet()

        return rawCombinedData.builder().run {

            rawCombinedData.variables().forEach { variable ->
                addVariables(variable, dateTimeVariables, DataFrame.Builder::putDateTime)
                addVariables(variable, asDiscreteVariables, DataFrame.Builder::putDiscrete)
            }

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