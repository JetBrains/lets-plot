/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.config

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.commons.data.DataType
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.data.DataFrameUtil
import org.jetbrains.letsPlot.core.plot.base.data.DataFrameUtil.variables
import org.jetbrains.letsPlot.core.plot.base.util.afterOrientation
import org.jetbrains.letsPlot.core.plot.builder.MarginSide
import org.jetbrains.letsPlot.core.plot.builder.VarBinding
import org.jetbrains.letsPlot.core.plot.builder.annotation.AnnotationSpecification
import org.jetbrains.letsPlot.core.plot.builder.assemble.CustomLegendOptions
import org.jetbrains.letsPlot.core.plot.builder.assemble.PosProvider
import org.jetbrains.letsPlot.core.plot.builder.data.OrderOptionUtil.OrderOption
import org.jetbrains.letsPlot.core.plot.builder.data.OrderOptionUtil.OrderOption.Companion.mergeWith
import org.jetbrains.letsPlot.core.plot.builder.sampling.Sampling
import org.jetbrains.letsPlot.core.plot.builder.tooltip.TooltipSpecification
import org.jetbrains.letsPlot.core.spec.*
import org.jetbrains.letsPlot.core.spec.Option.Geom.Choropleth.GEO_POSITIONS
import org.jetbrains.letsPlot.core.spec.Option.Layer
import org.jetbrains.letsPlot.core.spec.Option.Layer.ANNOTATIONS
import org.jetbrains.letsPlot.core.spec.Option.Layer.DEFAULT_LEGEND_GROUP_NAME
import org.jetbrains.letsPlot.core.spec.Option.Layer.GEOM
import org.jetbrains.letsPlot.core.spec.Option.Layer.INHERIT_AES
import org.jetbrains.letsPlot.core.spec.Option.Layer.MANUAL_KEY
import org.jetbrains.letsPlot.core.spec.Option.Layer.MAP_JOIN
import org.jetbrains.letsPlot.core.spec.Option.Layer.MARGINAL
import org.jetbrains.letsPlot.core.spec.Option.Layer.Marginal
import org.jetbrains.letsPlot.core.spec.Option.Layer.NONE
import org.jetbrains.letsPlot.core.spec.Option.Layer.ORIENTATION
import org.jetbrains.letsPlot.core.spec.Option.Layer.POS
import org.jetbrains.letsPlot.core.spec.Option.Layer.SHOW_LEGEND
import org.jetbrains.letsPlot.core.spec.Option.Layer.STAT
import org.jetbrains.letsPlot.core.spec.Option.Layer.TOOLTIPS
import org.jetbrains.letsPlot.core.spec.Option.Mapping.toOption
import org.jetbrains.letsPlot.core.spec.Option.Meta.DATA_META
import org.jetbrains.letsPlot.core.spec.Option.PlotBase.DATA
import org.jetbrains.letsPlot.core.spec.Option.PlotBase.MAPPING
import org.jetbrains.letsPlot.core.spec.config.DataConfigUtil.combinedDiscreteMapping
import org.jetbrains.letsPlot.core.spec.config.DataConfigUtil.layerMappingsAndCombinedData
import org.jetbrains.letsPlot.core.spec.conversion.AesOptionConversion

class LayerConfig constructor(
    layerOptions: Map<String, Any>,
    plotData: DataFrame,
    plotMappings: Map<String, String>,
    plotDataMeta: Map<*, *>,
    plotOrderOptions: List<OrderOption>,
    val geomProto: GeomProto,
    val aopConversion: AesOptionConversion,
    private val clientSide: Boolean,
    isMapPlot: Boolean
) : OptionsAccessor(
    layerOptions,
    initLayerDefaultOptions(layerOptions, geomProto)
) {

    val dtypes: Map<String, DataType>// = DataMetaUtil.getDataTypes(plotDataMeta) + DataMetaUtil.getDataTypes(getMap(DATA_META))
    val statKind: StatKind = StatKind.safeValueOf(getStringSafe(STAT))
    val stat: Stat = StatProto.createStat(statKind, options = this)
    val labelFormat: String? = getString(Option.Geom.Text.LABEL_FORMAT)

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
    val customLegendOptions: CustomLegendOptions?
        get() {
            val option = get(MANUAL_KEY) ?: return null

            val legendOptions = when (option) {
                is Map<*, *> -> {
                    @Suppress("UNCHECKED_CAST")
                    option as Map<String, Any>
                }

                is String -> mapOf(Layer.LayerKey.LABEL to option)
                else -> throw IllegalArgumentException("$MANUAL_KEY expected a string or option map, but was '$option'")
            }.let(::OptionsAccessor)

            val label = legendOptions.getString(Layer.LayerKey.LABEL) ?: return null
            val aesValues = LayerConfigUtil.initConstants(
                legendOptions,
                consumedAesSet = Aes.values().toSet(),
                aopConversion
            )
            val groupName = legendOptions.getString(Layer.LayerKey.GROUP).let { name ->
                if (name == null || name == DEFAULT_LEGEND_GROUP_NAME) "" else name
            }
            return CustomLegendOptions(
                label = label,
                group = groupName,
                index = legendOptions.getInteger(Layer.LayerKey.INDEX),
                aesValues = aesValues
            )
        }

    private val _samplings: List<Sampling> = when (clientSide) {
        true -> emptyList()
        else -> initSampling(this, geomProto.geomKind, geomProto.preferredSampling())
    }

    val samplings: List<Sampling>
        get() {
            check(!clientSide)
            return _samplings
        }

    val isYOrientation: Boolean

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


    internal val aggregateOperation: ((List<Double?>) -> Double?) = when (getString(POS)) {
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

    internal var ownData: DataFrame
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

        @Suppress("NAME_SHADOWING")
        val plotMappings = when (getBoolean(INHERIT_AES, true)) {
            true -> plotMappings
            else -> emptyMap()
        }
        val layerMappings = getMap(MAPPING).mapValues { (_, variable) -> variable as String }
        val combinedMappings = plotMappings + layerMappings

        val combinedDiscreteMappings = combinedDiscreteMapping(
            commonMappings = plotMappings,
            ownMappings = layerMappings,
            commonDiscreteAes = DataMetaUtil.getAsDiscreteAesSet(plotDataMeta),
            ownDiscreteAes = DataMetaUtil.getAsDiscreteAesSet(getMap(DATA_META))
        )

        val hasHorizontalAes = setOf(Aes.XMIN, Aes.XMAX).any { aes -> toOption(aes) in combinedMappings || aes in explicitConstantAes }
        isYOrientation = when (hasOwn(ORIENTATION)) {
            true -> getString(ORIENTATION)?.lowercase()?.let {
                when (it) {
                    "y" -> true
                    "x" -> false
                    else -> throw IllegalArgumentException("$ORIENTATION expected x|y but was $it")
                }
            } ?: false

            false -> {
                when {
                    clientSide -> false
                    !isOrientationApplicable() -> false
                    DataConfigUtil.isAesDiscrete(
                        Aes.X,
                        plotData,
                        ownData,
                        plotMappings,
                        layerMappings,
                        combinedDiscreteMappings
                    ) -> false
                    !DataConfigUtil.isAesDiscrete(
                        Aes.Y,
                        plotData,
                        ownData,
                        plotMappings,
                        layerMappings,
                        combinedDiscreteMappings
                    ) && !hasHorizontalAes -> false
                    else -> {
                        setOrientationY()
                        true
                    }
                }
            }
        }

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

        val baseDTypes = DataMetaUtil.getDataTypes(plotDataMeta) + DataMetaUtil.getDataTypes(getMap(DATA_META))
        val discreteVarsDTypes = if (clientSide) {
            combinedDiscreteMappings
                .entries.associate { (aes, varName) ->
                DataMetaUtil.asDiscreteName(aes, varName) to baseDTypes.getOrElse(varName) { DataType.UNKNOWN }
            }
        } else {
            emptyMap()
        }

        dtypes = baseDTypes + discreteVarsDTypes


        // init AES constants excluding mapped AES
        constantsMap = LayerConfigUtil.initConstants(
            layerOptions = this,
            consumedAesSet = consumedAesSet - aesMappings.keys,
            aopConversion = aopConversion
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
        var fieldName: String? = when (val groupBy = mappingOptions[Option.Mapping.GROUP]) {
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

    private fun isOrientationApplicable(): Boolean {
        val isSuitableGeomKind = geomProto.geomKind in listOf(
            GeomKind.BAR,
            GeomKind.BOX_PLOT,
            GeomKind.VIOLIN,
            GeomKind.LOLLIPOP,
            GeomKind.Y_DOT_PLOT,
            GeomKind.CROSS_BAR,
            GeomKind.ERROR_BAR,
            GeomKind.LINE_RANGE,
            GeomKind.POINT_RANGE
        )
        val isSuitableStatKind = statKind in listOf(
            StatKind.COUNT,
            StatKind.SUMMARY,
            StatKind.BOXPLOT,
            StatKind.BOXPLOT_OUTLIER,
            StatKind.YDOTPLOT,
            StatKind.YDENSITY
        )

        return isSuitableGeomKind || isSuitableStatKind
    }

    private fun setOrientationY() {
        check(!clientSide)
        update(ORIENTATION, "y")
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

        private fun initSampling(opts: OptionsAccessor, geomKind: GeomKind, defaultSampling: Sampling): List<Sampling> {
            return if (opts.has(Layer.SAMPLING)) {
                SamplingConfig.create(opts.getSafe(Layer.SAMPLING), geomKind)
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
