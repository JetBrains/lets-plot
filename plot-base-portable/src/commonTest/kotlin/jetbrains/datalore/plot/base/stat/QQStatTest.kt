/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat

import jetbrains.datalore.plot.base.data.TransformVar
import kotlin.test.Test

class QQStatTest : BaseStatTest() {
    @Test
    fun emptyQQDataFrame() {
        testEmptyDataFrame(Stats.qq())
    }

    @Test
    fun oneElementQQDataFrame() {
        val sampleValue = 3.14
        val df = dataFrame(mapOf(
            TransformVar.SAMPLE to listOf(sampleValue)
        ))
        val stat = Stats.qq()
        val statDf = stat.apply(df, statContext(df))

        val dist = QQStatUtil.getDistribution(QQStat.DEF_DISTRIBUTION, QQStat.DEF_DISTRIBUTION_PARAMETERS)
        checkStatVarValues(statDf, Stats.SAMPLE, listOf(sampleValue))
        checkStatVarValues(statDf, Stats.THEORETICAL, listOf(dist.inverseCumulativeProbability(0.5)))
    }

    @Test
    fun withOnlyNanValuesQQ() {
        val df = dataFrame(mapOf(
            TransformVar.SAMPLE to listOf(null, null, null)
        ))
        val stat = Stats.qq()
        val statDf = stat.apply(df, statContext(df))

        checkStatVarValues(statDf, Stats.SAMPLE, emptyList())
        checkStatVarValues(statDf, Stats.THEORETICAL, emptyList())
    }

    @Test
    fun withFewNanValuesQQ() {
        val sampleValues = listOf(1.0, null, null, -1.0)
        val df = dataFrame(mapOf(
            TransformVar.SAMPLE to sampleValues
        ))
        val stat = Stats.qq()
        val statDf = stat.apply(df, statContext(df))

        val dist = QQStatUtil.getDistribution(QQStat.DEF_DISTRIBUTION, QQStat.DEF_DISTRIBUTION_PARAMETERS)
        checkStatVarValues(statDf, Stats.SAMPLE, listOf(-1.0, 1.0))
        checkStatVarValues(statDf, Stats.THEORETICAL, listOf(
            dist.inverseCumulativeProbability(0.25),
            dist.inverseCumulativeProbability(0.75),
        ))
    }

    @Test
    fun emptyQQLineDataFrame() {
        testEmptyDataFrame(Stats.qqline())
    }

    @Test
    fun oneElementQQLineDataFrame() {
        val sampleValue = 3.14
        val df = dataFrame(mapOf(
            TransformVar.SAMPLE to listOf(sampleValue)
        ))
        val stat = Stats.qqline()
        val statDf = stat.apply(df, statContext(df))

        val dist = QQStatUtil.getDistribution(QQStat.DEF_DISTRIBUTION, QQStat.DEF_DISTRIBUTION_PARAMETERS)
        checkStatVarValues(statDf, Stats.SAMPLE, listOf(sampleValue, sampleValue))
        checkStatVarValues(statDf, Stats.THEORETICAL, listOf(
            dist.inverseCumulativeProbability(0.5),
            dist.inverseCumulativeProbability(0.5),
        ))
    }

    @Test
    fun withOnlyNanValuesQQLine() {
        val sampleValues = listOf(null, null, null)
        val df = dataFrame(mapOf(
            TransformVar.SAMPLE to sampleValues
        ))
        val stat = Stats.qqline()
        val statDf = stat.apply(df, statContext(df))

        checkStatVarValues(statDf, Stats.SAMPLE, emptyList())
        checkStatVarValues(statDf, Stats.THEORETICAL, emptyList())
    }

    @Test
    fun withFewNanValuesQQLine() {
        val sampleValues = listOf(1.0, null, null, -1.0)
        val df = dataFrame(mapOf(
            TransformVar.SAMPLE to sampleValues
        ))
        val stat = Stats.qqline()
        val statDf = stat.apply(df, statContext(df))

        val dist = QQStatUtil.getDistribution(QQStat.DEF_DISTRIBUTION, QQStat.DEF_DISTRIBUTION_PARAMETERS)
        checkStatVarValues(statDf, Stats.SAMPLE, listOf(0.0, 0.0))
        checkStatVarValues(statDf, Stats.THEORETICAL, listOf(
            dist.inverseCumulativeProbability(0.25),
            dist.inverseCumulativeProbability(0.75)
        ))
    }

    @Test
    fun emptyQQ2DataFrame() {
        testEmptyDataFrame(Stats.qq2())
    }

    @Test
    fun oneElementQQ2DataFrame() {
        val xSampleValue = 3.14
        val ySampleValue = 2.72
        val df = dataFrame(mapOf(
            TransformVar.X to listOf(xSampleValue),
            TransformVar.Y to listOf(ySampleValue)
        ))
        val stat = Stats.qq2()
        val statDf = stat.apply(df, statContext(df))

        checkStatVarValues(statDf, Stats.X, listOf(xSampleValue))
        checkStatVarValues(statDf, Stats.Y, listOf(ySampleValue))
    }

    @Test
    fun withOnlyNanValuesQQ2() {
        val nanValues = listOf(null, null, null)
        val nonNanValues = listOf(3.14, 2.72, 1.41)
        for ((xValues, yValues) in listOf(
            Pair(nanValues, nonNanValues),
            Pair(nonNanValues, nanValues),
            Pair(nanValues, nanValues),
        )) {
            val df = dataFrame(mapOf(
                TransformVar.X to xValues,
                TransformVar.Y to yValues
            ))
            val stat = Stats.qq2()
            val statDf = stat.apply(df, statContext(df))

            checkStatVarValues(statDf, Stats.X, emptyList())
            checkStatVarValues(statDf, Stats.Y, emptyList())
        }
    }

    @Test
    fun withFewNanValuesQQ2() {
        val xValues = listOf(1.0, 0.0, null, null)
        val yValues = listOf(null, null, null, -1.0)
        val df = dataFrame(mapOf(
            TransformVar.X to xValues,
            TransformVar.Y to yValues
        ))
        val stat = Stats.qq2()
        val statDf = stat.apply(df, statContext(df))

        checkStatVarValues(statDf, Stats.X, listOf(0.5, 0.5))
        checkStatVarValues(statDf, Stats.Y, listOf(-1.0, -1.0))
    }

    @Test
    fun emptyQQ2LineDataFrame() {
        testEmptyDataFrame(Stats.qq2line())
    }

    @Test
    fun oneElementQQ2LineDataFrame() {
        val xSampleValue = 3.14
        val ySampleValue = 2.72
        val df = dataFrame(mapOf(
            TransformVar.X to listOf(xSampleValue),
            TransformVar.Y to listOf(ySampleValue)
        ))
        val stat = Stats.qq2line()
        val statDf = stat.apply(df, statContext(df))

        checkStatVarValues(statDf, Stats.X, listOf(xSampleValue, xSampleValue))
        checkStatVarValues(statDf, Stats.Y, listOf(ySampleValue, ySampleValue))
    }

    @Test
    fun withOnlyNanValuesQQ2Line() {
        val nanValues = listOf(null, null, null)
        val nonNanValues = listOf(3.14, 2.72, 1.41)
        for ((xValues, yValues) in listOf(
            Pair(nanValues, nonNanValues),
            Pair(nonNanValues, nanValues),
            Pair(nanValues, nanValues),
        )) {
            val df = dataFrame(mapOf(
                TransformVar.X to xValues,
                TransformVar.Y to yValues
            ))
            val stat = Stats.qq2line()
            val statDf = stat.apply(df, statContext(df))

            checkStatVarValues(statDf, Stats.X, emptyList())
            checkStatVarValues(statDf, Stats.Y, emptyList())
        }
    }

    @Test
    fun withFewNanValuesQQ2Line() {
        val xSampleValues = listOf(-1.0, null, null, 1.0)
        val ySampleValues = listOf(null, -2.0, 2.0, null)
        val df = dataFrame(mapOf(
            TransformVar.X to xSampleValues,
            TransformVar.Y to ySampleValues
        ))
        val stat = Stats.qq2line()
        val statDf = stat.apply(df, statContext(df))

        checkStatVarValues(statDf, Stats.X, listOf(0.0, 0.0))
        checkStatVarValues(statDf, Stats.Y, listOf(-2.0, 2.0))
    }
}