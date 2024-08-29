/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.util.LayerOptions
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.util.PlotOptions
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.util.TooltipsOptions
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.Option.Waterfall
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.WaterfallPlotOptionsBuilder.Companion.DEF_ABSOLUTE_TOOLTIPS
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.WaterfallPlotOptionsBuilder.Companion.DEF_WIDTH
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.WaterfallPlotOptionsBuilder.Companion.DEF_CONNECTOR
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.WaterfallPlotOptionsBuilder.Companion.DEF_H_LINE
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.WaterfallPlotOptionsBuilder.Companion.DEF_LABEL
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.WaterfallPlotOptionsBuilder.Companion.DEF_LABEL_FORMAT
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.WaterfallPlotOptionsBuilder.Companion.DEF_RELATIVE_TOOLTIPS
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.WaterfallPlotOptionsBuilder.ElementLineOptions
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.WaterfallPlotOptionsBuilder.ElementTextOptions
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class OptionsBuilderTest {
    @Test
    fun `simple without measure`() {
        val xs = listOf("A", "B", "C")
        val ys = listOf(2.0, -1.0, 2.0)
        val plotOptions = getPlotOptions(
            data = mapOf("X" to xs, "Y" to ys),
            x = "X",
            y = "Y"
        )
        val (relativeStatData, absoluteStatData) = getStatData(plotOptions)
        checkData(
            mapOf(
                "X" to xs + listOf(null),
                "Y" to ys + listOf(null),
                Waterfall.Var.Stat.X.name to listOf(0.0, 1.0, 2.0, 3.0),
                Waterfall.Var.Stat.XLAB.name to listOf("A", "B", "C", "Total"),
                Waterfall.Var.Stat.YMIN.name to listOf(0.0, 1.0, 1.0, 0.0),
                Waterfall.Var.Stat.YMIDDLE.name to listOf(1.0, 1.5, 2.0, 1.5),
                Waterfall.Var.Stat.YMAX.name to listOf(2.0, 2.0, 3.0, 3.0),
                Waterfall.Var.Stat.MEASURE.name to listOf("relative", "relative", "relative", "total"),
                Waterfall.Var.Stat.FLOW_TYPE.name to listOf("Increase", "Decrease", "Increase", "Total"),
                Waterfall.Var.Stat.INITIAL.name to listOf(0.0, 2.0, 1.0, 0.0),
                Waterfall.Var.Stat.VALUE.name to listOf(2.0, 1.0, 3.0, 3.0),
                Waterfall.Var.Stat.DIFFERENCE.name to listOf(2.0, -1.0, 2.0, 3.0),
                Waterfall.Var.Stat.LABEL.name to listOf(2.0, -1.0, 2.0, 3.0),
                Waterfall.Var.Stat.RADIUS.name to listOf(0.1, 0.1, 0.1, 0.0)
            ),
            relativeStatData,
            absoluteStatData
        )
        checkXScale(plotOptions, listOf(0.0, 1.0, 2.0, 3.0), listOf("A", "B", "C", "Total"))
    }

    @Test
    fun `simple with measure`() {
        val xs = listOf("A", "B", "T1", "A", "B", "T2")
        val ys = listOf(2.0, -1.0, 100.0, 2.0, -1.0, null) // 100.0 shouldn't be taken into account because of the measure
        val measures = listOf("relative", "relative", "total", "relative", "relative", "total")
        val plotOptions = getPlotOptions(
            data = mapOf("X" to xs, "Y" to ys, "M" to measures),
            x = "X",
            y = "Y",
            measure = "M"
        )
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
    fun `empty dataset`() {
        fun testEmptyData(measure: String?, group: String?) {
            val data = mutableMapOf<String, List<Any?>>("X" to listOf<String>(), "Y" to listOf<Double>())
            if (measure != null) data[measure] = listOf()
            if (group != null) data[group] = listOf()
            val expectedData = mutableMapOf<String, List<Any?>>(
                "X" to listOf(),
                "Y" to listOf(),
                Waterfall.Var.Stat.X.name to listOf(),
                Waterfall.Var.Stat.XLAB.name to listOf(),
                Waterfall.Var.Stat.YMIN.name to listOf(),
                Waterfall.Var.Stat.YMIDDLE.name to listOf(),
                Waterfall.Var.Stat.YMAX.name to listOf(),
                Waterfall.Var.Stat.MEASURE.name to listOf(),
                Waterfall.Var.Stat.FLOW_TYPE.name to listOf(),
                Waterfall.Var.Stat.INITIAL.name to listOf(),
                Waterfall.Var.Stat.VALUE.name to listOf(),
                Waterfall.Var.Stat.DIFFERENCE.name to listOf(),
                Waterfall.Var.Stat.LABEL.name to listOf(),
                Waterfall.Var.Stat.RADIUS.name to listOf()
            )
            if (measure != null) expectedData[measure] = listOf()
            if (group != null) expectedData[group] = listOf()
            val plotOptions = getPlotOptions(
                data = data,
                x = "X",
                y = "Y",
                measure = measure,
                group = group,
                calcTotal = false
            )
            val (relativeStatData, absoluteStatData) = getStatData(plotOptions)
            checkData(expectedData, relativeStatData, absoluteStatData)
        }

        for (measure in listOf<String?>(null, "M")) {
            for (group in listOf<String?>(null, "G")) {
                testEmptyData(measure, group)
            }
        }
    }

    @Test
    fun `null and infinite values in dataset`() {
        val plotOptions = getPlotOptions(
            data = mapOf(
                "X" to listOf("A", "B", "T1", null, "B", "C", "T2"),
                "Y" to listOf(1.0, 1.0, null, 1.0, null, Double.POSITIVE_INFINITY, 1.0,),
                "M" to listOf("relative", "relative", "total", "relative", "relative", "relative", null),
                "G" to listOf(1, 1, 1, 2, 2, 2, 2),
            ),
            x = "X",
            y = "Y",
            measure = "M",
            group = "G"
        )
        val (relativeStatData, absoluteStatData) = getStatData(plotOptions)
        checkData(
            mapOf(
                "X" to listOf("A", "B", "T1"),
                "Y" to listOf(1.0, 1.0, null),
                "M" to listOf("relative", "relative", "total"),
                "G" to listOf(1.0, 1.0, 1.0), // 1.0 instead of 1 because of using DataUtil::standardiseData in the WaterfallPlotOptionsBuilder
                Waterfall.Var.Stat.X.name to listOf(0.0, 1.0, 2.0),
                Waterfall.Var.Stat.XLAB.name to listOf("A", "B", "T1"),
                Waterfall.Var.Stat.YMIN.name to listOf(0.0, 1.0, 0.0),
                Waterfall.Var.Stat.YMIDDLE.name to listOf(0.5, 1.5, 1.0),
                Waterfall.Var.Stat.YMAX.name to listOf(1.0, 2.0, 2.0),
                Waterfall.Var.Stat.MEASURE.name to listOf("relative", "relative", "total"),
                Waterfall.Var.Stat.FLOW_TYPE.name to listOf("Increase", "Increase", "Total"),
                Waterfall.Var.Stat.INITIAL.name to listOf(0.0, 1.0, 0.0),
                Waterfall.Var.Stat.VALUE.name to listOf(1.0, 2.0, 2.0),
                Waterfall.Var.Stat.DIFFERENCE.name to listOf(1.0, 1.0, 2.0),
                Waterfall.Var.Stat.LABEL.name to listOf(1.0, 1.0, 2.0),
                Waterfall.Var.Stat.RADIUS.name to listOf(0.1, 0.1, 0.0)
            ),
            relativeStatData,
            absoluteStatData
        )
    }

    @Test
    fun `one value in dataset, calcTotal = false`() {
        val plotOptions = getPlotOptions(
            data = mapOf("X" to listOf("A"), "Y" to listOf(1.0)),
            x = "X",
            y = "Y",
            calcTotal = false
        )
        val (relativeStatData, absoluteStatData) = getStatData(plotOptions)
        checkData(
            mapOf(
                "X" to listOf("A"),
                "Y" to listOf(1.0),
                Waterfall.Var.Stat.X.name to listOf(0.0),
                Waterfall.Var.Stat.XLAB.name to listOf("A"),
                Waterfall.Var.Stat.YMIN.name to listOf(0.0),
                Waterfall.Var.Stat.YMIDDLE.name to listOf(0.5),
                Waterfall.Var.Stat.YMAX.name to listOf(1.0),
                Waterfall.Var.Stat.MEASURE.name to listOf("relative"),
                Waterfall.Var.Stat.FLOW_TYPE.name to listOf("Increase"),
                Waterfall.Var.Stat.INITIAL.name to listOf(0.0),
                Waterfall.Var.Stat.VALUE.name to listOf(1.0),
                Waterfall.Var.Stat.DIFFERENCE.name to listOf(1.0),
                Waterfall.Var.Stat.LABEL.name to listOf(1.0),
                Waterfall.Var.Stat.RADIUS.name to listOf(0.0)
            ),
            relativeStatData,
            absoluteStatData
        )
    }

    @Test
    fun `one value in dataset, calcTotal = true`() {
        val plotOptions = getPlotOptions(
            data = mapOf("X" to listOf(), "Y" to listOf()),
            x = "X",
            y = "Y",
            calcTotal = true
        )
        val (relativeStatData, absoluteStatData) = getStatData(plotOptions)
        checkData(
            mapOf(
                "X" to listOf(null),
                "Y" to listOf(null),
                Waterfall.Var.Stat.X.name to listOf(0.0),
                Waterfall.Var.Stat.XLAB.name to listOf("Total"),
                Waterfall.Var.Stat.YMIN.name to listOf(0.0),
                Waterfall.Var.Stat.YMIDDLE.name to listOf(0.0),
                Waterfall.Var.Stat.YMAX.name to listOf(0.0),
                Waterfall.Var.Stat.MEASURE.name to listOf("total"),
                Waterfall.Var.Stat.FLOW_TYPE.name to listOf("Total"),
                Waterfall.Var.Stat.INITIAL.name to listOf(0.0),
                Waterfall.Var.Stat.VALUE.name to listOf(0.0),
                Waterfall.Var.Stat.DIFFERENCE.name to listOf(0.0),
                Waterfall.Var.Stat.LABEL.name to listOf(0.0),
                Waterfall.Var.Stat.RADIUS.name to listOf(0.0)
            ),
            relativeStatData,
            absoluteStatData
        )
    }

    @Test
    fun `one value in dataset, absolute measure`() {
        val plotOptions = getPlotOptions(
            data = mapOf("X" to listOf("A"), "Y" to listOf(1.0), "M" to listOf("absolute")),
            x = "X",
            y = "Y",
            measure = "M"
        )
        val (relativeStatData, absoluteStatData) = getStatData(plotOptions)
        checkData(
            mapOf(
                "X" to listOf("A"),
                "Y" to listOf(1.0),
                "M" to listOf("absolute"),
                Waterfall.Var.Stat.X.name to listOf(0.0),
                Waterfall.Var.Stat.XLAB.name to listOf("A"),
                Waterfall.Var.Stat.YMIN.name to listOf(0.0),
                Waterfall.Var.Stat.YMIDDLE.name to listOf(0.5),
                Waterfall.Var.Stat.YMAX.name to listOf(1.0),
                Waterfall.Var.Stat.MEASURE.name to listOf("absolute"),
                Waterfall.Var.Stat.FLOW_TYPE.name to listOf("Absolute"),
                Waterfall.Var.Stat.INITIAL.name to listOf(0.0),
                Waterfall.Var.Stat.VALUE.name to listOf(1.0),
                Waterfall.Var.Stat.DIFFERENCE.name to listOf(1.0),
                Waterfall.Var.Stat.LABEL.name to listOf(1.0),
                Waterfall.Var.Stat.RADIUS.name to listOf(0.0)
            ),
            relativeStatData,
            absoluteStatData
        )
    }

    @Test
    fun `one value in dataset, relative measure`() {
        val plotOptions = getPlotOptions(
            data = mapOf("X" to listOf("A"), "Y" to listOf(1.0), "M" to listOf("relative")),
            x = "X",
            y = "Y",
            measure = "M"
        )
        val (relativeStatData, absoluteStatData) = getStatData(plotOptions)
        checkData(
            mapOf(
                "X" to listOf("A"),
                "Y" to listOf(1.0),
                "M" to listOf("relative"),
                Waterfall.Var.Stat.X.name to listOf(0.0),
                Waterfall.Var.Stat.XLAB.name to listOf("A"),
                Waterfall.Var.Stat.YMIN.name to listOf(0.0),
                Waterfall.Var.Stat.YMIDDLE.name to listOf(0.5),
                Waterfall.Var.Stat.YMAX.name to listOf(1.0),
                Waterfall.Var.Stat.MEASURE.name to listOf("relative"),
                Waterfall.Var.Stat.FLOW_TYPE.name to listOf("Increase"),
                Waterfall.Var.Stat.INITIAL.name to listOf(0.0),
                Waterfall.Var.Stat.VALUE.name to listOf(1.0),
                Waterfall.Var.Stat.DIFFERENCE.name to listOf(1.0),
                Waterfall.Var.Stat.LABEL.name to listOf(1.0),
                Waterfall.Var.Stat.RADIUS.name to listOf(0.0)
            ),
            relativeStatData,
            absoluteStatData
        )
    }

    @Test
    fun `one value in dataset, total measure`() {
        val plotOptions = getPlotOptions(
            data = mapOf("X" to listOf("A"), "Y" to listOf(null), "M" to listOf("total")),
            x = "X",
            y = "Y",
            measure = "M"
        )
        val (relativeStatData, absoluteStatData) = getStatData(plotOptions)
        checkData(
            mapOf(
                "X" to listOf("A"),
                "Y" to listOf(null),
                "M" to listOf("total"),
                Waterfall.Var.Stat.X.name to listOf(0.0),
                Waterfall.Var.Stat.XLAB.name to listOf("A"),
                Waterfall.Var.Stat.YMIN.name to listOf(0.0),
                Waterfall.Var.Stat.YMIDDLE.name to listOf(0.0),
                Waterfall.Var.Stat.YMAX.name to listOf(0.0),
                Waterfall.Var.Stat.MEASURE.name to listOf("total"),
                Waterfall.Var.Stat.FLOW_TYPE.name to listOf("Total"),
                Waterfall.Var.Stat.INITIAL.name to listOf(0.0),
                Waterfall.Var.Stat.VALUE.name to listOf(0.0),
                Waterfall.Var.Stat.DIFFERENCE.name to listOf(0.0),
                Waterfall.Var.Stat.LABEL.name to listOf(0.0),
                Waterfall.Var.Stat.RADIUS.name to listOf(0.0)
            ),
            relativeStatData,
            absoluteStatData
        )
    }

    @Test
    fun `grouping works with specified measure`() {
        val xs = listOf("A", "B", "T1", "A", "B", "T2")
        val ys = listOf(2.0, -1.0, null, 2.0, -1.0, null)
        val measures = listOf("relative", "relative", "total", "relative", "relative", "total")
        val groups = listOf("a", "a", "a", "b", "b", "b")
        val plotOptions = getPlotOptions(
            data = mapOf("X" to xs, "Y" to ys, "M" to measures, "G" to groups),
            x = "X",
            y = "Y",
            measure = "M",
            group = "G"
        )
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

    // issue #1152
    @Test
    fun `grouping works without specified measure`() {
        val xs = listOf("A", "B", "A", "B")
        val ys = listOf(2.0, -1.0, 2.0, -1.0)
        val groups = listOf("a", "a", "b", "b")
        val plotOptions = getPlotOptions(
            data = mapOf("X" to xs, "Y" to ys, "G" to groups),
            x = "X",
            y = "Y",
            group = "G"
        )
        val (relativeStatData, absoluteStatData) = getStatData(plotOptions)
        checkData(
            mapOf(
                "X" to listOf("A", "B", null, "A", "B", null),
                "Y" to listOf(2.0, -1.0, null, 2.0, -1.0, null),
                "G" to listOf("a", "a", "a", "b", "b", "b"),
                Waterfall.Var.Stat.X.name to listOf(0.0, 1.0, 2.0, 3.0, 4.0, 5.0),
                Waterfall.Var.Stat.XLAB.name to listOf("A", "B", "Total", "A", "B", "Total"),
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
        checkXScale(plotOptions, listOf(0.0, 1.0, 2.0, 3.0, 4.0, 5.0), listOf("A", "B", "Total", "A", "B", "Total"))
    }

    // issue #1153
    @Test
    fun `stat data preserves original columns`() {
        val xs = listOf("A", "B")
        val ys = listOf(2.0, -1.0)
        val extras = listOf(null, -5.0)
        val plotOptions = getPlotOptions(
            data = mapOf(
                "X" to xs,
                "Y" to ys,
                "E" to extras // Additional column, that is not used in statistic
            ),
            x = "X",
            y = "Y"
        )
        val (relativeStatData, absoluteStatData) = getStatData(plotOptions)
        checkData(
            mapOf(
                "X" to xs + listOf(null),
                "Y" to ys + listOf(null),
                "E" to extras + listOf(null), // Additional column should be preserved
                Waterfall.Var.Stat.X.name to listOf(0.0, 1.0, 2.0),
                Waterfall.Var.Stat.XLAB.name to listOf("A", "B", "Total"),
                Waterfall.Var.Stat.YMIN.name to listOf(0.0, 1.0, 0.0),
                Waterfall.Var.Stat.YMIDDLE.name to listOf(1.0, 1.5, 0.5),
                Waterfall.Var.Stat.YMAX.name to listOf(2.0, 2.0, 1.0),
                Waterfall.Var.Stat.MEASURE.name to listOf("relative", "relative", "total"),
                Waterfall.Var.Stat.FLOW_TYPE.name to listOf("Increase", "Decrease", "Total"),
                Waterfall.Var.Stat.INITIAL.name to listOf(0.0, 2.0, 0.0),
                Waterfall.Var.Stat.VALUE.name to listOf(2.0, 1.0, 1.0),
                Waterfall.Var.Stat.DIFFERENCE.name to listOf(2.0, -1.0, 1.0),
                Waterfall.Var.Stat.LABEL.name to listOf(2.0, -1.0, 1.0),
                Waterfall.Var.Stat.RADIUS.name to listOf(0.1, 0.1, 0.0)
            ),
            relativeStatData,
            absoluteStatData
        )
    }

    @Test
    fun `check parameter sortedValue`() {
        val plotOptions = getPlotOptions(
            data = mapOf(
                "X" to listOf("A", "B", "C", "D", "T1", "A", "T2"),
                "Y" to listOf(-1.0, -3.0, 4.0, 2.0, null, 2.0, null),
                "M" to listOf("relative", "relative", "relative", "relative", "total", "relative", "total")
            ),
            x = "X",
            y = "Y",
            measure = "M",
            sortedValue = true
        )
        val (relativeStatData, absoluteStatData) = getStatData(plotOptions)
        checkData(
            mapOf(
                "X" to listOf("C", "B", "D", "A", "T1", "A", "T2"),
                "Y" to listOf(4.0, -3.0, 2.0, -1.0, null, 2.0, null),
                "M" to listOf("relative", "relative", "relative", "relative", "total", "relative", "total"),
                Waterfall.Var.Stat.X.name to listOf(0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0),
                Waterfall.Var.Stat.XLAB.name to listOf("C", "B", "D", "A", "T1", "A", "T2"),
                Waterfall.Var.Stat.YMIN.name to listOf(0.0, 1.0, 1.0, 2.0, 0.0, 2.0, 0.0),
                Waterfall.Var.Stat.YMIDDLE.name to listOf(2.0, 2.5, 2.0, 2.5, 1.0, 3.0, 2.0),
                Waterfall.Var.Stat.YMAX.name to listOf(4.0, 4.0, 3.0, 3.0, 2.0, 4.0, 4.0),
                Waterfall.Var.Stat.MEASURE.name to listOf("relative", "relative", "relative", "relative", "total", "relative", "total"),
                Waterfall.Var.Stat.FLOW_TYPE.name to listOf("Increase", "Decrease", "Increase", "Decrease", "Total", "Increase", "Total"),
                Waterfall.Var.Stat.INITIAL.name to listOf(0.0, 4.0, 1.0, 3.0, 0.0, 2.0, 0.0),
                Waterfall.Var.Stat.VALUE.name to listOf(4.0, 1.0, 3.0, 2.0, 2.0, 4.0, 4.0),
                Waterfall.Var.Stat.DIFFERENCE.name to listOf(4.0, -3.0, 2.0, -1.0, 2.0, 2.0, 2.0),
                Waterfall.Var.Stat.LABEL.name to listOf(4.0, -3.0, 2.0, -1.0, 2.0, 2.0, 4.0),
                Waterfall.Var.Stat.RADIUS.name to listOf(0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.0)
            ),
            relativeStatData,
            absoluteStatData
        )
    }

    @Test
    fun `check parameter threshold`() {
        val plotOptions = getPlotOptions(
            data = mapOf(
                "X" to listOf("A", "B", "C", "D", "T1", "A", "T2"),
                "Y" to listOf(-1.0, -3.0, 4.0, 2.0, null, 2.0, null),
                "M" to listOf("relative", "relative", "relative", "relative", "total", "relative", "total")
            ),
            x = "X",
            y = "Y",
            measure = "M",
            threshold = 2.0
        )
        val (relativeStatData, absoluteStatData) = getStatData(plotOptions)
        checkData(
            mapOf(
                "X" to listOf("B", "C", "Other", "T1", "Other", "T2"),
                "Y" to listOf(-3.0, 4.0, 1.0, null, 2.0, null),
                "M" to listOf("relative", "relative", "relative", "total", "relative", "total"),
                Waterfall.Var.Stat.X.name to listOf(0.0, 1.0, 2.0, 3.0, 4.0, 5.0),
                Waterfall.Var.Stat.XLAB.name to listOf("B", "C", "Other", "T1", "Other", "T2"),
                Waterfall.Var.Stat.YMIN.name to listOf(-3.0, -3.0, 1.0, 0.0, 2.0, 0.0),
                Waterfall.Var.Stat.YMIDDLE.name to listOf(-1.5, -1.0, 1.5, 1.0, 3.0, 2.0),
                Waterfall.Var.Stat.YMAX.name to listOf(0.0, 1.0, 2.0, 2.0, 4.0, 4.0),
                Waterfall.Var.Stat.MEASURE.name to listOf("relative", "relative", "relative", "total", "relative", "total"),
                Waterfall.Var.Stat.FLOW_TYPE.name to listOf("Decrease", "Increase", "Increase", "Total", "Increase", "Total"),
                Waterfall.Var.Stat.INITIAL.name to listOf(0.0, -3.0, 1.0, 0.0, 2.0, 0.0),
                Waterfall.Var.Stat.VALUE.name to listOf(-3.0, 1.0, 2.0, 2.0, 4.0, 4.0),
                Waterfall.Var.Stat.DIFFERENCE.name to listOf(-3.0, 4.0, 1.0, 2.0, 2.0, 2.0),
                Waterfall.Var.Stat.LABEL.name to listOf(-3.0, 4.0, 1.0, 2.0, 2.0, 4.0),
                Waterfall.Var.Stat.RADIUS.name to listOf(0.1, 0.1, 0.1, 0.1, 0.1, 0.0)
            ),
            relativeStatData,
            absoluteStatData
        )
    }

    @Test
    fun `check parameter maxValues`() {
        val plotOptions = getPlotOptions(
            data = mapOf(
                "X" to listOf("A", "B", "C", "D", "T1", "A", "T2"),
                "Y" to listOf(-1.0, -3.0, 4.0, 2.0, null, 2.0, null),
                "M" to listOf("relative", "relative", "relative", "relative", "total", "relative", "total")
            ),
            x = "X",
            y = "Y",
            measure = "M",
            maxValues = 2
        )
        val (relativeStatData, absoluteStatData) = getStatData(plotOptions)
        checkData(
            mapOf(
                "X" to listOf("B", "C", "Other", "T1", "A", "T2"),
                "Y" to listOf(-3.0, 4.0, 1.0, null, 2.0, null),
                "M" to listOf("relative", "relative", "relative", "total", "relative", "total"),
                Waterfall.Var.Stat.X.name to listOf(0.0, 1.0, 2.0, 3.0, 4.0, 5.0),
                Waterfall.Var.Stat.XLAB.name to listOf("B", "C", "Other", "T1", "A", "T2"),
                Waterfall.Var.Stat.YMIN.name to listOf(-3.0, -3.0, 1.0, 0.0, 2.0, 0.0),
                Waterfall.Var.Stat.YMIDDLE.name to listOf(-1.5, -1.0, 1.5, 1.0, 3.0, 2.0),
                Waterfall.Var.Stat.YMAX.name to listOf(0.0, 1.0, 2.0, 2.0, 4.0, 4.0),
                Waterfall.Var.Stat.MEASURE.name to listOf("relative", "relative", "relative", "total", "relative", "total"),
                Waterfall.Var.Stat.FLOW_TYPE.name to listOf("Decrease", "Increase", "Increase", "Total", "Increase", "Total"),
                Waterfall.Var.Stat.INITIAL.name to listOf(0.0, -3.0, 1.0, 0.0, 2.0, 0.0),
                Waterfall.Var.Stat.VALUE.name to listOf(-3.0, 1.0, 2.0, 2.0, 4.0, 4.0),
                Waterfall.Var.Stat.DIFFERENCE.name to listOf(-3.0, 4.0, 1.0, 2.0, 2.0, 2.0),
                Waterfall.Var.Stat.LABEL.name to listOf(-3.0, 4.0, 1.0, 2.0, 2.0, 4.0),
                Waterfall.Var.Stat.RADIUS.name to listOf(0.1, 0.1, 0.1, 0.1, 0.1, 0.0)
            ),
            relativeStatData,
            absoluteStatData
        )
    }

    @Test
    fun `check parameter base`() {
        val xs = listOf("A", "B", "T1", "A", "B", "T2")
        val ys = listOf(2.0, -1.0, null, 2.0, -1.0, null)
        val measures = listOf("relative", "relative", "total", "relative", "relative", "total")
        val plotOptions = getPlotOptions(
            data = mapOf("X" to xs, "Y" to ys, "M" to measures),
            x = "X",
            y = "Y",
            measure = "M",
            base = -2.0
        )
        val (relativeStatData, absoluteStatData) = getStatData(plotOptions)
        checkData(
            mapOf(
                "X" to xs,
                "Y" to ys,
                "M" to measures,
                Waterfall.Var.Stat.X.name to listOf(0.0, 1.0, 2.0, 3.0, 4.0, 5.0),
                Waterfall.Var.Stat.XLAB.name to listOf("A", "B", "T1", "A", "B", "T2"),
                Waterfall.Var.Stat.YMIN.name to listOf(-2.0, -1.0, -2.0, -1.0, 0.0, -2.0),
                Waterfall.Var.Stat.YMIDDLE.name to listOf(-1.0, -0.5, -1.5, 0.0, 0.5, -1.0),
                Waterfall.Var.Stat.YMAX.name to listOf(0.0, 0.0, -1.0, 1.0, 1.0, 0.0),
                Waterfall.Var.Stat.MEASURE.name to listOf("relative", "relative", "total", "relative", "relative", "total"),
                Waterfall.Var.Stat.FLOW_TYPE.name to listOf("Increase", "Decrease", "Total", "Increase", "Decrease", "Total"),
                Waterfall.Var.Stat.INITIAL.name to listOf(-2.0, 0.0, -2.0, -1.0, 1.0, -2.0),
                Waterfall.Var.Stat.VALUE.name to listOf(0.0, -1.0, -1.0, 1.0, 0.0, 0.0),
                Waterfall.Var.Stat.DIFFERENCE.name to listOf(2.0, -1.0, 1.0, 2.0, -1.0, 1.0),
                Waterfall.Var.Stat.LABEL.name to listOf(2.0, -1.0, -1.0, 2.0, -1.0, 0.0),
                Waterfall.Var.Stat.RADIUS.name to listOf(0.1, 0.1, 0.1, 0.1, 0.1, 0.0)
            ),
            relativeStatData,
            absoluteStatData
        )
    }

    @Test
    fun `check parameter calcTotal`() {
        val xs = listOf("A", "B", "C")
        val ys = listOf(2.0, -1.0, 2.0)
        val plotOptions = getPlotOptions(
            data = mapOf("X" to xs, "Y" to ys),
            x = "X",
            y = "Y",
            calcTotal = false // calcTotal is true by default, so case is checked by 'simple' test
        )
        val (relativeStatData, absoluteStatData) = getStatData(plotOptions)
        checkData(
            mapOf(
                "X" to xs,
                "Y" to ys,
                Waterfall.Var.Stat.X.name to listOf(0.0, 1.0, 2.0),
                Waterfall.Var.Stat.XLAB.name to listOf("A", "B", "C"),
                Waterfall.Var.Stat.YMIN.name to listOf(0.0, 1.0, 1.0),
                Waterfall.Var.Stat.YMIDDLE.name to listOf(1.0, 1.5, 2.0),
                Waterfall.Var.Stat.YMAX.name to listOf(2.0, 2.0, 3.0),
                Waterfall.Var.Stat.MEASURE.name to listOf("relative", "relative", "relative"),
                Waterfall.Var.Stat.FLOW_TYPE.name to listOf("Increase", "Decrease", "Increase"),
                Waterfall.Var.Stat.INITIAL.name to listOf(0.0, 2.0, 1.0),
                Waterfall.Var.Stat.VALUE.name to listOf(2.0, 1.0, 3.0),
                Waterfall.Var.Stat.DIFFERENCE.name to listOf(2.0, -1.0, 2.0),
                Waterfall.Var.Stat.LABEL.name to listOf(2.0, -1.0, 2.0),
                Waterfall.Var.Stat.RADIUS.name to listOf(0.1, 0.1, 0.0)
            ),
            relativeStatData,
            absoluteStatData
        )
        checkXScale(plotOptions, listOf(0.0, 1.0, 2.0), listOf("A", "B", "C"))
    }

    @Test
    fun `check parameter totalTitle without measure`() {
        val xs = listOf("A", "B", "C")
        val ys = listOf(2.0, -1.0, 2.0)
        val plotOptions = getPlotOptions(
            data = mapOf("X" to xs, "Y" to ys),
            x = "X",
            y = "Y",
            totalTitle = "Result"
        )
        val (relativeStatData, absoluteStatData) = getStatData(plotOptions)
        checkData(
            mapOf(
                "X" to xs + listOf(null),
                "Y" to ys + listOf(null),
                Waterfall.Var.Stat.X.name to listOf(0.0, 1.0, 2.0, 3.0),
                Waterfall.Var.Stat.XLAB.name to listOf("A", "B", "C", "Result"),
                Waterfall.Var.Stat.YMIN.name to listOf(0.0, 1.0, 1.0, 0.0),
                Waterfall.Var.Stat.YMIDDLE.name to listOf(1.0, 1.5, 2.0, 1.5),
                Waterfall.Var.Stat.YMAX.name to listOf(2.0, 2.0, 3.0, 3.0),
                Waterfall.Var.Stat.MEASURE.name to listOf("relative", "relative", "relative", "total"),
                Waterfall.Var.Stat.FLOW_TYPE.name to listOf("Increase", "Decrease", "Increase", "Result"),
                Waterfall.Var.Stat.INITIAL.name to listOf(0.0, 2.0, 1.0, 0.0),
                Waterfall.Var.Stat.VALUE.name to listOf(2.0, 1.0, 3.0, 3.0),
                Waterfall.Var.Stat.DIFFERENCE.name to listOf(2.0, -1.0, 2.0, 3.0),
                Waterfall.Var.Stat.LABEL.name to listOf(2.0, -1.0, 2.0, 3.0),
                Waterfall.Var.Stat.RADIUS.name to listOf(0.1, 0.1, 0.1, 0.0)
            ),
            relativeStatData,
            absoluteStatData
        )
        checkXScale(plotOptions, listOf(0.0, 1.0, 2.0, 3.0), listOf("A", "B", "C", "Result"))
    }

    @Test
    fun `check parameter totalTitle with measure`() {
        val xs = listOf("A", "B", "T1", "A", "B", "T2")
        val ys = listOf(2.0, -1.0, null, 2.0, -1.0, null)
        val measures = listOf("relative", "relative", "total", "relative", "relative", "total")
        val plotOptions = getPlotOptions(
            data = mapOf("X" to xs, "Y" to ys, "M" to measures),
            x = "X",
            y = "Y",
            measure = "M",
            totalTitle = "Result"
        )
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
                Waterfall.Var.Stat.FLOW_TYPE.name to listOf("Increase", "Decrease", "Result", "Increase", "Decrease", "Result"),
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
    fun `check hline`() {
        listOf(
            Pair(false, ElementLineOptions(blank = true)),
            Pair(true, ElementLineOptions(blank = false))
        ).forEach { (expectedPresence, elementOptions) ->
            getPlotOptions(
                data = mapOf("X" to listOf("A"), "Y" to listOf(1.0)),
                x = "X",
                y = "Y",
                hLineOptions = elementOptions
            ).let { plotOptions ->
                val elementIsPresented = plotOptions.layerOptions!!.map { it.geom }.contains(GeomKind.H_LINE)
                assert(expectedPresence == elementIsPresented) { "Wrong presence of horizontal line layer" }
            }
        }
    }

    @Test
    fun `check connectors`() {
        listOf(
            Pair(false, ElementLineOptions(blank = true)),
            Pair(true, ElementLineOptions(blank = false))
        ).forEach { (expectedPresence, elementOptions) ->
            getPlotOptions(
                data = mapOf("X" to listOf("A"), "Y" to listOf(1.0)),
                x = "X",
                y = "Y",
                connectorOptions = elementOptions
            ).let { plotOptions ->
                val elementIsPresented = plotOptions.layerOptions!!.map { it.geom }.contains(GeomKind.SPOKE)
                assert(expectedPresence == elementIsPresented) { "Wrong presence of connectors layer" }
            }
        }
    }

    @Test
    fun `check labels`() {
        listOf(
            Pair(false, ElementTextOptions(blank = true)),
            Pair(true, ElementTextOptions(blank = false))
        ).forEach { (expectedPresence, elementOptions) ->
            getPlotOptions(
                data = mapOf("X" to listOf("A"), "Y" to listOf(1.0)),
                x = "X",
                y = "Y",
                labelOptions = elementOptions
            ).let { plotOptions ->
                val elementIsPresented = plotOptions.layerOptions!!.map { it.geom }.contains(GeomKind.TEXT)
                assert(expectedPresence == elementIsPresented) { "Wrong presence of labels layer" }
            }
        }
    }

    @Test
    fun `x values with repetitions`() {
        val xs = listOf("A", "A", "A")
        val ys = listOf(2.0, -1.0, 2.0)
        val plotOptions = getPlotOptions(
            data = mapOf("X" to xs, "Y" to ys),
            x = "X",
            y = "Y"
        )
        val (relativeStatData, absoluteStatData) = getStatData(plotOptions)
        checkData(
            mapOf(
                "X" to xs + listOf(null),
                "Y" to ys + listOf(null),
                Waterfall.Var.Stat.X.name to listOf(0.0, 1.0, 2.0, 3.0),
                Waterfall.Var.Stat.XLAB.name to listOf("A", "A", "A", "Total"),
                Waterfall.Var.Stat.YMIN.name to listOf(0.0, 1.0, 1.0, 0.0),
                Waterfall.Var.Stat.YMIDDLE.name to listOf(1.0, 1.5, 2.0, 1.5),
                Waterfall.Var.Stat.YMAX.name to listOf(2.0, 2.0, 3.0, 3.0),
                Waterfall.Var.Stat.MEASURE.name to listOf("relative", "relative", "relative", "total"),
                Waterfall.Var.Stat.FLOW_TYPE.name to listOf("Increase", "Decrease", "Increase", "Total"),
                Waterfall.Var.Stat.INITIAL.name to listOf(0.0, 2.0, 1.0, 0.0),
                Waterfall.Var.Stat.VALUE.name to listOf(2.0, 1.0, 3.0, 3.0),
                Waterfall.Var.Stat.DIFFERENCE.name to listOf(2.0, -1.0, 2.0, 3.0),
                Waterfall.Var.Stat.LABEL.name to listOf(2.0, -1.0, 2.0, 3.0),
                Waterfall.Var.Stat.RADIUS.name to listOf(0.1, 0.1, 0.1, 0.0)
            ),
            relativeStatData,
            absoluteStatData
        )
        checkXScale(plotOptions, listOf(0.0, 1.0, 2.0, 3.0), listOf("A", "A", "A", "Total"))
    }

    private fun getPlotOptions(
        data: Map<String, List<Any?>>,
        x: String,
        y: String,
        measure: String? = null,
        group: String? = null,
        relativeTooltipsOptions: TooltipsOptions? = DEF_RELATIVE_TOOLTIPS,
        absoluteTooltipsOptions: TooltipsOptions? = DEF_ABSOLUTE_TOOLTIPS,
        calcTotal: Boolean = true,
        totalTitle: String? = null,
        sortedValue: Boolean = false,
        threshold: Double? = null,
        maxValues: Int? = null,
        base: Double = 0.0,
        hLineOptions: ElementLineOptions = DEF_H_LINE,
        connectorOptions: ElementLineOptions = DEF_CONNECTOR,
        labelOptions: ElementTextOptions = DEF_LABEL
    ): PlotOptions {
        return WaterfallPlotOptionsBuilder(
            data = data,
            x = x,
            y = y,
            measure = measure,
            group = group,
            color = null,
            fill = null,
            size = null,
            alpha = null,
            lineType = null,
            width = DEF_WIDTH,
            showLegend = false,
            relativeTooltipsOptions = relativeTooltipsOptions,
            absoluteTooltipsOptions = absoluteTooltipsOptions,
            calcTotal = calcTotal,
            totalTitle = totalTitle,
            sortedValue = sortedValue,
            threshold = threshold,
            maxValues = maxValues,
            base = base,
            hLineOptions = hLineOptions,
            hLineOnTop = false,
            connectorOptions = connectorOptions,
            labelOptions = labelOptions,
            labelFormat = DEF_LABEL_FORMAT
        ).build()
    }

    private fun getStatData(plotOptions: PlotOptions): Pair<Map<String, List<Any?>>, Map<String, List<Any?>>> {
        val boxOptions = getBoxOptions(plotOptions)
        val relativeStatData = boxOptions.first.data
        val absoluteStatData = boxOptions.second.data
        assertNotNull(relativeStatData) { "Crossbar data shouldn't be null" }
        assertNotNull(absoluteStatData) { "Crossbar data shouldn't be null" }
        return Pair(relativeStatData, absoluteStatData)
    }

    private fun getBoxOptions(plotOptions: PlotOptions): Pair<LayerOptions, LayerOptions> {
        val boxOptions = plotOptions.layerOptions!!.filter { it.geom == GeomKind.CROSS_BAR }
        assert(2 == boxOptions.size) { "Expected 2 crossbar layers in waterfall plot, but got ${boxOptions.size}" }
        return Pair(boxOptions[0], boxOptions[1])
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
        assert(expectedData.values.map(List<*>::size).toSet().size == 1) { "All columns in expected data should have the same size" }
        val rowsCount = expectedData.getValue(Waterfall.Var.Stat.X.name).size
        val measures = expectedData.getValue(Waterfall.Var.Stat.MEASURE.name)
        for (column in expectedKeys) {
            val expectedValues = expectedData.getValue(column)
            val relativeValues = relativeData.getValue(column)
            val absoluteValues = absoluteData.getValue(column)
            assert(rowsCount == relativeValues.size) { "Rows count in relative data should be equal to expected data, but $rowsCount != ${relativeValues.size} = |$column|" }
            assert(rowsCount == absoluteValues.size) { "Rows count in absolute data should be equal to expected data, but $rowsCount != ${absoluteValues.size} = |$column|" }
            for (row in 0 until rowsCount) {
                val measure = measures[row]
                val expectedValue = expectedValues[row]
                val relativeValue = relativeValues[row]
                val absoluteValue = absoluteValues[row]
                when (column) {
                    Waterfall.Var.Stat.YMIN.name,
                    Waterfall.Var.Stat.YMIDDLE.name,
                    Waterfall.Var.Stat.YMAX.name -> {
                        if (measure == WaterfallPlotOptionsBuilder.Measure.RELATIVE.value) {
                            assert(absoluteValue == null) { "Relative values for $column in absolute data should be null" }
                            assertNotNull(relativeValue, "Relative values for $column in relative data shouldn't be null")
                            assert(expectedValue == relativeValue) { "Values for $column in relative data should be equal to expected values, but $expectedValues != $relativeValues in $row-th row" }
                        } else {
                            assert(relativeValue == null) { "Absolute values for $column in relative data should be null" }
                            assertNotNull(absoluteValue, "Absolute values for $column in absolute data shouldn't be null")
                            assert(expectedValue == absoluteValue) { "Values for $column in absolute data should be equal to expected values, but $expectedValues != $absoluteValues in $row-th row" }
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
                        compareValues(expectedValue, relativeValue, column, row)
                        assertNotNull(absoluteValue) { "Values for $column in absolute data shouldn't be null" }
                        compareValues(expectedValue, absoluteValue, column, row)
                    }
                    else -> {
                        compareValues(expectedValue, relativeValue, column, row)
                        compareValues(expectedValue, absoluteValue, column, row)
                    }
                }
            }
        }
    }

    private fun compareValues(expected: Any?, actual: Any?, column: String, row: Int) {
        when (column) {
            Waterfall.Var.Stat.RADIUS.name -> {
                assertEquals(expected as Double, actual as Double, 1e-16, "Values in $column should be equal, but $expected != $actual in row $row")
            }
            else -> {
                assert(expected == actual) { "Values in $column should be equal, but $expected != $actual in row $row" }
            }
        }
    }

    private fun checkXScale(plotOptions: PlotOptions, expectedBreaks: List<Double>, expectedLabels: List<String>) {
        val scaleXOptions = plotOptions.scaleOptions!!.first { it.aes == Aes.X }
        assert(expectedBreaks == scaleXOptions.breaks) { "Expected breaks for X scale: $expectedBreaks, but got ${scaleXOptions.breaks}" }
        assert(expectedLabels == scaleXOptions.labels) { "Expected labels for X scale: $expectedLabels, but got ${scaleXOptions.labels}" }
    }
}