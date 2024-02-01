/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.front.tiles

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.plot.base.Scale
import org.jetbrains.letsPlot.core.plot.base.ScaleMapper
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
        scaleByAesBeforeFacets: Map<Aes<*>, Scale>,
        mappersByAesNP: Map<Aes<*>, ScaleMapper<*>>, // all non-positional mappers
        theme: Theme,
        fontRegistry: FontFamilyRegistry
    ): PlotGeomTiles {
        require(hasLayers(layerConfigs)) { "No layers in plot" }
        return when {
            facets.isDefined && isFacettable(layerConfigs, facets) -> createFacetTiles(
                layerConfigs,
                facets,
                scaleByAesBeforeFacets,
                mappersByAesNP,
                coordProvider,
                theme,
                fontRegistry,
            )

            else -> createSingletonTiles(
                layerConfigs,
                scaleByAesBeforeFacets,
                mappersByAesNP,
                coordProvider,
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
        scaleByAes: Map<Aes<*>, Scale>,
        mappersNP: Map<Aes<*>, ScaleMapper<*>>,
        coordProvider: CoordProvider,
        theme: Theme,
        fontRegistry: FontFamilyRegistry
    ): SimplePlotGeomTiles {

        val scaleMapByLayer = layerConfigs.map {
            PlotGeomTilesUtil.buildLayerScaleMap(it, scaleByAes)
        }

        val containsLiveMap = layerConfigs.any { it.geomProto.geomKind == GeomKind.LIVE_MAP }

        val geomInteractionByLayer = PlotGeomTilesUtil.geomInteractionByLayer(
            layerConfigs,
            scaleMapByLayer,
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
                scaleMapByLayer[layerIndex],
                mappersNP
            )
        }

        return SimplePlotGeomTiles(
            geomLayers,
            scaleByAes,
            mappersNP,
            coordProvider,
            containsLiveMap
        )
    }

    private fun createFacetTiles(
        layerConfigs: List<LayerConfig>,
        facets: PlotFacets,
        scaleByAesBeforeFacets: Map<Aes<*>, Scale>,
        mappersByAesNP: Map<Aes<*>, ScaleMapper<*>>,
        coordProvider: CoordProvider,
        theme: Theme,
        fontRegistry: FontFamilyRegistry
    ): FacetedPlotGeomTiles {

        ///////////////////////////////////
        // ToDo: scaleMap can be different for different tiles.
        // TMP: Just duplicate the code in SimplePlotGeomTilesBuilder
        ///////////////////////////////////

        val scaleMapByLayer = layerConfigs.map {
            PlotGeomTilesUtil.buildLayerScaleMap(it, scaleByAesBeforeFacets)
        }

        val containsLiveMap = layerConfigs.any { it.geomProto.geomKind == GeomKind.LIVE_MAP }

        val geomInteractionByLayer = PlotGeomTilesUtil.geomInteractionByLayer(
            layerConfigs,
            scaleMapByLayer,
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

        // Create tiles
        val geomLayersByTile: MutableList<MutableList<GeomLayer>> = mutableListOf()
        for ((layerIndex, layerConfig) in layerConfigs.withIndex()) {
            //
            // Layer tiles
            //
            val layerData = layerConfig.combinedData
            val layerDataByTile = PlotConfigUtil.splitLayerDataByTile(layerData, facets)

            val geomLayerByTile = layerDataByTile.map { layerTileData ->
                geomLayerBuildersByLayer[layerIndex].build(
                    layerTileData,
                    scaleMapByLayer[layerIndex],
                    mappersByAesNP,
                )
            }

            //
            // Stack geom layers by tile.
            //
            if (geomLayersByTile.isEmpty()) {
                geomLayerByTile.forEach { _ -> geomLayersByTile.add(ArrayList<GeomLayer>()) }
            }
            for ((tileIndex, geomLayer) in geomLayerByTile.withIndex()) {
                val tileGeomLayers = geomLayersByTile[tileIndex]
                tileGeomLayers.add(geomLayer)
            }
        }

        return FacetedPlotGeomTiles(
            geomLayersByTile,
            scaleByAesBeforeFacets,
            mappersByAesNP,
            coordProvider,
            containsLiveMap
        )
    }
}