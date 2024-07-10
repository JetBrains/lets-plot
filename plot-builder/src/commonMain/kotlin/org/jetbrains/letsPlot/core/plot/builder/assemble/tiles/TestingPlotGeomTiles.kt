/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.assemble.tiles

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.commons.unsupported.UNSUPPORTED
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.Scale
import org.jetbrains.letsPlot.core.plot.base.ScaleMapper
import org.jetbrains.letsPlot.core.plot.builder.GeomLayer
import org.jetbrains.letsPlot.core.plot.builder.coord.CoordProvider
import org.jetbrains.letsPlot.core.plot.builder.coord.CoordProviders

internal class TestingPlotGeomTiles(
    private val geomLayers: List<GeomLayer>,
    override val scalesBeforeFacets: Map<Aes<*>, Scale>,
    override val defaultFormatters: Map<Any, (Any) -> String> = emptyMap(),
    coordProvider: CoordProvider = CoordProviders.cartesian(),
    containsLiveMap: Boolean = false
) : PlotGeomTilesBase(
    scalesBeforeFacets,
    defaultFormatters,
    coordProvider,
    containsLiveMap
) {
    override val isSingleTile: Boolean = true
    override val mappersNP: Map<Aes<*>, ScaleMapper<*>>
        get() = UNSUPPORTED("Not yet implemented")

    override fun layersByTile(): List<List<GeomLayer>> {
        return listOf(geomLayers)
    }

    override fun scaleXByTile(): List<Scale> {
        return listOf(scalesBeforeFacets.getValue(Aes.X))
    }

    override fun scaleYByTile(): List<Scale> {
        return listOf(scalesBeforeFacets.getValue(Aes.Y))
    }

    override fun overallXYContinuousDomains(): Pair<DoubleSpan?, DoubleSpan?> {
        UNSUPPORTED("Not yet implemented")
    }
}