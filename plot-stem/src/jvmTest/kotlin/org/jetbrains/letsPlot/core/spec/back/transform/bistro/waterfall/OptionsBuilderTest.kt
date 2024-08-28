/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.util.PlotOptions
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.Option.Waterfall
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.WaterfallPlotOptionsBuilder.Companion.DEF_CONNECTOR
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.WaterfallPlotOptionsBuilder.Companion.DEF_H_LINE
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.WaterfallPlotOptionsBuilder.Companion.DEF_LABEL
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.WaterfallPlotOptionsBuilder.Companion.DEF_LABEL_FORMAT
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class OptionsBuilderTest {
    @Test
    fun simple() {
        val xs = listOf("A", "B", "T1", "A", "B", "T2")
        val ys = listOf(2.0, -1.0, null, 2.0, -1.0, null)
        val measures = listOf("relative", "relative", "total", "relative", "relative", "total")
        val plotOptions = WaterfallPlotOptionsBuilder(
            data = mapOf("X" to xs, "Y" to ys, "M" to measures),
            x = "X",
            y = "Y",
            measure = "M",
            color = null,
            fill = null,
            size = null,
            alpha = null,
            lineType = null,
            width = 0.9,
            showLegend = false,
            relativeTooltipsOptions = null,
            absoluteTooltipsOptions = null,
            calcTotal = true,
            totalTitle = null,
            sortedValue = false,
            threshold = null,
            maxValues = null,
            hLineOptions = DEF_H_LINE,
            hLineOnTop = false,
            connectorOptions = DEF_CONNECTOR,
            labelOptions = DEF_LABEL,
            labelFormat = DEF_LABEL_FORMAT,
            group = null
        ).build()
        val (relativeStatData, absoluteStatData) = getStatData(plotOptions)
        checkData(
            mapOf(
                "X" to xs,
                "Y" to ys,
                "M" to measures,
                Waterfall.Var.Stat.X.name to listOf(0.0, 1.0, 2.0, 3.0, 4.0, 5.0),
                Waterfall.Var.Stat.XLAB.name to listOf("A", "B", "T1", "A", "B", "T2"),
                Waterfall.Var.Stat.YMIN.name to listOf(0.0, 1.0, 0.0, 1.0, 2.0, 0.0),
                Waterfall.Var.Stat.YMIDDLE.name to listOf(1.0, 1.5, 0.5, 2.0, 2.5, 1.0),
                Waterfall.Var.Stat.YMAX.name to listOf(2.0, 2.0, 1.0, 3.0, 3.0, 2.0),
                Waterfall.Var.Stat.MEASURE.name to listOf("relative", "relative", "total", "relative", "relative", "total"),
                Waterfall.Var.Stat.FLOW_TYPE.name to listOf("Increase", "Decrease", "Total", "Increase", "Decrease", "Total"),
                Waterfall.Var.Stat.INITIAL.name to listOf(0.0, 2.0, 0.0, 1.0, 3.0, 0.0),
                Waterfall.Var.Stat.VALUE.name to listOf(2.0, 1.0, 1.0, 3.0, 2.0, 2.0),
                Waterfall.Var.Stat.DIFFERENCE.name to listOf(2.0, -1.0, 1.0, 2.0, -1.0, 1.0),
                Waterfall.Var.Stat.LABEL.name to listOf(2.0, -1.0, 1.0, 2.0, -1.0, 2.0),
                Waterfall.Var.Stat.RADIUS.name to listOf(0.1, 0.1, 0.1, 0.1, 0.1, 0.0)
            ),
            relativeStatData,
            absoluteStatData
        )
        checkXScale(plotOptions, listOf(0.0, 1.0, 2.0, 3.0, 4.0, 5.0), listOf("A", "B", "T1", "A", "B", "T2"))
    }

    @Test
    fun grouping() {
        val xs = listOf("A", "B", "T1", "A", "B", "T2")
        val ys = listOf(2.0, -1.0, null, 2.0, -1.0, null)
        val measures = listOf("relative", "relative", "total", "relative", "relative", "total")
        val groups = listOf("a", "a", "a", "b", "b", "b")
        val plotOptions = WaterfallPlotOptionsBuilder(
            data = mapOf("X" to xs, "Y" to ys, "M" to measures, "G" to groups),
            x = "X",
            y = "Y",
            measure = "M",
            group = "G",
            color = null,
            fill = null,
            size = null,
            alpha = null,
            lineType = null,
            width = 0.9,
            showLegend = false,
            relativeTooltipsOptions = null,
            absoluteTooltipsOptions = null,
            calcTotal = true,
            totalTitle = null,
            sortedValue = false,
            threshold = null,
            maxValues = null,
            hLineOptions = DEF_H_LINE,
            hLineOnTop = false,
            connectorOptions = DEF_CONNECTOR,
            labelOptions = DEF_LABEL,
            labelFormat = DEF_LABEL_FORMAT
        ).build()
        val (relativeStatData, absoluteStatData) = getStatData(plotOptions)
        checkData(
            mapOf(
                "X" to xs,
                "Y" to ys,
                "M" to measures,
                "G" to groups,
                Waterfall.Var.Stat.X.name to listOf(0.0, 1.0, 2.0, 3.0, 4.0, 5.0),
                Waterfall.Var.Stat.XLAB.name to listOf("A", "B", "T1", "A", "B", "T2"),
                Waterfall.Var.Stat.YMIN.name to listOf(0.0, 1.0, 0.0, 0.0, 1.0, 0.0),
                Waterfall.Var.Stat.YMIDDLE.name to listOf(1.0, 1.5, 0.5, 1.0, 1.5, 0.5),
                Waterfall.Var.Stat.YMAX.name to listOf(2.0, 2.0, 1.0, 2.0, 2.0, 1.0),
                Waterfall.Var.Stat.MEASURE.name to listOf("relative", "relative", "total", "relative", "relative", "total"),
                Waterfall.Var.Stat.FLOW_TYPE.name to listOf("Increase", "Decrease", "Total", "Increase", "Decrease", "Total"),
                Waterfall.Var.Stat.INITIAL.name to listOf(0.0, 2.0, 0.0, 0.0, 2.0, 0.0),
                Waterfall.Var.Stat.VALUE.name to listOf(2.0, 1.0, 1.0, 2.0, 1.0, 1.0),
                Waterfall.Var.Stat.DIFFERENCE.name to listOf(2.0, -1.0, 1.0, 2.0, -1.0, 1.0),
                Waterfall.Var.Stat.LABEL.name to listOf(2.0, -1.0, 1.0, 2.0, -1.0, 1.0),
                Waterfall.Var.Stat.RADIUS.name to listOf(0.1, 0.1, 0.0, 0.1, 0.1, 0.0)
            ),
            relativeStatData,
            absoluteStatData
        )
        checkXScale(plotOptions, listOf(0.0, 1.0, 2.0, 3.0, 4.0, 5.0), listOf("A", "B", "T1", "A", "B", "T2"))
    }

    private fun getStatData(plotOptions: PlotOptions): Pair<Map<String, List<Any?>>, Map<String, List<Any?>>> {
        val boxOptions = plotOptions.layerOptions!!.filter { it.geom == GeomKind.CROSS_BAR }
        assert(2 == boxOptions.size) { "Expected 2 crossbar layers in waterfall plot, but got ${boxOptions.size}" }
        val (relativeStatData, absoluteStatData) = boxOptions.map { it.data }
        assertNotNull(relativeStatData) { "Crossbar data shouldn't be null" }
        assertNotNull(absoluteStatData) { "Crossbar data shouldn't be null" }
        return Pair(relativeStatData, absoluteStatData)
    }

    private fun checkData(
        expectedData: Map<String, List<Any?>>,
        relativeData: Map<String, List<Any?>>,
        absoluteData: Map<String, List<Any?>>
    ) {
        val expectedKeys = expectedData.keys.toSet()
        val relativeKeys = relativeData.keys.filter { it != Waterfall.Var.MEASURE_GROUP.name }.toSet()
        val absoluteKeys = absoluteData.keys.filter { it != Waterfall.Var.MEASURE_GROUP.name }.toSet()
        assert(expectedKeys == relativeKeys) { "Columns in expected and relative datasets should be equal, but $expectedKeys != $relativeKeys" }
        assert(expectedKeys == absoluteKeys) { "Columns in expected and absolute datasets should be equal, but $expectedKeys != $absoluteKeys" }
        val rowsCount = expectedData.getValue(Waterfall.Var.Stat.X.name).size
        for (i in 0 until rowsCount) {
            val measure = expectedData.getValue(Waterfall.Var.Stat.MEASURE.name)[i]
            for (column in expectedData.keys) {
                val expectedValue = expectedData.getValue(column)[i]
                val relativeValue = relativeData.getValue(column)[i]
                val absoluteValue = absoluteData.getValue(column)[i]
                when (column) {
                    Waterfall.Var.Stat.YMIN.name,
                    Waterfall.Var.Stat.YMIDDLE.name,
                    Waterfall.Var.Stat.YMAX.name -> {
                        if (measure == WaterfallPlotOptionsBuilder.Measure.RELATIVE.value) {
                            assert(absoluteValue == null) { "Relative values for $column in absolute data should be null" }
                            assertNotNull(relativeValue, "Relative values for $column in relative data shouldn't be null")
                            assert(expectedValue == relativeValue) { "Values for $column in relative data should be equal to expected values" }
                        } else {
                            assert(relativeValue == null) { "Absolute values for $column in relative data should be null" }
                            assertNotNull(absoluteValue, "Absolute values for $column in absolute data shouldn't be null")
                            assert(expectedValue == absoluteValue) { "Values for $column in absolute data should be equal to expected values" }
                        }
                    }
                    Waterfall.Var.Stat.X.name,
                    Waterfall.Var.Stat.XLAB.name,
                    Waterfall.Var.Stat.MEASURE.name,
                    Waterfall.Var.Stat.FLOW_TYPE.name,
                    Waterfall.Var.Stat.INITIAL.name,
                    Waterfall.Var.Stat.VALUE.name,
                    Waterfall.Var.Stat.DIFFERENCE.name,
                    Waterfall.Var.Stat.RADIUS.name,
                    Waterfall.Var.Stat.LABEL.name -> {
                        assertNotNull(relativeValue) { "Values for $column in relative data shouldn't be null" }
                        compareValues(expectedValue, relativeValue, column)
                        assertNotNull(absoluteValue) { "Values for $column in absolute data shouldn't be null" }
                        compareValues(expectedValue, absoluteValue, column)
                    }
                    else -> {
                        compareValues(expectedValue, relativeValue, column)
                        compareValues(expectedValue, absoluteValue, column)
                    }
                }
            }
        }
    }

    private fun compareValues(expected: Any?, actual: Any?, column: String) {
        when (column) {
            Waterfall.Var.Stat.RADIUS.name -> {
                assertEquals(expected as Double, actual as Double, 1e-16, "Values in $column should be equal, but $expected != $actual")
            }
            else -> {
                assert(expected == actual) { "Values in $column should be equal, but $expected != $actual" }
            }
        }
    }

    private fun checkXScale(plotOptions: PlotOptions, expectedBreaks: List<Double>, expectedLabels: List<String>) {
        val scaleXOptions = plotOptions.scaleOptions!!.first { it.aes == Aes.X }
        assert(expectedBreaks == scaleXOptions.breaks) { "Expected breaks for X scale: $expectedBreaks, but got ${scaleXOptions.breaks}" }
        assert(expectedLabels == scaleXOptions.labels) { "Expected labels for X scale: $expectedLabels, but got ${scaleXOptions.labels}" }
    }
}