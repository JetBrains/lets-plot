/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat

import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.StatContext
import jetbrains.datalore.plot.base.data.TransformVar
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DensityRidgesStatTest {
    private fun statContext(d: DataFrame): StatContext {
        return SimpleStatContext(d)
    }

    private fun dataFrame(dataMap: Map<DataFrame.Variable, List<Double?>>): DataFrame {
        val builder = DataFrame.Builder()
        for (key in dataMap.keys) {
            builder.put(key, dataMap.getValue(key))
        }
        return builder.build()
    }

    private fun densityRidgesStat(trim: Boolean = true, quantiles: List<Double>? = null): DensityRidgesStat {
        return DensityRidgesStat(
            trim = trim,
            bandWidth = null,
            bandWidthMethod = DensityStat.DEF_BW,
            adjust = DensityStat.DEF_ADJUST,
            kernel = DensityStat.DEF_KERNEL,
            n = DensityStat.DEF_N,
            fullScanMax = DensityStat.DEF_FULL_SCAN_MAX,
            quantiles = quantiles ?: DensityRidgesStat.DEF_QUANTILES
        )
    }

    private fun checkStatVar(statDf: DataFrame, variable: DataFrame.Variable) {
        assertTrue(statDf.has(variable), "Has var " + variable.name)
    }

    private fun checkStatVarAndValuesDomain(statDf: DataFrame, variable: DataFrame.Variable, expectedValuesDomain: Set<Double?>) {
        checkStatVar(statDf, variable)
        assertEquals(statDf.getNumeric(variable).toSet(), expectedValuesDomain, "Unique values of var " + variable.name)
    }

    private fun checkStatVarAndValuesRange(statDf: DataFrame, variable: DataFrame.Variable, expectedValuesRange: DoubleSpan) {
        checkStatVar(statDf, variable)
        val actualMinValue = statDf.getNumeric(variable).minByOrNull { it!! }!!
        assertEquals(expectedValuesRange.lowerEnd, actualMinValue, "Min value of var " + variable.name)
        val actualMaxValue = statDf.getNumeric(variable).maxByOrNull { it!! }!!
        assertEquals(expectedValuesRange.upperEnd, actualMaxValue, "Max value of var " + variable.name)
    }

    private fun checkStatVarAndMaxValue(statDf: DataFrame, variable: DataFrame.Variable, expectedMaxValue: Double) {
        checkStatVar(statDf, variable)
        val actualMaxValue = statDf.getNumeric(variable).maxByOrNull { it!! }!!
        assertEquals(expectedMaxValue, actualMaxValue, "Max value of var " + variable.name)
    }

    @Test
    fun emptyDataFrame() {
        val df = dataFrame(emptyMap())
        val stat = densityRidgesStat()
        val statDf = stat.normalize(stat.apply(df, statContext(df)))

        checkStatVarAndValuesDomain(statDf, Stats.X, emptySet())
        checkStatVarAndValuesDomain(statDf, Stats.Y, emptySet())
        checkStatVarAndValuesDomain(statDf, Stats.HEIGHT, emptySet())
    }

    @Test
    fun oneElementDataFrame() {
        val xValue = 3.14
        val df = dataFrame(mapOf(
            TransformVar.X to listOf(xValue)
        ))
        val stat = densityRidgesStat()
        val statDf = stat.normalize(stat.apply(df, statContext(df)))

        checkStatVarAndValuesDomain(statDf, Stats.Y, setOf(0.0))
        checkStatVarAndMaxValue(statDf, Stats.HEIGHT, 1.0)
    }

    @Test
    fun twoElementsInDataFrame() {
        val x = listOf(2.71, 3.14)
        val df = dataFrame(mapOf(
            TransformVar.X to x
        ))
        val stat = densityRidgesStat()
        val statDf = stat.normalize(stat.apply(df, statContext(df)))

        checkStatVarAndValuesRange(statDf, Stats.X, DoubleSpan(2.71, 3.14))
        checkStatVarAndValuesDomain(statDf, Stats.Y, setOf(0.0))
        checkStatVarAndMaxValue(statDf, Stats.HEIGHT, 1.0)
    }

    @Test
    fun withNanValues() {
        val x = listOf(3.0, null, 2.0, 3.0, 0.0, 1.0, 1.0, 2.0)
        val y = listOf(null, 4.0, 3.0, 3.0, 1.0, 1.0, 2.0, 2.0)
        val df = dataFrame(mapOf(
            TransformVar.X to x,
            TransformVar.Y to y
        ))
        val stat = densityRidgesStat()
        val statDf = stat.normalize(stat.apply(df, statContext(df)))

        checkStatVarAndValuesRange(statDf, Stats.X, DoubleSpan(0.0, 3.0))
        checkStatVarAndValuesDomain(statDf, Stats.Y, setOf(1.0, 2.0, 3.0))
        checkStatVarAndMaxValue(statDf, Stats.HEIGHT, 1.0)
    }
}