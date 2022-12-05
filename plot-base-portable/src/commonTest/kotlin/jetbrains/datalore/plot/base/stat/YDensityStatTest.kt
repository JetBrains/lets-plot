/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat

import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.data.TransformVar
import kotlin.test.Test

class YDensityStatTest : BaseStatTest() {

    private fun yDensityStat(scale: YDensityStat.Scale? = null): YDensityStat {
        return YDensityStat(
            scale = scale ?: YDensityStat.DEF_SCALE,
            trim = YDensityStat.DEF_TRIM,
            tailsCutoff = YDensityStat.DEF_TAILS_CUTOFF,
            bandWidth = null,
            bandWidthMethod = DensityStat.DEF_BW,
            adjust = DensityStat.DEF_ADJUST,
            kernel = DensityStat.DEF_KERNEL,
            n = DensityStat.DEF_N,
            fullScanMax = DensityStat.DEF_FULL_SCAN_MAX
        )
    }

    private fun filteredDataFrame(df: DataFrame, variable: DataFrame.Variable, filterFun: (Double?) -> Boolean): DataFrame {
        val indices = df.getNumeric(variable)
            .mapIndexed { index, v -> if (filterFun(v)) index else null }
            .filterNotNull()

        return df.selectIndices(indices)
    }

    @Test
    fun emptyDataFrame() {
        testEmptyDataFrame(yDensityStat())
    }

    @Test
    fun oneElementDataFrame() {
        val yValue = 3.14
        val df = dataFrame(mapOf(
            TransformVar.Y to listOf(yValue)
        ))
        val stat = yDensityStat()
        val statDf = stat.normalize(stat.apply(df, statContext(df)))

        checkStatVarDomain(statDf, Stats.X, setOf(0.0))
        checkStatVarMaxValue(statDf, Stats.VIOLIN_WIDTH, 1.0)
    }

    @Test
    fun twoElementsInDataFrame() {
        val y = listOf(2.71, 3.14)
        val df = dataFrame(mapOf(
            TransformVar.Y to y
        ))
        val stat = yDensityStat()
        val statDf = stat.normalize(stat.apply(df, statContext(df)))

        checkStatVarDomain(statDf, Stats.X, setOf(0.0))
        checkStatVarRange(statDf, Stats.Y, DoubleSpan(2.71, 3.14))
        checkStatVarMaxValue(statDf, Stats.VIOLIN_WIDTH, 1.0)
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

        checkStatVarDomain(statDf, Stats.X, setOf(1.0, 2.0, 3.0))
        checkStatVarRange(statDf, Stats.Y, DoubleSpan(0.0, 3.0))
        checkStatVarMaxValue(statDf, Stats.VIOLIN_WIDTH, 1.0)
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

            checkStatVarDomain(statDf, Stats.X, setOf(0.0, 1.0))
            checkStatVarRange(statDf0, Stats.Y, DoubleSpan(0.0, 3.0))
            checkStatVarRange(statDf1, Stats.Y, DoubleSpan(0.0, 1.0))
            when (scale) {
                YDensityStat.Scale.AREA -> {
                    checkStatVarMaxLimit(statDf0, Stats.VIOLIN_WIDTH, 0.5)
                    checkStatVarMaxValue(statDf1, Stats.VIOLIN_WIDTH, 1.0)
                }
                YDensityStat.Scale.COUNT -> {
                    checkStatVarMaxLimit(statDf0, Stats.VIOLIN_WIDTH, 0.5)
                    checkStatVarMaxValue(statDf1, Stats.VIOLIN_WIDTH, 0.5)
                }
                YDensityStat.Scale.WIDTH -> {
                    checkStatVarMaxValue(statDf0, Stats.VIOLIN_WIDTH, 1.0)
                    checkStatVarMaxValue(statDf1, Stats.VIOLIN_WIDTH, 1.0)
                }
            }
        }
    }
}