/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.front.tiles

import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.theme.FontFamilyRegistry
import org.jetbrains.letsPlot.core.plot.base.theme.Theme
import org.jetbrains.letsPlot.core.plot.builder.GeomLayer
import org.jetbrains.letsPlot.core.plot.builder.assemble.PlotFacets
import org.jetbrains.letsPlot.core.plot.builder.assemble.PlotGeomTiles
import org.jetbrains.letsPlot.core.plot.builder.assemble.tiles.FacetedPlotGeomTiles
import org.jetbrains.letsPlot.core.plot.builder.assemble.tiles.SimplePlotGeomTiles
import org.jetbrains.letsPlot.core.plot.builder.coord.CoordProvider
import org.jetbrains.letsPlot.core.spec.PlotConfigUtil
import org.jetbrains.letsPlot.core.spec.config.LayerConfig

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

        return SimplePlotGeomTiles(
            geomLayers,
            commonScalesBeforeFacets,
            mappersNP,
            coordProvider,
            containsLiveMap
        )
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

        ///////////////////////////////////
        // ToDo: scaleMap can be different for different tiles.
        // TMP: Just duplicate the code in SimplePlotGeomTilesBuilder
        ///////////////////////////////////


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

        ///////////////////////////////////
//        val freeDiscreteTransformsX =
//            facets.freeHScale && !commonScalesBeforeFacets.getValue(Aes.X).isContinuousDomain
//        val freeDiscreteTransformsY =
//            facets.freeVScale && !commonScalesBeforeFacets.getValue(Aes.Y).isContinuousDomain
//
//        val setup = if (freeDiscreteTransformsX || freeDiscreteTransformsY) {
//            PlotConfigUtil.createPlotAesBindingSetup(layerConfigs, false)
//        } else {
//            null
//        }

        val dataByLayerByTile: List<MutableList<DataFrame>> = List(facets.numTiles) { ArrayList<DataFrame>() }
        layerConfigs.map { it.combinedData }.forEach() { layerData ->
            val layerDataByTile = PlotConfigUtil.splitLayerDataByTile(layerData, facets)
            layerDataByTile.forEachIndexed { tileIndex, data ->
                dataByLayerByTile[tileIndex].add(data)
            }
        }


        // ToDo: discrete transforms X/Y by tile.


        // Create tiles
        val geomLayersByTile: MutableList<List<GeomLayer>> = mutableListOf()
        for (tileDataByLayer: List<DataFrame> in dataByLayerByTile) {
            val tileGeomLayers = tileDataByLayer.mapIndexed() { layerIndex, layerData ->
                geomLayerBuildersByLayer[layerIndex].build(
                    layerData,
                    scalesByLayerBeforeFacets[layerIndex],
                    mappersByAesNP,
                )
            }

            geomLayersByTile.add(tileGeomLayers)
        }

        return FacetedPlotGeomTiles(
            geomLayersByTile,
            commonScalesBeforeFacets,
            mappersByAesNP,
            coordProvider,
            containsLiveMap
        )
    }
}