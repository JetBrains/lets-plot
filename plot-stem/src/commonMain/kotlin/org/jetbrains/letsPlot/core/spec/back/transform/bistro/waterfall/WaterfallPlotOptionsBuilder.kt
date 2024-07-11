/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall

import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.spec.Option
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.corr.DataUtil
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.Option.WaterfallBox
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.util.LayerOptions
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.util.PlotOptions
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.util.plot

class WaterfallPlotOptionsBuilder(
    private val data: Map<*, *>,
    private val x: String?,
    private val y: String?,
    private val calcTotal: Boolean,
    private val sortedValue: Boolean,
    private val threshold: Double?,
    private val maxValues: Int?
) {
    fun build(): PlotOptions {
        val boxLayerData = boxLayerData(data, x, y, calcTotal, sortedValue, threshold, maxValues)
        return plot {
            layerOptions = listOf(
                LayerOptions().apply {
                    geom = GeomKind.CROSS_BAR
                    this.data = boxLayerData
                    setParameter(Option.PlotBase.MAPPING, getBoxMappings())
                },
            )
        }
    }

    private fun boxLayerData(
        data: Map<*, *>,
        x: String?,
        y: String?,
        calcTotal: Boolean,
        sortedValue: Boolean,
        threshold: Double?,
        maxValues: Int?
    ): Map<String, List<Any?>> {
        val xVar = x ?: error("Parameter x should be specified")
        val yVar = y ?: error("Parameter y should be specified")
        return WaterfallUtil.calculateBoxStat(
            DataUtil.standardiseData(data),
            x = xVar,
            y = yVar,
            calcTotal = calcTotal,
            sortedValue = sortedValue,
            threshold = threshold,
            maxValues = maxValues
        )
    }

    private fun getBoxMappings(): HashMap<String, String> {
        return hashMapOf(
            WaterfallBox.Aes.X to WaterfallBox.Var.X,
            WaterfallBox.Aes.YMIN to WaterfallBox.Var.YMIN,
            WaterfallBox.Aes.YMAX to WaterfallBox.Var.YMAX,
            WaterfallBox.Aes.FILL to WaterfallBox.Var.FLOW_TYPE
        )
    }

    companion object {
        const val DEF_CALC_TOTAL: Boolean = true
        const val DEF_SORTED_VALUE: Boolean = false
    }
}