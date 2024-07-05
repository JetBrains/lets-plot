/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.front.tiles

import org.jetbrains.letsPlot.core.commons.data.DataType
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.stat.Stats
import org.jetbrains.letsPlot.core.plot.base.theme.FontFamilyRegistry
import org.jetbrains.letsPlot.core.plot.base.theme.Theme
import org.jetbrains.letsPlot.core.plot.builder.GeomLayer
import org.jetbrains.letsPlot.core.plot.builder.VarBinding
import org.jetbrains.letsPlot.core.plot.builder.assemble.PlotFacets
import org.jetbrains.letsPlot.core.plot.builder.assemble.PlotGeomTiles
import org.jetbrains.letsPlot.core.plot.builder.assemble.tiles.FacetedPlotGeomTiles
import org.jetbrains.letsPlot.core.plot.builder.assemble.tiles.SimplePlotGeomTiles
import org.jetbrains.letsPlot.core.plot.builder.coord.CoordProvider
import org.jetbrains.letsPlot.core.spec.PlotConfigUtil
import org.jetbrains.letsPlot.core.spec.config.LayerConfig
import org.jetbrains.letsPlot.core.spec.config.PlotConfigTransforms

internal object PlotTilesConfig {
    fun createGeomTiles(
        layerConfigs: List<LayerConfig>,
        facets: PlotFacets,
        coordProvider: CoordProvider,
        commonScalesBeforeFacets: Map<Aes<*>, Scale>,
        mappersByAesNP: Map<Aes<*>, ScaleMapper<*>>, // all non-positional mappers
        theme: Theme,
        fontRegistry: FontFamilyRegistry
    ): PlotGeomTiles {
        require(hasLayers(layerConfigs)) { "No layers in plot" }

        val containsLiveMap = layerConfigs.any { it.geomProto.geomKind == GeomKind.LIVE_MAP }
        val scalesByLayerBeforeFacets = layerConfigs.map {
            PlotGeomTilesUtil.buildLayerScaleMap(it, commonScalesBeforeFacets)
        }

        return when {
            facets.isDefined && isFacettable(layerConfigs, facets) -> createFacetTiles(
                layerConfigs,
                facets,
                commonScalesBeforeFacets,
                scalesByLayerBeforeFacets,
                mappersByAesNP,
                coordProvider,
                containsLiveMap,
                theme,
                fontRegistry,
            )

            else -> createSingletonTiles(
                layerConfigs,
                commonScalesBeforeFacets,
                scalesByLayerBeforeFacets,
                mappersByAesNP,
                coordProvider,
                containsLiveMap,
                theme,
                fontRegistry,
            )
        }
    }

    private fun hasLayers(layerConfigs: List<LayerConfig>): Boolean {
        return layerConfigs.any { !it.isMarginal }
    }

    private fun isFacettable(layerConfigs: List<LayerConfig>, facets: PlotFacets): Boolean {
        return layerConfigs.any {
            facets.isFacettable(it.combinedData)
        }
    }

    private fun createSingletonTiles(
        layerConfigs: List<LayerConfig>,
        commonScalesBeforeFacets: Map<Aes<*>, Scale>,
        scalesByLayerBeforeFacets: List<Map<Aes<*>, Scale>>,
        mappersNP: Map<Aes<*>, ScaleMapper<*>>,
        coordProvider: CoordProvider,
        containsLiveMap: Boolean,
        theme: Theme,
        fontRegistry: FontFamilyRegistry
    ): SimplePlotGeomTiles {

        val geomInteractionByLayer = PlotGeomTilesUtil.geomInteractionByLayer(
            layerConfigs,
            scalesByLayerBeforeFacets,
            coordProvider,
            theme,
            containsLiveMap
        )

        val geomLayerBuildersByLayer = layerConfigs.mapIndexed { layerIndex, layerConfig ->
            PlotGeomTilesUtil.createLayerBuilder(
                layerConfig,
                fontRegistry,
                geomInteractionByLayer[layerIndex],
                theme
            )
        }

        val geomLayers: List<GeomLayer> = geomLayerBuildersByLayer.mapIndexed { layerIndex, layerBuilder ->
            layerBuilder.build(
                layerConfigs[layerIndex].combinedData,
                scalesByLayerBeforeFacets[layerIndex],
                mappersNP
            )
        }

        val defaultFormatters = createDefaultFormatters(layerConfigs)

        return SimplePlotGeomTiles(
            geomLayers,
            commonScalesBeforeFacets,
            defaultFormatters,
            mappersNP,
            coordProvider,
            containsLiveMap
        )
    }

    private fun createDefaultFormatters(layerConfigs: List<LayerConfig>): Map<Pair<Aes<*>?, String?>, (Any) -> String> {
        fun key(varName: String) = Pair(null, varName)
        fun key(aes: Aes<*>) = Pair(aes, null)

        val variableDefaultFormatters = mutableMapOf<Pair<Aes<*>?, String?>, (Any) -> String>()

        layerConfigs
            .flatMap { it.dtypes.entries }
            .forEach { (varName, dtype) -> variableDefaultFormatters[key(varName)] = dtype.formatter }

        Stats.VARS.keys
            .forEach { statVarName -> variableDefaultFormatters[key(statVarName)] = DataType.FLOATING.formatter }

        val aesDefaultFormatters = layerConfigs
            .flatMap(LayerConfig::varBindings)
            .associate { binding ->
                val formatter = variableDefaultFormatters[key(binding.variable.name)]
                    ?: when {
                        binding.variable.isStat || binding.variable.isTransform -> DataType.FLOATING.formatter
                        else -> DataType.STRING.formatter
                    }

                key(binding.aes) to formatter
            }

        val defaultFormatters = variableDefaultFormatters + aesDefaultFormatters
        return defaultFormatters
    }

    private fun createFacetTiles(
        layerConfigs: List<LayerConfig>,
        facets: PlotFacets,
        commonScalesBeforeFacets: Map<Aes<*>, Scale>,
        scalesByLayerBeforeFacets: List<Map<Aes<*>, Scale>>,
        mappersByAesNP: Map<Aes<*>, ScaleMapper<*>>,
        coordProvider: CoordProvider,
        containsLiveMap: Boolean,
        theme: Theme,
        fontRegistry: FontFamilyRegistry
    ): FacetedPlotGeomTiles {


        val geomInteractionByLayer = PlotGeomTilesUtil.geomInteractionByLayer(
            layerConfigs,
            scalesByLayerBeforeFacets,
            coordProvider,
            theme,
            containsLiveMap
        )

        val geomLayerBuildersByLayer = layerConfigs.mapIndexed { layerIndex, layerConfig ->
            PlotGeomTilesUtil.createLayerBuilder(
                layerConfig,
                fontRegistry,
                geomInteractionByLayer[layerIndex],
                theme
            )
        }

        // Data by facet (tile).
        val dataByLayerByTile: List<MutableList<DataFrame>> = List(facets.numTiles) { ArrayList<DataFrame>() }
        layerConfigs.map { it.combinedData }.forEach() { layerData ->
            val layerDataByTile = PlotConfigUtil.splitLayerDataByTile(layerData, facets)
            layerDataByTile.forEachIndexed { tileIndex, data ->
                dataByLayerByTile[tileIndex].add(data)
            }
        }

        // Different X/Y disctere transform by facet (tile) if needed.
        val (adjustedDiscreteXTransformByTile, adjustedDiscreteYTransformByTile) = run {
            val bindingsByLayer: List<List<VarBinding>> = layerConfigs.map { it.varBindings }
            adjustedDiscreteXYTransformByTile(
                commonScalesBeforeFacets,
                bindingsByLayer,
                dataByLayerByTile,
                facets
            )
        }

        // Create tiles
        val geomLayersByTile: MutableList<List<GeomLayer>> = mutableListOf()
        for ((tileIndex, tileDataByLayer: List<DataFrame>) in dataByLayerByTile.withIndex()) {
            val tileGeomLayers = tileDataByLayer.mapIndexed() { layerIndex, layerData ->
                val layerScalesBeforeFacets = scalesByLayerBeforeFacets[layerIndex]
                val tileLayerAdjustedDiscreteTransformX = adjustedDiscreteXTransformByTile?.get(tileIndex)
                val tileLayerAdjustedDiscreteTransformY = adjustedDiscreteYTransformByTile?.get(tileIndex)
                val tileLayerScales = withAdjustDiscreteScales(
                    layerScalesBeforeFacets,
                    tileLayerAdjustedDiscreteTransformX,
                    tileLayerAdjustedDiscreteTransformY
                )

                geomLayerBuildersByLayer[layerIndex].build(
                    layerData,
                    tileLayerScales,
                    mappersByAesNP,
                )
            }

            geomLayersByTile.add(tileGeomLayers)
        }

        return FacetedPlotGeomTiles(
            geomLayersByTile,
            commonScalesBeforeFacets,
            mappersByAesNP,
            createDefaultFormatters(layerConfigs),
            coordProvider,
            containsLiveMap
        )
    }

    private fun adjustedDiscreteXYTransformByTile(
        commonScalesBeforeFacets: Map<Aes<*>, Scale>,
        bindingsByLayer: List<List<VarBinding>>,
        dataByLayerByTile: List<List<DataFrame>>,
        facets: PlotFacets,
    ): Pair<List<DiscreteTransform>?, List<DiscreteTransform>?> {

        val discreteTransformXBeforeFactes = commonScalesBeforeFacets.getValue(Aes.X).transform.let {
            if (it is DiscreteTransform) it else null
        }
        val discreteTransformYBeforeFactes = commonScalesBeforeFacets.getValue(Aes.Y).transform.let {
            if (it is DiscreteTransform) it else null
        }

        val freeDiscreteTransformsX =
            facets.freeHScale && discreteTransformXBeforeFactes?.let { !it.hasDomainLimits() } ?: false
        val freeDiscreteTransformsY =
            facets.freeVScale && discreteTransformYBeforeFactes?.let { !it.hasDomainLimits() } ?: false

        return if (freeDiscreteTransformsX || freeDiscreteTransformsY) {
            val positionalDiscreteAesSet = commonScalesBeforeFacets.filterKeys {
                Aes.isPositionalXY(it)
            }.filterValues {
                it.transform is DiscreteTransform
            }.keys

            adjustedDiscreteXYTransformByTile(
                positionalDiscreteAesSet,
                bindingsByLayer,
                dataByLayerByTile,
                facets,
                discreteTransformXBeforeFactes,
                discreteTransformYBeforeFactes,
                freeDiscreteTransformsX,
                freeDiscreteTransformsY
            )
        } else {
            Pair(null, null)
        }

    }

    private fun adjustedDiscreteXYTransformByTile(
        positionalDiscreteAesSet: Set<Aes<*>>,
        bindingsByLayer: List<List<VarBinding>>,
        dataByLayerByTile: List<List<DataFrame>>,
        facets: PlotFacets,
        discreteTransformXBeforeFactes: DiscreteTransform?,
        discreteTransformYBeforeFactes: DiscreteTransform?,
        freeDiscreteTransformsX: Boolean,
        freeDiscreteTransformsY: Boolean
    ): Pair<List<DiscreteTransform>?, List<DiscreteTransform>?> {
        check(freeDiscreteTransformsX || freeDiscreteTransformsY)

        val discreteXDomainByTile = ArrayList<Collection<Any>>()
        val discreteYDomainByTile = ArrayList<Collection<Any>>()
        for (dataByTileLayer: List<DataFrame> in dataByLayerByTile) {
            val tileBindingSetup = PlotConfigUtil.createPlotAesBindingSetup(
                bindingsByLayer = bindingsByLayer,
                dataByLayer = dataByTileLayer,
                excludeStatVariables = false
            )

            val tileDiscreteDomainByPositionalAes = PlotConfigTransforms.discreteDomainByAes(
                discreteAesSet = positionalDiscreteAesSet,
                dataByVarBinding = tileBindingSetup.dataByVarBinding
            )

            if (freeDiscreteTransformsX) {
                val tileDiscreteDomainX = tileDiscreteDomainByPositionalAes.filterKeys {
                    Aes.isPositionalX(it)
                }.values.reduceOrNull { acc, elem -> acc.union(elem) } ?: emptyList()

                discreteXDomainByTile.add(tileDiscreteDomainX)
            }

            if (freeDiscreteTransformsY) {
                val tileDiscreteDomainY = tileDiscreteDomainByPositionalAes.filterKeys {
                    Aes.isPositionalY(it)
                }.values.reduceOrNull { acc, elem -> acc.union(elem) } ?: emptyList()

                discreteYDomainByTile.add(tileDiscreteDomainY)
            }
        }

        val adjustedDiscreteXTransformByTile =
            if (freeDiscreteTransformsX && discreteTransformXBeforeFactes != null) {
                val discreteXDomainBeforeFacets = discreteTransformXBeforeFactes.effectiveDomain
                val adjustedDiscreteXDomainByTilea = facets.adjustFreeDisctereHDomainsByTile(
                    discreteXDomainBeforeFacets,
                    discreteXDomainByTile
                )

                adjustedDiscreteXDomainByTilea.map { adjustedDomain ->
                    discreteTransformXBeforeFactes.withDomain(adjustedDomain)
                }
            } else {
                null
            }

        val adjustedDiscreteYTransformByTile =
            if (freeDiscreteTransformsY && discreteTransformYBeforeFactes != null) {
                val discreteYDomainBeforeFacets = discreteTransformYBeforeFactes.effectiveDomain
                val adjustedDiscreteYDomainByTilea = facets.adjustFreeDisctereVDomainsByTile(
                    discreteYDomainBeforeFacets,
                    discreteYDomainByTile
                )

                adjustedDiscreteYDomainByTilea.map { adjustedDomain ->
                    discreteTransformYBeforeFactes.withDomain(adjustedDomain)
                }
            } else {
                null
            }

        return Pair(
            adjustedDiscreteXTransformByTile,
            adjustedDiscreteYTransformByTile
        )
    }

    private fun withAdjustDiscreteScales(
        scalesBeforeFacets: Map<Aes<*>, Scale>,
        adjustedDiscreteTransformX: DiscreteTransform?,
        adjustedDiscreteTransformY: DiscreteTransform?,
    ): Map<Aes<*>, Scale> {
        val withAdjustedScalesX = adjustedDiscreteTransformX?.let { transformX ->
            scalesBeforeFacets.mapValues { (aes, scale) ->
                when (Aes.isPositionalX(aes)) {
                    true -> {
                        scale.with()
                            .discreteTransform(transformX)
                            .build()
                    }

                    else -> scale
                }
            }
        } ?: scalesBeforeFacets

        // also with adjusted y-scales
        return adjustedDiscreteTransformY?.let { transformY ->
            withAdjustedScalesX.mapValues { (aes, scale) ->
                when (Aes.isPositionalY(aes)) {
                    true -> {
                        scale.with()
                            .discreteTransform(transformY)
                            .build()
                    }

                    else -> scale
                }
            }
        } ?: withAdjustedScalesX
    }
}