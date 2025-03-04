/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.vegalite

import org.jetbrains.letsPlot.commons.intern.json.JsonSupport
import org.jetbrains.letsPlot.core.spec.Option
import org.jetbrains.letsPlot.core.spec.getList
import org.jetbrains.letsPlot.core.spec.plotson.toJson
import org.jetbrains.letsPlot.core.spec.vegalite.VegaOption.Data
import org.jetbrains.letsPlot.core.spec.write

object VegaConfig {
    fun isVegaLiteSpec(opts: Map<String, Any>): Boolean {
        return VegaOption.DATA in opts && Option.Meta.KIND !in opts
    }

    fun toLetsPlotSpec(vegaSpec: MutableMap<String, Any?>): MutableMap<String, Any> {
        if (vegaSpec[VegaOption.LetsPlotExt.LOG_LETS_PLOT_SPEC] == true) {
            // deep copy data to avoid modification of the original data
            val specCopy = vegaSpec.let(JsonSupport::formatJson).let(JsonSupport::parseJson)

            val compactData = specCopy.getList(VegaOption.DATA, Data.VALUES)?.take(20) ?: emptyList()
            specCopy.write(VegaOption.DATA, Data.VALUES) { compactData }
            println(JsonSupport.formatJson(specCopy, pretty = true))
        }

        val plotOptions = VegaPlotConverter.convert(vegaSpec)
        val plotSpec = plotOptions.toJson()

        if (vegaSpec[VegaOption.LetsPlotExt.LOG_LETS_PLOT_SPEC] == true) {
            plotOptions.data = plotOptions.data?.mapValues { (_, values) -> values.take(5) }
            plotOptions.layerOptions?.forEach { layerOptions ->
                layerOptions.data = layerOptions.data?.mapValues { (_, values) -> values.take(5) }
            }
            println(JsonSupport.formatJson(plotOptions.toJson(), pretty = true))
        }

        return plotSpec
    }

    internal fun getPlotKind(opts: Map<*, *>): VegaPlotKind {
        return when {
            VegaOption.LAYER in opts -> VegaPlotKind.MULTI_LAYER
            VegaOption.MARK in opts -> VegaPlotKind.SINGLE_LAYER

            VegaOption.FACET in opts -> throw IllegalArgumentException("VegaLite: Facet is not supported")
            VegaOption.REPEAT in opts -> throw IllegalArgumentException("VegaLite: Repeat is not supported")
            VegaOption.VCONCAT in opts -> throw IllegalArgumentException("VegaLite: VConcat is not supported")
            VegaOption.HCONCAT in opts -> throw IllegalArgumentException("VegaLite: HConcat is not supported")
            VegaOption.CONCAT in opts -> throw IllegalArgumentException("VegaLite: Concat is not supported")

            VegaOption.CONFIG in opts -> throw IllegalArgumentException("VegaLite: Config is not supported")
            else -> throw IllegalArgumentException("VegaLite: Unknown plot kind. No 'mark' or 'layer' found.")
        }
    }
}
