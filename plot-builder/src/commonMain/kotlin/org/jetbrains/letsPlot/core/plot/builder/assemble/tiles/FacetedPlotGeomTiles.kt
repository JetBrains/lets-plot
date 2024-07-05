/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.assemble.tiles

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.Scale
import org.jetbrains.letsPlot.core.plot.base.ScaleMapper
import org.jetbrains.letsPlot.core.plot.builder.GeomLayer
import org.jetbrains.letsPlot.core.plot.builder.coord.CoordProvider

class FacetedPlotGeomTiles constructor(
    private val layersByTile: List<List<GeomLayer>>,
    scalesBeforeFacets: Map<Aes<*>, Scale>,
    override val mappersNP: Map<Aes<*>, ScaleMapper<*>>, // all non-positional mappers
    override val defaultFormatters: Map<Pair<Aes<*>?, String?>, (Any) -> String>,
    coordProvider: CoordProvider,
    containsLiveMap: Boolean
) : PlotGeomTilesBase(
    scalesBeforeFacets,
    defaultFormatters,
    coordProvider,
    containsLiveMap
) {
    override val isSingleTile: Boolean = false

    override fun layersByTile(): List<List<GeomLayer>> {
        return layersByTile
    }

    override fun scaleXByTile(): List<Scale> {
        return layersByTile.map { geomLayers ->
            geomLayers.first().scaleMap.getValue(Aes.X)
        }
    }

    override fun scaleYByTile(): List<Scale> {
        return layersByTile.map { geomLayers ->
            geomLayers.first().scaleMap.getValue(Aes.Y)
        }
    }

    override fun overallXYContinuousDomains(): Pair<DoubleSpan?, DoubleSpan?> {
        check(!containsLiveMap) { "Not applicable to LiveMap." }
        // Not implemented -:was not yet needed.
        return Pair(null, null)
    }
}