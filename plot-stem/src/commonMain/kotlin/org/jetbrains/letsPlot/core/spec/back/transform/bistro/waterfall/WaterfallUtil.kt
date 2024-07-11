/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall

import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.Option.WaterfallBox
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil
import org.jetbrains.letsPlot.core.plot.base.data.DataFrameUtil
import kotlin.math.max
import kotlin.math.min

internal object WaterfallUtil {
    // TODO params: sorted_value, threshold, max_values
    fun computeBoxStat(
        data: Map<String, List<Any?>>,
        x: String,
        y: String,
        calcTotal: Boolean
    ): Map<String, List<Any?>> {
        val (xs, ys) = extractXYSeries(data, x, y)
        val yPrev = ys.runningFold(0.0) { sum, value -> sum + value }.subList(0, ys.size)
        val yNext = ys.runningReduce { sum, value -> sum + value }
        val (yMin, yMax) = (yPrev zip yNext).map { Pair(min(it.first, it.second), max(it.first, it.second)) }.unzip()
        val flowType = ys.map { if (it >= 0) FlowType.INCREASE.toString() else FlowType.DECREASE.toString() }

        val computeLast: (Any) -> List<Any> = { if (calcTotal && ys.isNotEmpty()) listOf(it) else emptyList() }
        val xsLast = computeLast(FlowType.TOTAL.toString())
        val ysLast = computeLast(yNext.last() - ys.first())
        val yPrevLast = computeLast(ys.first())
        val yNextLast = computeLast(yNext.last())
        val yMinLast = computeLast(min(yNext.last(), 0.0))
        val yMaxLast = computeLast(max(yNext.last(), 0.0))
        val flowTypeLast = computeLast(FlowType.TOTAL.toString())

        return mapOf(
            WaterfallBox.Var.X to xs + xsLast,
            WaterfallBox.Var.YMIN to yMin + yMinLast,
            WaterfallBox.Var.YMAX to yMax + yMaxLast,
            WaterfallBox.Var.FLOW_TYPE to flowType + flowTypeLast,
            WaterfallBox.Var.INITIAL to yPrev + yPrevLast,
            WaterfallBox.Var.CUMULATIVE_SUM to yNext + yNextLast,
            WaterfallBox.Var.DIFFERENCE to ys + ysLast,
        )
    }

    private fun extractXYSeries(
        data: Map<String, List<Any?>>,
        x: String,
        y: String
    ): Pair<List<Any>, List<Double>> {
        val df = DataFrameUtil.fromMap(data)
        val xVar = df.variables().firstOrNull { it.name == x } ?: error("There is no column '$x' in data")
        val yVar = df.variables().firstOrNull { it.name == y } ?: error("There is no column '$y' in data")
        val xs = df[xVar]
        require(xs.size == xs.distinct().size) { "All values in column '$x' should be distinct" }
        val ys = df.getNumeric(yVar)
        return (xs zip ys)
            .filter { it.first != null && SeriesUtil.isFinite(it.second) }
            .map { Pair(it.first!!, it.second!!) }
            .unzip()
    }

    private enum class FlowType {
        INCREASE,
        DECREASE,
        TOTAL;

        override fun toString(): String {
            return name.lowercase().replaceFirstChar(Char::titlecase)
        }
    }
}