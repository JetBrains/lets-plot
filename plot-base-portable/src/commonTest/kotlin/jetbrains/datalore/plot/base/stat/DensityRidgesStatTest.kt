/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import jetbrains.datalore.plot.base.data.TransformVar
import kotlin.test.Test

class DensityRidgesStatTest : BaseStatTest() {

    private fun densityRidgesStat(trim: Boolean = true, quantiles: List<Double>? = null): DensityRidgesStat {
        return DensityRidgesStat(
            trim = trim,
            tailsCutoff = null,
            bandWidth = null,
            bandWidthMethod = DensityStat.DEF_BW,
            adjust = DensityStat.DEF_ADJUST,
            kernel = DensityStat.DEF_KERNEL,
            n = DensityStat.DEF_N,
            fullScanMax = DensityStat.DEF_FULL_SCAN_MAX,
            quantiles = quantiles ?: DensityRidgesStat.DEF_QUANTILES
        )
    }

    @Test
    fun emptyDataFrame() {
        testEmptyDataFrame(densityRidgesStat())
    }

    @Test
    fun oneElementDataFrame() {
        val xValue = 3.14
        val df = dataFrame(mapOf(
            TransformVar.X to listOf(xValue)
        ))
        val stat = densityRidgesStat()
        val statDf = stat.normalize(stat.apply(df, statContext(df)))

        checkStatVarDomain(statDf, Stats.Y, setOf(0.0))
        checkStatVarMaxValue(statDf, Stats.HEIGHT, 1.0)
    }

    @Test
    fun twoElementsInDataFrame() {
        val x = listOf(2.71, 3.14)
        val df = dataFrame(mapOf(
            TransformVar.X to x
        ))
        val stat = densityRidgesStat()
        val statDf = stat.normalize(stat.apply(df, statContext(df)))

        checkStatVarRange(statDf, Stats.X, DoubleSpan(2.71, 3.14))
        checkStatVarDomain(statDf, Stats.Y, setOf(0.0))
        checkStatVarMaxValue(statDf, Stats.HEIGHT, 1.0)
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

        checkStatVarRange(statDf, Stats.X, DoubleSpan(0.0, 3.0))
        checkStatVarDomain(statDf, Stats.Y, setOf(1.0, 2.0, 3.0))
        checkStatVarMaxValue(statDf, Stats.HEIGHT, 1.0)
    }
}