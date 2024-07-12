/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.back.transform.bistro

import org.jetbrains.letsPlot.core.spec.*
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.Option.Waterfall
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.WaterfallPlotOptionsBuilder
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.util.OptionsUtil
import org.jetbrains.letsPlot.core.spec.transform.SpecChange
import org.jetbrains.letsPlot.core.spec.transform.SpecChangeContext
import org.jetbrains.letsPlot.core.spec.transform.SpecSelector

class WaterfallPlotSpecChange : SpecChange {
    override fun apply(spec: MutableMap<String, Any>, ctx: SpecChangeContext) {
        val waterfallPlotSpec = buildWaterfallPlotSpec(spec)

        // Set layers
        spec[Option.Plot.LAYERS] = waterfallPlotSpec.get(Option.Plot.LAYERS) ?: error("Missing layers in waterfall plot")

        spec.remove("bistro")
    }

    private fun buildWaterfallPlotSpec(plotSpec: MutableMap<String, Any>): Map<String, Any> {
        val bistroSpec = plotSpec.getMap(Option.Plot.BISTRO) ?: error("'bistro' not found in PlotSpec")
        val waterfallPlotOptionsBuilder = WaterfallPlotOptionsBuilder(
            data = plotSpec.getMap(Option.PlotBase.DATA) ?: emptyMap<Any, Any>(),
            x = bistroSpec.getString(Waterfall.X),
            y = bistroSpec.getString(Waterfall.Y),
            color = bistroSpec.getString(Waterfall.COLOR),
            fill = bistroSpec.getString(Waterfall.FILL) ?: WaterfallPlotOptionsBuilder.FLOW_TYPE_COLOR_VALUE,
            size = bistroSpec.getDouble(Waterfall.SIZE) ?: WaterfallPlotOptionsBuilder.DEF_SIZE,
            calcTotal = bistroSpec.getBool(Waterfall.CALCULATE_TOTAL) ?: WaterfallPlotOptionsBuilder.DEF_CALC_TOTAL,
            sortedValue = bistroSpec.getBool(Waterfall.SORTED_VALUE) ?: WaterfallPlotOptionsBuilder.DEF_SORTED_VALUE,
            threshold = bistroSpec.getDouble(Waterfall.THRESHOLD),
            maxValues = bistroSpec.getInt(Waterfall.MAX_VALUES)
        )
        val waterfallPlotOptions = waterfallPlotOptionsBuilder.build()
        return OptionsUtil.toSpec(waterfallPlotOptions)
    }

    companion object {
        fun specSelector(): SpecSelector {
            return SpecSelector.root()
        }
    }
}