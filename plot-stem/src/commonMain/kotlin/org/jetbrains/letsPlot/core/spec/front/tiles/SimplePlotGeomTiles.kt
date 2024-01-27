/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.front.tiles

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.theme.FontFamilyRegistry
import org.jetbrains.letsPlot.core.plot.base.theme.Theme
import org.jetbrains.letsPlot.core.plot.builder.GeomLayer
import org.jetbrains.letsPlot.core.plot.builder.assemble.PlotFacets
import org.jetbrains.letsPlot.core.plot.builder.assemble.PositionalScalesUtil
import org.jetbrains.letsPlot.core.plot.builder.coord.CoordProvider
import org.jetbrains.letsPlot.core.spec.config.LayerConfig

class SimplePlotGeomTiles private constructor(
    private val geomLayers: List<GeomLayer>,
    private val scaleByAes: Map<Aes<*>, Scale>,
    mappersNP: Map<Aes<*>, ScaleMapper<*>>, // all non-positional mappers
    coordProvider: CoordProvider,
    containsLiveMap: Boolean
) : PlotGeomTilesBase(
    scaleByAes,
    mappersNP,
    coordProvider,
    containsLiveMap
) {
    private val scaleXProto: Scale = scaleByAes.getValue(Aes.X)
    private val scaleYProto: Scale = scaleByAes.getValue(Aes.Y)

    override val isSingleTile: Boolean = true

    override fun layersByTile(): List<List<GeomLayer>> {
        return listOf(geomLayers)
    }

    override fun scalesByTile(): List<Map<Aes<*>, Scale>> {
        return listOf(scaleByAes)
    }

    override fun overallXYContinuousDomains(): Pair<DoubleSpan?, DoubleSpan?> {
        check(!containsLiveMap) { "Not applicable to LiveMap." }
        val xyTransformedDomains = PositionalScalesUtil.computePlotXYTransformedDomains(
            coreLayersByTile(),
            scaleXProto,
            scaleYProto,
            PlotFacets.UNDEFINED
        )
        val pair = xyTransformedDomains[0].let {
            val xTransform = scaleXProto.transform
            val yTransform = scaleYProto.transform
            Pair(
                if (xTransform is ContinuousTransform) xTransform.applyInverse(it.first) else null,
                if (yTransform is ContinuousTransform) yTransform.applyInverse(it.second) else null,
            )
        }
        return pair
    }

    companion object {
        fun create(
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

        fun demoAndTest(
            geomLayers: List<GeomLayer>,
            scaleByAes: Map<Aes<*>, Scale>,
            mappersNP: Map<Aes<*>, ScaleMapper<*>>,
            coordProvider: CoordProvider,
        ): SimplePlotGeomTiles {
            return SimplePlotGeomTiles(
                geomLayers,
                scaleByAes,
                mappersNP,
                coordProvider,
                containsLiveMap = false
            )
        }
    }
}