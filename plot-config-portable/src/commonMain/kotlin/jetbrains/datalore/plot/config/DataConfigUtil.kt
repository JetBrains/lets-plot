/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.GeomKind
import jetbrains.datalore.plot.base.Stat
import jetbrains.datalore.plot.base.data.DataFrameUtil
import jetbrains.datalore.plot.base.stat.Stats
import jetbrains.datalore.plot.base.util.YOrientationBaseUtil

internal object DataConfigUtil {
    /**
     * returns mappings and DataFrame extended with auto-generated discrete mappings and variables
     */
    fun createDataFrame(
        commonData: DataFrame,
        ownData: DataFrame,
        commonMappings: Map<String, String>,
        ownMappings: Map<String, String>,
        commonDiscreteAes: Set<String>,
        ownDiscreteAes: Set<String>,
        isClientSide: Boolean
    ): Pair<Map<String, String>, DataFrame> {

        if (isClientSide) {
            return Pair(
                // no new discrete mappings, all job was done on server side
                ownMappings,
                // re-insert existing variables as discrete
                DataFrameUtil.toMap(ownData)
                    .filter { (varName, _) -> DataMetaUtil.isDiscrete(varName) }
                    .entries
                    .fold(DataFrame.Builder(ownData)) { acc, (varName, values) ->
                        val variable = DataFrameUtil.findVariableOrFail(ownData, varName)
                        // re-insert as discrete
                        acc.remove(variable)
                        acc.putDiscrete(variable, values)
                    }
                    .build()
            )

        }

        // server side

        // own names not yet encoded, i.e. 'cyl'
        val ownDiscreteMappings = run {
            return@run ownMappings.filter { (aes, _) -> aes in ownDiscreteAes }
        }

        // Original (not encoded) discrete var names from both common and own mappings.
        val combinedDiscreteVars = run {
            // common names already encoded by PlotConfig, i.e. '@as_discrete@cyl'. Restore original name.
            val commonDiscreteVars =
                commonMappings.filterKeys { it in commonDiscreteAes }.values.map(DataMetaUtil::fromDiscrete).toSet()

            val ownDiscreteVariables = ownDiscreteMappings.values.toSet()
            val ownSimpleVars = ownMappings.values.toSet() - ownDiscreteVariables

            // minus own non-discrete mappings (simple layer var overrides discrete plot var)
            return@run ownDiscreteVariables + commonDiscreteVars - ownSimpleVars
        }

        val combinedDfVars = DataFrameUtil.toMap(commonData) + DataFrameUtil.toMap(ownData)

        return Pair(
            ownMappings + ownDiscreteMappings.mapValues { (_, varName) ->
                DataMetaUtil.toDiscrete(varName)
            },
            combinedDfVars
                .filter { (dfVarName, _) -> dfVarName in combinedDiscreteVars }
                .mapKeys { (dfVarName, _) -> DataFrameUtil.createVariable(DataMetaUtil.toDiscrete(dfVarName)) }
                .entries
                .fold(DataFrame.Builder(ownData)) { acc, (dfVar, values) -> acc.putDiscrete(dfVar, values) }
                .build()
        )
    }


    fun layerMappingsAndCombinedData(
        layerOptions: Map<*, *>,
        geomKind: GeomKind,
        stat: Stat,

        sharedData: DataFrame,
        layerData: DataFrame,

        plotDataMeta: Map<*, *>,
        ownDataMeta: Map<String, Any>,

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
        }.run {
            // Mark 'DateTime' variables
            val dateTimeVariables = DataMetaUtil.getDateTimeColumns(plotDataMeta) +
                    DataMetaUtil.getDateTimeColumns(ownDataMeta)
            DataFrameUtil.addDateTimeVariables(this, dateTimeVariables)
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
}