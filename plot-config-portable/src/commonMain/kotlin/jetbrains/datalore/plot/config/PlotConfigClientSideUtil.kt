/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.base.gcommon.base.Preconditions.checkState
import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.spatial.MercatorUtils
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.GeomKind
import jetbrains.datalore.plot.base.data.DataFrameUtil.variables
import jetbrains.datalore.plot.builder.GeomLayer
import jetbrains.datalore.plot.builder.assemble.GeomLayerBuilder
import jetbrains.datalore.plot.builder.assemble.GuideOptions
import jetbrains.datalore.plot.builder.assemble.PlotAssembler
import jetbrains.datalore.plot.builder.assemble.TypedScaleProviderMap
import jetbrains.datalore.plot.builder.coord.CoordProvider
import jetbrains.datalore.plot.builder.interact.GeomInteraction
import jetbrains.datalore.plot.common.data.SeriesUtil

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
        val coordProvider = config.coordProvider
        val layersByTile = buildPlotLayers(config)
        val assembler =
            PlotAssembler.multiTile(layersByTile, coordProvider, config.theme)
        assembler.setTitle(config.title)
        assembler.setGuideOptionsMap(config.guideOptionsMap)
        assembler.facets = config.facets
        return assembler
    }

    private fun buildPlotLayers(cfg: PlotConfigClientSide): List<List<GeomLayer>> {
        val dataByLayer = ArrayList<DataFrame>()
        for (layerConfig in cfg.layerConfigs) {
            val layerData = layerConfig.combinedData
            dataByLayer.add(layerData)
        }

        val layersDataByTile = PlotConfigUtil.toLayersDataByTile(dataByLayer, cfg.facets).iterator()

        val scaleProvidersMap = cfg.scaleProvidersMap
        val layerBuilders = ArrayList<GeomLayerBuilder>()
        val layersByTile = ArrayList<List<GeomLayer>>()
        while (layersDataByTile.hasNext()) {
            val panelLayers = ArrayList<GeomLayer>()
            val tileDataByLayer = layersDataByTile.next()

            val isMultilayer = tileDataByLayer.size > 1
            val isLiveMap = cfg.layerConfigs.any { it.geomProto.geomKind == GeomKind.LIVE_MAP }

            for (layerIndex in tileDataByLayer.indices) {
                checkState(layerBuilders.size >= layerIndex)

                if (layerBuilders.size == layerIndex) {
                    val layerConfig = cfg.layerConfigs[layerIndex]
                    val geomInteraction =
                        GeomInteractionUtil.configGeomTargets(layerConfig, isMultilayer, isLiveMap, cfg.theme)

                    layerBuilders.add(createLayerBuilder(layerConfig, scaleProvidersMap, geomInteraction))
                }

                val layerTileData = tileDataByLayer[layerIndex]
                val layer = layerBuilders[layerIndex].build(layerTileData)
                panelLayers.add(layer)
            }
            layersByTile.add(panelLayers)
        }

        return layersByTile
    }

    private fun createLayerBuilder(
        layerConfig: LayerConfig,
        scaleProvidersMap: TypedScaleProviderMap,
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

        // scale providers
        for (aes in scaleProvidersMap.keySet()) {
            @Suppress("UNCHECKED_CAST")
            layerBuilder.addScaleProvider(aes as Aes<Any>, scaleProvidersMap[aes])
        }

        layerBuilder.disableLegend(layerConfig.isLegendDisabled)

        layerBuilder
            .locatorLookupSpec(geomInteraction.createLookupSpec())
            .contextualMappingProvider(geomInteraction)

        return layerBuilder
    }

    private fun doProjection(proj: ((Double) -> Double), range: ClosedRange<Double>?) = range?.let {
        ClosedRange(proj(range.lowerEnd), proj(range.upperEnd))
    }

    fun getMapCoordinateProvider(
        xDomain: ClosedRange<Double>,
        yDomain: ClosedRange<Double>,
        xLim: ClosedRange<Double>?,
        yLim: ClosedRange<Double>?
    ): CoordProvider {
        val projDX = SeriesUtil.span(
            doProjection({ MercatorUtils.getMercatorX(it) }, xDomain)!!
        )

        val projDY = SeriesUtil.span(
            doProjection({ MercatorUtils.getMercatorY(it) }, yDomain)!!
        )

        val dx = SeriesUtil.span(xDomain)
        val dy = SeriesUtil.span(yDomain)

        val ratio = (projDY / projDX) / (dy / dx)

        @Suppress("NAME_SHADOWING")
        val xLim = doProjection({ MercatorUtils.getMercatorX(it) }, xLim)

        @Suppress("NAME_SHADOWING")
        val yLim = doProjection({ MercatorUtils.getMercatorY(it) }, yLim)

        val opts: MutableMap<String, Any> = mutableMapOf(
            Option.Meta.NAME to Option.CoordName.MAP,
            CoordProto.RATIO to ratio
        )

        if (xLim != null) {
            opts[CoordProto.X_LIM] = xLim
        }

        if (yLim != null) {
            opts[CoordProto.Y_LIM] = yLim
        }

        return CoordConfig.create(opts).coord
    }
}
