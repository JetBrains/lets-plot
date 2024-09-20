/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.vegalite

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.util.PlotOptions
import org.jetbrains.letsPlot.core.spec.vegalite.Option.Encodings.Channels

internal class ErrorBandMarkConverter private constructor(
    vegaSpec: Map<*, *>,
    plotOptions: PlotOptions
) : MarkConverterBase(vegaSpec, plotOptions) {

    companion object {
        fun process(spec: Map<*, *>, plotOptions: PlotOptions) {
            ErrorBandMarkConverter(spec, plotOptions).process()
        }
    }

    private fun process() {
        plotOptions.appendLayer {
            geom = GeomKind.RIBBON
            data = Util.transformData(dataVegaSpec)

            mappings = when (Util.iHorizontal(encodingVegaSpec)) {
                true -> Util.transformMappings(
                    encodingVegaSpec,
                    Channels.X to Aes.XMIN,
                    Channels.X2 to Aes.XMAX,
                    Channels.Y2 to Aes.YMAX
                )

                false -> Util.transformMappings(
                    encodingVegaSpec,
                    Channels.Y to Aes.YMIN,
                    Channels.Y2 to Aes.YMAX,
                    Channels.X2 to Aes.XMAX
                )
            }

        }
    }
}
