/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.GeomKind
import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.base.ScaleMapper
import jetbrains.datalore.plot.base.data.DataFrameUtil.variables
import jetbrains.datalore.plot.builder.GeomLayer
import jetbrains.datalore.plot.builder.MarginalLayerUtil
import jetbrains.datalore.plot.builder.VarBinding
import jetbrains.datalore.plot.builder.assemble.GeomLayerBuilder
import jetbrains.datalore.plot.builder.assemble.GuideOptions
import jetbrains.datalore.plot.builder.assemble.PlotAssembler
import jetbrains.datalore.plot.builder.assemble.PlotFacets
import jetbrains.datalore.plot.builder.interact.GeomInteraction
import jetbrains.datalore.plot.builder.presentation.FontFamilyRegistry
import jetbrains.datalore.plot.builder.theme.Theme

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
        return PlotAssembler(
            layersByTile,
            config.scaleMap,
            config.mappersByAesNP,
            config.facets,
            config.coordProvider,
            config.xAxisPosition,
            config.yAxisPosition,
            config.theme,
            title = config.title,
            subtitle = config.subtitle,
            caption = config.caption,
            guideOptionsMap = config.guideOptionsMap
        )
    }

    private fun buildPlotLayers(plotConfig: PlotConfigClientSide): List<List<GeomLayer>> {
        return buildPlotLayers(
            plotConfig.layerConfigs,
            plotConfig.facets,
            plotConfig.scaleMap,
            plotConfig.mappersByAesNP,
            plotConfig.theme,
            plotConfig.fontFamilyRegistry,
        )
    }

    private fun buildPlotLayers(
        layerConfigs: List<LayerConfig>,
        facets: PlotFacets,
        commonScaleMap: Map<Aes<*>, Scale>,
        mappersByAesNP: Map<Aes<*>, ScaleMapper<*>>, // all non-positional mappers
        theme: Theme,
        fontRegistry: FontFamilyRegistry
    ): List<List<GeomLayer>> {
        val isLiveMap = layerConfigs.any { it.geomProto.geomKind == GeomKind.LIVE_MAP }
        val geomLayerListByTile: MutableList<MutableList<GeomLayer>> = mutableListOf()

        for ((layerIndex, layerConfig) in layerConfigs.withIndex()) {
            //
            // Layer scales
            //
            val layerCommonScales = when (layerConfig.isMarginal) {
                true -> MarginalLayerUtil.toMarginalScaleMap(
                    commonScaleMap,
                    layerConfig.marginalSide,
                    flipOrientation = false    // Positional aes are already flipped in the "common scale map".
                )

                false -> commonScaleMap
            }

            val layerAddedScales = createScalesForStatPositionalBindings(
                layerConfig.varBindings,
                layerConfig.isYOrientation,
                commonScaleMap
            ).let { scaleByAes ->
                when (layerConfig.isMarginal) {
                    true -> MarginalLayerUtil.toMarginalScaleMap(
                        scaleByAes,
                        layerConfig.marginalSide,
                        flipOrientation = layerConfig.isYOrientation
                    )

                    false -> scaleByAes
                }
            }

            val layerScaleMap = layerCommonScales + layerAddedScales

            //
            // Layer geom interaction
            //
            val geomInteraction = if (layerConfig.isMarginal) {
                // marginal layer doesn't have interactions
                null
            } else {
                val otherLayerWithTooltips = layerConfigs
                    .filterIndexed { index, _ -> index != layerIndex }
                    .any { !it.tooltips.hideTooltips() }

                GeomInteractionUtil.configGeomTargets(
                    layerConfig,
                    layerScaleMap,
                    otherLayerWithTooltips,
                    isLiveMap,
                    theme
                )
            }

            //
            // Layer builder
            //
            val geomLayerBuilder = createLayerBuilder(
                layerConfig,
                fontRegistry,
                geomInteraction,
                theme
            )

            //
            // Layer tiles
            //
            val layerData = layerConfig.combinedData
            val layerDataByTile = PlotConfigUtil.splitLayerDataByTile(layerData, facets)

            val geomLayerByTile = layerDataByTile.map { layerTileData ->
                geomLayerBuilder.build(
                    layerTileData,
                    layerScaleMap,
                    mappersByAesNP,
                )
            }

            //
            // Stack geom layers by tile.
            //
            if (geomLayerListByTile.isEmpty()) {
                geomLayerByTile.forEach { _ -> geomLayerListByTile.add(ArrayList<GeomLayer>()) }
            }
            for ((tileIndex, geomLayer) in geomLayerByTile.withIndex()) {
                val tileGeomLayers = geomLayerListByTile[tileIndex]
                tileGeomLayers.add(geomLayer)
            }
        }

        return geomLayerListByTile
    }

    private fun createScalesForStatPositionalBindings(
        layerVarBindings: List<VarBinding>,
        isYOrientation: Boolean,
        commonScaleMap: Map<Aes<*>, Scale>,
    ): Map<Aes<*>, Scale> {
        val statPositionalBindings =
            layerVarBindings.filter { it.variable.isStat }
                .filterNot { it.aes == Aes.X || it.aes == Aes.Y }
                .filter { Aes.isPositionalXY(it.aes) }

        return statPositionalBindings.map { binding ->
            val positionalAes = when (isYOrientation) {
                true -> if (Aes.isPositionalX(binding.aes)) Aes.Y else Aes.X
                false -> if (Aes.isPositionalX(binding.aes)) Aes.X else Aes.Y
            }
            val scaleProto = commonScaleMap.getValue(positionalAes)
            val aesScale = scaleProto.with().name(binding.variable.label).build()
            binding.aes to aesScale
        }.toMap()
    }

    private fun createLayerBuilder(
        layerConfig: LayerConfig,
        fontFamilyRegistry: FontFamilyRegistry,
        geomInteraction: GeomInteraction?,
        theme: Theme
    ): GeomLayerBuilder {
        val geomProvider = (layerConfig.geomProto as GeomProtoClientSide).geomProvider(layerConfig)

        val stat = layerConfig.stat
        val layerBuilder = GeomLayerBuilder(
            geomProvider = geomProvider,
            stat = stat,
            posProvider = layerConfig.posProvider,
            fontFamilyRegistry = fontFamilyRegistry
        )
            .yOrientation(layerConfig.isYOrientation)
            .marginal(layerConfig.isMarginal, layerConfig.marginalSide, layerConfig.marginalSize)

        // Color aesthetics
        layerBuilder
            .colorByAes(layerConfig.colorByAes)
            .fillByAes(layerConfig.fillByAes)

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

        layerBuilder.dataMetaAsDiscreteAesList(layerConfig.dataMetaAsDiscreteAesList)

        layerBuilder.disableLegend(layerConfig.isLegendDisabled)

        geomInteraction?.let {
            layerBuilder
                .locatorLookupSpec(it.createLookupSpec())
                .contextualMappingProvider(it)
        }
        // annotations
        layerBuilder.annotationSpecification(layerConfig.annotations, theme.plot().textStyle())

        return layerBuilder
    }
}
