/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.vegalite

import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.util.PlotOptions
import org.jetbrains.letsPlot.core.spec.getDouble
import org.jetbrains.letsPlot.core.spec.vegalite.Option.Encodings
import org.jetbrains.letsPlot.core.spec.vegalite.Option.Mark

internal class BarMarkConverter private constructor(
    vegaSpec: Map<*, *>,
    plotOptions: PlotOptions
) : MarkConverterBase(vegaSpec, plotOptions) {
    companion object {
        fun process(spec: Map<*, *>, plotOptions: PlotOptions) {
            return BarMarkConverter(spec, plotOptions).process()
        }
    }

    private fun process() {
        plotOptions.appendLayer {
            data = Util.transformData(dataVegaSpec)
            mappings = Util.transformMappings(encodingVegaSpec)

            if (encodingVegaSpec.values.any { Encodings.BIN in it }) {
                geom = GeomKind.HISTOGRAM
            } else {
                geom = GeomKind.BAR
                width = markVegaSpec.getDouble(Mark.WIDTH, Mark.Width.BAND)
            }
        }
    }
}