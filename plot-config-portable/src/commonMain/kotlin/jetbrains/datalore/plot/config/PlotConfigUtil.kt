/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.base.gcommon.base.Preconditions.checkArgument
import jetbrains.datalore.base.gcommon.collect.Lists
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.builder.assemble.PlotFacets
import jetbrains.datalore.plot.builder.assemble.TypedScaleProviderMap
import jetbrains.datalore.plot.base.scale.ScaleProvider
import jetbrains.datalore.plot.config.PlotConfig.Companion.PLOT_COMPUTATION_MESSAGES

object PlotConfigUtil {

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

    // backend
    fun addComputationMessage(accessor: OptionsAccessor, message: String?) {
        checkArgument(message != null)
        val computationMessages = ArrayList(
            getComputationMessages(
                accessor
            )
        )
        computationMessages.add(message!!)
        accessor.update(PLOT_COMPUTATION_MESSAGES, computationMessages)
    }

    // frontend
    fun findComputationMessages(spec: Map<*, *>): List<String> {
        val result: List<String> =
            when {
                PlotConfig.isPlotSpec(spec) -> getComputationMessages(spec)
                PlotConfig.isGGBunchSpec(spec) -> {
                    val bunchConfig = BunchConfig(spec)
                    bunchConfig.bunchItems.flatMap { getComputationMessages(it.featureSpec) }
                }
                else -> throw RuntimeException("Unexpected plot spec kind: ${PlotConfig.specKind(spec)}")
            }

        return result.distinct()
    }

    private fun getComputationMessages(opts: Map<*, *>): List<String> {
        return getComputationMessages(
            OptionsAccessor.over(
                opts
            )
        )
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
