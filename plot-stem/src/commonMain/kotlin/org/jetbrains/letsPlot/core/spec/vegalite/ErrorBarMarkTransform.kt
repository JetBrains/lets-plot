/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.vegalite

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.util.PlotOptions
import org.jetbrains.letsPlot.core.spec.getMap
import org.jetbrains.letsPlot.core.spec.vegalite.Option.Encodings

class ErrorBarMarkTransform private constructor(
    val vegaSpec: Map<*, *>,
    val plotOptions: PlotOptions
) {
    private val markVegaSpec =
        Util.readMark(vegaSpec[Option.MARK]!!).second // can't get into BarMarkTransform without MARK
    private val encodingVegaSpec = vegaSpec.getMap(Encodings.ENCODING)
    private val dataVegaSpec = vegaSpec.getMap(Option.DATA)

    companion object {
        fun process(spec: Map<*, *>, plotOptions: PlotOptions) {
            ErrorBarMarkTransform(spec, plotOptions).process()
        }
    }

    private fun process() {
        plotOptions.appendLayer {
            geom = GeomKind.ERROR_BAR
            data = dataVegaSpec?.let(Util::transformData)

            mappings = encodingVegaSpec
                ?.let {
                    val customChannelMappings = when (Util.iHorizontal(it)) {
                        true -> mapOf(
                            Encodings.Channels.X to Aes.XMIN,
                            Encodings.Channels.X2 to Aes.XMAX,
                            Encodings.Channels.Y2 to Aes.YMAX
                        )
                        false -> mapOf(
                            Encodings.Channels.Y to Aes.YMIN,
                            Encodings.Channels.Y2 to Aes.YMAX,
                            Encodings.Channels.X2 to Aes.XMAX
                        )
                    }

                    Util.transformMappings(it, customChannelMappings)
                }
        }
    }
}