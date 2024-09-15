/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.vegalite

import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.util.PlotOptions
import org.jetbrains.letsPlot.core.spec.getMap
import org.jetbrains.letsPlot.core.spec.vegalite.Option.Encodings

internal class RuleMarkTransform private constructor(
    val vegaSpec: Map<*, *>,
    val plotOptions: PlotOptions
) {
    private val markVegaSpec =
        Util.readMark(vegaSpec[Option.MARK]!!).second // can't get into BarMarkTransform without MARK
    private val encodingVegaSpec = vegaSpec.getMap(Encodings.ENCODING)
    private val dataVegaSpec = vegaSpec.getMap(Option.DATA)

    companion object {
        fun process(spec: Map<*, *>, plotOptions: PlotOptions) {
            RuleMarkTransform(spec, plotOptions).process()
        }
    }

    private fun process() {
        if (processAsHLine()) return
        else if (processAsVLine()) return
        else (processAsSegment())
    }

    private fun processAsVLine(): Boolean {
        if (encodingVegaSpec != null
            && Encodings.Channels.X in encodingVegaSpec.keys
            && Encodings.Channels.X2 !in encodingVegaSpec.keys
            && Encodings.Channels.Y !in encodingVegaSpec.keys
            && Encodings.Channels.Y2 !in encodingVegaSpec.keys
        ) {
            plotOptions.appendLayer {
                geom = GeomKind.V_LINE
                data = dataVegaSpec?.let(Util::transformData)
                mappings = encodingVegaSpec.let(Util::transformMappings)
            }

            return true
        } else {
            return false
        }
    }

    private fun processAsHLine(): Boolean {
        if (encodingVegaSpec != null
            && Encodings.Channels.X !in encodingVegaSpec.keys
            && Encodings.Channels.X2 !in encodingVegaSpec.keys
            && Encodings.Channels.Y in encodingVegaSpec.keys
            && Encodings.Channels.Y2 !in encodingVegaSpec.keys
        ) {
            plotOptions.appendLayer {
                geom = GeomKind.H_LINE
                data = dataVegaSpec?.let(Util::transformData)
                mappings = encodingVegaSpec.let(Util::transformMappings)
            }

            return true
        } else {
            return false
        }
    }

    private fun processAsSegment() {
        if (encodingVegaSpec != null) {
            plotOptions.appendLayer {
                geom = GeomKind.SEGMENT
                data = dataVegaSpec?.let(Util::transformData)
                mappings = encodingVegaSpec.let(Util::transformMappings)
            }
        }
    }
}