/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.base.gcommon.base.Preconditions.checkArgument
import jetbrains.datalore.base.gcommon.base.Preconditions.checkState
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.Stat
import jetbrains.datalore.plot.base.data.DataFrameUtil
import jetbrains.datalore.plot.base.data.DataFrameUtil.variables
import jetbrains.datalore.plot.base.stat.Stats
import jetbrains.datalore.plot.builder.VarBinding
import jetbrains.datalore.plot.builder.assemble.PosProvider
import jetbrains.datalore.plot.builder.sampling.Sampling
import jetbrains.datalore.plot.builder.tooltip.TooltipSpecification
import jetbrains.datalore.plot.config.ConfigUtil.createAesMapping
import jetbrains.datalore.plot.config.DataMetaUtil.createDataFrame
import jetbrains.datalore.plot.config.Option.Geom.Choropleth.GEO_POSITIONS
import jetbrains.datalore.plot.config.Option.Layer.GEOM
import jetbrains.datalore.plot.config.Option.Layer.MAP_JOIN
import jetbrains.datalore.plot.config.Option.Layer.NONE
import jetbrains.datalore.plot.config.Option.Layer.SHOW_LEGEND
import jetbrains.datalore.plot.config.Option.Layer.STAT
import jetbrains.datalore.plot.config.Option.Layer.TOOLTIPS
import jetbrains.datalore.plot.config.Option.PlotBase.DATA
import jetbrains.datalore.plot.config.Option.PlotBase.MAPPING

class LayerConfig(
    layerOptions: Map<String, Any>,
    sharedData: DataFrame,
    plotMappings: Map<*, *>,
    plotDiscreteAes: Set<*>,
    val geomProto: GeomProto,
    private val clientSide: Boolean
) : OptionsAccessor(
    layerOptions,
    initDefaultOptions(layerOptions, geomProto)
) {

    val stat: Stat
    val statKind: StatKind = StatKind.safeValueOf(getStringSafe(STAT))

    val explicitGroupingVarName: String?
    val posProvider: PosProvider
    private val myCombinedData: DataFrame

    val varBindings: List<VarBinding>
    val constantsMap: Map<Aes<*>, Any>

    private val mySamplings: List<Sampling>?
    val tooltips: TooltipSpecification

    var ownData: DataFrame? = null
        private set
    private var myOwnDataUpdated = false

    val combinedData: DataFrame
        get() {
            checkState(!myOwnDataUpdated)
            return myCombinedData
        }

    val isLegendDisabled: Boolean
        get() = if (hasOwn(SHOW_LEGEND)) {
            !getBoolean(SHOW_LEGEND, true)
        } else false

    val samplings: List<Sampling>?
        get() {
            checkState(!clientSide)
            return mySamplings
        }

    init {
        val (layerMappings, layerData) = createDataFrame(
            options = this,
            commonData = sharedData,
            commonDiscreteAes = plotDiscreteAes,
            commonMappings = plotMappings,
            isClientSide = clientSide
        )

        if (!clientSide) {
            update(MAPPING, layerMappings)
        }

        // mapping (inherit from plot) + 'layer' mapping
        val combinedMappings = plotMappings + layerMappings

        var combinedData =
            if (!(sharedData.isEmpty || layerData.isEmpty) && sharedData.rowCount() == layerData.rowCount()) {
                DataFrameUtil.appendReplace(sharedData, layerData)
            } else if (!layerData.isEmpty) {
                layerData
            } else {
                sharedData
            }

        var aesMappings: Map<Aes<*>, DataFrame.Variable>
        if (clientSide && GeoConfig.isApplicable(layerOptions, combinedMappings)) {
            val geoConfig = GeoConfig(
                geomProto.geomKind,
                combinedData,
                layerOptions,
                combinedMappings
            )
            combinedData = geoConfig.dataAndCoordinates
            aesMappings = geoConfig.mappings

        } else {
            aesMappings = createAesMapping(combinedData, combinedMappings)
        }

        stat = StatProto.createStat(statKind, OptionsAccessor(mergedOptions))
        if (clientSide) {
            // add stat default mappings
            val statDefMapping = Stats.defaultMapping(stat)
            // Only keys (aes) in 'statDefMapping' that are not already present in 'aesMappinds'.
            aesMappings = statDefMapping + aesMappings
        }

        // drop from aes mapping constant that were defined explicitly.
        val explicitConstantAes = Option.Mapping.REAL_AES_OPTION_NAMES
            .filter { hasOwn(it) }
            .map { Option.Mapping.toAes(it) }
        aesMappings = aesMappings - explicitConstantAes

        // init AES constants excluding mapped AES
        constantsMap = LayerConfigUtil.initConstants(this, aesMappings.keys)

        // grouping
        explicitGroupingVarName = initGroupingVarName(combinedData, combinedMappings)

        posProvider = LayerConfigUtil.initPositionAdjustments(
            this,
            geomProto.preferredPositionAdjustments(this)
        )

        val consumedAesSet = HashSet(geomProto.renders())
        if (!clientSide) {
            consumedAesSet.addAll(stat.consumes())
        }

        // tooltip list
        tooltips = if (has(TOOLTIPS)) {
            when (get(TOOLTIPS)) {
                is Map<*, *> -> {
                    TooltipConfig(getMap(TOOLTIPS), constantsMap, explicitGroupingVarName).createTooltips()
                }
                NONE -> {
                    // not show tooltips
                    TooltipSpecification.withoutTooltip()
                }
                else -> {
                    error("Incorrect tooltips specification")
                }
            }
        } else {
            TooltipSpecification.defaultTooltip()
        }

        varBindings = LayerConfigUtil.createBindings(
            combinedData,
            aesMappings,
            consumedAesSet,
            clientSide
        )
        ownData = layerData
        myCombinedData = combinedData

        mySamplings = if (clientSide) {
            null
        } else {
            LayerConfigUtil.initSampling(this, geomProto.preferredSampling())
        }
    }

    private fun initGroupingVarName(data: DataFrame, mappingOptions: Map<*, *>): String? {
        val groupBy = mappingOptions[Option.Mapping.GROUP]
        var fieldName: String? = if (groupBy is String)
            groupBy
        else
            null

        if (fieldName == null && has(GEO_POSITIONS)) {
            // 'default' group is important for 'geom_map'
            val groupVar = variables(data)["group"]
            if (groupVar != null) {
                fieldName = groupVar.name
            }
        }
        return fieldName
    }

    fun hasVarBinding(varName: String): Boolean {
        for (binding in varBindings) {
            if (binding.variable.name == varName) {
                return true
            }
        }
        return false
    }

    fun replaceOwnData(dataFrame: DataFrame?) {
        checkState(!clientSide)   // This class is immutable on client-side
        require(dataFrame != null)
        update(DATA, DataFrameUtil.toMap(dataFrame))
        ownData = dataFrame
        myOwnDataUpdated = true
    }

    fun hasExplicitGrouping(): Boolean {
        return explicitGroupingVarName != null
    }

    fun isExplicitGrouping(varName: String): Boolean {
        return explicitGroupingVarName != null && explicitGroupingVarName == varName
    }

    fun getVariableForAes(aes: Aes<*>): DataFrame.Variable? {
        return varBindings.find { it.aes == aes }?.variable
    }

    fun getMapJoin(): Pair<String, String>? {
        if (!hasOwn(MAP_JOIN)) {
            return null
        }

        val mapJoin = getList(MAP_JOIN)
        require(mapJoin.size == 2) { "map_join require 2 parameters" }

        val (dataVar, mapVar) = mapJoin
        require(dataVar is String && mapVar is String) { "map_join parameters type should be a String" }

        return Pair(dataVar, mapVar)
    }

    private companion object {
        private fun initDefaultOptions(
            layerOptions: Map<*, *>,
            geomProto: GeomProto
        ): Map<String, Any> {
            checkArgument(
                layerOptions.containsKey(GEOM) || layerOptions.containsKey(STAT),
                "Either 'geom' or 'stat' must be specified."
            )

            val defaults = HashMap<String, Any>()
            defaults.putAll(geomProto.defaultOptions())

            var statName: String? = layerOptions[STAT] as String?
            if (statName == null) {
                statName = defaults[STAT] as String
            }

            return defaults + StatProto.defaultOptions(statName, geomProto.geomKind)
        }
    }
}
