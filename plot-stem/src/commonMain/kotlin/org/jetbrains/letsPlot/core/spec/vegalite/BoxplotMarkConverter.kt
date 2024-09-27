/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.vegalite

import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.spec.StatKind
import org.jetbrains.letsPlot.core.spec.plotson.PlotOptions

internal class BoxplotMarkConverter private constructor(
    vegaSpec: Map<*, *>,
    plotOptions: PlotOptions
) : MarkConverterBase(vegaSpec, plotOptions) {
    companion object {
        fun process(spec: Map<*, *>, plotOptions: PlotOptions) {
            BoxplotMarkConverter(spec, plotOptions).process()
        }
    }

    private fun process() {
        plotOptions.appendLayer {
            geom = GeomKind.BOX_PLOT
            initDataAndMappings()
        }

        plotOptions.appendLayer {
            geom = GeomKind.POINT
            stat = StatKind.BOXPLOT_OUTLIER.name.lowercase()
            initDataAndMappings()
        }
    }
}
