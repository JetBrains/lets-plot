/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.vegalite

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.spec.plotson.PlotOptions
import org.jetbrains.letsPlot.core.spec.vegalite.Option.Encodings.Channels

internal class TickMarkConverter private constructor(
    vegaSpec: Map<*, *>,
    plotOptions: PlotOptions
) : MarkConverterBase(vegaSpec, plotOptions) {
    companion object {
        fun process(spec: Map<*, *>, plotOptions: PlotOptions) {
            TickMarkConverter(spec, plotOptions).process()
        }
    }

    private fun process() {
        plotOptions.appendLayer {
            geom = GeomKind.CROSS_BAR
            size = 0.1 // thickness of the tick
            width = 0.6

            initDataAndMappings(
                customChannelMapping = listOf(
                    Channels.X to Aes.XMIN,
                    Channels.X to Aes.XMAX
                )
            )
        }
    }
}