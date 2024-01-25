/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.front.tiles

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.Scale
import org.jetbrains.letsPlot.core.plot.base.ScaleMapper
import org.jetbrains.letsPlot.core.plot.base.theme.FontFamilyRegistry
import org.jetbrains.letsPlot.core.plot.base.theme.Theme
import org.jetbrains.letsPlot.core.plot.builder.GeomLayer
import org.jetbrains.letsPlot.core.plot.builder.assemble.PlotFacets
import org.jetbrains.letsPlot.core.plot.builder.coord.CoordProvider
import org.jetbrains.letsPlot.core.spec.config.LayerConfig

abstract class PlotGeomTilesBuilder(
    private val coordProvider: CoordProvider,
    private val mappersByAesNP: Map<Aes<*>, ScaleMapper<*>>, // all non-positional mappers
    private val theme: Theme,
    private val fontRegistry: FontFamilyRegistry,
    private val isLiveMap: Boolean
) {

    abstract fun layersByTile(): List<List<GeomLayer>>

    companion object {
        fun create(
            layerConfigs: List<LayerConfig>,
            facets: PlotFacets,
            coordProvider: CoordProvider,
            scaleByAesBeforeFacets: Map<Aes<*>, Scale>,
            mappersByAesNP: Map<Aes<*>, ScaleMapper<*>>, // all non-positional mappers
            theme: Theme,
            fontRegistry: FontFamilyRegistry
        ): PlotGeomTilesBuilder {
            require(hasLayers(layerConfigs)) { "No layers in plot" }
            return when {
                facets.isDefined && isFacettable(layerConfigs, facets) -> FacetedPlotGeomTilesBuilder.create(
                    layerConfigs,
                    facets,
                    scaleByAesBeforeFacets,
                    coordProvider,
                    mappersByAesNP,
                    theme,
                    fontRegistry,
                )

                else -> SimplePlotGeomTilesBuilder.create(
                    layerConfigs,
                    scaleByAesBeforeFacets,
                    coordProvider,
                    mappersByAesNP,
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