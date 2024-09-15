/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.vegalite

import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.spec.StatKind
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.util.PlotOptions
import org.jetbrains.letsPlot.core.spec.getMap
import org.jetbrains.letsPlot.core.spec.vegalite.Option.Encodings

class BoxplotMarkTransform private constructor(
    val vegaSpec: Map<*, *>,
    val plotOptions: PlotOptions
) {
    private val markVegaSpec = Util.readMark(vegaSpec[Option.MARK]!!).second
    private val encodingVegaSpec = vegaSpec.getMap(Encodings.ENCODING)
    private val dataVegaSpec = vegaSpec.getMap(Option.DATA)

    companion object {
        fun process(spec: Map<*, *>, plotOptions: PlotOptions) {
            BoxplotMarkTransform(spec, plotOptions).process()
        }
    }

    private fun process() {
        plotOptions.appendLayer {
            geom = GeomKind.BOX_PLOT
            data = dataVegaSpec?.let(Util::transformData)
            mappings = encodingVegaSpec?.let { Util.transformMappings(it) }
        }

        plotOptions.appendLayer {
            geom = GeomKind.POINT
            data = dataVegaSpec?.let(Util::transformData)
            mappings = encodingVegaSpec?.let { Util.transformMappings(it) }
            stat = StatKind.BOXPLOT_OUTLIER.name.lowercase()
        }
    }
}
