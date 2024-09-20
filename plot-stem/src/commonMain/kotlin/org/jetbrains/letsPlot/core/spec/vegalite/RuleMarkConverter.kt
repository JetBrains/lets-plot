/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.vegalite

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.util.PlotOptions
import org.jetbrains.letsPlot.core.spec.getString
import org.jetbrains.letsPlot.core.spec.vegalite.Option.Encodings
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
            geom = when {
                isVLine() -> GeomKind.V_LINE
                isHLine() -> GeomKind.H_LINE
                isVSegment() -> GeomKind.SEGMENT
                isHSegment() -> GeomKind.SEGMENT
                else -> error("Rule mark can be used only for vertical or horizontal lines or segments.\nEncoding: $encodingVegaSpec")
            }

            val extraMappings = when {
                isHSegment() -> mapOf(Aes.YEND to encodingVegaSpec.getString(Channels.Y, Encodings.FIELD)!!)
                isVSegment() -> mapOf(Aes.XEND to encodingVegaSpec.getString(Channels.X, Encodings.FIELD)!!)
                else -> emptyMap()
            }

            mappings = when {
                isVLine() -> Util.transformMappings(encodingVegaSpec, mapOf(Channels.X to Aes.XINTERCEPT))
                isHLine() -> Util.transformMappings(encodingVegaSpec, mapOf(Channels.Y to Aes.YINTERCEPT))
                isHSegment() -> Util.transformMappings(encodingVegaSpec, mapOf(Channels.X2 to Aes.XEND))
                isVSegment() -> Util.transformMappings(encodingVegaSpec, mapOf(Channels.Y2 to Aes.YEND))
                else -> error("Rule mark can be used only for vertical or horizontal lines or segments")
            } + extraMappings
        }
    }

    private fun isVLine(): Boolean =
        Channels.X in encodingVegaSpec.keys
                && listOf(Channels.X2, Channels.Y, Channels.Y2).none(encodingVegaSpec::contains)

    private fun isHLine(): Boolean =
        Channels.Y in encodingVegaSpec.keys &&
                listOf(Channels.X, Channels.X2, Channels.Y2).none(encodingVegaSpec::contains)

    private fun isVSegment(): Boolean =
        listOf(Channels.X, Channels.Y, Channels.Y2).all(encodingVegaSpec::contains)
                && Channels.X2 !in encodingVegaSpec.keys

    private fun isHSegment(): Boolean =
        listOf(Channels.X, Channels.X2, Channels.Y).all(encodingVegaSpec::contains)
                && Channels.Y2 !in encodingVegaSpec
}