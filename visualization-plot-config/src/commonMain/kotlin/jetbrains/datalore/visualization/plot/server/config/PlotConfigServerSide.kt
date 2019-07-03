package jetbrains.datalore.visualization.plot.server.config

import jetbrains.datalore.base.values.Pair
import jetbrains.datalore.visualization.plot.base.DataFrame
import jetbrains.datalore.visualization.plot.base.DataFrame.Variable
import jetbrains.datalore.visualization.plot.base.data.DataFrameUtil
import jetbrains.datalore.visualization.plot.base.stat.Stats
import jetbrains.datalore.visualization.plot.builder.assemble.TypedScaleProviderMap
import jetbrains.datalore.visualization.plot.builder.data.DataProcessing
import jetbrains.datalore.visualization.plot.builder.data.GroupingContext
import jetbrains.datalore.visualization.plot.config.LayerConfig
import jetbrains.datalore.visualization.plot.config.PlotConfig
import jetbrains.datalore.visualization.plot.config.PlotConfigUtil
import jetbrains.datalore.visualization.plot.config.transform.encode.DataSpecEncodeTransforms
import jetbrains.datalore.visualization.plot.server.config.transform.PlotConfigServerSideTransforms.entryTransform
import jetbrains.datalore.visualization.plot.server.config.transform.PlotConfigServerSideTransforms.migrationTransform

class PlotConfigServerSide private constructor(opts: Map<String, Any>) : PlotConfig(opts) {

    override fun createLayerConfig(
            layerOptions: Map<*, *>, sharedData: DataFrame?, plotMapping: Map<*, *>,
            scaleProviderByAes: TypedScaleProviderMap): LayerConfig {

        return LayerConfig(
                layerOptions,
                sharedData!!,
                plotMapping,
                StatProtoServerSide(),
                scaleProviderByAes,
                false)
    }

    /**
     * WARN! Side effects - performs modifications deep in specs tree
     */
    private fun updatePlotSpec() {
        val layerIndexWhereSamplingOccurred = HashSet<Int>()
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
                //if (layerConfig.getStat() != Stats.IDENTITY || samplingInfoByLayerIndex.containsKey(layerIndex)) {
                if (layerConfig.stat !== Stats.IDENTITY || layerIndexWhereSamplingOccurred.contains(layerIndex)) {
                    val layerStatData = dataByLayerAfterStat[layerIndex]
                    layerConfig.replaceOwnData(layerStatData)
                }
                //if (samplingInfoByLayerIndex.containsKey(layerIndex)) {
                //  PlotConfig.addComputationMessages(this, PlotSampling.generateSamplingOccurredMessage(layerConfig));
                //}
            }
        }

        dropUnusedDataBeforeEncoding(layerConfigs)
    }

    private fun dropUnusedDataBeforeEncoding(layerConfigs: List<LayerConfig>) {
        var plotData = sharedData
        val plotVars = DataFrameUtil.variables(plotData!!)
        val plotVarsToKeep = HashSet<String>()
        for (varName in plotVars.keys) {
            var dropPlotVar = true
            for (layerConfig in layerConfigs) {
                val layerData = layerConfig.ownData

                if (!DataFrameUtil.variables(layerData!!).containsKey(varName)) {
                    // don't drop if used in mapping
                    if (layerConfig.hasVarBinding(varName) || layerConfig.isExplicitGrouping(varName)) {
                        dropPlotVar = false
                    }
                    // don't drop if used for facets
                    val facets = facets
                    if (varName == facets.xVar) {
                        dropPlotVar = false
                    }
                    if (varName == facets.yVar) {
                        dropPlotVar = false
                    }
                    if (!dropPlotVar) {
                        break
                    }
                }
            }

            if (!dropPlotVar) {
                plotVarsToKeep.add(varName)
            }
        }

        if (plotVarsToKeep.size < plotVars.size) {
            plotData = DataFrameUtil.removeAllExcept(plotData, plotVarsToKeep)
            replaceSharedData(plotData)
        }

        // clean-up data in layers
        for (layerConfig in layerConfigs) {
            var layerData = layerConfig.ownData
            val stat = layerConfig.stat
            // keep all original vars
            // keep default-mapped stat vars only if not overwritten by actual mapping
            val defStatMapping = Stats.defaultMapping(stat)
            val bindings = layerConfig.varBindings
            val varsToKeep = HashSet(defStatMapping.values)  // initially add all def stat mapping
            for (binding in bindings) {
                val aes = binding.aes
                if (stat.hasDefaultMapping(aes)) {
                    varsToKeep.remove(stat.getDefaultMapping(aes))
                }
                val `var` = binding.variable
                varsToKeep.add(`var`)
            }

            // drop var if aes is not rendered by geom
            val geomProvider = layerConfig.geomProvider
            val renderedAes = HashSet(geomProvider.renders())
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

            val varNamesToKeep = HashSet<String>()
            for (`var` in varsToKeep) {
                varNamesToKeep.add(`var`.name)
            }
            varNamesToKeep.add(Stats.GROUP.name)
            val facets = facets
            if (facets.xVar != null) {
                varNamesToKeep.add(facets.xVar!!)
            }
            if (facets.yVar != null) {
                varNamesToKeep.add(facets.yVar!!)
            }

            if (layerConfig.hasExplicitGrouping()) {
                varNamesToKeep.add(layerConfig.explicitGroupingVarName!!)
            }

            layerData = DataFrameUtil.removeAllExcept(layerData!!, varNamesToKeep)
            layerConfig.replaceOwnData(layerData)
        }
    }

    private fun dataByTileByLayerAfterStat(layerIndexAndSamplingMessage: (Int, String) -> Unit): List<List<DataFrame>> {

        // transform layers data before stat
        val dataByLayer = ArrayList<DataFrame>()
        for (layerConfig in layerConfigs) {
            var layerData = layerConfig.combinedData
            layerData = DataProcessing.transformOriginals(layerData, layerConfig.varBindings)
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

        val scaleProvidersMap = scaleProvidersMap

        for ((layerIndex, layerConfig) in layerConfigs.withIndex()) {

            val statCtx = ConfiguredStatContext(dataByLayer, scaleProvidersMap)
            for (tileIndex in inputDataByTileByLayer.indices) {
                val tileLayerInputData = inputDataByTileByLayer[tileIndex][layerIndex]
                val varBindings = layerConfig.varBindings
                val groupingContext = GroupingContext(tileLayerInputData,
                        varBindings, layerConfig.explicitGroupingVarName, true)

                val groupingContextAfterStat: GroupingContext
                val stat = layerConfig.stat
                var tileLayerDataAfterStat: DataFrame
                if (stat === Stats.IDENTITY) {
                    // Do not apply stat
                    tileLayerDataAfterStat = tileLayerInputData
                    groupingContextAfterStat = groupingContext
                } else {
                    val tileLayerDataAndGroupingContextAfterStat = DataProcessing.buildStatData(tileLayerInputData,
                            stat, varBindings,
                            groupingContext,
                            facets.xVar,
                            facets.yVar,
                            statCtx)

                    tileLayerDataAfterStat = tileLayerDataAndGroupingContextAfterStat.data
                    groupingContextAfterStat = tileLayerDataAndGroupingContextAfterStat.groupingContext
                }

                // Apply sampling to layer tile data if necessary
                tileLayerDataAfterStat = PlotSampling.apply(tileLayerDataAfterStat, // layerConfig,
                        layerConfig.samplings!!,
                        groupingContextAfterStat.groupMapper,
                        { message ->
                            layerIndexAndSamplingMessage(
                                    layerIndex,
                                    createSamplingMessage(message, layerConfig))
                        })
                result[tileIndex].add(tileLayerDataAfterStat)
            }

        }

        return result
    }

    private fun createSamplingMessage(samplingExpression: String, layerConfig: LayerConfig): String {
        val geomKind = layerConfig.geomProvider.geomKind

        var stat: String = layerConfig.stat::class.simpleName!!
        stat = stat.replace("Stat", " stat")
        stat = stat.replace("([a-z])([A-Z]+)".toRegex(), "$1_$2").toLowerCase()

        return samplingExpression + " was applied to [" + geomKind.name.toLowerCase() + "/" + stat + "] layer"
    }

    companion object {

        /**
         * For tests only
         */
        internal fun createLayerConfigsWithoutEncoding(plotSpec: Map<String, Any>): List<LayerConfig> {
            return PlotConfigServerSide(plotSpec).layerConfigs
        }

        /**
         * For tests
         * Also used in setup where specs are not serialized, i.e. when js object with specs is generated and directly inserted into HTML
         */
        fun processTransformWithoutEncoding(plotSpec: MutableMap<String, Any>): MutableMap<String, Any> {
            return processTransform(plotSpec, false)
        }

        fun processTransform(opts: MutableMap<String, Any>): MutableMap<String, Any> {
            return processTransform(opts, true)
        }

        private fun processTransform(plotSpecRaw: MutableMap<String, Any>, encodeOnExit: Boolean): MutableMap<String, Any> {
            var plotSpec = migrationTransform().apply(plotSpecRaw)
            plotSpec = entryTransform().apply(plotSpec)
            PlotConfigServerSide(plotSpec).updatePlotSpec()
            if (encodeOnExit) {
                plotSpec = DataSpecEncodeTransforms.serverSideEncode(false).apply(plotSpec)
            }
            return plotSpec
        }
    }
}
