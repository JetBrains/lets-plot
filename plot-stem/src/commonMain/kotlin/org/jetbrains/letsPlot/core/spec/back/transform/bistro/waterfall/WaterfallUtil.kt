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
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.Option.WaterfallBox.DEF_MEASURE
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.WaterfallPlotOptionsBuilder.FlowType
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.WaterfallPlotOptionsBuilder.Companion.OTHER_NAME
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.WaterfallPlotOptionsBuilder.Measure
import kotlin.math.*

internal object WaterfallUtil {
    fun prepareData(
        originalData: Map<String, List<*>>,
        measure: String?,
        calcTotal: Boolean
    ): Map<String, List<*>> {
        // standardData always contains 'measure' column, even if it's not in the original data
        val standardData = if (measure == null) {
            val datasetSize = originalData.values.firstOrNull()?.size ?: 0
            val measures = List(datasetSize) { Measure.RELATIVE.value }.let { measures ->
                if (calcTotal) measures + listOf(Measure.TOTAL.value) else measures
            }
            originalData.toList().associate { (columnName, columnValues) ->
                columnName to if (calcTotal) {
                    columnValues + listOf(null)
                } else {
                    columnValues
                }
            } + mapOf(DEF_MEASURE to measures)
        } else {
            originalData
        }
        // Form column with measure groups (in each group there is only one 'total' measure value)
        val measures = extractMeasures(standardData, measure ?: DEF_MEASURE)
        val measureGroup = mutableListOf<Int>()
        var group = 0
        for (measureValue in measures) {
            measureGroup.add(group)
            if (measureValue == Measure.TOTAL.value) {
                group += 1
            }
        }
        // As a result: original data + columns 'measure' and 'measure_group'
        return standardData + mapOf(WaterfallBox.MEASURE_GROUP to measureGroup)
    }

    fun groupBy(
        data: Map<String, List<*>>,
        group: String?
    ): List<Map<String, List<*>>> {
        return if (group != null && group in data.keys) {
            val groupValues = data.getValue(group)
            val result = mutableListOf<Map<String, List<*>>>()
            for (groupValue in groupValues.distinct()) {
                val indices = groupValues.withIndex().map { (i, v) -> Pair(i, v) }.filter { (_, v) -> v == groupValue }.unzip().first
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

    fun markSkipBoxes(data: Map<String, List<*>>, key: String, filter: (Any?) -> Boolean): Map<String, List<*>> {
        val indices = data.getValue(key).withIndex().map { (i, v) -> Pair(i, v) }.filter { (_, v) -> filter(v) }.unzip().first
        return data.entries.associate { (k, v) ->
            k to when (k) {
                WaterfallBox.Var.YMIN,
                WaterfallBox.Var.YMAX -> v.mapIndexed { i, y -> if (i in indices) y else null }
                else -> v
            }
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
        val rawMeasures = extractMeasures(data, measure)
        val (xs, ys, measures) = extractSeries(data, x, y, rawMeasures)
            .let { sortSeries(it, sortedValue) }
            .let { filterSeries(it, threshold, maxValues) }
        if (xs.isEmpty()) {
            return emptyBoxStat()
        }

        val initials = mutableListOf<Double>()
        val values = mutableListOf<Double>()
        val yMins = mutableListOf<Double>()
        val yMaxs = mutableListOf<Double>()
        val flowTypes = mutableListOf<String>()
        for (i in ys.indices) {
            val yPrev = when (measures[i]) {
                Measure.RELATIVE.value -> values.lastOrNull() ?: initialY
                else -> initialY
            }
            val yNext = when (measures[i]) {
                Measure.RELATIVE.value -> yPrev + ys[i]
                else -> ys[i]
            }
            initials.add(yPrev)
            values.add(yNext)
            yMins.add(min(yPrev, yNext))
            yMaxs.add(max(yPrev, yNext))
            val flowType = when {
                measures[i] == Measure.ABSOLUTE.value -> flowTypeTitles.getValue(FlowType.ABSOLUTE).title
                yPrev <= yNext -> flowTypeTitles.getValue(FlowType.INCREASE).title
                else -> flowTypeTitles.getValue(FlowType.DECREASE).title
            }
            flowTypes.add(flowType)
        }

        val calcTotal = rawMeasures.lastOrNull() == Measure.TOTAL.value
        val defaultTotalTitle = flowTypeTitles[FlowType.TOTAL]?.title
        val calculateLast: (Any?) -> List<Any?> = { if (calcTotal && ys.isNotEmpty()) listOf(it) else emptyList() }
        val xsLast = calculateLast(extractTotalTitle(data, x, calcTotal, defaultTotalTitle))
        val ysLast = calculateLast(values.last() - (base + initialY))
        val measuresLast = calculateLast(Measure.TOTAL.value)
        val initialsLast = calculateLast(base + initialY)
        val valuesLast = calculateLast(values.last())
        val yMinsLast = calculateLast(min(values.last(), base))
        val yMaxsLast = calculateLast(max(values.last(), base))
        val flowTypesLast = calculateLast(defaultTotalTitle)

        return mapOf(
            WaterfallBox.Var.X to (xs + xsLast).indices.map { (initialX + it).toDouble() }.toList(),
            WaterfallBox.Var.XLAB to xs + xsLast,
            WaterfallBox.Var.YMIN to yMins + yMinsLast,
            WaterfallBox.Var.YMAX to yMaxs + yMaxsLast,
            WaterfallBox.Var.MEASURE to measures + measuresLast,
            WaterfallBox.Var.FLOW_TYPE to flowTypes + flowTypesLast,
            WaterfallBox.Var.INITIAL to initials + initialsLast,
            WaterfallBox.Var.VALUE to values + valuesLast,
            WaterfallBox.Var.DIFFERENCE to ys + ysLast,
        )
    }

    private fun emptyBoxStat(): Map<String, List<*>> {
        return mapOf(
            WaterfallBox.Var.X to emptyList(),
            WaterfallBox.Var.XLAB to emptyList(),
            WaterfallBox.Var.YMIN to emptyList<Double>(),
            WaterfallBox.Var.YMAX to emptyList<Double>(),
            WaterfallBox.Var.MEASURE to emptyList<String>(),
            WaterfallBox.Var.FLOW_TYPE to emptyList<String>(),
            WaterfallBox.Var.INITIAL to emptyList<Double>(),
            WaterfallBox.Var.VALUE to emptyList<Double>(),
            WaterfallBox.Var.DIFFERENCE to emptyList<Double>(),
        )
    }

    fun calculateConnectorStat(
        boxData: Map<String, List<*>>,
        radius: Double
    ): Map<String, List<*>> {
        val radii = boxData.getValue(WaterfallBox.Var.MEASURE).let { measures ->
            if (measures.isEmpty()) {
                emptyList()
            } else {
                measures.drop(1).map { measure ->
                    when (measure) {
                        Measure.ABSOLUTE.value -> 0.0
                        else -> radius
                    }
                } + listOf(0.0)
            }
        }
        return mapOf(
            WaterfallConnector.Var.X to boxData.getValue(WaterfallBox.Var.X),
            WaterfallConnector.Var.Y to boxData.getValue(WaterfallBox.Var.VALUE),
            WaterfallConnector.Var.RADIUS to radii
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
        val values = boxData.getValue(WaterfallBox.Var.VALUE)
        val flowType = boxData.getValue(WaterfallBox.Var.FLOW_TYPE)
        val labels = flowType.indices.map { i ->
            if (totalTitle != null && flowType[i] == totalTitle) {
                values[i]
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

    private fun extractTotalTitle(
        data: Map<String, List<*>>,
        x: String,
        calcTotal: Boolean,
        defaultTitle: String?
    ): String? {
        return when (calcTotal) {
            true -> {
                val df = DataFrameUtil.fromMap(data)
                val xVar = DataFrameUtil.findVariableOrFail(df, x)
                return df[xVar].map { it?.toString() }.last() ?: defaultTitle
            }
            false -> null
        }
    }

    private fun extractMeasures(
        data: Map<String, List<*>>,
        measure: String
    ): List<String?> {
        val df = DataFrameUtil.fromMap(data)
        val measureVar = DataFrameUtil.findVariableOrFail(df, measure)
        return df[measureVar].map { it?.toString() }
    }

    private fun extractSeries(
        data: Map<String, List<*>>,
        x: String,
        y: String,
        measures: List<String?>
    ): Triple<List<Any>, List<Double>, List<String>> {
        val df = DataFrameUtil.fromMap(data)

        val xVar = DataFrameUtil.findVariableOrFail(df, x)
        val yVar = DataFrameUtil.findVariableOrFail(df, y)

        val xs = df[xVar].map { it?.toString() }
        val ys = df.getNumeric(yVar)

        fun <T> dropLastFilter(s: List<T>): List<T> {
            if (measures.lastOrNull() == Measure.TOTAL.value) {
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
                val toExclude: (Double) -> Boolean = { it.absoluteValue <= threshold }
                val otherValue = series.second.filter { toExclude(it) }.sum()
                val (ys, ps) = (series.second zip (series.first zip series.third)).filter { (y, _) -> !toExclude(y) }.unzip()
                val (xs, ms) = ps.unzip()
                val xsLast = if (otherValue.absoluteValue > 0) listOf(OTHER_NAME) else emptyList()
                val ysLast = if (otherValue.absoluteValue > 0) listOf(otherValue) else emptyList()
                val msLast = if (otherValue.absoluteValue > 0) listOf(Measure.RELATIVE.value) else emptyList()
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
                val msLast = if (otherValue.absoluteValue > 0) listOf(Measure.RELATIVE.value) else emptyList()
                Triple(xs + xsLast, ys + ysLast, ms + msLast)
            }
            else -> series
        }
    }


}