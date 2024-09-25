/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.vegalite

import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.plot.base.render.point.NamedShape
import org.jetbrains.letsPlot.core.spec.plotson.PlotOptions

internal class CircleMarkConverter private constructor(
    vegaSpec: Map<*, *>,
    plotOptions: PlotOptions
) : MarkConverterBase(vegaSpec, plotOptions) {

    companion object {
        fun process(spec: Map<*, *>, plotOptions: PlotOptions) {
            CircleMarkConverter(spec, plotOptions).process()
        }
    }

    private fun process() {
        plotOptions.appendLayer {
            geom = GeomKind.POINT
            shape = NamedShape.SOLID_CIRCLE
            data = Util.transformData(dataVegaSpec)
            mappings = Util.transformMappings(encodingVegaSpec)
        }
    }
}