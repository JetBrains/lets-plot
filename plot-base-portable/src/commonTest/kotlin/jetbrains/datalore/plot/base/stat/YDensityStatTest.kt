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

class YDensityStatTest {
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

    private fun filteredDataFrame(df: DataFrame, variable: DataFrame.Variable, filterFun: (Double?) -> Boolean): DataFrame {
        val indices = df.getNumeric(variable)
            .mapIndexed { index, v -> if (filterFun(v)) index else null }
            .filterNotNull()

        return df.selectIndices(indices)
    }

    private fun yDensityStat(scale: YDensityStat.Scale? = null): YDensityStat {
        return YDensityStat(
            scale = scale ?: YDensityStat.DEF_SCALE,
            bandWidth = null,
            bandWidthMethod = DensityStat.DEF_BW,
            adjust = DensityStat.DEF_ADJUST,
            kernel = DensityStat.DEF_KERNEL,
            n = DensityStat.DEF_N,
            fullScanMax = DensityStat.DEF_FULL_SCAN_MAX
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

    private fun checkStatVarAndMaxLimit(statDf: DataFrame, variable: DataFrame.Variable, expectedMaxLimit: Double) {
        checkStatVar(statDf, variable)
        val actualMaxValue = statDf.getNumeric(variable).maxByOrNull { it!! }!!
        assertTrue(expectedMaxLimit - actualMaxValue > 0, "Max value of var " + variable.name + " limited")
    }

    @Test
    fun emptyDataFrame() {
        val df = dataFrame(emptyMap())
        val stat = yDensityStat()
        val statDf = stat.normalize(stat.apply(df, statContext(df)))

        checkStatVarAndValuesDomain(statDf, Stats.X, emptySet())
        checkStatVarAndValuesDomain(statDf, Stats.Y, emptySet())
        checkStatVarAndValuesDomain(statDf, Stats.VIOLIN_WIDTH, emptySet())
    }

    @Test
    fun oneElementDataFrame() {
        val yValue = 3.14
        val df = dataFrame(mapOf(
            TransformVar.Y to listOf(yValue)
        ))
        val stat = yDensityStat()
        val statDf = stat.normalize(stat.apply(df, statContext(df)))

        checkStatVarAndValuesDomain(statDf, Stats.X, setOf(0.0))
        checkStatVarAndMaxValue(statDf, Stats.VIOLIN_WIDTH, 1.0)
    }

    @Test
    fun twoElementsInDataFrame() {
        val y = listOf(2.71, 3.14)
        val df = dataFrame(mapOf(
            TransformVar.Y to y
        ))
        val stat = yDensityStat()
        val statDf = stat.normalize(stat.apply(df, statContext(df)))

        checkStatVarAndValuesDomain(statDf, Stats.X, setOf(0.0))
        checkStatVarAndValuesRange(statDf, Stats.Y, DoubleSpan(2.71, 3.14))
        checkStatVarAndMaxValue(statDf, Stats.VIOLIN_WIDTH, 1.0)
    }

    @Test
    fun withNanValues() {
        val x = listOf(null, 4.0, 3.0, 3.0, 1.0, 1.0, 2.0, 2.0)
        val y = listOf(3.0, null, 2.0, 3.0, 0.0, 1.0, 1.0, 2.0)
        val df = dataFrame(mapOf(
            TransformVar.X to x,
            TransformVar.Y to y
        ))
        val stat = yDensityStat()
        val statDf = stat.normalize(stat.apply(df, statContext(df)))

        checkStatVarAndValuesDomain(statDf, Stats.X, setOf(1.0, 2.0, 3.0))
        checkStatVarAndValuesRange(statDf, Stats.Y, DoubleSpan(0.0, 3.0))
        checkStatVarAndMaxValue(statDf, Stats.VIOLIN_WIDTH, 1.0)
    }

    @Test
    fun changeScales() {
        val x = listOf(0.0, 0.0, 0.0, 0.0, 1.0, 1.0)
        val y = listOf(0.0, 1.0, 2.0, 3.0, 0.0, 1.0)
        val df = dataFrame(mapOf(
            TransformVar.X to x,
            TransformVar.Y to y
        ))

        for (scale in YDensityStat.Scale.values()) {
            val stat = yDensityStat(scale = scale)
            val statDf = stat.normalize(stat.apply(df, statContext(df)))
            val statDf0 = filteredDataFrame(statDf, Stats.X) { it == 0.0 }
            val statDf1 = filteredDataFrame(statDf, Stats.X) { it == 1.0 }

            checkStatVarAndValuesDomain(statDf, Stats.X, setOf(0.0, 1.0))
            checkStatVarAndValuesRange(statDf0, Stats.Y, DoubleSpan(0.0, 3.0))
            checkStatVarAndValuesRange(statDf1, Stats.Y, DoubleSpan(0.0, 1.0))
            when (scale) {
                YDensityStat.Scale.AREA -> {
                    checkStatVarAndMaxLimit(statDf0, Stats.VIOLIN_WIDTH, 0.5)
                    checkStatVarAndMaxValue(statDf1, Stats.VIOLIN_WIDTH, 1.0)
                }
                YDensityStat.Scale.COUNT -> {
                    checkStatVarAndMaxLimit(statDf0, Stats.VIOLIN_WIDTH, 0.5)
                    checkStatVarAndMaxValue(statDf1, Stats.VIOLIN_WIDTH, 0.5)
                }
                YDensityStat.Scale.WIDTH -> {
                    checkStatVarAndMaxValue(statDf0, Stats.VIOLIN_WIDTH, 1.0)
                    checkStatVarAndMaxValue(statDf1, Stats.VIOLIN_WIDTH, 1.0)
                }
            }
        }
    }
}