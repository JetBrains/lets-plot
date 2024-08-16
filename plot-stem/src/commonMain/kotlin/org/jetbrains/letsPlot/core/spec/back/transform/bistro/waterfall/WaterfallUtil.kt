/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall

import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.Option.WaterfallBox
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.Option.WaterfallConnector
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.Option.WaterfallLabel
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.data.DataFrameUtil
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.Option.WaterfallBox.DEF_MEASURE_VAR_NAME
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.WaterfallPlotOptionsBuilder.FlowType
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.WaterfallPlotOptionsBuilder.Companion.OTHER_NAME
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.WaterfallPlotOptionsBuilder.Measure
import kotlin.math.*

internal object WaterfallUtil {
    fun prepareData(
        originalDf: DataFrame,
        measure: String?,
        calcTotal: Boolean
    ): DataFrame {
        // standardData always contains 'measure' column, even if it's not in the original data
        val standardData = if (measure == null) {
            val measures = List(originalDf.rowCount()) { Measure.RELATIVE.value }.let { measures ->
                if (calcTotal) measures + listOf(Measure.TOTAL.value) else measures
            }
            if (calcTotal) {
                DataUtil.addRow(originalDf) { null }
            } else {
                originalDf
            }.let { df ->
                DataUtil.addColumn(df, DataFrame.Variable(DEF_MEASURE_VAR_NAME), measures)
            }
        } else {
            originalDf
        }
        // Form column with measure groups (in each group there is only one 'total' measure value)
        val measures = extractMeasures(standardData, measure ?: DEF_MEASURE_VAR_NAME)
        val measureGroup = mutableListOf<Int>()
        var group = 0
        for (measureValue in measures) {
            measureGroup.add(group)
            if (measureValue == Measure.TOTAL.value) {
                group += 1
            }
        }
        // As a result: original data + columns 'measure' and 'measure_group'
        return DataUtil.addColumn(standardData, DataFrame.Variable(WaterfallBox.MEASURE_GROUP_VAR_NAME), measureGroup)
    }

    fun markSkipBoxes(data: Map<String, List<*>>, key: String, filter: (Any?) -> Boolean): Map<String, List<*>> {
        val indices = data.getValue(key).withIndex().map { (i, v) -> Pair(i, v) }.filter { (_, v) -> filter(v) }.unzip().first
        return data.entries.associate { (k, v) ->
            k to when (k) {
                WaterfallBox.Var.YMIN.name,
                WaterfallBox.Var.YMAX.name -> v.mapIndexed { i, y -> if (i in indices) y else null }
                else -> v
            }
        }
    }

    fun calculateBoxStat(
        df: DataFrame,
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
    ): DataFrame {
        val rawMeasures = extractMeasures(df, measure)
        val (xs, ys, measures) = extractSeries(df, x, y, rawMeasures)
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
        val xsLast = calculateLast(extractTotalTitle(df, x, calcTotal, defaultTotalTitle))
        val ysLast = calculateLast(values.last() - (base + initialY))
        val measuresLast = calculateLast(Measure.TOTAL.value)
        val initialsLast = calculateLast(base + initialY)
        val valuesLast = calculateLast(values.last())
        val yMinsLast = calculateLast(min(values.last(), base))
        val yMaxsLast = calculateLast(max(values.last(), base))
        val flowTypesLast = calculateLast(defaultTotalTitle)

        return DataFrame.Builder()
            .put(WaterfallBox.Var.X, (xs + xsLast).indices.map { (initialX + it).toDouble() }.toList())
            .put(WaterfallBox.Var.XLAB, xs + xsLast)
            .put(WaterfallBox.Var.YMIN, yMins + yMinsLast)
            .put(WaterfallBox.Var.YMAX, yMaxs + yMaxsLast)
            .put(WaterfallBox.Var.MEASURE, measures + measuresLast)
            .put(WaterfallBox.Var.FLOW_TYPE, flowTypes + flowTypesLast)
            .put(WaterfallBox.Var.INITIAL, initials + initialsLast)
            .put(WaterfallBox.Var.VALUE, values + valuesLast)
            .put(WaterfallBox.Var.DIFFERENCE, ys + ysLast)
            .build()
    }

    fun emptyBoxStat(): DataFrame {
        return DataFrame.Builder()
            .put(WaterfallBox.Var.X, emptyList<Any>())
            .put(WaterfallBox.Var.XLAB, emptyList<Any>())
            .put(WaterfallBox.Var.YMIN, emptyList<Double>())
            .put(WaterfallBox.Var.YMAX, emptyList<Double>())
            .put(WaterfallBox.Var.MEASURE, emptyList<String>())
            .put(WaterfallBox.Var.FLOW_TYPE, emptyList<String>())
            .put(WaterfallBox.Var.INITIAL, emptyList<Double>())
            .put(WaterfallBox.Var.VALUE, emptyList<Double>())
            .put(WaterfallBox.Var.DIFFERENCE, emptyList<Double>())
            .build()
    }

    fun calculateConnectorStat(
        boxData: DataFrame,
        radius: Double
    ): DataFrame {
        val radii = boxData[WaterfallBox.Var.MEASURE].let { measures ->
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
        return DataFrame.Builder()
            .put(WaterfallConnector.Var.X, boxData[WaterfallBox.Var.X])
            .put(WaterfallConnector.Var.Y, boxData[WaterfallBox.Var.VALUE])
            .put(WaterfallConnector.Var.RADIUS, radii)
            .build()
    }

    fun emptyConnectorStat(): DataFrame {
        return DataFrame.Builder()
            .put(WaterfallConnector.Var.X, emptyList<Any>())
            .put(WaterfallConnector.Var.Y, emptyList<Double>())
            .put(WaterfallConnector.Var.RADIUS, emptyList<String>())
            .build()
    }

    fun calculateLabelStat(
        boxData: DataFrame,
        totalTitle: String?
    ): DataFrame {
        @Suppress("UNCHECKED_CAST")
        val yMin = boxData[WaterfallBox.Var.YMIN] as List<Double>
        if (yMin.isEmpty()) {
            return emptyLabelStat()
        }
        @Suppress("UNCHECKED_CAST")
        val yMax = boxData[WaterfallBox.Var.YMAX] as List<Double>
        val ys = (yMin zip yMax).map { (min, max) -> (min + max) / 2 }
        val dys = boxData[WaterfallBox.Var.DIFFERENCE]
        val values = boxData[WaterfallBox.Var.VALUE]
        val flowType = boxData[WaterfallBox.Var.FLOW_TYPE]
        val labels = flowType.indices.map { i ->
            if (totalTitle != null && flowType[i] == totalTitle) {
                values[i]
            } else {
                dys[i]
            }
        }
        return DataFrame.Builder()
            .put(WaterfallLabel.Var.X, boxData[WaterfallBox.Var.X])
            .put(WaterfallLabel.Var.Y, ys)
            .put(WaterfallLabel.Var.LABEL, labels)
            .put(WaterfallLabel.Var.FLOW_TYPE, boxData[WaterfallBox.Var.FLOW_TYPE])
            .build()
    }

    fun emptyLabelStat(): DataFrame {
        return DataFrame.Builder()
            .put(WaterfallLabel.Var.X, emptyList<Any>())
            .put(WaterfallLabel.Var.Y, emptyList<Double>())
            .put(WaterfallLabel.Var.LABEL, emptyList<String>())
            .put(WaterfallLabel.Var.FLOW_TYPE, emptyList<String>())
            .build()
    }

    private fun extractTotalTitle(
        df: DataFrame,
        x: String,
        calcTotal: Boolean,
        defaultTitle: String?
    ): String? {
        return when (calcTotal) {
            true -> {
                val xVar = DataFrameUtil.findVariableOrFail(df, x)
                return df[xVar].map { it?.toString() }.last() ?: defaultTitle
            }
            false -> null
        }
    }

    private fun extractMeasures(
        df: DataFrame,
        measure: String
    ): List<String?> {
        val measureVar = DataFrameUtil.findVariableOrFail(df, measure)
        return df[measureVar].map { it?.toString() }
    }

    private fun extractSeries(
        df: DataFrame,
        x: String,
        y: String,
        measures: List<String?>
    ): Triple<List<Any>, List<Double>, List<String>> {
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