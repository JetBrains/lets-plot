/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.assemble

import org.jetbrains.letsPlot.commons.formatting.number.NumberFormat.ExponentFormat
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.PlotContext
import org.jetbrains.letsPlot.core.plot.base.Scale
import org.jetbrains.letsPlot.core.plot.builder.GeomLayer
import org.jetbrains.letsPlot.core.plot.builder.assemble.tiles.TestingPlotGeomTiles

object TestingPlotContext {

    fun create(layer: GeomLayer): PlotContext {
        return create(
            listOf(layer),
            layer.scaleMap
        )
    }

    fun create(
        layers: List<GeomLayer>,
        scaleMap: Map<Aes<*>, Scale>
    ): PlotContext {
        return PlotAssemblerPlotContext(
            geomTiles = TestingPlotGeomTiles(layers, scaleMap),
            exponentFormat = ExponentFormat.E,
            minExponent = null,
            maxExponent = null
        )
    }
}
