/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.vegalite

import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.util.PlotOptions
import org.jetbrains.letsPlot.core.spec.vegalite.Option.Encodings.Channels

internal class RuleMarkConverter private constructor(
    vegaSpec: Map<*, *>,
    plotOptions: PlotOptions
) : MarkConverterBase(vegaSpec, plotOptions) {

    companion object {
        fun process(spec: Map<*, *>, plotOptions: PlotOptions) {
            RuleMarkConverter(spec, plotOptions).process()
        }
    }

    private fun process() {
        plotOptions.appendLayer {
            data = Util.transformData(dataVegaSpec)
            mappings = Util.transformMappings(encodingVegaSpec)

            when {
                isVLine() -> geom = GeomKind.V_LINE
                isHLine() -> geom = GeomKind.H_LINE
                isSegment() -> geom = GeomKind.SEGMENT
            }
        }
    }

    private fun isVLine(): Boolean = (
            Channels.X in encodingVegaSpec.keys
            && Channels.X2 !in encodingVegaSpec.keys
            && Channels.Y !in encodingVegaSpec.keys
            && Channels.Y2 !in encodingVegaSpec.keys)

    private fun isHLine(): Boolean = (Channels.X !in encodingVegaSpec.keys
            && Channels.X2 !in encodingVegaSpec.keys
            && Channels.Y in encodingVegaSpec.keys
            && Channels.Y2 !in encodingVegaSpec.keys)

    private fun isSegment(): Boolean = (Channels.X in encodingVegaSpec.keys
            && Channels.X2 in encodingVegaSpec.keys
            && Channels.Y in encodingVegaSpec.keys
            && Channels.Y2 in encodingVegaSpec.keys)
}