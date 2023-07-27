/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat

import org.jetbrains.letsPlot.core.plot.base.data.TransformVar
import kotlin.test.Test

class ECDFStatTest : BaseStatTest() {
    @Test
    fun emptyDataFrame() {
        testEmptyDataFrame(ECDFStat(null))
    }

    @Test
    fun oneElementDataFrame() {
        val x = 3.14
        val df = dataFrame(mapOf(
            TransformVar.X to listOf(x)
        ))
        val stat = ECDFStat(null)
        val statDf = stat.apply(df, statContext(df))

        checkStatVarValues(statDf, Stats.X, listOf(x))
        checkStatVarValues(statDf, Stats.Y, listOf(1.0))
    }

    @Test
    fun withOnlyNanValues() {
        val df = dataFrame(mapOf(
            TransformVar.X to listOf(null, null, null)
        ))
        val stat = ECDFStat(null)
        val statDf = stat.apply(df, statContext(df))

        checkStatVarValues(statDf, Stats.X, emptyList())
        checkStatVarValues(statDf, Stats.Y, emptyList())
    }

    @Test
    fun withFewNanValues() {
        val df = dataFrame(mapOf(
            TransformVar.X to listOf(-1.0, null, null, 1.0)
        ))
        val stat = ECDFStat(null)
        val statDf = stat.apply(df, statContext(df))

        checkStatVarValues(statDf, Stats.X, listOf(-1.0, 1.0))
        checkStatVarValues(statDf, Stats.Y, listOf(0.5, 1.0))
    }

    @Test
    fun withRepetitions() {
        val df = dataFrame(mapOf(
            TransformVar.X to listOf(-2.0, -1.0, -1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 2.0)
        ))
        val stat = ECDFStat(null)
        val statDf = stat.apply(df, statContext(df))

        checkStatVarValues(statDf, Stats.X, listOf(-2.0, -1.0, 0.0, 1.0, 2.0))
        checkStatVarValues(statDf, Stats.Y, listOf(0.1, 0.3, 0.7, 0.9, 1.0))
    }

    @Test
    fun withShuffledData() {
        val df = dataFrame(mapOf(
            TransformVar.X to listOf(2.0, -1.0, 0.0, 1.0, 0.0, 0.0, -1.0, 0.0, 1.0, -2.0)
        ))
        val stat = ECDFStat(null)
        val statDf = stat.apply(df, statContext(df))

        checkStatVarValues(statDf, Stats.X, listOf(2.0, -1.0, 0.0, 1.0, -2.0))
        checkStatVarValues(statDf, Stats.Y, listOf(1.0, 0.3, 0.7, 0.9, 0.1))
    }

    @Test
    fun withInterpolation() {
        val df = dataFrame(mapOf(
            TransformVar.X to listOf(-2.0, -1.0, -1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 2.0)
        ))
        val stat = ECDFStat(3)
        val statDf = stat.apply(df, statContext(df))

        checkStatVarValues(statDf, Stats.X, listOf(-2.0, 0.0, 2.0))
        checkStatVarValues(statDf, Stats.Y, listOf(0.1, 0.7, 1.0))
    }
}