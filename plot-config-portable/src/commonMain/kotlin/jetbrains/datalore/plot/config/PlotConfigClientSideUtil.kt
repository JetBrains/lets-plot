/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.base.gcommon.base.Preconditions.checkState
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.GeomKind
import jetbrains.datalore.plot.base.data.DataFrameUtil.variables
import jetbrains.datalore.plot.builder.GeomLayer
import jetbrains.datalore.plot.builder.assemble.GeomLayerBuilder
import jetbrains.datalore.plot.builder.assemble.GuideOptions
import jetbrains.datalore.plot.builder.assemble.PlotAssembler
import jetbrains.datalore.plot.builder.interact.GeomInteraction

object PlotConfigClientSideUtil {
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

    fun createPlotAssembler(opts: Map<String, Any>): PlotAssembler {
        val config = PlotConfigClientSide.create(opts)
        val layersByTile = buildPlotLayers(config)
        val assembler =
            PlotAssembler.multiTile(config.scaleMap, layersByTile, config.coordProvider, config.theme)
        assembler.setTitle(config.title)
        assembler.setGuideOptionsMap(config.guideOptionsMap)
        assembler.facets = config.facets
        return assembler
    }

    private fun buildPlotLayers(plotConfig: PlotConfigClientSide): List<List<GeomLayer>> {
        val dataByLayer = ArrayList<DataFrame>()
        for (layerConfig in plotConfig.layerConfigs) {
            val layerData = layerConfig.combinedData
            dataByLayer.add(layerData)
        }

        val layersDataByTile = PlotConfigUtil.toLayersDataByTile(dataByLayer, plotConfig.facets).iterator()

        val layerBuilders = ArrayList<GeomLayerBuilder>()
        val layersByTile = ArrayList<List<GeomLayer>>()
        while (layersDataByTile.hasNext()) {
            val panelLayers = ArrayList<GeomLayer>()
            val tileDataByLayer = layersDataByTile.next()

            val isMultilayer = tileDataByLayer.size > 1
            val isLiveMap = plotConfig.layerConfigs.any { it.geomProto.geomKind == GeomKind.LIVE_MAP }

            for (layerIndex in tileDataByLayer.indices) {
                checkState(layerBuilders.size >= layerIndex)

                if (layerBuilders.size == layerIndex) {
                    val layerConfig = plotConfig.layerConfigs[layerIndex]
                    val geomInteraction =
                        GeomInteractionUtil.configGeomTargets(layerConfig, plotConfig.scaleMap, isMultilayer, isLiveMap, plotConfig.theme)

                    layerBuilders.add(createLayerBuilder(layerConfig, /*scaleProvidersMap,*/ geomInteraction))
                }

                val layerTileData = tileDataByLayer[layerIndex]
                val layer = layerBuilders[layerIndex].build(layerTileData, plotConfig.scaleMap)
                panelLayers.add(layer)
            }
            layersByTile.add(panelLayers)
        }

        return layersByTile
    }

    private fun createLayerBuilder(
        layerConfig: LayerConfig,
        geomInteraction: GeomInteraction
    ): GeomLayerBuilder {
        val geomProvider = (layerConfig.geomProto as GeomProtoClientSide).geomProvider(layerConfig)

        val stat = layerConfig.stat
        val layerBuilder = GeomLayerBuilder()
            .stat(stat)
            .geom(geomProvider)
            .pos(layerConfig.posProvider)


        val constantAesMap = layerConfig.constantsMap
        for (aes in constantAesMap.keys) {
            @Suppress("UNCHECKED_CAST", "MapGetWithNotNullAssertionOperator")
            layerBuilder.addConstantAes(aes as Aes<Any>, constantAesMap[aes]!!)
        }

        if (layerConfig.hasExplicitGrouping()) {
            layerBuilder.groupingVarName(layerConfig.explicitGroupingVarName!!)
        }

        // no map_join, data=gdf or map=gdf - group values and geometries by GEO_ID
        variables(layerConfig.combinedData)[GeoConfig.GEO_ID]?.let {
            layerBuilder.pathIdVarName(GeoConfig.GEO_ID)
        }

        // with map_join use data variable to group values and geometries
        layerConfig.mergedOptions.dataJoinVariable()?.let {
            layerBuilder.pathIdVarName(it)
        }

        // variable bindings
        val bindings = layerConfig.varBindings
        for (binding in bindings) {
            layerBuilder.addBinding(binding)
        }

        layerBuilder.disableLegend(layerConfig.isLegendDisabled)
            .setTooltipProperties(layerConfig.tooltips.tooltipProperties)

        layerBuilder
            .locatorLookupSpec(geomInteraction.createLookupSpec())
            .contextualMappingProvider(geomInteraction)

        return layerBuilder
    }
}
