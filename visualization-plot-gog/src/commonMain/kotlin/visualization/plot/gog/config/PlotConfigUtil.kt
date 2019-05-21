package jetbrains.datalore.visualization.plot.gog.config

import jetbrains.datalore.base.gcommon.base.Preconditions.checkArgument
import jetbrains.datalore.base.gcommon.collect.Lists
import jetbrains.datalore.visualization.plot.base.data.DataFrame
import jetbrains.datalore.visualization.plot.base.render.Aes
import jetbrains.datalore.visualization.plot.builder.GuideOptions
import jetbrains.datalore.visualization.plot.builder.assemble.PlotFacets
import jetbrains.datalore.visualization.plot.builder.assemble.TypedScaleProviderMap
import jetbrains.datalore.visualization.plot.builder.scale.ScaleProvider
import jetbrains.datalore.visualization.plot.gog.config.PlotConfig.Companion.PLOT_COMPUTATION_MESSAGES

object PlotConfigUtil {
    internal fun createGuideOptionsMap(scaleConfigs: List<ScaleConfig<*>>): Map<Aes<*>, GuideOptions> {
        val guideOptionsByAes = HashMap<Aes<*>, GuideOptions>()

        // ToDo: 'guide_xxx' can also be found in 'guides(<aes>=....)'

        for (scaleConfig in scaleConfigs) {
            if (scaleConfig.hasGuideOptions()) {
                val guideOptions = scaleConfig.gerGuideOptions().createGuideOptions()
                guideOptionsByAes[scaleConfig.aes] = guideOptions
            }
        }
        return guideOptionsByAes
    }

    fun toLayersDataByTile(dataByLayer: List<DataFrame>, facets: PlotFacets): List<List<DataFrame>> {
        // Plot consists of one or more tiles,
        // each tile consists of layers
        val layersDataByTile = ArrayList<MutableList<DataFrame>>()
        layersDataByTile.add(ArrayList())

        // if 'facets' then slice layers by panels
        var xLevels: List<*> = emptyList<Any>()
        var yLevels: List<*> = emptyList<Any>()

        val hasFacets = facets.isDefined
        if (hasFacets) {
            xLevels = facets.xLevels!!
            yLevels = facets.yLevels!!
            if (xLevels.isEmpty()) {
                xLevels = listOf<Any?>(null)
            }
            if (yLevels.isEmpty()) {
                yLevels = listOf<Any?>(null)
            }

            val numTiles = xLevels.size * yLevels.size
            while (layersDataByTile.size < numTiles) {
                layersDataByTile.add(ArrayList())
            }
        }

        for (layerData in dataByLayer) {
            if (!hasFacets) {
                layersDataByTile[0].add(layerData)
            } else {
                // create layer for each 'facet tile' in grid
                for (row in yLevels.indices) {
                    val yLevel = yLevels[row]
                    for (col in xLevels.indices) {
                        val xLevel = xLevels[col]
                        val panelLayerData = facets.dataSubset(layerData, xLevel, yLevel)
                        val panelIndex = row * xLevels.size + col
                        layersDataByTile[panelIndex].add(panelLayerData)
                    }
                }
            }
        }
        return layersDataByTile
    }

    fun addComputationMessage(accessor: OptionsAccessor, message: String?) {
        checkArgument(message != null)
        val computation_messages = ArrayList<String>(getComputationMessages(accessor))
        computation_messages.add(message!!)
        accessor.update(PLOT_COMPUTATION_MESSAGES, computation_messages)
    }

    fun findComputationMessages(spec: Map<*, *>): List<String> {
        val result: List<String>
        if (PlotConfig.isPlotSpec(spec)) {
            result = getComputationMessages(spec)
        } else if (PlotConfig.isGGBunchSpec(spec)) {
            val bunchConfig = BunchConfig(spec)
            result = ArrayList()
            for (bunchItem in bunchConfig.bunchItems) {
                result.addAll(getComputationMessages(bunchItem.featureSpec))
            }
        } else {
            throw RuntimeException("Unexpected plot spec kind: " + PlotConfig.specKind(spec))
        }

        return result.distinct()
    }

    private fun getComputationMessages(opts: Map<*, *>): List<String> {
        return getComputationMessages(OptionsAccessor.over(opts))
    }

    private fun getComputationMessages(accessor: OptionsAccessor): List<String> {
        return Lists.transform(accessor.getList(PLOT_COMPUTATION_MESSAGES)) { it as String }
    }

    internal fun createScaleProviders(scaleConfigs: List<ScaleConfig<Any>>): TypedScaleProviderMap {
        val scaleProviderByAes = HashMap<Aes<*>, ScaleProvider<*>>()
        for (scaleConfig in scaleConfigs) {
            val scaleProvider = scaleConfig.createScaleProvider()
            scaleProviderByAes[scaleConfig.aes] = scaleProvider
        }
        return TypedScaleProviderMap(scaleProviderByAes)
    }
}
