/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.vegalite

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.util.PlotOptions
import org.jetbrains.letsPlot.core.spec.vegalite.Option.Encodings

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

            if (Encodings.Channels.X2 in encodingVegaSpec
                || Encodings.Channels.Y2 in encodingVegaSpec
            ) {
                geom = GeomKind.RECT
                mappings = Util.transformMappings(
                    encodingVegaSpec, customChannelMapping = mapOf(
                        Encodings.Channels.X to Aes.XMIN,
                        Encodings.Channels.Y to Aes.YMIN,
                        Encodings.Channels.X2 to Aes.XMAX,
                        Encodings.Channels.Y2 to Aes.YMAX
                    )
                )
            } else {
                geom = GeomKind.RASTER
                mappings = Util.transformMappings(encodingVegaSpec, customChannelMapping = mapOf(
                    Encodings.Channels.COLOR to Aes.FILL
                ))
            }
        }
    }
}