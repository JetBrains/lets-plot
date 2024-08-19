/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall

import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.Option.Waterfall
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.data.DataFrameUtil
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.Option.Waterfall.Var.DEF_MEASURE
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.WaterfallPlotOptionsBuilder.FlowType
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.WaterfallPlotOptionsBuilder.Companion.OTHER_NAME
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.WaterfallPlotOptionsBuilder.Measure
import kotlin.math.*

internal object WaterfallUtil {
    fun prepareData(
        originalDf: DataFrame,
        measure: String?,
        calcTotal: Boolean,
        newRowValues: (DataFrame.Variable) -> Any?
    ): DataFrame {
        // standardData always contains 'measure' column, even if it's not in the original data
        val standardData = if (measure == null) {
            val measures = List(originalDf.rowCount()) { Measure.RELATIVE.value }.let { measures ->
                if (calcTotal) measures + listOf(Measure.TOTAL.value) else measures
            }
            if (calcTotal) {
                DataUtil.addRow(originalDf, newRowValues)
            } else {
                originalDf
            }.let { df ->
                DataUtil.setColumn(df, DEF_MEASURE, measures)
            }
        } else {
            originalDf
        }
        // Form column with measure groups (in each group there is only one 'total' measure value)
        val measures = extractMeasures(standardData, measure ?: DEF_MEASURE.name)
        val measureGroup = mutableListOf<Int>()
        var group = 0
        for (measureValue in measures) {
            measureGroup.add(group)
            if (measureValue == Measure.TOTAL.value) {
                group += 1
            }
        }
        // As a result: original data + columns 'measure' and 'measure_group'
        return DataUtil.setColumn(standardData, Waterfall.Var.MEASURE_GROUP, measureGroup)
    }

    fun markSkipBoxes(data: Map<String, List<*>>, key: String, filter: (Any?) -> Boolean): Map<String, List<*>> {
        val indices = data.getValue(key).withIndex().map { (i, v) -> Pair(i, v) }.filter { (_, v) -> filter(v) }.unzip().first
        return data.entries.associate { (k, v) ->
            k to when (k) {
                Waterfall.Var.Stat.YMIN.name,
                Waterfall.Var.Stat.YMAX.name,
                Waterfall.Var.Stat.YMIDDLE.name -> v.mapIndexed { i, y -> if (i in indices) y else null }
                else -> v
            }
        }
    }

    fun calculateStat(
        rawDf: DataFrame,
        x: String,
        y: String,
        measure: String,
        sortedValue: Boolean,
        threshold: Double?,
        maxValues: Int?,
        initialX: Int,
        initialY: Double,
        base: Double,
        flowTypeTitles: Map<FlowType, FlowType.FlowTypeData>,
        newRowValues: (DataFrame.Variable) -> Any?
    ): DataFrame {
        val defaultTotalTitle = flowTypeTitles[FlowType.TOTAL]?.title
        val xVar = DataFrameUtil.findVariableOrFail(rawDf, x)
        val yVar = DataFrameUtil.findVariableOrFail(rawDf, y)
        val measureVar = DataFrameUtil.findVariableOrFail(rawDf, measure)

        val df = filterFinite(rawDf, xVar, yVar, measureVar)
            .let { sortData(it, yVar, sortedValue) }
            .let { filterData(it, xVar, yVar, measureVar, threshold, maxValues, newRowValues) }

        val measures = df[measureVar].map { it!!.toString() } // 'measure' is not null after filterFinite()

        val calcTotal = measures.lastOrNull() == Measure.TOTAL.value

        val rawYs = df.getNumeric(yVar)

        val initials = mutableListOf<Double>()
        val values = mutableListOf<Double>()
        val flowTypes = mutableListOf<String>()
        for (i in rawYs.indices) {
            val yPrev = when (Measure.byValue(measures[i])) {
                Measure.ABSOLUTE -> base
                Measure.RELATIVE -> values.lastOrNull() ?: initialY
                Measure.TOTAL -> base
            }
            val yNext = when (Measure.byValue(measures[i])) {
                Measure.ABSOLUTE -> rawYs[i]!! // Only for the 'total' measure y could be null
                Measure.RELATIVE -> yPrev + rawYs[i]!!
                Measure.TOTAL -> values.lastOrNull() ?: initialY
            }
            initials.add(yPrev)
            values.add(yNext)
            val flowType = when {
                measures[i] == Measure.ABSOLUTE.value -> flowTypeTitles.getValue(FlowType.ABSOLUTE).title
                measures[i] == Measure.TOTAL.value -> flowTypeTitles.getValue(FlowType.TOTAL).title
                yPrev <= yNext -> flowTypeTitles.getValue(FlowType.INCREASE).title
                else -> flowTypeTitles.getValue(FlowType.DECREASE).title
            }
            flowTypes.add(flowType)
        }

        fun <T> replaceLast(values: List<T>, value: T): List<T> {
            return if (measures.isNotEmpty()) {
                if (calcTotal) {
                    values.dropLast(1) + listOf(value)
                } else {
                    values
                }
            } else {
                emptyList()
            }
        }

        val totalTitle = extractTotalTitle(rawDf, x, calcTotal, defaultTotalTitle)
        val xs = replaceLast(df[xVar].map { it.toString() }, totalTitle)
        val ys = replaceLast(rawYs, values.last() - (base + initialY))
        val labels = flowTypes.indices.map { i ->
            if (measures[i] != Measure.RELATIVE.value) {
                values[i]
            } else {
                ys[i]
            }
        }

        val builder = DataFrame.Builder()
        df.variables().map { variable ->
            builder.put(variable, df[variable])
        }
        return builder
            .put(Waterfall.Var.Stat.X, xs.indices.map { (initialX + it).toDouble() }.toList())
            .put(Waterfall.Var.Stat.XLAB, xs)
            .put(Waterfall.Var.Stat.YMIN, (initials zip values).map { (initial, value) -> min(initial, value) })
            .put(Waterfall.Var.Stat.YMIDDLE, (initials zip values).map { (initial, value) -> (initial + value) / 2.0 })
            .put(Waterfall.Var.Stat.YMAX, (initials zip values).map { (initial, value) -> max(initial, value) })
            .put(Waterfall.Var.Stat.MEASURE, measures)
            .put(Waterfall.Var.Stat.FLOW_TYPE, flowTypes)
            .put(Waterfall.Var.Stat.INITIAL, initials)
            .put(Waterfall.Var.Stat.VALUE, values)
            .put(Waterfall.Var.Stat.DIFFERENCE, ys)
            .put(Waterfall.Var.Stat.LABEL, labels)
            .build()
    }

    fun emptyStat(): DataFrame {
        return DataFrame.Builder()
            .put(Waterfall.Var.Stat.X, emptyList<Double?>())
            .put(Waterfall.Var.Stat.XLAB, emptyList<String?>())
            .put(Waterfall.Var.Stat.YMIN, emptyList<Double>())
            .put(Waterfall.Var.Stat.YMIDDLE, emptyList<Double>())
            .put(Waterfall.Var.Stat.YMAX, emptyList<Double>())
            .put(Waterfall.Var.Stat.MEASURE, emptyList<String>())
            .put(Waterfall.Var.Stat.FLOW_TYPE, emptyList<String>())
            .put(Waterfall.Var.Stat.INITIAL, emptyList<Double>())
            .put(Waterfall.Var.Stat.VALUE, emptyList<Double>())
            .put(Waterfall.Var.Stat.DIFFERENCE, emptyList<Double?>())
            .put(Waterfall.Var.Stat.RADIUS, emptyList<Double>())
            .put(Waterfall.Var.Stat.LABEL, emptyList<Double?>())
            .build()
    }

    fun appendRadius(
        df: DataFrame,
        radius: Double
    ): DataFrame {
        val radii = df[Waterfall.Var.Stat.MEASURE].let { measures ->
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
        return DataUtil.setColumn(df, Waterfall.Var.Stat.RADIUS, radii)
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

    private fun filterFinite(
        df: DataFrame,
        xVar: DataFrame.Variable,
        yVar: DataFrame.Variable,
        measureVar: DataFrame.Variable
    ): DataFrame {
        fun <T> getIndices(values: List<T?>, filter: (T?) -> Boolean): Set<Int> {
            return values.withIndex().map { (i, v) -> Pair(i, v) }.filter { (_, v) -> filter(v) }.unzip().first.toSet()
        }
        val xIndices = getIndices(df[xVar]) { it != null }
        val yIndices = getIndices(df.getNumeric(yVar)) { SeriesUtil.isFinite(it) }
        val measureIndices = getIndices(df[measureVar]) { it != null }
        val tail = if (df[measureVar].lastOrNull() == Measure.TOTAL.value) {
            setOf(df.rowCount() - 1)
        } else {
            emptySet()
        }
        return df.slice(xIndices.intersect(yIndices).intersect(measureIndices).union(tail))
    }

    private fun sortData(df: DataFrame, yVar: DataFrame.Variable, sortedValue: Boolean): DataFrame {
        if (!sortedValue) return df
        val indices = df.getNumeric(yVar)
            .withIndex()
            .map { (i, v) -> Pair(i, v) }
            .sortedByDescending { (_, y) -> y?.absoluteValue ?: 0.0 }
            .unzip()
            .first
        return df.slice(indices)
    }

    private fun filterData(
        df: DataFrame,
        xVar: DataFrame.Variable,
        yVar: DataFrame.Variable,
        measureVar: DataFrame.Variable,
        threshold: Double?,
        maxValues: Int?,
        newRowValues: (DataFrame.Variable) -> Any?
    ): DataFrame {
        val calcTotal = df[measureVar].lastOrNull() == Measure.TOTAL.value
        val indexedYs = df.getNumeric(yVar)
            .withIndex()
            .map { (i, v) -> Pair(i, v) }
        val indices = when {
            threshold != null -> {
                indexedYs.filter { (_, y) -> y == null || y.absoluteValue > threshold }.unzip().first
            }
            maxValues != null && maxValues > 0 -> {
                val subListSize = if (calcTotal) maxValues + 1 else maxValues
                indexedYs
                    .sortedByDescending { (_, y) -> y?.absoluteValue ?: Double.MAX_VALUE }
                    .map(Pair<Int, Double?>::first)
                    .subList(0, subListSize)
                    .sorted()
            }
            else -> indexedYs.indices
        }
        val otherValue = indexedYs.filter { (i, _) -> i !in indices }.map { it.second }.filterNotNull().sum()
        return if (otherValue.absoluteValue > 0.0) {
            val filteredDf = df.slice(indices)
            val otherRowValues = { variable: DataFrame.Variable ->
                when (variable) {
                    xVar -> OTHER_NAME
                    yVar -> otherValue
                    measureVar -> Measure.RELATIVE.value
                    else -> newRowValues(variable)
                }
            }
            val position = if (calcTotal) filteredDf.rowCount() - 1 else null
            return DataUtil.addRow(filteredDf, otherRowValues, position)
        } else {
            df.slice(indices)
        }
    }
}