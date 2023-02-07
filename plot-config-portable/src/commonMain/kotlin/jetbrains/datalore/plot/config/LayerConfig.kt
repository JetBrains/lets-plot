/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.GeomKind
import jetbrains.datalore.plot.base.Stat
import jetbrains.datalore.plot.base.data.DataFrameUtil
import jetbrains.datalore.plot.base.data.DataFrameUtil.variables
import jetbrains.datalore.plot.base.stat.Stats
import jetbrains.datalore.plot.base.util.YOrientationBaseUtil
import jetbrains.datalore.plot.base.util.afterOrientation
import jetbrains.datalore.plot.builder.MarginSide
import jetbrains.datalore.plot.builder.VarBinding
import jetbrains.datalore.plot.builder.annotation.AnnotationSpecification
import jetbrains.datalore.plot.builder.assemble.PosProvider
import jetbrains.datalore.plot.builder.data.OrderOptionUtil.OrderOption
import jetbrains.datalore.plot.builder.data.OrderOptionUtil.OrderOption.Companion.mergeWith
import jetbrains.datalore.plot.builder.data.OrderOptionUtil.createOrderSpec
import jetbrains.datalore.plot.builder.sampling.Sampling
import jetbrains.datalore.plot.builder.tooltip.TooltipSpecification
import jetbrains.datalore.plot.common.data.SeriesUtil
import jetbrains.datalore.plot.config.ConfigUtil.createAesMapping
import jetbrains.datalore.plot.config.DataMetaUtil.createDataFrame
import jetbrains.datalore.plot.config.DataMetaUtil.inheritToNonDiscrete
import jetbrains.datalore.plot.config.Option.Geom.Choropleth.GEO_POSITIONS
import jetbrains.datalore.plot.config.Option.Layer.ANNOTATIONS
import jetbrains.datalore.plot.config.Option.Layer.GEOM
import jetbrains.datalore.plot.config.Option.Layer.MAP_JOIN
import jetbrains.datalore.plot.config.Option.Layer.MARGINAL
import jetbrains.datalore.plot.config.Option.Layer.Marginal
import jetbrains.datalore.plot.config.Option.Layer.NONE
import jetbrains.datalore.plot.config.Option.Layer.ORIENTATION
import jetbrains.datalore.plot.config.Option.Layer.POS
import jetbrains.datalore.plot.config.Option.Layer.SHOW_LEGEND
import jetbrains.datalore.plot.config.Option.Layer.STAT
import jetbrains.datalore.plot.config.Option.Layer.TOOLTIPS
import jetbrains.datalore.plot.config.Option.PlotBase.DATA
import jetbrains.datalore.plot.config.Option.PlotBase.MAPPING

class LayerConfig constructor(
    layerOptions: Map<String, Any>,
    sharedData: DataFrame,
    plotMappings: Map<*, *>,
    plotDataMeta: Map<*, *>,
    plotOrderOptions: List<OrderOption>,
    val geomProto: GeomProto,
    private val clientSide: Boolean,
    isMapPlot: Boolean
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
    private val myOrderOptions: List<OrderOption>
    val tooltips: TooltipSpecification
    val annotations: AnnotationSpecification

    var ownData: DataFrame? = null
        private set
    private var myOwnDataUpdated = false

    val combinedData: DataFrame
        get() {
            // 'combinedData' is only valid before 'stst'/'sampling' occurs.
            check(!myOwnDataUpdated)
            return myCombinedData
        }

    val isLegendDisabled: Boolean
        get() = if (hasOwn(SHOW_LEGEND)) {
            !getBoolean(SHOW_LEGEND, true)
        } else false

    val samplings: List<Sampling>
        get() {
            check(!clientSide)
            return mySamplings!!
        }

    val isLiveMap: Boolean
        get() = geomProto.geomKind == GeomKind.LIVE_MAP

    val orderOptions: List<OrderOption>
        get() = myOrderOptions

    val aggregateOperation: ((List<Double?>) -> Double?) = when (getString(POS)) {
        PosProto.STACK -> SeriesUtil::sum
        else -> { v: List<Double?> -> SeriesUtil.mean(v, defaultValue = null) }
    }

    val isYOrientation: Boolean
        get() = when (hasOwn(ORIENTATION)) {
            true -> getString(ORIENTATION)?.lowercase()?.let {
                when (it) {
                    "y" -> true
                    "x" -> false
                    else -> throw IllegalArgumentException("$ORIENTATION expected x|y but was $it")
                }
            } ?: false
            false -> false
        }

    // Marginal layers
    val isMarginal: Boolean = getBoolean(MARGINAL, false)
    val marginalSide: MarginSide = if (isMarginal) {
        when (val side = getStringSafe(Marginal.SIDE).lowercase()) {
            Marginal.SIDE_LEFT -> MarginSide.LEFT
            Marginal.SIDE_RIGHT -> MarginSide.RIGHT
            Marginal.SIDE_TOP -> MarginSide.TOP
            Marginal.SIDE_BOTTOM -> MarginSide.BOTTOM
            else -> throw IllegalArgumentException("${Marginal.SIDE} expected l|r|t|b but was '$side'")
        }
    } else {
        MarginSide.LEFT
    }
    val marginalSize: Double = getDoubleDef(Marginal.SIZE, Marginal.SIZE_DEFAULT)


    init {
        val (layerMappings, layerData) = createDataFrame(
            options = this,
            commonData = sharedData,
            commonDiscreteAes = DataMetaUtil.getAsDiscreteAesSet(plotDataMeta),
            commonMappings = plotMappings,
            isClientSide = clientSide
        )

        if (!clientSide) {
            update(MAPPING, layerMappings)
        }

        stat = StatProto.createStat(statKind, OptionsAccessor(mergedOptions))
        val consumedAesSet: Set<Aes<*>> = geomProto.renders().toSet().let {
            when (clientSide) {
                true -> it
                false -> it + stat.consumes()
            }
        }.afterOrientation(isYOrientation)

        // mapping (inherit from plot) + 'layer' mapping
        val combinedMappingOptions = (plotMappings + layerMappings).filterKeys {
            // Only keep those mapping options which can be consumed by this layer.
            // ToDo: report to user that some mappings are not applicable to this layer.
            @Suppress("CascadeIf")
            if (it == Option.Mapping.GROUP) {
                true
            } else if (it is String) {
                val aes = Option.Mapping.toAes(it)
                when (statKind) {
                    StatKind.QQ,
                    StatKind.QQ_LINE -> consumedAesSet.contains(aes) || aes == Aes.SAMPLE
                    else -> consumedAesSet.contains(aes)
                }
            } else {
                false
            }
        }

        // If layer has no mapping then no data is needed.
        val dropData: Boolean = (combinedMappingOptions.isEmpty() &&
                // Do not touch GeoDataframe - empty mapping is OK in this case.
                !GeoConfig.isGeoDataframe(layerOptions, DATA) &&
                !GeoConfig.isApplicable(layerOptions, combinedMappingOptions, isMapPlot)
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
            val dateTimeVariables =
                DataMetaUtil.getDateTimeColumns(plotDataMeta) + DataMetaUtil.getDateTimeColumns(getMap(Option.Meta.DATA_META))
            DataFrameUtil.addDateTimeVariables(this, dateTimeVariables)
        }

        var aesMappings: Map<Aes<*>, DataFrame.Variable>
        if (clientSide && GeoConfig.isApplicable(layerOptions, combinedMappingOptions, isMapPlot)) {
            val geoConfig = GeoConfig(
                geomProto.geomKind,
                combinedData,
                layerOptions,
                combinedMappingOptions
            )
            combinedData = geoConfig.dataAndCoordinates
            aesMappings = geoConfig.mappings

        } else {
            aesMappings = createAesMapping(combinedData, combinedMappingOptions)
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
        val explicitConstantAes = Option.Mapping.REAL_AES_OPTION_NAMES
            .filter { hasOwn(it) }
            .map { Option.Mapping.toAes(it) }
        aesMappings = aesMappings - explicitConstantAes

        // init AES constants excluding mapped AES
        constantsMap = LayerConfigUtil.initConstants(this, consumedAesSet - aesMappings.keys)

        // grouping
        explicitGroupingVarName = initGroupingVarName(combinedData, combinedMappingOptions)

        posProvider = PosProto.createPosProvider(LayerConfigUtil.positionAdjustmentOptions(this, geomProto))

        varBindings = LayerConfigUtil.createBindings(
            combinedData,
            aesMappings,
            consumedAesSet,
            clientSide
        )
        ownData = layerData

        mySamplings = if (clientSide) {
            null
        } else {
            LayerConfigUtil.initSampling(this, geomProto.preferredSampling())
        }

        // tooltip list
        tooltips = if (has(TOOLTIPS)) {
            when (get(TOOLTIPS)) {
                is Map<*, *> -> {
                    TooltipConfig(
                        opts = getMap(TOOLTIPS),
                        constantsMap = constantsMap,
                        groupingVarName = explicitGroupingVarName,
                        varBindings = varBindings.filter { it.aes in geomProto.renders() } // use rendered only (without stat.consumes())
                    ).createTooltips()
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

        annotations = if (has(ANNOTATIONS)) {
            AnnotationConfig(
                opts = getMap(ANNOTATIONS),
                constantsMap = constantsMap,
                groupingVarName = explicitGroupingVarName,
                varBindings = varBindings.filter { it.aes in geomProto.renders() } // use rendered only (without stat.consumes())
            ).createAnnotations()
        } else {
            AnnotationSpecification.NONE
        }

        // TODO: handle order options combining to a config parsing stage
        myOrderOptions = (
                plotOrderOptions.filter { orderOption -> orderOption.variableName in varBindings.map { it.variable.name } }
                        + DataMetaUtil.getOrderOptions(layerOptions, combinedMappingOptions)
                )
            .inheritToNonDiscrete(combinedMappingOptions)
            .groupingBy(OrderOption::variableName)
            .reduce { _, combined, element -> combined.mergeWith(element) }
            .values.toList()

        myCombinedData = if (clientSide) {
            val orderSpecs = myOrderOptions.map {
                createOrderSpec(combinedData.variables(), varBindings, it, aggregateOperation)
            }
            DataFrame.Builder(combinedData).addOrderSpecs(orderSpecs).build()
        } else {
            combinedData
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
        check(!clientSide)   // This class is immutable on client-side
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

    fun getMapJoin(): Pair<List<*>, List<*>>? {
        if (!hasOwn(MAP_JOIN)) {
            return null
        }

        val mapJoin = getList(MAP_JOIN)
        require(mapJoin.size == 2) { "map_join require 2 parameters" }

        val (dataVar, mapVar) = mapJoin
        require(dataVar != null)
        require(mapVar != null)
        require(dataVar is List<*>) {
            "Wrong map_join parameter type: should be a list of strings, but was ${dataVar::class.simpleName}"
        }
        require(mapVar is List<*>) {
            "Wrong map_join parameter type: should be a list of string, but was ${mapVar::class.simpleName}"
        }

        return Pair(dataVar, mapVar)
    }

    private companion object {
        private fun initDefaultOptions(
            layerOptions: Map<*, *>,
            geomProto: GeomProto
        ): Map<String, Any> {
            require(layerOptions.containsKey(GEOM) || layerOptions.containsKey(STAT)) {
                "Either 'geom' or 'stat' must be specified."
            }

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
