/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall

import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.Option.WaterfallBox
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil
import org.jetbrains.letsPlot.core.plot.base.data.DataFrameUtil
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.WaterfallPlotOptionsBuilder.FlowType
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.WaterfallPlotOptionsBuilder.Companion.OTHER_NAME
import kotlin.math.*

internal object WaterfallUtil {
    fun calculateBoxStat(
        data: Map<String, List<Any?>>,
        x: String,
        y: String,
        calcTotal: Boolean,
        sortedValue: Boolean,
        threshold: Double?,
        maxValues: Int?
    ): Map<String, List<Any?>> {
        val (xs, ys) = extractXYSeries(data, x, y)
            .let { sortXYSeries(it, sortedValue) }
            .let { filterXYSeries(it, threshold, maxValues) }
        val yPrev = ys.runningFold(0.0) { sum, value -> sum + value }.subList(0, ys.size)
        val yNext = ys.runningReduce { sum, value -> sum + value }
        val (yMin, yMax) = (yPrev zip yNext).map { Pair(min(it.first, it.second), max(it.first, it.second)) }.unzip()
        val flowType = ys.map { if (it >= 0) FlowType.INCREASE.toString() else FlowType.DECREASE.toString() }

        val calculateLast: (Any) -> List<Any> = { if (calcTotal && ys.isNotEmpty()) listOf(it) else emptyList() }
        val xsLast = calculateLast(FlowType.TOTAL.toString())
        val ysLast = calculateLast(yNext.last() - ys.first())
        val yPrevLast = calculateLast(ys.first())
        val yNextLast = calculateLast(yNext.last())
        val yMinLast = calculateLast(min(yNext.last(), 0.0))
        val yMaxLast = calculateLast(max(yNext.last(), 0.0))
        val flowTypeLast = calculateLast(FlowType.TOTAL.toString())

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

    private fun sortXYSeries(
        xySeries: Pair<List<Any>, List<Double>>,
        sortedValue: Boolean
    ): Pair<List<Any>, List<Double>> {
        if (!sortedValue) return xySeries
        return (xySeries.first zip xySeries.second)
            .sortedByDescending { it.second.absoluteValue }
            .unzip()
    }

    private fun filterXYSeries(
        xySeries: Pair<List<Any>, List<Double>>,
        threshold: Double?,
        maxValues: Int?
    ): Pair<List<Any>, List<Double>> {
        return when {
            threshold != null -> {
                val otherValue = xySeries.second.filter { it.absoluteValue < threshold }.sum()
                val (xs, ys) = (xySeries.first zip xySeries.second).filter { it.second.absoluteValue >= threshold }.unzip()
                val xsLast = if (otherValue.absoluteValue > 0) listOf(OTHER_NAME) else emptyList()
                val ysLast = if (otherValue.absoluteValue > 0) listOf(otherValue) else emptyList()
                Pair(xs + xsLast, ys + ysLast)
            }
            maxValues != null && maxValues > 0 -> {
                val indices = xySeries.second.withIndex().map { Pair(it.index, it.value) }.sortedByDescending { it.second.absoluteValue }.unzip().first.subList(0, maxValues)
                val otherValue = xySeries.second.withIndex().filter { it.index !in indices }.sumOf { it.value }
                val xs = xySeries.first.withIndex().filter { it.index in indices }.map(IndexedValue<Any>::value)
                val ys = xySeries.second.withIndex().filter { it.index in indices }.map(IndexedValue<Double>::value)
                val xsLast = if (otherValue.absoluteValue > 0) listOf(OTHER_NAME) else emptyList()
                val ysLast = if (otherValue.absoluteValue > 0) listOf(otherValue) else emptyList()
                Pair(xs + xsLast, ys + ysLast)
            }
            else -> xySeries
        }
    }


}