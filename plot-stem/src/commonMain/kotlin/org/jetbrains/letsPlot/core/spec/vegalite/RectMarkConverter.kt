/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.vegalite

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.spec.plotson.PlotOptions
import org.jetbrains.letsPlot.core.spec.vegalite.Option.Encodings.Channels

internal class RectMarkConverter private constructor(
    vegaSpec: Map<*, *>,
    plotOptions: PlotOptions
) : MarkConverterBase(vegaSpec, plotOptions) {

    companion object {
        fun process(spec: Map<*, *>, plotOptions: PlotOptions) {
            RectMarkConverter(spec, plotOptions).process()
        }
    }

    private fun process() {
        plotOptions.appendLayer {
            data = Util.transformData(dataVegaSpec)

            if (listOf(Channels.X2, Channels.Y2).any { it in encodingVegaSpec }) {
                geom = GeomKind.RECT
                mappings = Util.transformMappings(
                    encodingVegaSpec,
                        Channels.X to Aes.XMIN,
                        Channels.Y to Aes.YMIN,
                        Channels.X2 to Aes.XMAX,
                        Channels.Y2 to Aes.YMAX,
                )
            } else {
                geom = GeomKind.RASTER
                mappings = Util.transformMappings(encodingVegaSpec, Channels.COLOR to Aes.FILL)
            }
        }
    }
}