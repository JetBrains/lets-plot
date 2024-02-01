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
    coordProvider: CoordProvider,
    containsLiveMap: Boolean
) : PlotGeomTilesBase(
    scalesBeforeFacets,
    coordProvider,
    containsLiveMap
) {
    override val isSingleTile: Boolean = false

    private val scalesByTile: List<Map<Aes<*>, Scale>> = layersByTile.map {
        // ToDo: different set of scales for each tile
        scalesBeforeFacets
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
}