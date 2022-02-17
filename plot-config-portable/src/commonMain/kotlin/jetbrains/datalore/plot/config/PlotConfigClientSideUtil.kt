/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

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
        for (scaleConfig in scaleConfigs) {
            if (scaleConfig.hasGuideOptions()) {
                val guideOptions = scaleConfig.getGuideOptions().createGuideOptions()
                guideOptionsByAes[scaleConfig.aes] = guideOptions
            }
        }
        return guideOptionsByAes
    }

    internal fun createGuideOptionsMap(guideOptionsList: Map<String, Any>): Map<Aes<*>, GuideOptions> {
        val guideOptionsByAes = HashMap<Aes<*>, GuideOptions>()
        for ((key, value) in guideOptionsList) {
            val aes = Option.Mapping.toAes(key)
            guideOptionsByAes[aes] = GuideConfig.create(value).createGuideOptions()
        }
        return guideOptionsByAes
    }

    fun createPlotAssembler(config: PlotConfigClientSide): PlotAssembler {
        val layersByTile = buildPlotLayers(config)
        val assembler = PlotAssembler.multiTile(
            layersByTile,
            config.scaleMap.get(Aes.X),
            config.scaleMap.get(Aes.Y),
            config.mappersByAesNP,
            config.coordProvider,
            config.theme
        )
        assembler.title = config.title
        assembler.subtitle = config.subtitle
        assembler.caption = config.caption
        assembler.guideOptionsMap = config.guideOptionsMap
        assembler.facets = config.facets
        return assembler
    }

    private fun buildPlotLayers(plotConfig: PlotConfigClientSide): List<List<GeomLayer>> {
        val dataByLayer = ArrayList<DataFrame>()
        for (layerConfig in plotConfig.layerConfigs) {
            val layerData = layerConfig.combinedData
            dataByLayer.add(layerData)
        }

        val layersDataByTile = PlotConfigUtil.toLayersDataByTile(dataByLayer, plotConfig.facets)

        val layerBuilders = ArrayList<GeomLayerBuilder>()
        val layersByTile = ArrayList<List<GeomLayer>>()
        for (tileDataByLayer in layersDataByTile) {
            val panelLayers = ArrayList<GeomLayer>()

            val isLiveMap = plotConfig.layerConfigs.any { it.geomProto.geomKind == GeomKind.LIVE_MAP }

            for (layerIndex in tileDataByLayer.indices) {
                check(layerBuilders.size >= layerIndex)

                if (layerBuilders.size == layerIndex) {
                    val otherLayerWithTooltips = plotConfig.layerConfigs
                        .filterIndexed { index, _ -> index != layerIndex }
                        .any { !it.tooltips.hideTooltips() }

                    val layerConfig = plotConfig.layerConfigs[layerIndex]
                    val geomInteraction =
                        GeomInteractionUtil.configGeomTargets(
                            layerConfig,
                            plotConfig.scaleMap,
                            otherLayerWithTooltips,
                            isLiveMap,
                            plotConfig.theme
                        )

                    layerBuilders.add(createLayerBuilder(layerConfig, geomInteraction))
                }

                val layerTileData = tileDataByLayer[layerIndex]
                val layer = layerBuilders[layerIndex].build(
                    layerTileData,
                    plotConfig.scaleMap,
                    plotConfig.mappersByAesNP,
                )
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

        // variable bindings
        val bindings = layerConfig.varBindings
        for (binding in bindings) {
            layerBuilder.addBinding(binding)
        }

        layerBuilder.disableLegend(layerConfig.isLegendDisabled)

        layerBuilder
            .locatorLookupSpec(geomInteraction.createLookupSpec())
            .contextualMappingProvider(geomInteraction)

        return layerBuilder
    }
}
