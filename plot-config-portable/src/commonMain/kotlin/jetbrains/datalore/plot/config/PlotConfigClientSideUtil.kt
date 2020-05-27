/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.base.gcommon.base.Preconditions.checkState
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.GeomKind
import jetbrains.datalore.plot.base.interact.GeomTargetLocator.LookupStrategy
import jetbrains.datalore.plot.base.interact.ValueSource
import jetbrains.datalore.plot.builder.GeomLayer
import jetbrains.datalore.plot.builder.assemble.GeomLayerBuilder
import jetbrains.datalore.plot.builder.assemble.GuideOptions
import jetbrains.datalore.plot.builder.assemble.PlotAssembler
import jetbrains.datalore.plot.builder.assemble.TypedScaleProviderMap
import jetbrains.datalore.plot.builder.interact.GeomInteractionBuilder
import jetbrains.datalore.plot.builder.interact.GeomInteractionBuilder.Companion.AREA_GEOM
import jetbrains.datalore.plot.builder.interact.GeomInteractionBuilder.Companion.NON_AREA_GEOM
import jetbrains.datalore.plot.builder.theme.Theme
import jetbrains.datalore.plot.builder.tooltip.CompositeValue
import jetbrains.datalore.plot.config.Option.Meta.MapJoin
import jetbrains.datalore.plot.builder.tooltip.TooltipLineSpecification

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

            for (layerIndex in tileDataByLayer.indices) {
                checkState(layerBuilders.size >= layerIndex)

                if (layerBuilders.size == layerIndex) {
                    val layerConfig = cfg.layerConfigs[layerIndex]
                    val layerBuilder = createLayerBuilder(layerConfig, scaleProvidersMap)
                    configGeomTargets(layerBuilder, layerConfig, isMultilayer, cfg.theme)
                    layerBuilders.add(layerBuilder)
                }

                val layerTileData = tileDataByLayer[layerIndex]
                val layer = layerBuilders[layerIndex].build(layerTileData)
                panelLayers.add(layer)
            }
            layersByTile.add(panelLayers)
        }

        return layersByTile
    }

    private fun configGeomTargets(
        layerBuilder: GeomLayerBuilder,
        layerConfig: LayerConfig,
        multilayer: Boolean,
        theme: Theme
    ) {
        val axisWithoutTooltip = ArrayList<Aes<*>>()
        if (!theme.axisX().showTooltip()) axisWithoutTooltip.add(Aes.X)
        if (!theme.axisY().showTooltip()) axisWithoutTooltip.add(Aes.Y)

        val geomInteraction = createGeomInteractionBuilder(
            layerConfig,
            axisWithoutTooltip,
            multilayer
        ).build()

        layerBuilder
            .locatorLookupSpec(geomInteraction.createLookupSpec())
            .contextualMappingProvider(geomInteraction)
     }

    private fun createLayerBuilder(
        layerConfig: LayerConfig,
        scaleProvidersMap: TypedScaleProviderMap
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
        return layerBuilder
    }

    internal fun createGeomInteractionBuilder(
        renders: List<Aes<*>>,
        geomKind: GeomKind,
        statKind: StatKind,
        multilayer: Boolean
    ): GeomInteractionBuilder {

        val builder = initGeomInteractionBuilder(renders, geomKind, statKind)

        if (multilayer) {
            // Only these kinds of geoms should be switched to NEAREST XY strategy on a multilayer plot.
            // Rect, histogram and other column alike geoms should not switch searching strategy, otherwise
            // tooltips behaviour becomes unexpected(histogram shows tooltip when cursor is close enough,
            // but not above a column).
            if (listOf(GeomKind.LINE, GeomKind.DENSITY, GeomKind.AREA, GeomKind.FREQPOLY).contains(geomKind)) {
                builder.multilayerLookupStrategy()
            } else if (statKind === StatKind.SMOOTH) {
                when (geomKind) {
                    GeomKind.POINT, GeomKind.CONTOUR -> builder.multilayerLookupStrategy()

                    else -> {
                    }
                }
            }
        }

        return builder
    }

    internal fun createGeomInteractionBuilder(
        layerConfig: LayerConfig,
        axisWithoutTooltip: List<Aes<*>>,
        multilayer: Boolean
    ): GeomInteractionBuilder {

        val builder = createGeomInteractionBuilder(
            layerConfig.geomProto.renders(),
            layerConfig.geomProto.geomKind,
            layerConfig.statKind,
            multilayer
        )

        // Set aes lists
        val hiddenAesList = createHiddenAesList(layerConfig.geomProto.geomKind) + axisWithoutTooltip
        val axisAes = createAxisAesList(builder, layerConfig.geomProto.geomKind) - hiddenAesList
        val aesList = createTooltipAesList(layerConfig, builder.getAxisFromFunctionKind) - hiddenAesList

        builder.axisAes(axisAes)
            .tooltipAes(aesList)
            .tooltipValueSources(createTooltipValueSourceList(layerConfig.tooltips))

        return builder
    }

    private fun createHiddenAesList(geomKind: GeomKind): List<Aes<*>> {
        return when (geomKind) {
            GeomKind.BOX_PLOT -> listOf(Aes.Y)
            GeomKind.RECT -> listOf(Aes.XMIN, Aes.YMIN, Aes.XMAX, Aes.YMAX)
            else -> emptyList()
        }
    }

    private fun createAxisAesListByGeomKind(geomKind: GeomKind): List<Aes<*>> {
        return if (geomKind === GeomKind.SMOOTH)
            listOf(Aes.X)
        else
            emptyList()
    }

    private fun createAxisAesList(geomBuilder: GeomInteractionBuilder, geomKind: GeomKind): List<Aes<*>> {
        return when {
            !geomBuilder.isAxisTooltipEnabled -> emptyList()
            else -> {
                val axisAesFromConfig = createAxisAesListByGeomKind(geomKind)
                if (axisAesFromConfig.isNotEmpty())
                    axisAesFromConfig
                else
                    geomBuilder.getAxisFromFunctionKind
            }
        }
    }

    private fun createTooltipAesList(
        layerConfig: LayerConfig,
        axisAes: List<Aes<*>>
    ): List<Aes<*>> {

        fun isVariableContinuous(aes: Aes<*>): Boolean {
            val scale = layerConfig.getScaleForAes(aes)
            return scale?.isContinuous ?: false
        }

        // remove axis mapping: if aes and axis are bound to the same data
        val aesListForTooltip = ArrayList(layerConfig.geomProto.renders() - axisAes)
        for (aes in axisAes) {
            if (isVariableContinuous(aes)) {
                val axisVariable = layerConfig.getVariableForAes(aes)
                aesListForTooltip.removeAll { layerConfig.getVariableForAes(it) == axisVariable }
            }
        }

        // remove auto generated mappings
        aesListForTooltip.removeAll { layerConfig.getScaleForAes(it)?.name == MapJoin.ID }

        // remove map_id mapping
        aesListForTooltip.removeAll { it === Aes.MAP_ID }

        return aesListForTooltip
    }

    private fun createTooltipValueSourceList(tooltipLineSpecifications: List<TooltipLineSpecification>?): List<ValueSource>? {
        if (tooltipLineSpecifications == null) {
            return null
        }

        val tooltipValueSourceList = mutableListOf<ValueSource>()
        tooltipLineSpecifications.forEach { tooltipLineSpecification ->
            if (tooltipLineSpecification.data.size == 1) {
                tooltipValueSourceList.add(tooltipLineSpecification.data.single())
            } else {
                tooltipValueSourceList.add(
                    CompositeValue(
                        tooltipLineSpecification.data,
                        tooltipLineSpecification.label,
                        tooltipLineSpecification.format
                    )
                )
            }
        }
        return tooltipValueSourceList
    }

    private fun initGeomInteractionBuilder(
        renders: List<Aes<*>>,
        geomKind: GeomKind,
        statKind: StatKind
    ): GeomInteractionBuilder {
        val builder = GeomInteractionBuilder(renders)
        if (statKind === StatKind.SMOOTH) {
            when (geomKind) {
                GeomKind.POINT,
                GeomKind.CONTOUR -> return builder.univariateFunction(LookupStrategy.NEAREST)
                else -> {
                }
            }
        }

        when (geomKind) {
            GeomKind.DENSITY,
            GeomKind.FREQPOLY,
            GeomKind.HISTOGRAM,
            GeomKind.LINE,
            GeomKind.AREA,
            GeomKind.BAR,
            GeomKind.ERROR_BAR,
            GeomKind.CROSS_BAR,
            GeomKind.POINT_RANGE,
            GeomKind.LINE_RANGE -> return builder.univariateFunction(LookupStrategy.HOVER)
            GeomKind.BOX_PLOT -> return builder.univariateFunction(LookupStrategy.HOVER)
            GeomKind.V_LINE -> return builder.univariateFunction(LookupStrategy.HOVER)
            GeomKind.SMOOTH,
            GeomKind.POINT,
            GeomKind.CONTOUR,
            GeomKind.RIBBON,
            GeomKind.DENSITY2D -> {
                return builder.bivariateFunction(NON_AREA_GEOM)
            }
            GeomKind.PATH -> {
                when (statKind) {
                    StatKind.CONTOUR, StatKind.CONTOURF, StatKind.DENSITY2D -> return builder.bivariateFunction(
                        NON_AREA_GEOM
                    )
                    else -> {
                    }
                }
                return builder.bivariateFunction(AREA_GEOM)
            }
            GeomKind.H_LINE,
            GeomKind.TILE,
            GeomKind.DENSITY2DF,
            GeomKind.CONTOURF,
            GeomKind.POLYGON,
            GeomKind.BIN_2D,
            GeomKind.MAP -> return builder.bivariateFunction(AREA_GEOM)
            GeomKind.RECT -> return builder.bivariateFunction(AREA_GEOM)

            GeomKind.LIVE_MAP -> return builder.multilayerLookupStrategy()

            else -> return builder.none()
        }
    }
}
