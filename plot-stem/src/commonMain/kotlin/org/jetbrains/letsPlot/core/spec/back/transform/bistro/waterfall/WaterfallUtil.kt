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
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.WaterfallPlotOptionsBuilder.FlowType
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.WaterfallPlotOptionsBuilder.Companion.OTHER_NAME
import kotlin.math.*

internal object WaterfallUtil {
    fun prepareData(
        data: Map<String, List<*>>,
        measure: String?,
        calcTotal: Boolean
    ): Map<String, List<*>> {
        val standardData = data.let { d ->
            if (measure == null) {
                val measures = List(d.values.firstOrNull()?.size ?: 0) { "relative" }.let { if (calcTotal) it + listOf("total") else it }
                d.toList().associate { (column, values) ->
                    column to if (calcTotal) {
                        values + listOf(null)
                    } else {
                        values
                    }
                } + mapOf("_measure_" to measures)
            } else {
                d
            }
        }
        val df = DataFrameUtil.fromMap(standardData)
        val measureVar = DataFrameUtil.findVariableOrFail(df, measure ?: "_measure_")
        val measures = df[measureVar].map { it?.toString() }
        val measureGroup = mutableListOf<Int>()
        var group = 0
        for (m in measures) {
            measureGroup.add(group)
            if (m == "total") {
                group += 1
            }
        }
        return standardData + mapOf(WaterfallBox.MEASURE_GROUP to measureGroup)
    }

    fun groupBy(
        data: Map<String, List<*>>,
        group: String?
    ): List<Map<String, List<*>>> {
        return if (group != null && group in data.keys) {
            val groupValues = data.getValue(group)
            val result = mutableListOf<Map<String, List<*>>>()
            for (g in groupValues.distinct()) {
                val indices = groupValues.withIndex().map { (i, v) -> Pair(i, v) }.filter { (_, v) -> v == g }.unzip().first
                result.add(data.entries.associate { (k, v) -> k to v.slice(indices) })
            }
            result
        } else {
            listOf(data)
        }
    }

    fun concat(datasets: List<Map<String, List<*>>>): Map<String, List<*>> {
        val keys = datasets.firstOrNull { data -> data.keys.any() }?.keys ?: return emptyBoxStat()
        return keys.associateWith { key ->
            datasets.map { data -> data[key] ?: emptyList<Any?>() }
                    .fold(emptyList<Any?>()) { result, values -> result + values }
        }
    }

    fun calculateBoxStat(
        data: Map<String, List<*>>,
        x: String,
        y: String,
        measure: String,
        sortedValue: Boolean,
        threshold: Double?,
        maxValues: Int?,
        initialX: Int,
        initialY: Double,
        base: Double,
        flowTypeTitles: Map<FlowType, FlowType.FlowTypeData>
    ): Map<String, List<*>> {
        val (xs, ys, measures) = extractSeries(data, x, y, measure)
            .let { sortSeries(it, sortedValue) }
            .let { filterSeries(it, threshold, maxValues) }
        if (xs.isEmpty()) {
            return emptyBoxStat()
        }

        val calcTotal = calcTotal(data, measure)
        val yPrev = ys.runningFold(initialY) { sum, value -> sum + value }.dropLast(1)
        val yNext = ys.runningFold(initialY) { sum, value -> sum + value }.drop(1)
        val (yMin, yMax) = (yPrev zip yNext).map { (x, y) -> Pair(min(x, y), max(x, y)) }.unzip()
        val flowType = ys.map { if (it >= 0) flowTypeTitles.getValue(FlowType.INCREASE).title else flowTypeTitles.getValue(FlowType.DECREASE).title }

        val calculateLast: (Any?) -> List<Any?> = { if (calcTotal && ys.isNotEmpty()) listOf(it) else emptyList() }
        val xsLast = calculateLast(extractTotalTitle(data, x, flowTypeTitles, calcTotal))
        val ysLast = calculateLast(yNext.last() - (base + initialY + ys.first()))
        val measuresLast = calculateLast("total")
        val yPrevLast = calculateLast(base + initialY + ys.first())
        val yNextLast = calculateLast(yNext.last())
        val yMinLast = calculateLast(min(yNext.last(), base))
        val yMaxLast = calculateLast(max(yNext.last(), base))
        val flowTypeLast = calculateLast(flowTypeTitles[FlowType.TOTAL]?.title)

        return mapOf(
            WaterfallBox.Var.X to (xs + xsLast).indices.map { (initialX + it).toDouble() }.toList(),
            WaterfallBox.Var.XLAB to xs + xsLast,
            WaterfallBox.Var.YMIN to yMin + yMinLast,
            WaterfallBox.Var.YMAX to yMax + yMaxLast,
            WaterfallBox.Var.MEASURE to measures + measuresLast,
            WaterfallBox.Var.FLOW_TYPE to flowType + flowTypeLast,
            WaterfallBox.Var.INITIAL to yPrev + yPrevLast,
            WaterfallBox.Var.CUMULATIVE_SUM to yNext + yNextLast,
            WaterfallBox.Var.DIFFERENCE to ys + ysLast,
        )
    }

    private fun emptyBoxStat(): Map<String, List<*>> {
        return mapOf(
            WaterfallBox.Var.X to emptyList(),
            WaterfallBox.Var.XLAB to emptyList(),
            WaterfallBox.Var.YMIN to emptyList<Double>(),
            WaterfallBox.Var.YMAX to emptyList<Double>(),
            WaterfallBox.Var.FLOW_TYPE to emptyList<String>(),
            WaterfallBox.Var.INITIAL to emptyList<Double>(),
            WaterfallBox.Var.CUMULATIVE_SUM to emptyList<Double>(),
            WaterfallBox.Var.DIFFERENCE to emptyList<Double>(),
        )
    }

    fun calculateConnectorStat(
        boxData: Map<String, List<*>>,
        radius: Double
    ): Map<String, List<*>> {
        val rs = boxData.getValue(WaterfallBox.Var.X).let { xs ->
            if (xs.isEmpty()) {
                emptyList()
            } else {
                List(xs.size - 1) { radius } + listOf(0.0)
            }
        }
        return mapOf(
            WaterfallConnector.Var.X to boxData.getValue(WaterfallBox.Var.X),
            WaterfallConnector.Var.Y to boxData.getValue(WaterfallBox.Var.CUMULATIVE_SUM),
            WaterfallConnector.Var.RADIUS to rs
        )
    }

    fun calculateLabelStat(
        boxData: Map<String, List<*>>,
        totalTitle: String?
    ): Map<String, List<*>> {
        @Suppress("UNCHECKED_CAST")
        val yMin = boxData.getValue(WaterfallBox.Var.YMIN) as List<Double>
        if (yMin.isEmpty()) {
            return mapOf(
                WaterfallLabel.Var.X to emptyList(),
                WaterfallLabel.Var.Y to emptyList<Double>(),
                WaterfallLabel.Var.LABEL to emptyList(),
                WaterfallLabel.Var.FLOW_TYPE to emptyList<String>()
            )
        }
        @Suppress("UNCHECKED_CAST")
        val yMax = boxData.getValue(WaterfallBox.Var.YMAX) as List<Double>
        val ys = (yMin zip yMax).map { (min, max) -> (min + max) / 2 }
        val dys = boxData.getValue(WaterfallBox.Var.DIFFERENCE)
        val cumulativeSum = boxData.getValue(WaterfallBox.Var.CUMULATIVE_SUM)
        val flowType = boxData.getValue(WaterfallBox.Var.FLOW_TYPE)
        val labels = flowType.indices.map { i ->
            if (totalTitle != null && flowType[i] == totalTitle) {
                cumulativeSum[i]
            } else {
                dys[i]
            }
        }
        return mapOf(
            WaterfallLabel.Var.X to boxData.getValue(WaterfallBox.Var.X),
            WaterfallLabel.Var.Y to ys,
            WaterfallLabel.Var.LABEL to labels,
            WaterfallLabel.Var.FLOW_TYPE to boxData.getValue(WaterfallBox.Var.FLOW_TYPE)
        )
    }

    private fun calcTotal(
        data: Map<String, List<*>>,
        measure: String
    ): Boolean {
        val df = DataFrameUtil.fromMap(data)
        val measureVar = DataFrameUtil.findVariableOrFail(df, measure)
        val measures = df[measureVar].map { it?.toString() }
        return measures.lastOrNull() == "total"
    }

    private fun extractTotalTitle(
        data: Map<String, List<*>>,
        x: String,
        flowTypeTitles: Map<FlowType, FlowType.FlowTypeData>,
        calcTotal: Boolean
    ): String? {
        return when (calcTotal) {
            true -> {
                val df = DataFrameUtil.fromMap(data)
                val xVar = DataFrameUtil.findVariableOrFail(df, x)
                return df[xVar].map { it?.toString() }.last() ?: flowTypeTitles[FlowType.TOTAL]?.title
            }
            false -> null
        }
    }

    private fun extractSeries(
        data: Map<String, List<*>>,
        x: String,
        y: String,
        measure: String
    ): Triple<List<Any>, List<Double>, List<String>> {
        val df = DataFrameUtil.fromMap(data)

        val measureVar = DataFrameUtil.findVariableOrFail(df, measure)
        val xVar = DataFrameUtil.findVariableOrFail(df, x)
        val yVar = DataFrameUtil.findVariableOrFail(df, y)

        val measures = df[measureVar].map { it?.toString() }
        val xs = df[xVar].map { it?.toString() }
        val ys = df.getNumeric(yVar)

        fun <T> dropLastFilter(s: List<T>): List<T> {
            if (measures.lastOrNull() == "total") {
                return s.dropLast(1)
            }
            return s
        }
        val (ms, ps) = (dropLastFilter(measures) zip (dropLastFilter(xs) zip dropLastFilter(ys)))
            .filter { (m, p) -> m != null && p.first != null && SeriesUtil.isFinite(p.second) }
            .map { (m, p) -> Pair(m!!, Pair(p.first!!, p.second!!)) }
            .unzip()
        val (newXs, newYs) = ps.unzip()
        return Triple(newXs, newYs, ms)
    }

    private fun sortSeries(
        series: Triple<List<Any>, List<Double>, List<String>>,
        sortedValue: Boolean
    ): Triple<List<Any>, List<Double>, List<String>> {
        if (!sortedValue) return series
        val (ys, ps) = (series.second zip (series.first zip series.third))
            .sortedByDescending { (y, _) -> y.absoluteValue }
            .unzip()
        val (xs, ms) = ps.unzip()
        return Triple(xs, ys, ms)
    }

    private fun filterSeries(
        series: Triple<List<Any>, List<Double>, List<String>>,
        threshold: Double?,
        maxValues: Int?
    ): Triple<List<Any>, List<Double>, List<String>> {
        return when {
            threshold != null -> {
                val otherValue = series.second.filter { it.absoluteValue < threshold }.sum()
                val (ys, ps) = (series.second zip (series.first zip series.third)).filter { (y, _) -> y.absoluteValue >= threshold }.unzip()
                val (xs, ms) = ps.unzip()
                val xsLast = if (otherValue.absoluteValue > 0) listOf(OTHER_NAME) else emptyList()
                val ysLast = if (otherValue.absoluteValue > 0) listOf(otherValue) else emptyList()
                val msLast = if (otherValue.absoluteValue > 0) listOf("relative") else emptyList()
                Triple(xs + xsLast, ys + ysLast, ms + msLast)
            }
            maxValues != null && maxValues > 0 -> {
                val indices = series.second.withIndex()
                    .sortedByDescending { (_, y) -> y.absoluteValue }
                    .map(IndexedValue<*>::index)
                    .subList(0, maxValues)
                    .sorted()
                val otherValue = series.second.withIndex().filter { it.index !in indices }.sumOf { it.value }
                val xs = series.first.slice(indices)
                val ys = series.second.slice(indices)
                val ms = series.third.slice(indices)
                val xsLast = if (otherValue.absoluteValue > 0) listOf(OTHER_NAME) else emptyList()
                val ysLast = if (otherValue.absoluteValue > 0) listOf(otherValue) else emptyList()
                val msLast = if (otherValue.absoluteValue > 0) listOf("relative") else emptyList()
                Triple(xs + xsLast, ys + ysLast, ms + msLast)
            }
            else -> series
        }
    }


}