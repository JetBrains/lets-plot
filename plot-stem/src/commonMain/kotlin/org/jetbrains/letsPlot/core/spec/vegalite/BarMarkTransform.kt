/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.vegalite

import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.spec.asMapOfMaps
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.util.PlotOptions
import org.jetbrains.letsPlot.core.spec.getDouble
import org.jetbrains.letsPlot.core.spec.getMap
import org.jetbrains.letsPlot.core.spec.vegalite.Option.Encodings
import org.jetbrains.letsPlot.core.spec.vegalite.Option.Mark

internal class BarMarkTransform private constructor(
    val vegaSpec: Map<*, *>,
    val plotOptions: PlotOptions
) {
    private val markVegaSpec =
        Util.readMark(vegaSpec[Option.MARK]!!).second // can't get into BarMarkTransform without MARK
    private val encodingVegaSpec = vegaSpec.getMap(Encodings.ENCODING)?.asMapOfMaps()
    private val dataVegaSpec = vegaSpec.getMap(Option.DATA)

    companion object {
        fun process(spec: Map<*, *>, plotOptions: PlotOptions) {
            return BarMarkTransform(spec, plotOptions).process()
        }
    }

    private fun process() {
        if (processHistogram()) return

        plotOptions.appendLayer {
            geom = GeomKind.BAR
            data = dataVegaSpec?.let(Util::transformData)
            width = markVegaSpec.getDouble(Mark.WIDTH, Mark.Width.BAND)
            mappings = encodingVegaSpec?.let(Util::transformMappings)
        }
    }

    private fun processHistogram(): Boolean {
        if (encodingVegaSpec == null) return false
        if (encodingVegaSpec.values.none { Encodings.BIN in it }) return false

        plotOptions.appendLayer {
            geom = GeomKind.HISTOGRAM
            data = dataVegaSpec?.let(Util::transformData)
            mappings = encodingVegaSpec.let(Util::transformMappings)
        }

        return true
    }
}