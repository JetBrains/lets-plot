/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.data.TransformVar
import kotlin.test.Test

class Density2dStatTest : BaseStatTest() {
    @Test
    fun basicTest() {
        val df = dataFrame(mapOf(
            TransformVar.X to listOf(0.0, 0.0, 4.0),
            TransformVar.Y to listOf(0.0, 2.0, 0.0)
        ))
        val stat = Stats.density2d(isContour = false)
        val statDf = stat.apply(df, statContext(df))

        checkStatVarRange(statDf, Stats.X, DoubleSpan(0.0, 4.0))
        checkStatVarRange(statDf, Stats.Y, DoubleSpan(0.0, 2.0))
        checkStatVarMaxLimit(statDf, Stats.DENSITY, 1.0)
    }

    @Test
    fun emptyDataFrame() {
        testEmptyDataFrame(Stats.density2d())
    }

    @Test
    fun oneElementDataFrame() {
        val df = dataFrame(mapOf(
            TransformVar.X to listOf(0.0),
            TransformVar.Y to listOf(0.0)
        ))
        val stat = Stats.density2d(isContour = false)
        val statDf = stat.apply(df, statContext(df))

        checkStatVarRange(statDf, Stats.X, DoubleSpan(-0.5, 0.5))
        checkStatVarRange(statDf, Stats.Y, DoubleSpan(-0.5, 0.5))
        checkStatVarMaxLimit(statDf, Stats.DENSITY, 1.0)
    }

    @Test
    fun withNanInX() {
        val df = dataFrame(mapOf(
            TransformVar.X to listOf(0.0, 0.0, null, 4.0),
            TransformVar.Y to listOf(0.0, 2.0, 1.0, 0.0),
            TransformVar.WEIGHT to listOf(1.0, 1.0, 1.0, 2.0)
        ))
        val expectedDf = dataFrame(mapOf(
            TransformVar.X to listOf(0.0, 0.0, 4.0),
            TransformVar.Y to listOf(0.0, 2.0, 0.0),
            TransformVar.WEIGHT to listOf(1.0, 1.0, 2.0)
        ))
        check(df, expectedDf)
    }

    @Test
    fun withNanInY() {
        val df = dataFrame(mapOf(
            TransformVar.X to listOf(0.0, 0.0, 2.0, 4.0),
            TransformVar.Y to listOf(0.0, 2.0, null, 0.0),
            TransformVar.WEIGHT to listOf(1.0, 1.0, 1.0, 2.0)
        ))
        val expectedDf = dataFrame(mapOf(
            TransformVar.X to listOf(0.0, 0.0, 4.0),
            TransformVar.Y to listOf(0.0, 2.0, 0.0),
            TransformVar.WEIGHT to listOf(1.0, 1.0, 2.0)
        ))
        check(df, expectedDf)
    }

    @Test
    fun withNanInWeight() {
        val df = dataFrame(mapOf(
            TransformVar.X to listOf(0.0, 0.0, 2.0, 4.0),
            TransformVar.Y to listOf(0.0, 2.0, 1.0, 0.0),
            TransformVar.WEIGHT to listOf(1.0, 1.0, null, 2.0)
        ))
        val expectedDf = dataFrame(mapOf(
            TransformVar.X to listOf(0.0, 0.0, 2.0, 4.0),
            TransformVar.Y to listOf(0.0, 2.0, 1.0, 0.0),
            TransformVar.WEIGHT to listOf(1.0, 1.0, 0.0, 2.0)
        ))
        check(df, expectedDf)
    }

    private fun check(df: DataFrame, expectedDf: DataFrame) {
        for (stat in STATS) {
            val statDf = stat.apply(df, statContext(df))
            val expectedStatDf = stat.apply(expectedDf, statContext(expectedDf))

            checkStatVarValues(statDf, Stats.X, expectedStatDf.getNumeric(Stats.X))
            checkStatVarValues(statDf, Stats.Y, expectedStatDf.getNumeric(Stats.Y))
            checkStatVarValues(statDf, Stats.DENSITY, expectedStatDf.getNumeric(Stats.DENSITY))
        }
    }

    companion object {
        val STATS = listOf(
            Stats.density2d(isContour = false),
            Stats.density2df(bandWidthMethod = AbstractDensity2dStat.DEF_BW, isContour = false)
        )
    }
}