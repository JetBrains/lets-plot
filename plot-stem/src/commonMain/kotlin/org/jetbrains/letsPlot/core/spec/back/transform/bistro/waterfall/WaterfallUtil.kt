/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall

import org.jetbrains.letsPlot.commons.intern.indicesOf
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.Option.Waterfall
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.data.DataFrameUtil
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.util.addRow
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.util.setColumn
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
        totalRowValues: (DataFrame.Variable) -> Any?
    ): DataFrame {
        // standardData always contains 'measure' column, even if it's not in the original data
        val standardData = if (measure == null) {
            val measures = List(originalDf.rowCount()) { Measure.RELATIVE.value }.let { measures ->
                if (calcTotal) measures + listOf(Measure.TOTAL.value) else measures
            }
            if (calcTotal) {
                originalDf.addRow(totalRowValues)
            } else {
                originalDf
            }.setColumn(DEF_MEASURE, measures)
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
        return standardData.setColumn(Waterfall.Var.MEASURE_GROUP, measureGroup)
    }

    fun calculateStat(
        originalDf: DataFrame,
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
        otherRowValues: (DataFrame.Variable) -> Any?
    ): DataFrame {
        val defaultTotalTitle = flowTypeTitles[FlowType.TOTAL]?.title
        val xVar = DataFrameUtil.findVariableOrFail(originalDf, x)
        val yVar = DataFrameUtil.findVariableOrFail(originalDf, y)
        val measureVar = DataFrameUtil.findVariableOrFail(originalDf, measure)

        val df = filterFinite(originalDf, xVar, yVar, measureVar)
            .let { sortData(it, yVar, measureVar, sortedValue) }
            .let { filterData(it, xVar, yVar, measureVar, threshold, maxValues, otherRowValues) }

        val measures = df[measureVar].map { it!!.toString() } // 'measure' is not null after filterFinite()

        val rawYs = df.getNumeric(yVar)
        if (rawYs.isEmpty()) {
            return emptyStat(originalDf.variables())
        }

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
                if (calcTotal(df, measureVar)) {
                    values.dropLast(1) + listOf(value)
                } else {
                    values
                }
            } else {
                emptyList()
            }
        }

        val totalTitle = extractTotalTitle(originalDf, x, calcTotal(df, measureVar), defaultTotalTitle)
        val xs = replaceLast(df[xVar].map { it.toString() }, totalTitle)
        val ys = replaceLast(rawYs, values.last() - initialY) // last() is safe here because values is not empty, see rawYs.isEmpty() above
        val labels = flowTypes.indices.map { i ->
            if (measures[i] != Measure.RELATIVE.value) {
                values[i]
            } else {
                ys[i]
            }
        }

        return df.builder()
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

    fun emptyStat(variables: Iterable<DataFrame.Variable>): DataFrame {
        val emptyDfBuilder = DataFrame.Builder()
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
        variables.forEach { variable ->
            emptyDfBuilder.put(variable, emptyList<Any?>())
        }
        return emptyDfBuilder.build()
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
        return df.setColumn(Waterfall.Var.Stat.RADIUS, radii)
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
                return df[xVar].lastOrNull()?.toString() ?: defaultTitle
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
        val xIndices = df[xVar].indicesOf { it != null }.toSet()
        val yIndices = df.getNumeric(yVar).indicesOf { SeriesUtil.isFinite(it) }.toSet()
        val measureIndices = df[measureVar].indicesOf { it != null }.toSet()
        val tail = if (calcTotal(df, measureVar)) {
            setOf(df.rowCount() - 1)
        } else {
            emptySet()
        }
        return df.slice(xIndices.intersect(yIndices).intersect(measureIndices).union(tail))
    }

    private fun sortData(df: DataFrame, yVar: DataFrame.Variable, measureVar: DataFrame.Variable, sortedValue: Boolean): DataFrame {
        if (!sortedValue || df.rowCount() == 0) return df
        val ys = if (calcTotal(df, measureVar)) {
            df.getNumeric(yVar).dropLast(1)
        } else {
            df.getNumeric(yVar)
        }
        val indices = ys
            .sortedIndicesDescending { (_, y) -> y?.absoluteValue ?: 0.0 }
            .let { if (calcTotal(df, measureVar)) it + listOf(df.rowCount() - 1) else it }
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
        val ys = if (calcTotal(df, measureVar)) {
            df.getNumeric(yVar).dropLast(1)
        } else {
            df.getNumeric(yVar)
        }
        val indices = when {
            threshold != null -> {
                ys.indicesOf { it != null && it.absoluteValue > threshold }
            }
            maxValues != null && 0 < maxValues && maxValues < ys.size -> {
                ys
                    .sortedIndicesDescending { (_, y) -> y?.absoluteValue ?: Double.MAX_VALUE }
                    .subList(0, maxValues)
                    .sorted()
            }
            else -> ys.indices
        }
        val withTotalIndices = if (calcTotal(df, measureVar)) indices + listOf(df.rowCount() - 1) else indices
        val otherValue = ys.withIndex().filter { it.index !in indices }.mapNotNull(IndexedValue<Double?>::value).sum()
        return if (otherValue.absoluteValue > 0.0) {
            val filteredDf = df.slice(withTotalIndices)
            val otherRowValues = { variable: DataFrame.Variable ->
                when (variable) {
                    xVar -> OTHER_NAME
                    yVar -> otherValue
                    measureVar -> Measure.RELATIVE.value
                    else -> newRowValues(variable)
                }
            }
            val position = if (calcTotal(df, measureVar)) filteredDf.rowCount() - 1 else null
            filteredDf.addRow(otherRowValues, position)
        } else {
            df.slice(withTotalIndices)
        }
    }

    private fun calcTotal(df: DataFrame, measureVar: DataFrame.Variable): Boolean {
        return df[measureVar].lastOrNull()?.toString() == Measure.TOTAL.value
    }

    private fun <T, R : Comparable<R>> Iterable<T>.sortedIndicesDescending(selector: (IndexedValue<T>) -> R?) =
        withIndex().sortedWith(compareByDescending(selector)).map(IndexedValue<T>::index)
}