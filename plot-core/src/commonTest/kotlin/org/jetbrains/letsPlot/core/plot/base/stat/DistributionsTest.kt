/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat

import org.jetbrains.letsPlot.core.plot.base.stat.math3.*
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
        assertEquals(3.1671241833116014e-5, dist.cumulativeProbability(-4.0), DEFAULT_ACCURACY)
        assertEquals(0.15865525393145702, dist.cumulativeProbability(-1.0), DEFAULT_ACCURACY)
        assertEquals(0.5, dist.cumulativeProbability(0.0), DEFAULT_ACCURACY)
        assertEquals(0.841344746068543, dist.cumulativeProbability(1.0), DEFAULT_ACCURACY)
        assertEquals(0.9999683287581669, dist.cumulativeProbability(4.0), DEFAULT_ACCURACY)
        assertEquals(1.0, dist.cumulativeProbability(Double.POSITIVE_INFINITY))
    }

    @Test
    fun normalQuantileFunctionTest() {
        val dist = NormalDistribution(0.0, 1.0)
        assertEquals(Double.NEGATIVE_INFINITY, dist.inverseCumulativeProbability(0.0))
        assertEquals(-3.71901648545568, dist.inverseCumulativeProbability(0.0001), DEFAULT_ACCURACY)
        assertEquals(-0.5244005127080407, dist.inverseCumulativeProbability(0.3), DEFAULT_ACCURACY)
        assertEquals(0.0, dist.inverseCumulativeProbability(0.5), DEFAULT_ACCURACY)
        assertEquals(0.5244005127080407, dist.inverseCumulativeProbability(0.7), DEFAULT_ACCURACY)
        assertEquals(3.7190164854557084, dist.inverseCumulativeProbability(0.9999), DEFAULT_ACCURACY)
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
        val accuracy = 1e-15
        val dist = TDistribution(1.0)
        assertEquals(0.0, dist.cumulativeProbability(Double.NEGATIVE_INFINITY))
        assertEquals(0.077979130377369, dist.cumulativeProbability(-4.0), accuracy)
        assertEquals(0.25, dist.cumulativeProbability(-1.0), accuracy)
        assertEquals(0.5, dist.cumulativeProbability(0.0), accuracy)
        assertEquals(0.75, dist.cumulativeProbability(1.0), accuracy)
        assertEquals(0.922020869622631, dist.cumulativeProbability(4.0), accuracy)
        assertEquals(1.0, dist.cumulativeProbability(Double.POSITIVE_INFINITY))
    }

    @Test
    fun tQuantileFunctionTest() {
        val accuracy = TDistribution.DEFAULT_INVERSE_ABSOLUTE_ACCURACY
        val dist = TDistribution(1.0)
        assertEquals(Double.NEGATIVE_INFINITY, dist.inverseCumulativeProbability(0.0))
        assertEquals(-31.820515954, dist.inverseCumulativeProbability(0.01), accuracy)
        assertEquals(-0.726542528, dist.inverseCumulativeProbability(0.3), accuracy)
        assertEquals(0.0, dist.inverseCumulativeProbability(0.5), accuracy)
        assertEquals(0.726542528, dist.inverseCumulativeProbability(0.7), accuracy)
        assertEquals(31.820515954, dist.inverseCumulativeProbability(0.99), accuracy)
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
        assertEquals(0.0, dist.cumulativeProbability(-1.0), DEFAULT_ACCURACY)
        assertEquals(0.0, dist.cumulativeProbability(0.0), DEFAULT_ACCURACY)
        assertEquals(0.632120558828558, dist.cumulativeProbability(1.0), DEFAULT_ACCURACY)
        assertEquals(0.9816843611112658, dist.cumulativeProbability(4.0), DEFAULT_ACCURACY)
        assertEquals(0.9999546000702375, dist.cumulativeProbability(10.0), DEFAULT_ACCURACY)
        assertEquals(1.0, dist.cumulativeProbability(Double.POSITIVE_INFINITY))
    }

    @Test
    fun gammaQuantileFunctionTest() {
        val accuracy = GammaDistribution.DEFAULT_INVERSE_ABSOLUTE_ACCURACY
        val dist = GammaDistribution(1.0, 1.0)
        assertEquals(0.0, dist.inverseCumulativeProbability(0.0))
        assertEquals(0.000100005, dist.inverseCumulativeProbability(0.0001), accuracy)
        assertEquals(0.356674944, dist.inverseCumulativeProbability(0.3), accuracy)
        assertEquals(0.693147181, dist.inverseCumulativeProbability(0.5), accuracy)
        assertEquals(1.203972804, dist.inverseCumulativeProbability(0.7), accuracy)
        assertEquals(9.210340372, dist.inverseCumulativeProbability(0.9999), accuracy)
        assertEquals(Double.POSITIVE_INFINITY, dist.inverseCumulativeProbability(1.0))
    }


    companion object {
        const val DEFAULT_ACCURACY = 1e-16
    }
}