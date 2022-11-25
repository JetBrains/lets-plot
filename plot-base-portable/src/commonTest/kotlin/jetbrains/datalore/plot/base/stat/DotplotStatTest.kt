/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat

import jetbrains.datalore.plot.base.data.TransformVar
import kotlin.test.Test

class DotplotStatTest : BaseStatTest() {
    @Test
    fun emptyDataFrame() {
        testEmptyDataFrame(Stats.dotplot())
    }

    @Test
    fun oneElementDataFrame() {
        val xValue = 3.14
        val df = dataFrame(mapOf(
            TransformVar.X to listOf(xValue)
        ))
        val stat = Stats.dotplot()
        val statDf = stat.apply(df, statContext(df))

        checkStatVarValues(statDf, Stats.X, listOf(xValue))
        checkStatVarValues(statDf, Stats.COUNT, listOf(1.0))
        checkStatVarValues(statDf, Stats.DENSITY, listOf(1.0))
        checkStatVarValues(statDf, Stats.BIN_WIDTH, listOf(1.0))
    }

    @Test
    fun oneStackDataFrame() {
        val xValue = 2.71
        val df = dataFrame(mapOf(
            TransformVar.X to listOf(xValue, xValue, xValue)
        ))
        val stat = Stats.dotplot()
        val statDf = stat.apply(df, statContext(df))

        checkStatVarValues(statDf, Stats.X, listOf(xValue))
        checkStatVarValues(statDf, Stats.COUNT, listOf(3.0))
        checkStatVarValues(statDf, Stats.DENSITY, listOf(1.0))
        checkStatVarValues(statDf, Stats.BIN_WIDTH, listOf(1.0))
    }

    @Test
    fun withNanValues() {
        val df = dataFrame(mapOf(
            TransformVar.X to listOf(0.0, 1.0, 2.0, null)
        ))
        val stat = Stats.dotplot(binWidth = 1.5)
        val statDf = stat.apply(df, statContext(df))

        checkStatVarValues(statDf, Stats.X, listOf(0.5, 2.0))
        checkStatVarValues(statDf, Stats.COUNT, listOf(2.0, 1.0))
        checkStatVarValues(statDf, Stats.DENSITY, listOf(2.0 / 3, 1.0 / 3))
        checkStatVarValues(statDf, Stats.BIN_WIDTH, listOf(1.5, 1.5))
    }
}