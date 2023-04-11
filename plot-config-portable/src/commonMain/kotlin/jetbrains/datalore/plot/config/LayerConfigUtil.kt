/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.DataFrame.Variable
import jetbrains.datalore.plot.base.GeomKind
import jetbrains.datalore.plot.base.Stat
import jetbrains.datalore.plot.base.data.DataFrameUtil
import jetbrains.datalore.plot.base.stat.Stats
import jetbrains.datalore.plot.base.util.YOrientationBaseUtil
import jetbrains.datalore.plot.builder.VarBinding
import jetbrains.datalore.plot.config.Option.Layer.POS
import jetbrains.datalore.plot.config.aes.AesOptionConversion

internal object LayerConfigUtil {

    fun positionAdjustmentOptions(layerOptions: OptionsAccessor, geomProto: GeomProto): Map<String, Any> {
        val preferredPosOptions: Map<String, Any> = geomProto.preferredPositionAdjustmentOptions(layerOptions)
        val hasOwnPositionOptions = geomProto.hasOwnPositionAdjustmentOptions(layerOptions)
        val specifiedPosOptions: Map<String, Any> = when (val v = layerOptions[POS]) {
            null -> preferredPosOptions
            is Map<*, *> ->
                @Suppress("UNCHECKED_CAST")
                v as Map<String, Any>

            else ->
                mapOf(Option.Meta.NAME to v.toString())
        }

        // Geom's parameters have priority over function parameters
        return when {
            specifiedPosOptions[Option.Meta.NAME] == preferredPosOptions[Option.Meta.NAME] -> {
                // Merge
                if (hasOwnPositionOptions) {
                    specifiedPosOptions + preferredPosOptions
                } else {
                    preferredPosOptions + specifiedPosOptions
                }
            }
            hasOwnPositionOptions -> preferredPosOptions
            else -> specifiedPosOptions
        }
    }

    fun initConstants(layerOptions: OptionsAccessor, consumedAesSet: Set<Aes<*>>): Map<Aes<*>, Any> {
        val result = HashMap<Aes<*>, Any>()
        Option.Mapping.REAL_AES_OPTION_NAMES
            .filter(layerOptions::has)
            .associateWith(Option.Mapping::toAes)
            .filterValues { aes -> aes in consumedAesSet }
            .forEach { (option, aes) ->
                val optionValue = layerOptions.getSafe(option)
                val constantValue = AesOptionConversion.apply(aes, optionValue)
                    ?: throw IllegalArgumentException("Can't convert to '$option' value: $optionValue")
                result[aes] = constantValue
            }
        return result
    }

    fun createBindings(
        data: DataFrame,
        mapping: Map<Aes<*>, Variable>?,
        consumedAesSet: Set<Aes<*>>,
        clientSide: Boolean
    ): List<VarBinding> {

        val result = ArrayList<VarBinding>()
        if (mapping != null) {
            val aesSet = HashSet(consumedAesSet)
            aesSet.retainAll(mapping.keys)
            for (aes in aesSet) {
                val variable = mapping.getValue(aes)
                val binding: VarBinding = when {
                    data.has(variable) -> VarBinding(variable, aes)
                    variable.isStat && !clientSide -> VarBinding(variable, aes) // 'stat' is not yet built.
                    else -> throw IllegalArgumentException(
                        data.undefinedVariableErrorMessage(variable.name)
                    )
                }
                result.add(binding)
            }
        }
        return result
    }

    fun aesMappingAndCombinedData(
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
    ): Pair<Map<Aes<*>, Variable>, DataFrame> {

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
        }.run {
            // Mark 'DateTime' variables
            val dateTimeVariables = DataMetaUtil.getDateTimeColumns(plotDataMeta) +
                    DataMetaUtil.getDateTimeColumns(ownDataMeta)
            DataFrameUtil.addDateTimeVariables(this, dateTimeVariables)
        }

        var aesMappings: Map<Aes<*>, Variable>
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
