/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.server.config

import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.DataFrame.Variable
import jetbrains.datalore.plot.base.data.DataFrameUtil
import jetbrains.datalore.plot.base.stat.Stats
import jetbrains.datalore.plot.builder.VarBinding
import jetbrains.datalore.plot.builder.assemble.PlotFacets
import jetbrains.datalore.plot.builder.data.DataProcessing
import jetbrains.datalore.plot.builder.data.OrderOptionUtil.OrderOption
import jetbrains.datalore.plot.builder.data.YOrientationUtil
import jetbrains.datalore.plot.builder.tooltip.DataFrameValue
import jetbrains.datalore.plot.config.*
import jetbrains.datalore.plot.config.Option.Meta.DATA_META
import jetbrains.datalore.plot.config.Option.Meta.GeoDataFrame.GDF
import jetbrains.datalore.plot.config.Option.Meta.GeoDataFrame.GEOMETRY

open class PlotConfigServerSide(
    opts: Map<String, Any>
) : PlotConfig(
    opts,
    isClientSide = false
) {

    /**
     * WARN! Side effects - performs modifications deep in specs tree
     */
    internal fun updatePlotSpec() {
        val layerIndexWhereSamplingOccurred = HashSet<Int>()

        val dataByLayerAfterStat = dataByLayerAfterStat() { layerIndex, message ->
            layerIndexWhereSamplingOccurred.add(layerIndex)
            PlotConfigUtil.addComputationMessage(this, message)
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

        // Re-create the "natural order" existed before faceting.
        if (facets.isDefined) {
            // When faceting, each layer' data was split to panels, then re-combined with loss of 'natural order'.
            layerConfigs.forEach { layerConfig ->
                val layerData = layerConfig.ownData
                if (facets.isFacettable(layerData)) {
                    val layerDataMetaUpdated = addFactorLevelsDataMeta(
                        layerData = layerData,
                        layerDataMeta = layerConfig.getMap(DATA_META),
                        stat = layerConfig.stat,
                        varBindings = layerConfig.varBindings,
                        transformByAes = transformByAes,
                        orderOptions = layerConfig.orderOptions,
                        yOrientation = layerConfig.isYOrientation
                    )
                    layerConfig.update(DATA_META, layerDataMetaUpdated)
                }
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
        val statCtx = ConfiguredStatContext(dataByLayer, transformByAes)

        return layerConfigs.mapIndexed { layerIndex, layerConfig ->
            applyLayerStatistic(
                layerConfig,
                layerData = dataByLayer[layerIndex],
                statCtx,
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
                        .filterIsInstance<DataFrameValue>()
                        .map(DataFrameValue::getVariableName) +
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

            return DataMetaUtil.updateFactorLevelsByVariable(layerDataMeta, levelsByVariable)
        }
    }
}
