/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall

import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.data.DataFrameUtil
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.WaterfallPlotOptionsBuilder.FlowType
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall.WaterfallPlotOptionsBuilder.FlowType.FlowTypeData
import org.junit.Test
import kotlin.test.assertEquals

class WaterfallUtilTest {
    @Test
    fun simple() {
        val xs = listOf("A", "B", "T")
        val ys = listOf(2.0, -1.0, null)
        val measures = listOf("relative", "relative", "total")
        val df = DataFrame.Builder()
            .put(DataFrameUtil.createVariable("X"), xs)
            .put(DataFrameUtil.createVariable("Y"), ys)
            .put(DataFrameUtil.createVariable("M"), measures)
            .build()
        val statDf = WaterfallUtil.calculateStat(
            rawDf = df,
            x = "X",
            y = "Y",
            measure = "M",
            sortedValue = false,
            threshold = null,
            maxValues = null,
            initialX = 0,
            initialY = 0.0,
            base = 0.0,
            flowTypeTitles = mapOf(
                FlowType.INCREASE to FlowTypeData("Increase", "green"),
                FlowType.DECREASE to FlowTypeData("Decrease", "red"),
                FlowType.TOTAL to FlowTypeData("Total", "blue"),
            ),
            otherRowValues = { null }
        )
        assertEquals(
            expected = mapOf(
                "X" to xs,
                "Y" to ys,
                "M" to measures,
                Option.Waterfall.Var.Stat.X.name to listOf(0.0, 1.0, 2.0),
                Option.Waterfall.Var.Stat.XLAB.name to listOf("A", "B", "T"),
                Option.Waterfall.Var.Stat.YMIN.name to listOf(0.0, 1.0, 0.0),
                Option.Waterfall.Var.Stat.YMIDDLE.name to listOf(1.0, 1.5, 0.5),
                Option.Waterfall.Var.Stat.YMAX.name to listOf(2.0, 2.0, 1.0),
                Option.Waterfall.Var.Stat.MEASURE.name to listOf("relative", "relative", "total"),
                Option.Waterfall.Var.Stat.FLOW_TYPE.name to listOf("Increase", "Decrease", "Total"),
                Option.Waterfall.Var.Stat.INITIAL.name to listOf(0.0, 2.0, 0.0),
                Option.Waterfall.Var.Stat.VALUE.name to listOf(2.0, 1.0, 1.0),
                Option.Waterfall.Var.Stat.DIFFERENCE.name to listOf(2.0, -1.0, 1.0),
                Option.Waterfall.Var.Stat.LABEL.name to listOf(2.0, -1.0, 1.0),
            ),
            actual = DataFrameUtil.toMap(statDf)
        )
    }
}