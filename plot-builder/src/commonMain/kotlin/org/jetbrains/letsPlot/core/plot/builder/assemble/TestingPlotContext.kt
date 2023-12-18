/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.assemble

import org.jetbrains.letsPlot.core.plot.base.PlotContext
import org.jetbrains.letsPlot.core.plot.base.Scale
import org.jetbrains.letsPlot.core.plot.builder.GeomLayer

object TestingPlotContext {

    fun create(layer: GeomLayer): PlotContext {
        return PlotAssemblerPlotContext(
            layersByTile = listOf(listOf(layer)),
            scaleMap = layer.scaleMap,
            superscriptExponent = false
        )
    }

    fun create(
        layers: List<GeomLayer>,
        scaleMap: Map<org.jetbrains.letsPlot.core.plot.base.Aes<*>, Scale>
    ): PlotContext {
        val layersByTile = listOf(layers) // 1 tile, 2 layers.
        return PlotAssemblerPlotContext(
            layersByTile = layersByTile,
            scaleMap = scaleMap,
            superscriptExponent = false
        )
    }
}