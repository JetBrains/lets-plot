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
import org.jetbrains.letsPlot.core.plot.builder.coord.CoordProvider
import org.jetbrains.letsPlot.core.spec.config.LayerConfig

internal class SimplePlotGeomTilesBuilder private constructor(
    private val geomLayers: List<GeomLayer>,
    private val scaleByAes: Map<Aes<*>, Scale>,
    coordProvider: CoordProvider,
    mappersByAesNP: Map<Aes<*>, ScaleMapper<*>>, // all non-positional mappers
    theme: Theme,
    fontRegistry: FontFamilyRegistry,
    isLiveMap: Boolean
) : PlotGeomTilesBuilder(
    coordProvider,
    mappersByAesNP,
    theme,
    fontRegistry,
    isLiveMap
) {

    override fun layersByTile(): List<List<GeomLayer>> {
        return listOf(geomLayers)
    }

    companion object {
        fun create(
            layerConfigs: List<LayerConfig>,
            scaleByAes: Map<Aes<*>, Scale>,
            coordProvider: CoordProvider,
            mappersByAesNP: Map<Aes<*>, ScaleMapper<*>>,
            theme: Theme,
            fontRegistry: FontFamilyRegistry
        ): SimplePlotGeomTilesBuilder {

            val scaleMapByLayer = layerConfigs.map {
                PlotGeomTilesUtil.buildLayerScaleMap(it, scaleByAes)
            }

            val isLiveMap = layerConfigs.any { it.geomProto.geomKind == GeomKind.LIVE_MAP }

            val geomInteractionByLayer = PlotGeomTilesUtil.geomInteractionByLayer(
                layerConfigs,
                scaleMapByLayer,
                coordProvider,
                theme,
                isLiveMap
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
                    mappersByAesNP
                )
            }

            return SimplePlotGeomTilesBuilder(
                geomLayers,
                scaleByAes,
                coordProvider,
                mappersByAesNP,
                theme,
                fontRegistry,
                isLiveMap
            )
        }
    }
}