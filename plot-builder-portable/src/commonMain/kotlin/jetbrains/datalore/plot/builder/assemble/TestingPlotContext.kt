/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.assemble

import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.base.unsupported.UNSUPPORTED
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.PlotContext
import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.builder.GeomLayer

object TestingPlotContext {
    val DUMMY: PlotContext = DummyPlotContext()

    fun create(layer: GeomLayer): PlotContext {
        return PlotAssemblerPlotContext(
            layersByTile = listOf(listOf(layer)),
            scaleMap = layer.scaleMap
        )
    }

    fun create(layers: List<GeomLayer>, scaleMap: TypedScaleMap): PlotContext {
        val layersByTile = listOf(layers) // 1 tile, 2 layers.
        return PlotAssemblerPlotContext(
            layersByTile = layersByTile,
            scaleMap = scaleMap
        )
    }

    private class DummyPlotContext : PlotContext {
        override val layers: List<PlotContext.Layer>
            get() = UNSUPPORTED("Not yet implemented")

        override fun getScale(aes: Aes<*>): Scale<*> {
            UNSUPPORTED("Not yet implemented")
        }

        override fun overallTransformedDomain(aes: Aes<*>): DoubleSpan {
            UNSUPPORTED("Not yet implemented")
        }
    }
}