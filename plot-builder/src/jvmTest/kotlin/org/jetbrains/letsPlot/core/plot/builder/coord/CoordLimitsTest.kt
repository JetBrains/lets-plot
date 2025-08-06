/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.coord

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CoordLimitsTest {

    private val dataDomain = DoubleRectangle(DoubleSpan(0.0, 4.0), DoubleSpan(0.0, 5.0))

    @Test
    fun testCartesianLimits() {
        testLimits(
            createProvider = { xLim, yLim ->
                CoordProviders.cartesian(
                    xLim,
                    yLim,
                    xReversed = false,
                    yReversed = false
                )
            },
            dataDomain = dataDomain
        )
    }

    @Test
    fun testFixedLimits() {
        testLimits(
            createProvider = { xLim, yLim ->
                CoordProviders.fixed(
                    1.0,
                    xLim,
                    yLim,
                    xReversed = false,
                    yReversed = false
                )
            },
            dataDomain = dataDomain
        )
    }

    @Test
    fun testPolarLimits() {
        testLimits(
            createProvider = { xLim, yLim ->
                CoordProviders.polar(
                    xLim,
                    yLim,
                    isTest = true
                )
            },
            dataDomain = dataDomain
        )
    }

    @Test
    fun testReversedAxes() {
        // Test with reversed x-axis
        val provider1 = CoordProviders.cartesian(
            xReversed = true
        )
        val adjusted1 = provider1.adjustDomain(dataDomain)
        assertEquals(dataDomain.xRange().lowerEnd, adjusted1.xRange().lowerEnd)
        assertEquals(dataDomain.xRange().upperEnd, adjusted1.xRange().upperEnd)

        // Test with reversed y-axis
        val provider2 = CoordProviders.cartesian(
            yReversed = true
        )
        val adjusted2 = provider2.adjustDomain(dataDomain)
        assertEquals(dataDomain.yRange().lowerEnd, adjusted2.yRange().lowerEnd)
        assertEquals(dataDomain.yRange().upperEnd, adjusted2.yRange().upperEnd)

        // Test limits with reversed axes
        val provider3 = CoordProviders.cartesian(
            xLim = Pair(-1.0, 6.0),
            yLim = Pair(-2.0, 7.0),
            xReversed = true,
            yReversed = true
        )
        val adjusted3 = provider3.adjustDomain(dataDomain)
        assertEquals(-1.0, adjusted3.xRange().lowerEnd)
        assertEquals(6.0, adjusted3.xRange().upperEnd)
        assertEquals(-2.0, adjusted3.yRange().lowerEnd)
        assertEquals(7.0, adjusted3.yRange().upperEnd)
    }

    private fun testLimits(
        createProvider: (Pair<Double?, Double?>, Pair<Double?, Double?>) -> CoordProvider,
        dataDomain: DoubleRectangle
    ) {
        // Test null limits (should keep the original domain)
        val provider1 = createProvider(Pair(null, null), Pair(null, null))
        val adjusted1 = provider1.adjustDomain(dataDomain)
        assertEquals(dataDomain.xRange().lowerEnd, adjusted1.xRange().lowerEnd)
        assertEquals(dataDomain.xRange().upperEnd, adjusted1.xRange().upperEnd)
        assertEquals(dataDomain.yRange().lowerEnd, adjusted1.yRange().lowerEnd)
        assertEquals(dataDomain.yRange().upperEnd, adjusted1.yRange().upperEnd)

        // Test partial limits (only upper bound)
        val provider2 = createProvider(Pair(null, 6.0), Pair(null, 7.0))
        val adjusted2 = provider2.adjustDomain(dataDomain)
        assertEquals(dataDomain.xRange().lowerEnd, adjusted2.xRange().lowerEnd)
        assertEquals(6.0, adjusted2.xRange().upperEnd)
        assertEquals(dataDomain.yRange().lowerEnd, adjusted2.yRange().lowerEnd)
        assertEquals(7.0, adjusted2.yRange().upperEnd)

        // Test partial limits (only lower bound)
        val provider3 = createProvider(Pair(-1.0, null), Pair(-2.0, null))
        val adjusted3 = provider3.adjustDomain(dataDomain)
        assertEquals(-1.0, adjusted3.xRange().lowerEnd)
        assertEquals(dataDomain.xRange().upperEnd, adjusted3.xRange().upperEnd)
        assertEquals(-2.0, adjusted3.yRange().lowerEnd)
        assertEquals(dataDomain.yRange().upperEnd, adjusted3.yRange().upperEnd)

        // Test full limits
        val provider4 = createProvider(Pair(-1.0, 6.0), Pair(-2.0, 7.0))
        val adjusted4 = provider4.adjustDomain(dataDomain)
        assertEquals(-1.0, adjusted4.xRange().lowerEnd)
        assertEquals(6.0, adjusted4.xRange().upperEnd)
        assertEquals(-2.0, adjusted4.yRange().lowerEnd)
        assertEquals(7.0, adjusted4.yRange().upperEnd)

        // Test invalid limits (lower > upper)
        assertFailsWith<IllegalArgumentException> {
            createProvider(Pair(6.0, 4.0), Pair(null, null)).adjustDomain(dataDomain)
        }

        // Test invalid limits (lower bound > data upper bound)
        assertFailsWith<IllegalArgumentException> {
            createProvider(Pair(6.0, null), Pair(null, null)).adjustDomain(dataDomain)
        }
    }
}