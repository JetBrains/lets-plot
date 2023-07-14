/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat

import org.jetbrains.letsPlot.core.plot.base.DataFrame
import kotlin.test.*

class DensityStatUtilTest : BaseStatTest() {
    @Test
    fun statQuantileForEmptyData() {
        val statSample = emptyList<Double>()
        val statDensity = emptyList<Double>()
        val quantiles = listOf(0.0, 0.5, 1.0)
        val statQuantile = DensityStatUtil.calculateStatQuantile(statSample, statDensity, quantiles)
        assertContentEquals(emptyList(), statQuantile, "Quantiles stat isn't calculated correctly when data is empty")
    }

    @Test
    fun statQuantileWithDifferentQuantiles() {
        val statSample = listOf(0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8)
        val statDensity = listOf(0.0, 0.1, 0.2, 0.3, 0.4, 0.3, 0.2, 0.1, 0.0)
        val quantiles = listOf(0.0, 0.5, 1.0)
        val statQuantile = DensityStatUtil.calculateStatQuantile(statSample, statDensity, quantiles)
        assertContentEquals(
            listOf(0.0, 0.5, 0.5, 0.5, 1.0, 1.0, 1.0, 1.0, 1.0),
            statQuantile,
            "Quantiles stat isn't calculated correctly"
        )
    }

    @Test
    fun statQuantileWithUnsortedQuantiles() {
        val statSample = listOf(0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8)
        val statDensity = listOf(0.0, 0.1, 0.2, 0.3, 0.4, 0.3, 0.2, 0.1, 0.0)
        val quantiles = listOf(0.0, 1.0, 0.5)
        val statQuantile = DensityStatUtil.calculateStatQuantile(statSample, statDensity, quantiles)
        assertContentEquals(
            listOf(0.0, 0.5, 0.5, 0.5, 1.0, 1.0, 1.0, 1.0, 1.0),
            statQuantile,
            "Quantiles stat isn't calculated correctly when quantiles is unsorted"
        )
    }

    @Test
    fun statQuantileWithDuplicatedQuantiles() {
        val statSample = listOf(0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8)
        val statDensity = listOf(0.0, 0.1, 0.2, 0.3, 0.4, 0.3, 0.2, 0.1, 0.0)
        val quantiles = listOf(0.0, 0.0, 0.5, 0.5, 1.0, 1.0)
        val statQuantile = DensityStatUtil.calculateStatQuantile(statSample, statDensity, quantiles)
        assertContentEquals(
            listOf(0.0, 0.5, 0.5, 0.5, 1.0, 1.0, 1.0, 1.0, 1.0),
            statQuantile,
            "Quantiles stat isn't calculated correctly when there is duplicates in quantiles parameter"
        )
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
}