/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat

import org.jetbrains.letsPlot.core.stat.*
import kotlin.test.*

class DistributionsTest {

    @Test
    fun uniformParametersTest() {
        assertFailsWith<IllegalStateException> {
            uniformCDF(1.0, 0.0)
        }
    }

    @Test
    fun uniformCDFTest() {
        val cdf = uniformCDF(0.0, 1.0)
        assertEquals(0.0, cdf(Double.NEGATIVE_INFINITY))
        assertEquals(0.0, cdf(-1.0))
        assertEquals(0.0, cdf(0.0))
        assertEquals(0.25, cdf(0.25))
        assertEquals(0.5, cdf(0.5))
        assertEquals(0.75, cdf(0.75))
        assertEquals(1.0, cdf(1.0))
        assertEquals(1.0, cdf(2.0))
        assertEquals(1.0, cdf(Double.POSITIVE_INFINITY))
    }

    @Test
    fun uniformQuantileFunctionTest() {
        val quantileFunction = uniformQuantile(0.0, 1.0)
        assertEquals(Double.NEGATIVE_INFINITY, quantileFunction(0.0))
        assertEquals(0.0001, quantileFunction(0.0001))
        assertEquals(0.3, quantileFunction(0.3))
        assertEquals(0.5, quantileFunction(0.5))
        assertEquals(0.7, quantileFunction(0.7))
        assertEquals(0.9999, quantileFunction(0.9999))
        assertEquals(Double.POSITIVE_INFINITY, quantileFunction(1.0))
    }

    @Test
    fun normalParametersTest() {
        assertFailsWith<IllegalStateException> {
            normalCDF(0.0, -1.0)
        }
        assertFailsWith<IllegalStateException> {
            normalCDF(0.0, 0.0)
        }
    }

    @Test
    fun normalCDFTest() {
        val cdf = normalCDF(0.0, 1.0)
        assertEquals(0.0, cdf(Double.NEGATIVE_INFINITY))
        assertEquals(3.1671241833116014e-5, cdf(-4.0), DEFAULT_ACCURACY)
        assertEquals(0.15865525393145702, cdf(-1.0), DEFAULT_ACCURACY)
        assertEquals(0.5, cdf(0.0), DEFAULT_ACCURACY)
        assertEquals(0.841344746068543, cdf(1.0), DEFAULT_ACCURACY)
        assertEquals(0.9999683287581669, cdf(4.0), DEFAULT_ACCURACY)
        assertEquals(1.0, cdf(Double.POSITIVE_INFINITY))
    }

    @Test
    fun normalQuantileFunctionTest() {
        val quantileFunction = normalQuantile(0.0, 1.0)
        assertEquals(Double.NEGATIVE_INFINITY, quantileFunction(0.0))
        assertEquals(-3.71901648545568, quantileFunction(0.0001), DEFAULT_ACCURACY)
        assertEquals(-0.5244005127080407, quantileFunction(0.3), DEFAULT_ACCURACY)
        assertEquals(0.0, quantileFunction(0.5), DEFAULT_ACCURACY)
        assertEquals(0.5244005127080407, quantileFunction(0.7), DEFAULT_ACCURACY)
        assertEquals(3.7190164854557084, quantileFunction(0.9999), DEFAULT_ACCURACY)
        assertEquals(Double.POSITIVE_INFINITY, quantileFunction(1.0))
    }

    @Test
    fun tParametersTest() {
        assertFailsWith<IllegalStateException> {
            tCDF(-1.0)
        }
        assertFailsWith<IllegalStateException> {
            tCDF(0.0)
        }
    }

    @Test
    fun tCDFTest() {
        val accuracy = 1e-15
        val cdf = tCDF(1.0)
        assertEquals(0.0, cdf(Double.NEGATIVE_INFINITY))
        assertEquals(0.077979130377369, cdf(-4.0), accuracy)
        assertEquals(0.25, cdf(-1.0), accuracy)
        assertEquals(0.5, cdf(0.0), accuracy)
        assertEquals(0.75, cdf(1.0), accuracy)
        assertEquals(0.922020869622631, cdf(4.0), accuracy)
        assertEquals(1.0, cdf(Double.POSITIVE_INFINITY))
    }

    @Test
    fun tQuantileFunctionTest() {
        val accuracy = 1e-9
        val quantileFunction = tQuantile(1.0)
        assertEquals(Double.NEGATIVE_INFINITY, quantileFunction(0.0))
        assertEquals(-31.820515954, quantileFunction(0.01), accuracy)
        assertEquals(-0.726542528, quantileFunction(0.3), accuracy)
        assertEquals(0.0, quantileFunction(0.5), accuracy)
        assertEquals(0.726542528, quantileFunction(0.7), accuracy)
        assertEquals(31.820515954, quantileFunction(0.99), accuracy)
        assertEquals(Double.POSITIVE_INFINITY, quantileFunction(1.0))
    }

    @Test
    fun gammaParametersTest() {
        assertFailsWith<IllegalStateException> {
            gammaCDF(-1.0, 1.0)
        }
        assertFailsWith<IllegalStateException> {
            gammaCDF(0.0, 1.0)
        }
        assertFailsWith<IllegalStateException> {
            gammaCDF(1.0, -1.0)
        }
        assertFailsWith<IllegalStateException> {
            gammaCDF(1.0, 0.0)
        }
    }

    @Test
    fun gammaCDFTest() {
        val cdf = gammaCDF(1.0, 1.0)
        assertEquals(0.0, cdf(Double.NEGATIVE_INFINITY))
        assertEquals(0.0, cdf(-1.0), DEFAULT_ACCURACY)
        assertEquals(0.0, cdf(0.0), DEFAULT_ACCURACY)
        assertEquals(0.632120558828558, cdf(1.0), DEFAULT_ACCURACY)
        assertEquals(0.9816843611112658, cdf(4.0), DEFAULT_ACCURACY)
        assertEquals(0.9999546000702375, cdf(10.0), DEFAULT_ACCURACY)
        assertEquals(1.0, cdf(Double.POSITIVE_INFINITY))
    }

    @Test
    fun gammaQuantileFunctionTest() {
        val accuracy = 1e-9
        val quantileFunction = gammaQuantile(1.0, 1.0)
        assertEquals(0.0, quantileFunction(0.0))
        assertEquals(0.000100005, quantileFunction(0.0001), accuracy)
        assertEquals(0.356674944, quantileFunction(0.3), accuracy)
        assertEquals(0.693147181, quantileFunction(0.5), accuracy)
        assertEquals(1.203972804, quantileFunction(0.7), accuracy)
        assertEquals(9.210340372, quantileFunction(0.9999), accuracy)
        assertEquals(Double.POSITIVE_INFINITY, quantileFunction(1.0))
    }


    companion object {
        const val DEFAULT_ACCURACY = 1e-16
    }
}