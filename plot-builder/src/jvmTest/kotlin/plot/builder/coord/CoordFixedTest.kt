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
        fun squareCoord(
            xDomain: ClosedRange<Double> = ClosedRange(0.0, 25.0),
            yDomain: ClosedRange<Double> = ClosedRange(0.0, 25.0),
            xLim: ClosedRange<Double>? = null,
            yLim: ClosedRange<Double>? = null,
            displaySize: DoubleVector = DoubleVector(800.0, 600.0)
        ): Pair<ClosedRange<Double>, ClosedRange<Double>> {
            return CoordProviders
                .fixed(1.0, xLim, yLim)
                .adjustDomains(xDomain, yDomain, displaySize)
        }

        // xLim
        run {
            // rectangular domain
            squareCoord(
                xDomain = ClosedRange(0.0, 40.0),
                yDomain = ClosedRange(0.0,20.0),
                xLim = ClosedRange(0.0, 10.0),
                displaySize = DoubleVector(40.0, 40.0)
            ).let { (adjustedXDomain, adjustedYDomain) ->
                assertEquals(ClosedRange(0.0, 10.0), adjustedXDomain)
                assertEquals(ClosedRange(5.0, 15.0), adjustedYDomain)
            }

            // rectangular domain with wide range from negative to positive
            squareCoord(
                xDomain = ClosedRange(-192.93730128989847, 195.64188153543947),
                yDomain = ClosedRange(-61.15901229903106, 86.57141452655196),
                xLim = ClosedRange(-130.0, 20.0),
                displaySize = DoubleVector(394.3, 291.0)
            ).let { (adjustedXDomain, adjustedYDomain) ->
                assertEquals(ClosedRange(-130.0, 20.0), adjustedXDomain)
                assertEquals(ClosedRange(-42.645054275537035, 68.05745650305794), adjustedYDomain)
            }

            // zero length y-domain
            squareCoord(
                xDomain = ClosedRange(0.0, 20.0),
                yDomain = ClosedRange(0.0, 0.0),
                xLim = ClosedRange(5.0, 15.0)
            ).let { (adjustedXDomain, adjustedYDomain) ->
                assertEquals(ClosedRange(5.0, 15.0), adjustedXDomain)
                assertEquals(ClosedRange(-3.75, 3.75), adjustedYDomain)
            }

            // xLim in range
            squareCoord(xLim = ClosedRange(1.0, 4.0))
                .let { (adjustedXDomain, adjustedYDomain) ->
                    assertEquals(ClosedRange(1.0, 4.0), adjustedXDomain)
                    assertEquals(ClosedRange(11.375, 13.625), adjustedYDomain)
                }

            // xLim wider than range
            squareCoord(xLim = ClosedRange(-3.0, 4.0))
                .let { (adjustedXDomain, adjustedYDomain) ->
                    assertEquals(ClosedRange(-3.0, 4.0), adjustedXDomain)
                    assertEquals(ClosedRange(9.875, 15.125), adjustedYDomain)
                }

            // xLim out of range
            squareCoord(xLim = ClosedRange(-9.0, -4.0))
                .let { (adjustedXDomain, adjustedYDomain) ->
                    assertEquals(ClosedRange(-9.0, -4.0), adjustedXDomain)
                    assertEquals(ClosedRange(10.625, 14.375), adjustedYDomain)
                }
        }

        // yLim
        run {
            // yLim in range
            squareCoord(yLim = ClosedRange(1.0, 4.0))
                .let { (adjustedXDomain, adjustedYDomain) ->
                    assertEquals(ClosedRange(10.5, 14.5), adjustedXDomain)
                    assertEquals(ClosedRange(1.0, 4.0), adjustedYDomain)
                }

            // yLim wider than range
            squareCoord(yLim = ClosedRange(-3.0, 6.0))
                .let { (adjustedXDomain, adjustedYDomain) ->
                    assertEquals(ClosedRange(6.5, 18.5), adjustedXDomain)
                    assertEquals(ClosedRange(-3.0, 6.0), adjustedYDomain)
                }

            // yLim out of range
            squareCoord(yLim = ClosedRange(-9.0, -6.0))
                .let { (adjustedXDomain, adjustedYDomain) ->
                    assertEquals(ClosedRange(10.5, 14.5), adjustedXDomain)
                    assertEquals(ClosedRange(-9.0, -6.0), adjustedYDomain)
                }
        }


        // xLim && yLim
        run {
            // intersecting, wider yLim wins
            squareCoord(xLim = ClosedRange(10.0, 15.0), yLim = ClosedRange(9.0, 16.0))
                .let { (adjustedXDomain, adjustedYDomain) ->
                    assertEquals(ClosedRange(7.833, 17.166), adjustedXDomain)
                    assertEquals(ClosedRange(9.0, 16.0), adjustedYDomain)
                }

            // intersecting, wider xLim wins
            squareCoord(xLim = ClosedRange(9.0, 16.0), yLim = ClosedRange(12.0, 14.0))
                .let { (adjustedXDomain, adjustedYDomain) ->
                    assertEquals(ClosedRange(9.0, 16.0), adjustedXDomain)
                    assertEquals(ClosedRange(10.375, 15.625), adjustedYDomain)
                }

            // non-intersecting, wider xLim wins
            squareCoord(xLim = ClosedRange(9.0, 16.0), yLim = ClosedRange(19.0, 22.0))
                .let { (adjustedXDomain, adjustedYDomain) ->
                    assertEquals(ClosedRange(9.0, 16.0), adjustedXDomain)
                    assertEquals(ClosedRange(17.875, 23.125), adjustedYDomain)
                }

            // non-intersecting, wider yLim wins
            squareCoord(xLim = ClosedRange(15.0, 16.0), yLim = ClosedRange(18.0, 22.0))
                .let { (adjustedXDomain, adjustedYDomain) ->
                    assertEquals(ClosedRange(12.833, 18.166), adjustedXDomain)
                    assertEquals(ClosedRange(18.0, 22.0), adjustedYDomain)
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