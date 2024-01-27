/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.front.tiles

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.plot.base.Scale
import org.jetbrains.letsPlot.core.plot.base.ScaleMapper
import org.jetbrains.letsPlot.core.plot.base.theme.FontFamilyRegistry
import org.jetbrains.letsPlot.core.plot.base.theme.Theme
import org.jetbrains.letsPlot.core.plot.builder.GeomLayer
import org.jetbrains.letsPlot.core.plot.builder.assemble.PlotFacets
import org.jetbrains.letsPlot.core.plot.builder.coord.CoordProvider
import org.jetbrains.letsPlot.core.spec.PlotConfigUtil
import org.jetbrains.letsPlot.core.spec.config.LayerConfig

internal class FacetedPlotGeomTiles private constructor(
    private val layersByTile: List<List<GeomLayer>>,
    scaleByAesBeforeFacets: Map<Aes<*>, Scale>,
    mappersNP: Map<Aes<*>, ScaleMapper<*>>, // all non-positional mappers
    coordProvider: CoordProvider,
    containsLiveMap: Boolean
) : PlotGeomTilesBase(
    scaleByAesBeforeFacets,
    mappersNP,
    coordProvider,
    containsLiveMap
) {
    override val isSingleTile: Boolean = false

    private val scalesByTile: List<Map<Aes<*>, Scale>> = layersByTile.map {
        // ToDo: different set of scales for each tile
        scaleByAesBeforeFacets
    }

    override fun layersByTile(): List<List<GeomLayer>> {
        return layersByTile
    }

    override fun scalesByTile(): List<Map<Aes<*>, Scale>> {
        return scalesByTile
    }

    override fun overallXYContinuousDomains(): Pair<DoubleSpan?, DoubleSpan?> {
        check(!containsLiveMap) { "Not applicable to LiveMap." }
        // ToDo: implement if needed
        return Pair(null, null)
    }


    companion object {
        fun create(
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
}