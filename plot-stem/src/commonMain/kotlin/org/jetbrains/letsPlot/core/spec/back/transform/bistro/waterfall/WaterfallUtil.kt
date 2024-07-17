/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall

import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.Option.WaterfallBox
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.Option.WaterfallConnector
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.Option.WaterfallLabel
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil
import org.jetbrains.letsPlot.core.plot.base.data.DataFrameUtil
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
        maxValues: Int?,
        initialY: Double,
        flowTypes: Map<String, WaterfallPlotOptionsBuilder.FlowType>
    ): Map<String, List<Any?>> {
        val (xs, ys) = extractXYSeries(data, x, y)
            .let { sortXYSeries(it, sortedValue) }
            .let { filterXYSeries(it, threshold, maxValues) }
        if (xs.isEmpty()) {
            return mapOf(
                WaterfallBox.Var.X to emptyList(),
                WaterfallBox.Var.YMIN to emptyList<Double>(),
                WaterfallBox.Var.YMAX to emptyList<Double>(),
                WaterfallBox.Var.FLOW_TYPE to emptyList<String>(),
                WaterfallBox.Var.INITIAL to emptyList<Double>(),
                WaterfallBox.Var.CUMULATIVE_SUM to emptyList<Double>(),
                WaterfallBox.Var.DIFFERENCE to emptyList<Double>(),
            )
        }
        val yPrev = ys.runningFold(initialY) { sum, value -> sum + value }.dropLast(1)
        val yNext = ys.runningFold(initialY) { sum, value -> sum + value }.drop(1)
        val (yMin, yMax) = (yPrev zip yNext).map { Pair(min(it.first, it.second), max(it.first, it.second)) }.unzip()
        val flowType = ys.map { if (it >= 0) flowTypes.getValue("increase").name else flowTypes.getValue("decrease").name }

        val calculateLast: (Any?) -> List<Any?> = { if (calcTotal && ys.isNotEmpty()) listOf(it) else emptyList() }
        val xsLast = calculateLast(flowTypes["total"]?.name)
        val ysLast = calculateLast(yNext.last() - (initialY + ys.first()))
        val yPrevLast = calculateLast(initialY + ys.first())
        val yNextLast = calculateLast(yNext.last())
        val yMinLast = calculateLast(min(yNext.last(), initialY))
        val yMaxLast = calculateLast(max(yNext.last(), initialY))
        val flowTypeLast = calculateLast(flowTypes["total"]?.name)

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

    fun calculateConnectorStat(
        boxData: Map<String, List<Any?>>
    ): Map<String, List<Any?>> {
        return mapOf(
            WaterfallConnector.Var.X to boxData.getValue(WaterfallBox.Var.X).dropLast(1),
            WaterfallConnector.Var.Y to boxData.getValue(WaterfallBox.Var.CUMULATIVE_SUM).dropLast(1),
        )
    }

    fun calculateLabelStat(
        boxData: Map<String, List<Any?>>,
        calcTotal: Boolean
    ): Map<String, List<Any?>> {
        val yMin = boxData.getValue(WaterfallBox.Var.YMIN) as List<Double>
        if (yMin.isEmpty()) {
            return mapOf(
                WaterfallLabel.Var.X to emptyList(),
                WaterfallLabel.Var.Y to emptyList<Double>(),
                WaterfallLabel.Var.LABEL to emptyList(),
                WaterfallLabel.Var.FLOW_TYPE to emptyList<String>()
            )
        }
        val yMax = boxData.getValue(WaterfallBox.Var.YMAX) as List<Double>
        val ys = (yMin zip yMax).map { (min, max) -> (min + max) / 2 }
        val dys = boxData.getValue(WaterfallBox.Var.DIFFERENCE)
        val labels = dys.dropLast(1) + listOf(
            if (calcTotal) {
                boxData.getValue(WaterfallBox.Var.CUMULATIVE_SUM).last()
            } else {
                dys.last()
            }
        )
        return mapOf(
            WaterfallLabel.Var.X to boxData.getValue(WaterfallBox.Var.X),
            WaterfallLabel.Var.Y to ys,
            WaterfallLabel.Var.LABEL to labels,
            WaterfallLabel.Var.FLOW_TYPE to boxData.getValue(WaterfallBox.Var.FLOW_TYPE)
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