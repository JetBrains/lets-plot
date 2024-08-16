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
        val df = DataFrame.Builder()
            .put(DataFrameUtil.createVariable("X"), listOf("A", "B", "T"))
            .put(DataFrameUtil.createVariable("Y"), listOf(2.0, -1.0, null))
            .put(DataFrameUtil.createVariable("M"), listOf("relative", "relative", "total"))
            .build()
        val boxStat = WaterfallUtil.calculateBoxStat(
            df = df,
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
            )
        )
        assertEquals(
            expected = mapOf(
                "..x.." to listOf(0.0, 1.0, 2.0),
                "..xlabel.." to listOf("A", "B", "T"),
                "..ymin.." to listOf(0.0, 1.0, 0.0),
                "..ymax.." to listOf(2.0, 2.0, 1.0),
                "..measure.." to listOf("relative", "relative", "total"),
                "..flow_type.." to listOf("Increase", "Decrease", "Total"),
                "..initial.." to listOf(0.0, 2.0, 0.0),
                "..value.." to listOf(2.0, 1.0, 1.0),
                "..dy.." to listOf(2.0, -1.0, 1.0),
            ),
            actual = DataFrameUtil.toMap(boxStat)
        )
    }
}