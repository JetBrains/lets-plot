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
import org.jetbrains.letsPlot.core.plot.builder.assemble.tiles.GeomLayerInfo
import org.jetbrains.letsPlot.core.plot.builder.coord.CoordProvider
import org.jetbrains.letsPlot.core.spec.config.LayerConfig

abstract class PlotGeomTilesBase(
    scaleByAesBeforeFacets: Map<Aes<*>, Scale>,
    override val mappersNP: Map<Aes<*>, ScaleMapper<*>>,
    override val coordProvider: CoordProvider,
    override val containsLiveMap: Boolean
) : PlotGeomTiles {

    override val xyContinuousTransforms: Pair<Transform?, Transform?> = Pair(
        scaleByAesBeforeFacets.getValue(Aes.X).transform.let { if (it is ContinuousTransform) it else null },
        scaleByAesBeforeFacets.getValue(Aes.Y).transform.let { if (it is ContinuousTransform) it else null }
    )

    override fun coreLayersByTile(): List<List<GeomLayer>> {
        return layersByTile().map { layers ->
            layers.filterNot { it.isMarginal }
        }
    }

    override fun marginalLayersByTile(): List<List<GeomLayer>> {
        return layersByTile().map { layers ->
            layers.filter { it.isMarginal }.filterNot { it.isLiveMap }
        }
    }

    override fun coreLayerInfos(): List<GeomLayerInfo> {
        return coreLayersByTile()[0].map(::GeomLayerInfo)
    }

    companion object {
        fun create(
            layerConfigs: List<LayerConfig>,
            facets: PlotFacets,
            coordProvider: CoordProvider,
            scaleByAesBeforeFacets: Map<Aes<*>, Scale>,
            mappersByAesNP: Map<Aes<*>, ScaleMapper<*>>, // all non-positional mappers
            theme: Theme,
            fontRegistry: FontFamilyRegistry
        ): PlotGeomTilesBase {
            require(hasLayers(layerConfigs)) { "No layers in plot" }
            return when {
                facets.isDefined && isFacettable(layerConfigs, facets) -> FacetedPlotGeomTiles.create(
                    layerConfigs,
                    facets,
                    scaleByAesBeforeFacets,
                    mappersByAesNP,
                    coordProvider,
                    theme,
                    fontRegistry,
                )

                else -> SimplePlotGeomTiles.create(
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
    }
}