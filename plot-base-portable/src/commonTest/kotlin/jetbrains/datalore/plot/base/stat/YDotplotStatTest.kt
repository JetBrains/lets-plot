/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat

import jetbrains.datalore.plot.base.data.TransformVar
import kotlin.test.Test

class YDotplotStatTest : BaseStatTest() {
    @Test
    fun emptyDataFrame() {
        testEmptyDataFrame(Stats.ydotplot())
    }

    @Test
    fun oneElementDataFrame() {
        val yValue = 3.14
        val df = df(mapOf(
            TransformVar.Y to listOf(yValue)
        ))
        val stat = Stats.ydotplot()
        val statDf = stat.apply(df, statContext(df))

        checkStatVar(statDf, Stats.X, listOf(0.0))
        checkStatVar(statDf, Stats.Y, listOf(yValue))
        checkStatVar(statDf, Stats.COUNT, listOf(1.0))
        checkStatVar(statDf, Stats.DENSITY, listOf(1.0))
        checkStatVar(statDf, Stats.BIN_WIDTH, listOf(1.0))
    }

    @Test
    fun withNanValues() {
        val df = df(mapOf(
            TransformVar.X to listOf(0.0, 0.0, 0.0, 1.0, null, 1.0),
            TransformVar.Y to listOf(0.0, 1.0, 2.0, 0.0, 1.0, null)
        ))
        val stat = Stats.ydotplot(binWidth = 1.5)
        val statDf = stat.apply(df, statContext(df))

        checkStatVar(statDf, Stats.X, listOf(0.0, 0.0, 1.0))
        checkStatVar(statDf, Stats.Y, listOf(0.5, 2.0, 0.0))
        checkStatVar(statDf, Stats.COUNT, listOf(2.0, 1.0, 1.0))
        checkStatVar(statDf, Stats.DENSITY, listOf(2.0 / 3, 1.0 / 3, 1.0))
        checkStatVar(statDf, Stats.BIN_WIDTH, listOf(1.5, 1.5, 1.5))
    }
}