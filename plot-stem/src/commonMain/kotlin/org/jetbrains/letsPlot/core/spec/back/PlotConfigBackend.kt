/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.back

import org.jetbrains.letsPlot.commons.formatting.string.StringFormat
import org.jetbrains.letsPlot.commons.intern.filterNotNullKeys
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.DataFrame.Variable
import org.jetbrains.letsPlot.core.plot.base.data.DataFrameUtil
import org.jetbrains.letsPlot.core.plot.base.scale.breaks.DateTimeBreaksHelper
import org.jetbrains.letsPlot.core.plot.base.stat.Stats
import org.jetbrains.letsPlot.core.plot.base.theme.Theme
import org.jetbrains.letsPlot.core.plot.builder.VarBinding
import org.jetbrains.letsPlot.core.plot.builder.assemble.PlotFacets
import org.jetbrains.letsPlot.core.plot.builder.data.DataProcessing
import org.jetbrains.letsPlot.core.plot.builder.data.OrderOptionUtil.OrderOption
import org.jetbrains.letsPlot.core.plot.builder.data.YOrientationUtil
import org.jetbrains.letsPlot.core.plot.builder.tooltip.data.DataFrameField
import org.jetbrains.letsPlot.core.spec.Option
import org.jetbrains.letsPlot.core.spec.Option.Meta.DATA_META
import org.jetbrains.letsPlot.core.spec.Option.Meta.GeoDataFrame.GDF
import org.jetbrains.letsPlot.core.spec.Option.Meta.GeoDataFrame.GEOMETRY
import org.jetbrains.letsPlot.core.spec.Option.Plot.SCALES
import org.jetbrains.letsPlot.core.spec.PlotConfigUtil
import org.jetbrains.letsPlot.core.spec.back.data.BackendDataProcUtil
import org.jetbrains.letsPlot.core.spec.back.data.PlotSampling
import org.jetbrains.letsPlot.core.spec.config.DataMetaUtil
import org.jetbrains.letsPlot.core.spec.config.LayerConfig
import org.jetbrains.letsPlot.core.spec.config.PlotConfig
import org.jetbrains.letsPlot.core.spec.config.PlotConfigTransforms
import org.jetbrains.letsPlot.core.spec.getString

open class PlotConfigBackend(
    opts: Map<String, Any>,
    containerTheme: Theme?,
) : PlotConfig(
    opts,
    containerTheme,
    isClientSide = false
) {
    private val transformByAes: Map<Aes<*>, Transform>

    init {
        transformByAes = PlotConfigTransforms.createTransforms(
            layerConfigs,
            scaleProviderByAes,
            mapperProviderByAes,
            excludeStatVariables = true  // No "stat" vars yet.
        )
    }

    /**
     * WARN! Side effects - performs modifications deep in specs tree
     */
    internal fun updatePlotSpec() {

        // Correct scales
        val plotDateTimeColumns = DataMetaUtil.getDateTimeColumns(getMap(DATA_META))
        layerConfigs.map { layerConfig ->
            val dateTimeColumns = plotDateTimeColumns + DataMetaUtil.getDateTimeColumns(layerConfig.getMap(DATA_META))

            // Detect date/time variables with mapping to discrete scale
            val dateTimeDiscreteBindings = layerConfig.varBindings
                .filter { it.variable.name in dateTimeColumns }
                .filter { scaleProviderByAes[it.aes]?.discreteDomain == true }

            val scaleUpdated = dateTimeDiscreteBindings.mapNotNull { binding ->
                val distinctValues = layerConfig.combinedData.distinctValues(binding.variable)
                selectDateTimeFormat(distinctValues)?.let { format ->
                    mapOf(
                        Option.Scale.AES to binding.aes.name,
                        Option.Scale.DATE_TIME to true,
                        Option.Scale.FORMAT to format
                    )
                }
            }
            if (scaleUpdated.isNotEmpty()) {
                val mergedOpts = PlotConfigUtil.mergeScaleOptions(scaleUpdated + getList(SCALES)).values.toList()
                update(SCALES, mergedOpts)
            }
        }

        val layerIndexWhereSamplingOccurred = HashSet<Int>()

        val dataByLayerAfterStat = dataByLayerAfterStat() { layerIndex, message ->
            layerIndexWhereSamplingOccurred.add(layerIndex)

            if (theme.plot().showMessage()) {
                PlotConfigUtil.addComputationMessage(this, message)
            }
        }

        // match the specified 'factor_levels' to the actual contents of the data set (on combined df before stat)
        val specifiedFactorLevelsByLayers = layerConfigs.map { layerConfig ->
            prepareLayerFactorLevelsByVariable(
                layerConfig.combinedData,
                plotDataMeta = getMap(DATA_META),
                layerDataMeta = layerConfig.getMap(DATA_META)
            )
        }

        // replace layer data with data after stat
        layerConfigs.withIndex().forEach { (layerIndex, layerConfig) ->
            // optimization: only replace layer' data if 'combined' data was changed (because of stat or sampling occurred)
            if (layerConfig.stat !== Stats.IDENTITY || layerIndexWhereSamplingOccurred.contains(layerIndex)) {
                val layerStatData = dataByLayerAfterStat[layerIndex]
                layerConfig.replaceOwnData(layerStatData)
            }
        }

        // Clean-up data before sending it to the front-end.
        dropUnusedDataBeforeEncoding(layerConfigs)

        // Re-create the "natural order" existed before faceting
        // or apply the specified order
        if (facets.isDefined || specifiedFactorLevelsByLayers.any { it.isNotEmpty() }) {
            layerConfigs.zip(specifiedFactorLevelsByLayers)
                .filter { (layerConfig, factorLevels) ->
                    // When faceting, each layer' data was split to panels, then re-combined with loss of 'natural order'.
                    facets.isFacettable(layerConfig.ownData)
                            || factorLevels.isNotEmpty()
                }
                .forEach { (layerConfig, factorLevels) ->
                    val layerDataMetaUpdated = addFactorLevelsDataMeta(
                        layerData = layerConfig.ownData,
                        layerDataMeta = layerConfig.getMap(DATA_META),
                        stat = layerConfig.stat,
                        varBindings = layerConfig.varBindings,
                        transformByAes = transformByAes,
                        orderOptions = layerConfig.orderOptions,
                        yOrientation = layerConfig.isYOrientation,
                        specifiedLayerFactorLevers = factorLevels
                    )
                    layerConfig.update(DATA_META, layerDataMetaUpdated)
                }
        }
    }

    private fun dropUnusedDataBeforeEncoding(layerConfigs: List<LayerConfig>) {
        // Clean-up shared data (aka plot data)
        val variablesToKeepByLayerConfig: Map<LayerConfig, Set<String>> =
            layerConfigs.associateWith { variablesToKeep(facets, it) }

        val plotData = sharedData
        val plotVars = DataFrameUtil.variables(plotData)
        val plotVarsToKeep = HashSet<String>()
        for (plotVar in plotVars.keys) {
            var canDropPlotVar = true
            for ((layerConfig, layerVarsToKeep) in variablesToKeepByLayerConfig) {
                val layerData = layerConfig.ownData
                if (DataFrameUtil.variables(layerData).containsKey(plotVar)) {
                    // This variable not needed for this layer
                    // because there is same variable in the plot's data.
                    continue
                }
                if (layerVarsToKeep.contains(plotVar)) {
                    // Have to keep this variable.
                    canDropPlotVar = false
                    break
                }
            }

            if (!canDropPlotVar) {
                plotVarsToKeep.add(plotVar)
            }
        }

        if (plotVarsToKeep.size < plotVars.size) {
            val plotDataCleaned = DataFrameUtil.removeAllExcept(plotData, plotVarsToKeep)
            replaceSharedData(plotDataCleaned)
        }

        // Clean-up data in layers.
        for ((layerConfig, layerVarsToKeep) in variablesToKeepByLayerConfig) {
            val layerData = layerConfig.ownData
            val layerDataCleaned = DataFrameUtil.removeAllExcept(layerData, layerVarsToKeep)
            layerConfig.replaceOwnData(layerDataCleaned)
        }
    }

    private fun dataByLayerAfterStat(layerMessageHandler: (Int, String) -> Unit): List<DataFrame> {
        // transform data before stat
        val dataByLayer: List<DataFrame> = layerConfigs.map { layer ->
            DataProcessing.transformOriginals(layer.combinedData, layer.varBindings, transformByAes)
        }

        return layerConfigs.mapIndexed { layerIndex, layerConfig ->
            applyLayerStatistic(
                layerConfig,
                layerData = dataByLayer[layerIndex],
                ConfiguredStatContext(dataByLayer, transformByAes),
            ) { message ->
                layerMessageHandler(layerIndex, message)
            }
        }
    }

    private fun applyLayerStatistic(
        layerConfig: LayerConfig,
        layerData: DataFrame,
        statCtx: StatContext,
        messageHandler: (String) -> Unit
    ): DataFrame {
        // slice data to tiles
        val dataByTileBeforeStat = PlotConfigUtil.splitLayerDataByTile(layerData, facets)

        val dataByTileAfterStat = dataByTileBeforeStat.map { tileDataBeforeStat ->

            val facetVariables = facets.variables.mapNotNull { facetVarName ->
                tileDataBeforeStat.variables().firstOrNull { it.name == facetVarName }
            }

            val groupingContextBeforeStat = BackendDataProcUtil.createGroupingContext(tileDataBeforeStat, layerConfig)

            val tileDataAfterStat: DataFrame
            val groupMapperAfterStat: (Int) -> Int

            if (layerConfig.stat == Stats.IDENTITY) {
                tileDataAfterStat = tileDataBeforeStat
                groupMapperAfterStat = groupingContextBeforeStat.groupMapper

            } else {
                val result: DataProcessing.DataAndGroupMapper = BackendDataProcUtil.applyStatisticTransform(
                    data = tileDataBeforeStat,
                    layerConfig = layerConfig,
                    statCtx = statCtx,
                    transformByAes = transformByAes,
                    facetVariables = facetVariables,
                    groupingContext = groupingContextBeforeStat
                ) { message -> messageHandler(message) }

                tileDataAfterStat = result.data
                groupMapperAfterStat = result.groupMapper
            }

            // Apply sampling to layer tile data if necessary
            PlotSampling.apply(
                tileDataAfterStat,
                layerConfig.samplings,
                groupMapperAfterStat
            ) { message -> messageHandler(BackendDataProcUtil.createSamplingMessage(message, layerConfig)) }
        }

        // merge tiles

        val mergedSerieByVarName = HashMap<String, Pair<Variable, ArrayList<Any?>>>()
        for (tileDataAfterStat in dataByTileAfterStat) {
            val variables = tileDataAfterStat.variables()
            if (mergedSerieByVarName.isEmpty()) {
                for (variable in variables) {
                    mergedSerieByVarName[variable.name] = Pair(variable, ArrayList(tileDataAfterStat[variable]))
                }
            } else {
                // Have to skip to not fail on mergedSerieByVarName.getValue(statVar)
                // Empty stat data contains all existing stat variables and mergedSerieByVarName doesn't
                if (tileDataAfterStat.rowCount() > 0) {
                    for (variable in variables) {
                        mergedSerieByVarName.getValue(variable.name).second.addAll(tileDataAfterStat[variable])
                    }
                }
            }
        }

        val builder = DataFrame.Builder()
        for (varName in mergedSerieByVarName.keys) {
            val variable = mergedSerieByVarName.getValue(varName).first
            val serie = mergedSerieByVarName.getValue(varName).second
            builder.put(variable, serie)
        }
        return builder.build()
    }

    companion object {

        private fun variablesToKeep(facets: PlotFacets, layerConfig: LayerConfig): Set<String> {
            val stat = layerConfig.stat
            // keep all original vars
            // keep default-mapped stat vars only if not overwritten by actual mapping
            val defStatMapping = Stats.defaultMapping(stat)
            val bindings = when (layerConfig.isYOrientation) {
                true -> YOrientationUtil.flipVarBinding(layerConfig.varBindings)
                false -> layerConfig.varBindings
            }

            val varsToKeep = HashSet(defStatMapping.values)  // initially add all def stat mapping
            for (binding in bindings) {
                val aes = binding.aes
                if (stat.hasDefaultMapping(aes)) {
                    varsToKeep.remove(stat.getDefaultMapping(aes))
                }
                varsToKeep.add(binding.variable)
            }

            // drop var if aes is not rendered by geom
            val renderedAes = HashSet(layerConfig.renderedAes)
            val renderedVars = HashSet<Variable>()
            val notRenderedVars = HashSet<Variable>()
            for (binding in bindings) {
                val aes = binding.aes
                if (renderedAes.contains(aes)) {
                    renderedVars.add(binding.variable)
                } else {
                    notRenderedVars.add(binding.variable)
                }
            }
            varsToKeep.removeAll(notRenderedVars)
            varsToKeep.addAll(renderedVars)

            return HashSet<String>() +
                    varsToKeep.map(Variable::name) +
                    Stats.GROUP.name +
                    listOfNotNull(layerConfig.getMap(DATA_META).getString(GDF, GEOMETRY)) +
                    (layerConfig.getMapJoin()?.first?.map { it as String } ?: emptyList()) +
                    facets.variables +
                    listOfNotNull(layerConfig.explicitGroupingVarName) +
                    (layerConfig.tooltips.valueSources + layerConfig.annotations.valueSources)
                        .filterIsInstance<DataFrameField>()
                        .map(DataFrameField::getVariableName) +
                    layerConfig.orderOptions.mapNotNull(OrderOption::byVariable)
        }

        private fun addFactorLevelsDataMeta(
            layerData: DataFrame,
            layerDataMeta: Map<String, Any>,
            stat: Stat,
            varBindings: List<VarBinding>,
            transformByAes: Map<Aes<*>, Transform>,
            orderOptions: List<OrderOption>,
            yOrientation: Boolean,
            specifiedLayerFactorLevers: Map<String, List<Any>>
        ): Map<String, Any> {

            // Use "discrete transforms" to re-create the "natural order" existed before faceting.

            val orderedVariables = orderOptions.map { it.variableName }

            @Suppress("UNCHECKED_CAST")
            val discreteTransformByAes = transformByAes
                .filterValues { it is DiscreteTransform } as Map<Aes<*>, DiscreteTransform>

            val statDefaultMappings = stat.getDefaultVariableMappings(yOrientation)
            val explicitMappings = varBindings
                .associate { it.variable to it.aes }

            val discreteAesByMappedVariable = (statDefaultMappings + explicitMappings)
                .filterValues { aes -> aes in discreteTransformByAes.keys }
                .filterKeys { variable -> layerData.has(variable) }
                .filterKeys { variable -> !(variable.name in orderedVariables) }

            val discreteTransformByVariable = discreteAesByMappedVariable
                .mapValues { (_, aes) -> discreteTransformByAes.getValue(aes) }

            val levelsByVariable: MutableMap<String, List<Any>> = mutableMapOf()
            for ((variable, transform) in discreteTransformByVariable) {
                val distinctValues = layerData.distinctValues(variable)
                val indices = transform.apply(distinctValues.toList())
                // null values -> last
                val orderedDistinctValues = distinctValues.zip(indices).sortedBy { it.second }.map { it.first }
                levelsByVariable[variable.name] = orderedDistinctValues
            }

            // apply specified factors
            levelsByVariable += specifiedLayerFactorLevers

            return DataMetaUtil.updateFactorLevelsByVariable(layerDataMeta, levelsByVariable)
        }

        private fun prepareLayerFactorLevelsByVariable(
            data: DataFrame,
            plotDataMeta: Map<*, *>,
            layerDataMeta: Map<*, *>
        ): Map<String, List<Any>> {
            val plotFactorLevelsByVar = DataMetaUtil.getFactorLevelsByVariable(plotDataMeta)
            val layerFactorLevelsByVar = DataMetaUtil.getFactorLevelsByVariable(layerDataMeta)
            val factorLevelsByVar = (plotFactorLevelsByVar + layerFactorLevelsByVar)
                .mapKeys { (varName, _) -> data.variables().find { it.name == varName } }
                .filterNotNullKeys()

            val orderDirectionsByVar = DataMetaUtil.getFactorLevelsOrderByVariable(plotDataMeta) +
                    DataMetaUtil.getFactorLevelsOrderByVariable(layerDataMeta)

            return factorLevelsByVar.map { (variable, levels) ->
                // append missed values to the tail of specified levels
                val distinctValues = data.distinctValues(variable)
                val tail = distinctValues - levels.toSet()
                val order = orderDirectionsByVar.getOrElse(variable.name) { 0 }
                val factors = (levels + tail).let { if (order >= 0) it else it.reversed() }
                variable.name to factors
            }.toMap()
        }

        private const val VALUES_LIMIT_TO_SELECT_FORMAT = 1_000_000
        private fun selectDateTimeFormat(distinctValues: Set<Any>): String? {
            if (distinctValues.any { it !is Number }) {
                return null
            }

            // Try the same formatter that is used for the continuous scale
            val breaksPattern = SeriesUtil.toDoubleList(distinctValues.toList())
                ?.let { doubleList -> SeriesUtil.range(doubleList) }
                ?.let { range ->
                    DateTimeBreaksHelper(
                        range.lowerEnd,
                        range.upperEnd,
                        distinctValues.size,
                        providedFormatter = null
                    ).pattern
                }

            // Other patterns to choose the most good one
            val patterns = listOf(
                "%Y",
                "%Y-%m",
                "%Y-%m-%d",
                "%Y-%m-%d %H:%M",
                "%Y-%m-%d %H:%M:%S",
            )
            if (distinctValues.size > VALUES_LIMIT_TO_SELECT_FORMAT) {
                return breaksPattern ?: patterns.last()
            }
            (listOfNotNull(breaksPattern) + patterns).forEach { pattern ->
                val formatter = StringFormat.forOneArg(pattern, type = StringFormat.FormatType.DATETIME_FORMAT)
                val formattedValues = mutableSetOf<String>()
                for (value in distinctValues) {
                    if (!formattedValues.add(formatter.format(value))) {
                        break
                    }
                }
                if (formattedValues.size == distinctValues.size) {
                    return pattern
                }
            }
            return patterns.last()
        }
    }
}
