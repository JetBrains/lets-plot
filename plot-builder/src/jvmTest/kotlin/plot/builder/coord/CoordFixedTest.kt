/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.coord

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Pair
import kotlin.math.abs
import kotlin.math.min
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.fail

internal class CoordFixedTest : jetbrains.datalore.plot.builder.coord.CoordTestBase() {

    @BeforeTest
    fun setUp() {
        dataBounds = DoubleRectangle(DoubleVector.ZERO, DATA_SPAN)
    }

    @Test
    fun limits() {
        fun squareCoord(xLim: ClosedRange<Double>? = null, yLim: ClosedRange<Double>? = null): Pair<ClosedRange<Double>, ClosedRange<Double>> {
            val dataBounds = DoubleRectangle(DoubleVector.ZERO, DoubleVector(25.0, 25.0))
            val screenSize = DoubleVector(800.0, 600.0)
            val (xDomain, yDomain) = CoordProviders
                .fixed(1.0, xLim, yLim)
                .adjustDomains(dataBounds.xRange(), dataBounds.yRange(), screenSize)
            return Pair(xDomain, yDomain)
        }

        // xLim
        run {

            // xLim in range
            squareCoord(xLim = ClosedRange(1.0, 4.0))
                .let { (xDomain, yDomain) ->
                    assertEquals(ClosedRange(1.0, 4.0), xDomain)
                    assertEquals(ClosedRange(1.375, 3.625), yDomain)
                }

            // xLim wider than range
            squareCoord(xLim = ClosedRange(-3.0, 4.0))
                .let { (xDomain, yDomain) ->
                    assertEquals(ClosedRange(-3.0, 4.0), xDomain)
                    assertEquals(ClosedRange(-2.125, 3.125), yDomain)
                }

            // xLim out of range
            squareCoord(xLim = ClosedRange(-9.0, -4.0))
                .let { (xDomain, yDomain) ->
                    assertEquals(ClosedRange(-9.0, -4.0), xDomain)
                    assertEquals(ClosedRange(-8.375, -4.625), yDomain)
                }

        }

        // yLim
        run {
            // yLim in range
            squareCoord(yLim = ClosedRange(1.0, 4.0))
                .let { (xDomain, yDomain) ->
                    assertEquals(ClosedRange(0.5, 4.5), xDomain)
                    assertEquals(ClosedRange(1.0, 4.0), yDomain)
                }

            // yLim wider than range
            squareCoord(yLim = ClosedRange(-3.0, 6.0))
                .let { (xDomain, yDomain) ->
                    assertEquals(ClosedRange(-4.5, 7.5), xDomain)
                    assertEquals(ClosedRange(-3.0, 6.0), yDomain)
                }

            // yLim out of range
            squareCoord(yLim = ClosedRange(-9.0, -6.0))
                .let { (xDomain, yDomain) ->
                    assertEquals(ClosedRange(-9.5, -5.5), xDomain)
                    assertEquals(ClosedRange(-9.0, -6.0), yDomain)
                }
        }


        // xLim && yLim
        run {
            // intersecting, wider yLim wins
            squareCoord(xLim = ClosedRange(10.0, 15.0), yLim = ClosedRange(9.0, 16.0))
                .let { (xDomain, yDomain) ->
                    assertEquals(ClosedRange(7.833, 17.166), xDomain)
                    assertEquals(ClosedRange(9.0, 16.0), yDomain)
                }

            // intersecting, wider xLim wins
            squareCoord(xLim = ClosedRange(9.0, 16.0), yLim = ClosedRange(12.0, 14.0))
                .let { (xDomain, yDomain) ->
                    assertEquals(ClosedRange(9.0, 16.0), xDomain)
                    assertEquals(ClosedRange(10.375, 15.625), yDomain)
                }

            // non-intersecting, wider xLim wins
            squareCoord(xLim = ClosedRange(9.0, 16.0), yLim = ClosedRange(19.0, 22.0))
                .let { (xDomain, yDomain) ->
                    assertEquals(ClosedRange(9.0, 16.0), xDomain)
                    assertEquals(ClosedRange(17.875, 23.125), yDomain)
                }

            // non-intersecting, wider yLim wins
            squareCoord(xLim = ClosedRange(15.0, 16.0), yLim = ClosedRange(18.0, 22.0))
                .let { (xDomain, yDomain) ->
                    assertEquals(ClosedRange(12.833, 18.166), xDomain)
                    assertEquals(ClosedRange(18.0, 22.0), yDomain)
                }
        }
    }

    @Test
    fun adjustDomains() {
        // fixed ratio == 1 (equal X and Y)
        val dataBounds = dataBounds
        val rangeX = dataBounds.xRange()
        val rangeY = dataBounds.yRange()

        tryAdjustDomains(2.0,
            PROVIDER_EQUAL_XY, rangeX,
            expand(rangeY, 2.0)
        )
        tryAdjustDomains(0.5,
            PROVIDER_EQUAL_XY,
            expand(rangeX, 2.0), rangeY)

        // stretched Y
        run {
            // two ratios compensate
            val ratio = 2.0
            tryAdjustDomains(ratio,
                PROVIDER_2x_Y, rangeX, rangeY)
        }
        run {
            // two ratios multiply
            val ratio = .5
            val expectedX = expand(
                rangeX,
                2.0 * (1 / ratio)
            )  // coord system (2) + display (2)
            tryAdjustDomains(ratio,
                PROVIDER_2x_Y, expectedX, rangeY)
        }

        // stretched X
        run {
            // two ratios multiply
            val ratio = 2.0
            val expectedY = expand(
                rangeX,
                2.0 * ratio
            )  // coord system (2) + display (2)
            tryAdjustDomains(ratio,
                PROVIDER_2x_X, rangeX, expectedY)
        }
        run {
            // two ratios multiply
            val ratio = .5
            val expectedX = expand(
                rangeX,
                2.0 * (1 / ratio)
            ) // coord system (2) + display (2)
            tryAdjustDomains(ratio,
                PROVIDER_2x_Y, expectedX, rangeY)
        }
    }

    @Test
    fun applyScales() {
        // Square grid fit into the display
        tryApplyScales(2.0,
            PROVIDER_EQUAL_XY, DoubleVector(1.0, 1.0))
        tryApplyScales(.5,
            PROVIDER_EQUAL_XY, DoubleVector(1.0, 1.0))

        tryApplyScales(2.0,
            PROVIDER_2x_Y, DoubleVector(1.0, 2.0))
        tryApplyScales(.5,
            PROVIDER_2x_Y, DoubleVector(.5, 1.0))

        tryApplyScales(2.0,
            PROVIDER_2x_X, DoubleVector(1.0, .5))
        tryApplyScales(.5,
            PROVIDER_2x_X, DoubleVector(2.0, 1.0))
    }

    private fun tryApplyScales(ratio: Double, provider: CoordProvider, multiplier: DoubleVector) {
        val shortSide = shortSideOfDisplay(ratio)
        tryApplyScales(ratio, provider,
                DoubleVector(0.0, 0.0),
                DoubleVector(shortSide * multiplier.x, shortSide * multiplier.y),
                DoubleVector.ZERO)
    }

    private fun shortSideOfDisplay(ratio: Double): Double {
        val displaySize = unitDisplaySize(ratio)
        return min(displaySize.x, displaySize.y)
    }

    private fun ClosedRange<Double>.equals(other: ClosedRange<Double>, epsilon: Double = 0.00001): Boolean {
        fun doubleEquals(expected: Double, actual: Double, epsilon: Double) = abs(expected - actual) < epsilon

        return doubleEquals(lowerEndpoint(), other.lowerEndpoint(), epsilon) &&
                doubleEquals(upperEndpoint(), other.upperEndpoint(), epsilon)
    }

    private fun assertEquals(expected: ClosedRange<Double>, actual: ClosedRange<Double>, epsilon: Double = 0.001) {
        if (!expected.equals(actual, epsilon)) {
            fail("$expected != $actual")
        }
    }

    companion object {
        private val PROVIDER_EQUAL_XY = CoordProviders.fixed(1.0)
        private val PROVIDER_2x_Y = CoordProviders.fixed(2.0)
        private val PROVIDER_2x_X = CoordProviders.fixed(0.5)

        private val DATA_SPAN = DoubleVector(10.0, 10.0)
    }
}