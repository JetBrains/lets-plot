package jetbrains.datalore.visualization.plot.gog.core.data.stat

import jetbrains.datalore.visualization.plot.gog.common.data.SeriesUtil
import jetbrains.datalore.visualization.plot.gog.core.data.*
import jetbrains.datalore.visualization.plot.gog.server.core.data.stat.StatsServerSide
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.*

class DensityStatTest {

    private fun statContext(d: DataFrame): StatContext {
        return SimpleStatContext(d)
    }

    private fun generateNormalDatapointsWithFixedEnds(n: Int, mu: Double, stddev: Double, halfRange: Double): List<Double> {
        val gaussian = ArrayList<Double>()
        val random = Random()
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

        val df = DataFrameUtil.fromMap(mapOf(
                TransformVar.X.name to test
        ))

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

            assertEquals(n.toLong(), statDf[Stats.X].size.toLong())
            assertEquals(n.toLong(), statDf[Stats.DENSITY].size.toLong())
            assertEquals(n.toLong(), statDf[Stats.COUNT].size.toLong())
            assertEquals(n.toLong(), statDf[Stats.SCALED].size.toLong())

            assertEquals(1.0, SeriesUtil.sum(statDf.getNumeric(Stats.DENSITY)) * binWidth, .01) //integral is one
            assertEquals(length.toDouble(), SeriesUtil.sum(statDf.getNumeric(Stats.COUNT)) * binWidth, length / 100.0) //integral is the number of data points
            assertEquals(1.0, Collections.max<Double>(statDf.getNumeric(Stats.SCALED)), 0.0) //maximum is one
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

        val df = DataFrameUtil.fromMap(mapOf(
                TransformVar.X.name to testX,
                TransformVar.Y.name to testY
        ))

        val n1 = 512
        val n2 = 256
        val binArea = SeriesUtil.span(SeriesUtil.range(testX)!!) / (n1 - 1) * SeriesUtil.span(SeriesUtil.range(testY)!!) / (n2 - 1)

        val stat = StatsServerSide.density2d()
        stat.nx = n1
        stat.ny = n2
        stat.isContour = false

        val statDf = stat.apply(df, statContext(df))
        assertTrue(statDf.has(Stats.X))
        assertTrue(statDf.has(Stats.Y))
        assertTrue(statDf.has(Stats.DENSITY))

        assertEquals((n1 * n2).toLong(), statDf[Stats.X].size.toLong())
        assertEquals((n1 * n2).toLong(), statDf[Stats.Y].size.toLong())
        assertEquals((n1 * n2).toLong(), statDf[Stats.DENSITY].size.toLong())

        assertEquals(1.0, SeriesUtil.sum(statDf.getNumeric(Stats.DENSITY)) * binArea, .01) //integral is one
    }
}