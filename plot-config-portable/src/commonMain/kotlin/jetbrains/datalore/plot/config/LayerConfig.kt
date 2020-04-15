/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.base.gcommon.base.Preconditions.checkArgument
import jetbrains.datalore.base.gcommon.base.Preconditions.checkState
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.base.Stat
import jetbrains.datalore.plot.base.data.DataFrameUtil
import jetbrains.datalore.plot.base.data.DataFrameUtil.variables
import jetbrains.datalore.plot.builder.VarBinding
import jetbrains.datalore.plot.builder.assemble.PosProvider
import jetbrains.datalore.plot.builder.assemble.TypedScaleProviderMap
import jetbrains.datalore.plot.builder.assemble.geom.DefaultAesAutoMapper
import jetbrains.datalore.plot.builder.sampling.Sampling
import jetbrains.datalore.plot.config.DataMetaUtil.createDataFrame
import jetbrains.datalore.plot.config.Option.Layer.GEOM
import jetbrains.datalore.plot.config.Option.Layer.SHOW_LEGEND
import jetbrains.datalore.plot.config.Option.Layer.STAT
import jetbrains.datalore.plot.config.Option.Layer.TOOLTIP
import jetbrains.datalore.plot.config.Option.PlotBase.DATA
import jetbrains.datalore.plot.config.Option.PlotBase.MAPPING

class LayerConfig(
    layerOptions: Map<*, *>,
    sharedData: DataFrame,
    plotMapping: Map<*, *>,
    plotDiscreteAes: Set<String>,
    val geomProto: GeomProto,
    statProto: StatProto,
    scaleProviderByAes: TypedScaleProviderMap,
    private val myClientSide: Boolean
) : OptionsAccessor(layerOptions, initDefaultOptions(layerOptions, geomProto, statProto)) {

    //    val geomProvider: GeomProvider
    val stat: Stat
    val explicitGroupingVarName: String?
    val posProvider: PosProvider
    private val myCombinedData: DataFrame
    val varBindings: List<VarBinding>
    val constantsMap: Map<Aes<*>, Any>
    val statKind: StatKind
    private val mySamplings: List<Sampling>?
    val tooltipAes: List<Aes<*>>?

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
            checkState(!myClientSide)
            return mySamplings
        }

    init {
        val (layerMappings, layerData) = createDataFrame(
            options = this,
            commonData = sharedData,
            commonDiscreteAes = plotDiscreteAes,
            commonMapping = plotMapping,
            isClientSide = myClientSide
        )

        if (!myClientSide) {
            update(MAPPING, layerMappings)
        }

        // mapping (inherit from plot) + 'layer' mapping
        val mappingOptions = plotMapping + getMap(MAPPING)

        var combinedData: DataFrame
        if (!(sharedData.isEmpty || layerData.isEmpty) && sharedData.rowCount() == layerData.rowCount()) {
            combinedData = DataFrameUtil.appendReplace(sharedData, layerData)
        } else if (!layerData.isEmpty) {
            combinedData = layerData
        } else {
            combinedData = sharedData
        }


        var aesMapping: Map<Aes<*>, DataFrame.Variable>?
        if (GeoPositionsDataUtil.hasGeoPositionsData(this) && myClientSide) {
            // join dataset and geo-positions data
            val dataAndMapping = GeoPositionsDataUtil.initDataAndMappingForGeoPositions(
                geomProto.geomKind,
                combinedData,
                GeoPositionsDataUtil.getGeoPositionsData(this),
                mappingOptions
            )
            combinedData = dataAndMapping.first
            aesMapping = dataAndMapping.second
        } else {
            aesMapping = ConfigUtil.createAesMapping(combinedData, mappingOptions)
        }

        // auto-map variables if necessary
        if (aesMapping.isEmpty()) {
            aesMapping = DefaultAesAutoMapper.forGeom(geomProto.geomKind).createMapping(combinedData)
            if (!myClientSide) {
                // store used mapping options to pass to client.
                val autoMappingOptions = HashMap<String, Any>()
                for (aes in aesMapping.keys) {
                    val option = Option.Mapping.toOption(aes)
                    val variable = aesMapping[aes]!!.name
                    autoMappingOptions[option] = variable
                }
                update(MAPPING, autoMappingOptions)
            }
        }

        // exclude constant aes from mapping
        val constants = LayerConfigUtil.initConstants(this)
        if (constants.isNotEmpty()) {
            aesMapping = HashMap(aesMapping)
            for (aes in constants.keys) {
                aesMapping.remove(aes)
            }
        }

        // grouping
        explicitGroupingVarName = initGroupingVarName(combinedData, mappingOptions)

        statKind = StatKind.safeValueOf(getString(STAT)!!)
        stat = statProto.createStat(statKind, mergedOptions)
        posProvider = LayerConfigUtil.initPositionAdjustments(
            this,
            geomProto.preferredPositionAdjustments(this)
        )
        constantsMap = constants

        val consumedAesSet = HashSet(geomProto.renders())
        if (!myClientSide) {
            consumedAesSet.addAll(stat.consumes())
        }

        // tooltip aes list
        this.tooltipAes = getTooltipAesList(aesMapping)

        val varBindings = LayerConfigUtil.createBindings(
            combinedData,
            aesMapping,
            scaleProviderByAes,
            consumedAesSet
        )

        this.varBindings = varBindings
        ownData = layerData
        myCombinedData = combinedData

        mySamplings = if (myClientSide)
            null
        else
            LayerConfigUtil.initSampling(this, geomProto.preferredSampling())
    }

    private fun initGroupingVarName(data: DataFrame, mappingOptions: Map<*, *>): String? {
        val groupBy = mappingOptions[Option.Mapping.GROUP]
        var fieldName: String? = if (groupBy is String)
            groupBy
        else
            null

        if (fieldName == null && GeoPositionsDataUtil.hasGeoPositionsData(this)) {
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
        checkState(!myClientSide)   // This class is immutable on client-side
        checkArgument(dataFrame != null)
        update(DATA, DataFrameUtil.toMap(dataFrame!!))
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

    fun getScaleForAes(aes: Aes<*>): Scale<*>? {
        return varBindings.find { it.aes == aes }?.scale
    }

    private fun getTooltipAesList(aesMapping: Map<Aes<*>, DataFrame.Variable>): List<Aes<*>>? {
        // tooltip list is not defined - will be used default tooltips
        if (!has(TOOLTIP))
            return null

        val aesStringList = getStringList(TOOLTIP)

        // check if all elements of list are aes
        (aesStringList - Aes.values().map { it.name }).firstOrNull {
            error("${it} is not aes name ")
        }

        // detach aes
        val aesList = Aes.values().filter { aesStringList.contains(it.name) }

        // check if aes list matches to mapping
        if (!aesMapping.keys.containsAll(aesList))
            error("Aes list does not match to mapping")

        return aesList
    }

    private companion object {
        private fun initDefaultOptions(layerOptions: Map<*, *>, geomProto: GeomProto, statProto: StatProto): Map<*, *> {
            checkArgument(
                layerOptions.containsKey(GEOM) || layerOptions.containsKey(STAT),
                "Either 'geom' or 'stat' must be specified"
            )

            val defaults = HashMap<String, Any>()
            defaults.putAll(geomProto.defaultOptions())

            var statName: String? = layerOptions[STAT] as String?
            if (statName == null) {
                statName = defaults[STAT] as String
            }
            defaults.putAll(statProto.defaultOptions(statName))

            return defaults
        }
    }
}
