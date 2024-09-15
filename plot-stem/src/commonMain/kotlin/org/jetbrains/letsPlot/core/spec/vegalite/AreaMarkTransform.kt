/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.vegalite

import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.util.PlotOptions
import org.jetbrains.letsPlot.core.spec.getMap

internal class AreaMarkTransform private constructor(
    val vegaSpec: Map<*, *>,
    val plotOptions: PlotOptions
) {
    private val markVegaSpec = Util.readMark(vegaSpec[Option.MARK]!!).second // can't get into BarMarkTransform without MARK
    private val encodingVegaSpec = vegaSpec.getMap(Option.Encodings.ENCODING)
    private val dataVegaSpec = vegaSpec.getMap(Option.DATA)

    companion object {
        fun process(spec: Map<*, *>, plotOptions: PlotOptions) {
            AreaMarkTransform(spec, plotOptions).process()
        }
    }

    private fun process() {
        plotOptions.appendLayer {
            geom = GeomKind.AREA
            data = dataVegaSpec?.let(Util::transformData)
            mappings = encodingVegaSpec?.let(Util::transformMappings)
        }
    }
}