/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.server.config

import jetbrains.datalore.base.logging.PortableLogging
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.DataFrame.Variable
import jetbrains.datalore.plot.base.data.DataFrameUtil
import jetbrains.datalore.plot.base.stat.Stats
import jetbrains.datalore.plot.builder.assemble.PlotFacets
import jetbrains.datalore.plot.builder.data.DataProcessing
import jetbrains.datalore.plot.builder.data.GroupingContext
import jetbrains.datalore.plot.builder.data.OrderOptionUtil.OrderOption
import jetbrains.datalore.plot.builder.data.YOrientationUtil
import jetbrains.datalore.plot.builder.tooltip.DataFrameValue
import jetbrains.datalore.plot.config.*
import jetbrains.datalore.plot.config.Option.Meta.DATA_META
import jetbrains.datalore.plot.config.Option.Meta.GeoDataFrame.GDF
import jetbrains.datalore.plot.config.Option.Meta.GeoDataFrame.GEOMETRY

open class PlotConfigServerSide(opts: Map<String, Any>) :
    PlotConfig(
        opts,
        isClientSide = false
    ) {

    override fun createLayerConfig(
        layerOptions: Map<String, Any>,
        sharedData: DataFrame,
        plotMappings: Map<*, *>,
        plotDataMeta: Map<*, *>,
        plotOrderOptions: List<OrderOption>,
        isMapPlot: Boolean
    ): LayerConfig {

        val geomName = layerOptions[Option.Layer.GEOM] as String
        val geomKind = Option.GeomName.toGeomKind(geomName)
        return LayerConfig(
            layerOptions,
            sharedData,
            plotMappings,
            plotDataMeta,
            plotOrderOptions,
            GeomProto(geomKind),
            clientSide = false,
            isMapPlot
        )
    }

    /**
     * WARN! Side effects - performs modifications deep in specs tree
     */
    internal fun updatePlotSpec() {
        val layerIndexWhereSamplingOccurred = HashSet<Int>()

        // apply tranform and stat
        val dataByTileByLayerAfterStat = dataByTileByLayerAfterStat { layerIndex, message ->
            layerIndexWhereSamplingOccurred.add(layerIndex)
            PlotConfigUtil.addComputationMessage(this, message)
        }

        // merge tiles
        val dataByLayerAfterStat = ArrayList<DataFrame>()
        val layerConfigs = layerConfigs
        for (layerIndex in layerConfigs.indices) {

            val layerSerieByVarName = HashMap<String, Pair<Variable, ArrayList<Any?>>>()
            // merge tiles
            for (tileDataByLayerAfterStat in dataByTileByLayerAfterStat) {
                val tileLayerDataAfterStat = tileDataByLayerAfterStat[layerIndex]
                val variables = tileLayerDataAfterStat.variables()
                if (layerSerieByVarName.isEmpty()) {
                    for (variable in variables) {
                        layerSerieByVarName[variable.name] = Pair(variable, ArrayList(tileLayerDataAfterStat[variable]))
                    }
                } else {
                    for (variable in variables) {
                        layerSerieByVarName[variable.name]!!.second.addAll(tileLayerDataAfterStat[variable])
                    }
                }
            }

            val builder = DataFrame.Builder()
            for (varName in layerSerieByVarName.keys) {
                val variable = layerSerieByVarName[varName]!!.first
                val serie = layerSerieByVarName[varName]!!.second
                builder.put(variable, serie)
            }
            val layerDataAfterStat = builder.build()
            dataByLayerAfterStat.add(layerDataAfterStat)
        }

        run {
            // replace layer data with data after stat
            for ((layerIndex, layerConfig) in layerConfigs.withIndex()) {
                // optimization: only replace layer' data if 'combined' data was changed (because of stat or sampling occurred)
                if (layerConfig.stat !== Stats.IDENTITY || layerIndexWhereSamplingOccurred.contains(layerIndex)) {
                    val layerStatData = dataByLayerAfterStat[layerIndex]
                    layerConfig.replaceOwnData(layerStatData)
                }
            }
        }

        dropUnusedDataBeforeEncoding(layerConfigs)
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
                val layerData = layerConfig.ownData!!
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
            val layerData = layerConfig.ownData!!
            val layerDataCleaned = DataFrameUtil.removeAllExcept(layerData, layerVarsToKeep)
            layerConfig.replaceOwnData(layerDataCleaned)
        }
    }


    private fun dataByTileByLayerAfterStat(layerIndexAndSamplingMessage: (Int, String) -> Unit): List<List<DataFrame>> {

        // transform layers data before stat
        val dataByLayer = ArrayList<DataFrame>()
        for (layerConfig in layerConfigs) {
            var layerData = layerConfig.combinedData
            layerData = DataProcessing.transformOriginals(layerData, layerConfig.varBindings, transformByAes)

            // ensure the same group order for facets
            if (facets.isDefined) {
                val groupingVariables = DataProcessing.defaultGroupingVariables(
                    layerData,
                    layerConfig.varBindings,
                    pathIdVarName = null
                )
                val groupingContext = GroupingContext(
                    layerData,
                    groupingVariables,
                    explicitGroupingVarName = layerConfig.explicitGroupingVarName,
                    expectMultiple = true // ?
                )
                layerData = DataProcessing.regroupData(layerData, groupingContext)
            }

            dataByLayer.add(layerData)
        }

        // slice data to tiles
        val facets = facets
        val inputDataByTileByLayer = PlotConfigUtil.toLayersDataByTile(dataByLayer, facets)

        // apply stat to each layer in each tile separately
        val result = ArrayList<MutableList<DataFrame>>()
        while (result.size < inputDataByTileByLayer.size) {
            result.add(ArrayList())
        }

        for ((layerIndex, layerConfig) in layerConfigs.withIndex()) {

            val statCtx = ConfiguredStatContext(dataByLayer, transformByAes)
            for (tileIndex in inputDataByTileByLayer.indices) {
                val tileLayerInputData = inputDataByTileByLayer[tileIndex][layerIndex]
                val facetVariables = facets.variables.mapNotNull { facetVarName ->
                    tileLayerInputData.variables().firstOrNull { it.name == facetVarName }
                }

                val tileLayerDataAfterStat = BackendDataProcUtil.applyStatisticalTransform(
                    data = tileLayerInputData,
                    layerConfig = layerConfig,
                    statCtx = statCtx,
                    transformByAes = transformByAes,
                    facetVariables = facetVariables,
                ) { message ->
                    layerIndexAndSamplingMessage(
                        layerIndex,
                        message
                    )
                }
                result[tileIndex].add(tileLayerDataAfterStat)
            }

        }

        return result
    }

    companion object {
        private val LOG = PortableLogging.logger(PlotConfigServerSide::class)

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
                    listOfNotNull(layerConfig.mergedOptions.getString(DATA_META, GDF, GEOMETRY)) +
                    (layerConfig.getMapJoin()?.first?.map { it as String } ?: emptyList()) +
                    facets.variables +
                    listOfNotNull(layerConfig.explicitGroupingVarName) +
                    (layerConfig.tooltips.valueSources + layerConfig.annotations.valueSources)
                        .filterIsInstance<DataFrameValue>()
                        .map(DataFrameValue::getVariableName) +
                    layerConfig.orderOptions.mapNotNull(OrderOption::byVariable)
        }
    }
}
