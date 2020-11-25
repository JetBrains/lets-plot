/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat

import jetbrains.datalore.base.assertion.assertEquals
import jetbrains.datalore.base.random.RandomGaussian
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.StatContext
import jetbrains.datalore.plot.base.data.DataFrameUtil
import jetbrains.datalore.plot.base.data.TransformVar
import jetbrains.datalore.plot.common.data.SeriesUtil
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DensityStatTest {

    private fun statContext(d: DataFrame): StatContext {
        return SimpleStatContext(d)
    }

    private fun generateNormalDatapointsWithFixedEnds(
        n: Int,
        mu: Double,
        stddev: Double,
        halfRange: Double
    ): List<Double> {
        val gaussian = ArrayList<Double>()
        val random = RandomGaussian(Random)
        //random.setSeed(43);
        for (i in 0 until n - 2) {
            gaussian.add(random.nextGaussian() * stddev + mu)
        }
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
        val binWidth = SeriesUtil.span(SeriesUtil.range(test)!!) / (n - 1)

        val stat = DensityStat()
        stat.setN(n)

        for (kernel in DensityStat.Kernel.values()) { //test for different kernels
            stat.setKernel(kernel)
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
            SeriesUtil.span(SeriesUtil.range(testX)!!) / (n1 - 1) * SeriesUtil.span(
                SeriesUtil.range(testY)!!) / (n2 - 1)

        val stat = Stats.density2d()
        stat.nx = n1
        stat.ny = n2
        stat.isContour = false

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