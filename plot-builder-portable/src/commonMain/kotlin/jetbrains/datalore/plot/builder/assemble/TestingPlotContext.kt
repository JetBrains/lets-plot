/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.assemble

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.PlotContext
import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.builder.GeomLayer

object TestingPlotContext {

    fun create(layer: GeomLayer): PlotContext {
        return PlotAssemblerPlotContext(
            layersByTile = listOf(listOf(layer)),
            scaleMap = layer.scaleMap
        )
    }

    fun create(layers: List<GeomLayer>, scaleMap: Map<Aes<*>, Scale>): PlotContext {
        val layersByTile = listOf(layers) // 1 tile, 2 layers.
        return PlotAssemblerPlotContext(
            layersByTile = layersByTile,
            scaleMap = scaleMap
        )
    }
}