/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat

import jetbrains.datalore.plot.base.DataFrame
import kotlin.test.*

class DensityStatUtilTest : BaseStatTest() {
    private fun constructStatData(
        axisValues: List<Double> = emptyList(),
        groupValues: List<Double> = emptyList(),
        binValues: List<Double> = emptyList(),
        axisVar: DataFrame.Variable = Stats.X,
        groupVar: DataFrame.Variable = Stats.QUANTILE,
        binVar: DataFrame.Variable? = null
    ): MutableMap<DataFrame.Variable, List<Double>> {
        val statData = mutableMapOf(
            axisVar to axisValues,
            groupVar to groupValues,
        )
        if (binVar != null)
            statData[binVar] = binValues
        return statData
    }

    @Test
    fun expandEmptyData() {
        val statData = constructStatData(binVar = Stats.Y)
        val expandedStatData = DensityStatUtil.expandByGroupEnds(statData, Stats.X, Stats.QUANTILE, Stats.Y)
        val statDf = dataFrame(expandedStatData)
        checkStatVarValues(statDf, Stats.X, listOf())
        checkStatVarValues(statDf, Stats.QUANTILE, listOf())
        checkStatVarValues(statDf, Stats.Y, listOf())
    }

    @Test
    fun expandDataWith1Quantile() {
        val statData = constructStatData(
            axisValues = listOf(2.72, 3.14),
            groupValues = listOf(1.0, 1.0)
        )
        val expandedStatDf = dataFrame(DensityStatUtil.expandByGroupEnds(statData, Stats.X, Stats.QUANTILE))
        checkStatVarValues(expandedStatDf, Stats.X, listOf(2.72, 3.14))
        checkStatVarValues(expandedStatDf, Stats.QUANTILE, listOf(1.0, 1.0))
    }

    @Test
    fun expandDataWith2Quantiles() {
        val statData = constructStatData(
            axisValues = listOf(2.72, 3.14),
            groupValues = listOf(0.5, 1.0)
        )
        val expandedStatDf = dataFrame(DensityStatUtil.expandByGroupEnds(statData, Stats.X, Stats.QUANTILE))
        checkStatVarValues(expandedStatDf, Stats.X, listOf(2.72, 2.72, 3.14))
        checkStatVarValues(expandedStatDf, Stats.QUANTILE, listOf(0.5, 1.0, 1.0))
    }

    @Test
    fun expandDataWithBinVarAnd1Quantile() {
        val statData = constructStatData(
            axisValues = listOf(2.72, 3.14, -3.14, -2.72),
            groupValues = listOf(1.0, 1.0, 1.0, 1.0),
            binValues = listOf(2.0, 2.0, 0.0, 0.0),
            binVar = Stats.Y
        )
        val expandedStatDf = dataFrame(DensityStatUtil.expandByGroupEnds(statData, Stats.X, Stats.QUANTILE, Stats.Y))
        checkStatVarValues(expandedStatDf, Stats.X, listOf(2.72, 3.14, -3.14, -2.72))
        checkStatVarValues(expandedStatDf, Stats.QUANTILE, listOf(1.0, 1.0, 1.0, 1.0))
        checkStatVarValues(expandedStatDf, Stats.Y, listOf(2.0, 2.0, 0.0, 0.0))
    }

    @Test
    fun expandDataWithBinVarAnd2Quantiles() {
        val statData = constructStatData(
            axisValues = listOf(2.72, 3.14, -3.14, -2.72),
            groupValues = listOf(0.5, 1.0, 0.5, 1.0),
            binValues = listOf(2.0, 2.0, 0.0, 0.0),
            binVar = Stats.Y
        )
        val expandedStatDf = dataFrame(DensityStatUtil.expandByGroupEnds(statData, Stats.X, Stats.QUANTILE, Stats.Y))
        checkStatVarValues(expandedStatDf, Stats.X, listOf(2.72, 2.72, 3.14, -3.14, -3.14, -2.72))
        checkStatVarValues(expandedStatDf, Stats.QUANTILE, listOf(0.5, 1.0, 1.0, 0.5, 1.0, 1.0))
        checkStatVarValues(expandedStatDf, Stats.Y, listOf(2.0, 2.0, 2.0, 0.0, 0.0, 0.0))
    }
}