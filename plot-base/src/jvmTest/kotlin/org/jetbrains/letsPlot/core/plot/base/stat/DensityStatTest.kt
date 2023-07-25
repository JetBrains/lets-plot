/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat

import demoAndTestShared.assertEquals
import org.jetbrains.letsPlot.commons.intern.random.RandomGaussian.Companion.normal
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.StatContext
import org.jetbrains.letsPlot.core.plot.base.data.DataFrameUtil
import org.jetbrains.letsPlot.core.plot.base.data.TransformVar
import org.jetbrains.letsPlot.core.plot.base.stat.DensityStat
import org.jetbrains.letsPlot.core.plot.base.stat.SimpleStatContext
import org.jetbrains.letsPlot.core.plot.base.stat.Stats
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DensityStatTest {

    private fun statContext(d: DataFrame): StatContext {
        return SimpleStatContext(d, emptyList())
    }

    private fun generateNormalDatapointsWithFixedEnds(
        n: Int,
        mu: Double,
        stddev: Double,
        halfRange: Double
    ): List<Double> {
        val gaussian = normal(
            count = n - 2,
            mean = mu,
            stddev
        ).toMutableList()

        gaussian.add(mu - halfRange)
        gaussian.add(mu + halfRange)
        return gaussian
    }

    @Test
    fun testDensityStat() {
        val length = 1000
        val mu = 0.0
        val stddev = 1.0
        val halfRange = 10 * stddev
        val test = generateNormalDatapointsWithFixedEnds(length, mu, stddev, halfRange)

        val df = DataFrameUtil.fromMap(
            mapOf(
                TransformVar.X.name to test
            )
        )

        val n = 512
        val binWidth = (SeriesUtil.range(test)!!).length / (n - 1)

        for (kernel in DensityStat.Kernel.values()) { //test for different kernels
            val stat = Stats.density(n = n, kernel = kernel, quantiles = emptyList())

            val statDf = stat.apply(df, statContext(df))
            assertTrue(statDf.has(Stats.X))
            assertTrue(statDf.has(Stats.DENSITY))
            assertTrue(statDf.has(Stats.COUNT))
            assertTrue(statDf.has(Stats.SCALED))

            assertEquals(n, statDf[Stats.X].size)
            assertEquals(n, statDf[Stats.DENSITY].size)
            assertEquals(n, statDf[Stats.COUNT].size)
            assertEquals(n, statDf[Stats.SCALED].size)

            assertEquals(1.0, SeriesUtil.sum(statDf.getNumeric(Stats.DENSITY)) * binWidth, .01) //integral is one
            assertEquals(
                length.toDouble(),
                SeriesUtil.sum(statDf.getNumeric(Stats.COUNT)) * binWidth,
                length / 100.0
            ) //integral is the number of data points
            assertEquals(1.0, statDf.getNumeric(Stats.SCALED).maxByOrNull { v -> v!! }, 0.0) //maximum is one
        }
    }

    @Test
    fun testDensity2dStat() {
        val length = 250
        val mu = 0.0
        val stddev = 1.0
        val halfRange = 10 * stddev
        val testX = generateNormalDatapointsWithFixedEnds(length, mu, stddev, halfRange)
        val testY = generateNormalDatapointsWithFixedEnds(length, mu, stddev, halfRange)

        val df = DataFrameUtil.fromMap(
            mapOf(
                TransformVar.X.name to testX,
                TransformVar.Y.name to testY
            )
        )

        val n1 = 512
        val n2 = 256
        val binArea =
            (SeriesUtil.range(testX)!!).length / (n1 - 1) * (SeriesUtil.range(testY)!!).length / (n2 - 1)

        val stat = Stats.density2d(nX = n1, nY = n2, isContour = false)

        val statDf = stat.apply(df, statContext(df))
        assertTrue(statDf.has(Stats.X))
        assertTrue(statDf.has(Stats.Y))
        assertTrue(statDf.has(Stats.DENSITY))

        assertEquals((n1 * n2), statDf[Stats.X].size)
        assertEquals((n1 * n2), statDf[Stats.Y].size)
        assertEquals((n1 * n2), statDf[Stats.DENSITY].size)

        assertEquals(1.0, SeriesUtil.sum(statDf.getNumeric(Stats.DENSITY)) * binArea, .01) //integral is one
    }
}