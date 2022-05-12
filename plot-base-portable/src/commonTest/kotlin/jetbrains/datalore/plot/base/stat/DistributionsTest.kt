/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat

import jetbrains.datalore.plot.base.stat.math3.GammaDistribution
import jetbrains.datalore.plot.base.stat.math3.NormalDistribution
import jetbrains.datalore.plot.base.stat.math3.TDistribution
import jetbrains.datalore.plot.base.stat.math3.UniformDistribution
import kotlin.test.*

class DistributionsTest {

    @Test
    fun uniformParametersTest() {
        assertFailsWith<IllegalStateException> {
            UniformDistribution(1.0, 0.0)
        }
    }

    @Test
    fun uniformCDFTest() {
        val dist = UniformDistribution(0.0, 1.0)
        assertEquals(0.0, dist.cumulativeProbability(Double.NEGATIVE_INFINITY))
        assertEquals(0.0, dist.cumulativeProbability(-1.0))
        assertEquals(0.0, dist.cumulativeProbability(0.0))
        assertEquals(0.25, dist.cumulativeProbability(0.25))
        assertEquals(0.5, dist.cumulativeProbability(0.5))
        assertEquals(0.75, dist.cumulativeProbability(0.75))
        assertEquals(1.0, dist.cumulativeProbability(1.0))
        assertEquals(1.0, dist.cumulativeProbability(2.0))
        assertEquals(1.0, dist.cumulativeProbability(Double.POSITIVE_INFINITY))
    }

    @Test
    fun uniformQuantileFunctionTest() {
        val dist = UniformDistribution(0.0, 1.0)
        assertEquals(Double.NEGATIVE_INFINITY, dist.inverseCumulativeProbability(0.0))
        assertEquals(0.0001, dist.inverseCumulativeProbability(0.0001))
        assertEquals(0.3, dist.inverseCumulativeProbability(0.3))
        assertEquals(0.5, dist.inverseCumulativeProbability(0.5))
        assertEquals(0.7, dist.inverseCumulativeProbability(0.7))
        assertEquals(0.9999, dist.inverseCumulativeProbability(0.9999))
        assertEquals(Double.POSITIVE_INFINITY, dist.inverseCumulativeProbability(1.0))
    }

    @Test
    fun normalParametersTest() {
        assertFailsWith<IllegalStateException> {
            NormalDistribution(0.0, -1.0)
        }
        assertFailsWith<IllegalStateException> {
            NormalDistribution(0.0, 0.0)
        }
    }

    @Test
    fun normalCDFTest() {
        val dist = NormalDistribution(0.0, 1.0)
        assertEquals(0.0, dist.cumulativeProbability(Double.NEGATIVE_INFINITY))
        assertEquals(3.1671241833116014e-5, dist.cumulativeProbability(-4.0))
        assertEquals(0.15865525393145702, dist.cumulativeProbability(-1.0))
        assertEquals(0.5, dist.cumulativeProbability(0.0))
        assertEquals(0.841344746068543, dist.cumulativeProbability(1.0))
        assertEquals(0.9999683287581669, dist.cumulativeProbability(4.0))
        assertEquals(1.0, dist.cumulativeProbability(Double.POSITIVE_INFINITY))
    }

    @Test
    fun normalQuantileFunctionTest() {
        val dist = NormalDistribution(0.0, 1.0)
        assertEquals(Double.NEGATIVE_INFINITY, dist.inverseCumulativeProbability(0.0))
        assertEquals(-3.71901648545568, dist.inverseCumulativeProbability(0.0001))
        assertEquals(-0.5244005127080407, dist.inverseCumulativeProbability(0.3))
        assertEquals(0.0, dist.inverseCumulativeProbability(0.5))
        assertEquals(0.5244005127080407, dist.inverseCumulativeProbability(0.7))
        assertEquals(3.7190164854557084, dist.inverseCumulativeProbability(0.9999))
        assertEquals(Double.POSITIVE_INFINITY, dist.inverseCumulativeProbability(1.0))
    }

    @Test
    fun tParametersTest() {
        assertFailsWith<IllegalStateException> {
            TDistribution(-1.0)
        }
        assertFailsWith<IllegalStateException> {
            TDistribution(0.0)
        }
    }

    @Test
    fun tCDFTest() {
        val dist = TDistribution(1.0)
        assertEquals(0.0, dist.cumulativeProbability(Double.NEGATIVE_INFINITY))
        assertEquals(0.077979130377369, dist.cumulativeProbability(-4.0), 1e-15)
        assertEquals(0.25, dist.cumulativeProbability(-1.0), 1e-15)
        assertEquals(0.5, dist.cumulativeProbability(0.0))
        assertEquals(0.75, dist.cumulativeProbability(1.0), 1e-15)
        assertEquals(0.922020869622631, dist.cumulativeProbability(4.0), 1e-15)
        assertEquals(1.0, dist.cumulativeProbability(Double.POSITIVE_INFINITY))
    }

    @Test
    fun tQuantileFunctionTest() {
        val dist = TDistribution(1.0)
        assertEquals(Double.NEGATIVE_INFINITY, dist.inverseCumulativeProbability(0.0))
        assertEquals(-31.8205159538, dist.inverseCumulativeProbability(0.01), 1e-10)
        assertEquals(-0.726542528, dist.inverseCumulativeProbability(0.3), 1e-10)
        assertEquals(0.0, dist.inverseCumulativeProbability(0.5))
        assertEquals(0.726542528, dist.inverseCumulativeProbability(0.7), 1e-10)
        assertEquals(31.8205159538, dist.inverseCumulativeProbability(0.99), 1e-10)
        assertEquals(Double.POSITIVE_INFINITY, dist.inverseCumulativeProbability(1.0))
    }

    @Test
    fun gammaParametersTest() {
        assertFailsWith<IllegalStateException> {
            GammaDistribution(-1.0, 1.0)
        }
        assertFailsWith<IllegalStateException> {
            GammaDistribution(0.0, 1.0)
        }
        assertFailsWith<IllegalStateException> {
            GammaDistribution(1.0, -1.0)
        }
        assertFailsWith<IllegalStateException> {
            GammaDistribution(1.0, 0.0)
        }
    }

    @Test
    fun gammaCDFTest() {
        val dist = GammaDistribution(1.0, 1.0)
        assertEquals(0.0, dist.cumulativeProbability(Double.NEGATIVE_INFINITY))
        assertEquals(0.0, dist.cumulativeProbability(-1.0))
        assertEquals(0.0, dist.cumulativeProbability(0.0))
        assertEquals(0.632120558828558, dist.cumulativeProbability(1.0))
        assertEquals(0.9816843611112658, dist.cumulativeProbability(4.0))
        assertEquals(0.9999546000702375, dist.cumulativeProbability(10.0))
        assertEquals(1.0, dist.cumulativeProbability(Double.POSITIVE_INFINITY))
    }

    @Test
    fun gammaQuantileFunctionTest() {
        val dist = GammaDistribution(1.0, 1.0)
        assertEquals(0.0, dist.inverseCumulativeProbability(0.0))
        assertEquals(0.0001, dist.inverseCumulativeProbability(0.0001), 1e-5)
        assertEquals(0.35667, dist.inverseCumulativeProbability(0.3), 1e-5)
        assertEquals(0.69315, dist.inverseCumulativeProbability(0.5), 1e-5)
        assertEquals(1.20397, dist.inverseCumulativeProbability(0.7), 1e-5)
        assertEquals(9.21034, dist.inverseCumulativeProbability(0.9999), 1e-5)
        assertEquals(Double.POSITIVE_INFINITY, dist.inverseCumulativeProbability(1.0))
    }
}