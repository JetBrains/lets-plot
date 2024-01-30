/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.assemble

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.commons.unsupported.UNSUPPORTED
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.builder.GeomLayer
import org.jetbrains.letsPlot.core.plot.builder.assemble.tiles.GeomLayerInfo
import org.jetbrains.letsPlot.core.plot.builder.coord.CoordProvider

object TestingPlotContext {

    fun create(layer: GeomLayer): PlotContext {
        return PlotAssemblerPlotContext(
            geomTiles = TestingPlotGeomTiles(listOf(layer)),
            scaleMap = layer.scaleMap,
            superscriptExponent = false
        )
    }

    fun create(
        layers: List<GeomLayer>,
        scaleMap: Map<Aes<*>, Scale>
    ): PlotContext {
        return PlotAssemblerPlotContext(
            geomTiles = TestingPlotGeomTiles(layers),
            scaleMap = scaleMap,
            superscriptExponent = false
        )
    }
}

private class TestingPlotGeomTiles(
    private val geomLayers: List<GeomLayer>
) : PlotGeomTiles {
    override val isSingleTile: Boolean
        get() = UNSUPPORTED("Not yet implemented")
    override val containsLiveMap: Boolean
        get() = UNSUPPORTED("Not yet implemented")
    override val xyContinuousTransforms: Pair<Transform?, Transform?>
        get() = UNSUPPORTED("Not yet implemented")
    override val mappersNP: Map<Aes<*>, ScaleMapper<*>>
        get() = UNSUPPORTED("Not yet implemented")
    override val coordProvider: CoordProvider
        get() = UNSUPPORTED("Not yet implemented")

    override fun layersByTile(): List<List<GeomLayer>> {
        return listOf(geomLayers)
    }

    override fun coreLayersByTile(): List<List<GeomLayer>> {
        UNSUPPORTED("Not yet implemented")
    }

    override fun marginalLayersByTile(): List<List<GeomLayer>> {
        UNSUPPORTED("Not yet implemented")
    }

    override fun scalesByTile(): List<Map<Aes<*>, Scale>> {
        UNSUPPORTED("Not yet implemented")
    }

    override fun overallXYContinuousDomains(): Pair<DoubleSpan?, DoubleSpan?> {
        UNSUPPORTED("Not yet implemented")
    }

    override fun coreLayerInfos(): List<GeomLayerInfo> {
        UNSUPPORTED("Not yet implemented")
    }

}