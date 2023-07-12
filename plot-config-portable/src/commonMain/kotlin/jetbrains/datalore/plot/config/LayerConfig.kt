/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import org.jetbrains.letsPlot.commons.values.Color
import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.data.DataFrameUtil
import jetbrains.datalore.plot.base.data.DataFrameUtil.variables
import jetbrains.datalore.plot.base.util.afterOrientation
import jetbrains.datalore.plot.builder.MarginSide
import jetbrains.datalore.plot.builder.VarBinding
import jetbrains.datalore.plot.builder.annotation.AnnotationSpecification
import jetbrains.datalore.plot.builder.assemble.PosProvider
import jetbrains.datalore.plot.builder.data.OrderOptionUtil.OrderOption
import jetbrains.datalore.plot.builder.data.OrderOptionUtil.OrderOption.Companion.mergeWith
import jetbrains.datalore.plot.builder.sampling.Sampling
import jetbrains.datalore.plot.builder.tooltip.TooltipSpecification
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil
import jetbrains.datalore.plot.config.DataConfigUtil.combinedDiscreteMapping
import jetbrains.datalore.plot.config.DataConfigUtil.layerMappingsAndCombinedData
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
import jetbrains.datalore.plot.config.Option.Meta.DATA_META
import jetbrains.datalore.plot.config.Option.PlotBase.DATA
import jetbrains.datalore.plot.config.Option.PlotBase.MAPPING

class LayerConfig constructor(
    layerOptions: Map<String, Any>,
    plotData: DataFrame,
    plotMappings: Map<String, String>,
    plotDataMeta: Map<*, *>,
    plotOrderOptions: List<OrderOption>,
    val geomProto: GeomProto,
    private val clientSide: Boolean,
    isMapPlot: Boolean
) : OptionsAccessor(
    layerOptions,
    initLayerDefaultOptions(layerOptions, geomProto)
) {

    val statKind: StatKind = StatKind.safeValueOf(getStringSafe(STAT))
    val stat: Stat = StatProto.createStat(statKind, options = this)

    val posProvider: PosProvider =
        PosProto.createPosProvider(
            LayerConfigUtil.positionAdjustmentOptions(layerOptions = this, geomProto)
        )

    val isLiveMap: Boolean = geomProto.geomKind == GeomKind.LIVE_MAP

    private val explicitConstantAes = Option.Mapping.REAL_AES_OPTION_NAMES
        .filter(::hasOwn)
        .map(Option.Mapping::toAes)

    // Color aesthetics
    val colorByAes: Aes<Color> = getPaintAes(Aes.COLOR, explicitConstantAes)
    val fillByAes: Aes<Color> = getPaintAes(Aes.FILL, explicitConstantAes)
    val renderedAes: List<Aes<*>> = GeomMeta.renders(geomProto.geomKind, colorByAes, fillByAes)
    val isLegendDisabled: Boolean
        get() = when (hasOwn(SHOW_LEGEND)) {
            true -> !getBoolean(SHOW_LEGEND, true)
            else -> false
        }

    private val _samplings: List<Sampling> = when (clientSide) {
        true -> emptyList()
        else -> initSampling(this, geomProto.preferredSampling())
    }

    val samplings: List<Sampling>
        get() {
            check(!clientSide)
            return _samplings
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


    val aggregateOperation: ((List<Double?>) -> Double?) = when (getString(POS)) {
        PosProto.STACK -> SeriesUtil::sum
        else -> { v: List<Double?> -> SeriesUtil.mean(v, defaultValue = null) }
    }

    var orderOptions: List<OrderOption>
        private set

    val explicitGroupingVarName: String?

    val varBindings: List<VarBinding>
    val constantsMap: Map<Aes<*>, Any>

    val tooltips: TooltipSpecification
    val annotations: AnnotationSpecification

    var ownData: DataFrame
        private set

    private var combinedDataValid = true
    var combinedData: DataFrame
        private set
        get() {
            check(combinedDataValid)
            return field
        }

    init {
        ownData = ConfigUtil.createDataFrame(get(DATA))

        val layerMappings = getMap(MAPPING).mapValues { (_, variable) -> variable as String }

        val combinedDiscreteMappings = combinedDiscreteMapping(
            commonMappings = plotMappings,
            ownMappings = layerMappings,
            commonDiscreteAes = DataMetaUtil.getAsDiscreteAesSet(plotDataMeta),
            ownDiscreteAes = DataMetaUtil.getAsDiscreteAesSet(getMap(DATA_META))
        )

        val consumedAesSet: Set<Aes<*>> = renderedAes.toSet().let {
            when (clientSide) {
                true -> it
                false -> it + stat.consumes()
            }
        }.afterOrientation(isYOrientation)

        // Combine plot + layer mappings.
        // Only keep those mappings which can be consumed by this layer.
        val consumedAesMappings = (plotMappings + layerMappings).filterKeys { aesName ->
            when (aesName) {
                Option.Mapping.GROUP -> true
                else -> {
                    val aes = Option.Mapping.toAes(aesName)
                    consumedAesSet.contains(aes)
                }
            }
        }

        val (aesMappings: Map<Aes<*>, DataFrame.Variable>,
            rawCombinedData: DataFrame) = layerMappingsAndCombinedData(
            layerOptions = layerOptions,
            geomKind = geomProto.geomKind,
            stat = stat,
            sharedData = plotData,
            layerData = ownData,
            combinedDiscreteMappings = combinedDiscreteMappings,
            consumedAesMappings = consumedAesMappings,
            explicitConstantAes = explicitConstantAes,
            isYOrientation = isYOrientation,
            clientSide = clientSide,
            isMapPlot = isMapPlot
        )

        // init AES constants excluding mapped AES
        constantsMap = LayerConfigUtil.initConstants(
            layerOptions = this,
            consumedAesSet = consumedAesSet - aesMappings.keys
        )

        // grouping
        explicitGroupingVarName = initGroupingVarName(rawCombinedData, consumedAesMappings)

        varBindings = LayerConfigUtil.createBindings(
            rawCombinedData,
            aesMappings,
            consumedAesSet,
            clientSide
        )

        // tooltip list
        tooltips = if (has(TOOLTIPS)) {
            initTooltipsSpec(
                tooltipOptions = getSafe(TOOLTIPS),
                varBindings = varBindings.filter { it.aes in renderedAes }, // use rendered only (without stat.consumes())
                constantsMap, explicitGroupingVarName
            )
        } else {
            TooltipSpecification.defaultTooltip()
        }

        annotations = if (has(ANNOTATIONS)) {
            AnnotationConfig(
                opts = getMap(ANNOTATIONS),
                varBindings = varBindings.filter { it.aes in renderedAes }, // use rendered only (without stat.consumes())
                constantsMap, explicitGroupingVarName
            ).createAnnotations()
        } else {
            AnnotationSpecification.NONE
        }

        orderOptions = initOrderOptions(plotOrderOptions, layerOptions, varBindings, consumedAesMappings, clientSide)

        // Apply data meta
        combinedData = DataConfigUtil.combinedDataWithDataMeta(
            rawCombinedData = rawCombinedData,
            varBindings = varBindings,
            plotDataMeta = plotDataMeta,
            ownDataMeta = getMap(DATA_META),
            asDiscreteAesSet = combinedDiscreteMappings.keys,
            orderOptions = orderOptions,
            aggregateOperation = aggregateOperation,
            clientSide = clientSide
        )
    }

    private fun initGroupingVarName(data: DataFrame, mappingOptions: Map<*, *>): String? {
        val groupBy = mappingOptions[Option.Mapping.GROUP]
        var fieldName: String? = when (groupBy) {
            is String -> groupBy
            else -> null
        }

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

        // Invalidate layer' "combined data"
        combinedDataValid = false
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

    // Decided that color/fill_by only affects mappings, constants always use original aes color/fill.
    // And the constant cancels mappings => the constant cancels color/fill_by.
    private fun getPaintAes(aes: Aes<Color>, explicitConstantAes: List<Aes<*>>): Aes<Color> {

        return when (aes) {
            in explicitConstantAes -> aes
            else -> {
                val optionName = when (aes) {
                    Aes.COLOR -> Option.Layer.COLOR_BY
                    Aes.FILL -> Option.Layer.FILL_BY
                    else -> aes.name
                }

                val colorBy: Aes<Color>? = getString(optionName)?.let { aesName ->
                    val aesByName = Option.Mapping.toAes(aesName)
                    require(Aes.isColor(aesByName)) { "'$optionName' should be an aesthetic related to color" }
                    @Suppress("UNCHECKED_CAST")
                    aesByName as Aes<Color>
                }

                when (colorBy) {
                    null -> aes
                    in explicitConstantAes -> aes
                    else -> colorBy
                }
            }
        }
    }


    private companion object {

        private fun initLayerDefaultOptions(
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

        private fun initSampling(opts: OptionsAccessor, defaultSampling: Sampling): List<Sampling> {
            return if (opts.has(Option.Layer.SAMPLING)) {
                SamplingConfig.create(opts.getSafe(Option.Layer.SAMPLING))
            } else {
                listOf(defaultSampling)
            }
        }

        private fun initTooltipsSpec(
            tooltipOptions: Any,  // An options map or just string "none"
            varBindings: List<VarBinding>,
            constantsMap: Map<Aes<*>, Any>,
            explicitGroupingVarName: String?
        ): TooltipSpecification {
            return when (tooltipOptions) {
                is Map<*, *> -> {
                    @Suppress("UNCHECKED_CAST")
                    TooltipConfig(
                        opts = tooltipOptions as Map<String, Any>,
                        constantsMap = constantsMap,
                        groupingVarName = explicitGroupingVarName,
                        varBindings = varBindings
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
        }

        private fun initOrderOptions(
            plotOrderOptions: List<OrderOption>,
            layerOptions: Map<String, Any>,
            varBindings: List<VarBinding>,
            combinedMappingOptions: Map<String, String>,
            clientSide: Boolean
        ): List<OrderOption> {
            val mappedVariables = varBindings.map { it.variable.name }

            @Suppress("NAME_SHADOWING")
            val plotOrderOptions = plotOrderOptions.filter { orderOption ->
                orderOption.variableName in mappedVariables
            }

            val ownOrderOptions = DataMetaUtil.getOrderOptions(
                layerOptions,
                combinedMappingOptions,
                clientSide
            )
            val orderOptions = plotOrderOptions + ownOrderOptions

            return orderOptions.let {
                if (clientSide) {
                    it.groupingBy(OrderOption::variableName)
                        .reduce { _, combined, element -> combined.mergeWith(element) }
                        .values.toList()
                } else {
                    // On server side order options are used just to keep variables after
                    it
                }
            }
        }
    }
}
