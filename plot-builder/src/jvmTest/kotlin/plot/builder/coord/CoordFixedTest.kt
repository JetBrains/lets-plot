/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.coord

import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import org.junit.Ignore
import kotlin.math.abs
import kotlin.math.min
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.fail

@Ignore
internal class CoordFixedTest : jetbrains.datalore.plot.builder.coord.CoordTestBase() {

    @BeforeTest
    fun setUp() {
        dataBounds = DoubleRectangle(DoubleVector.ZERO, DATA_SPAN)
    }

    @Test
    fun limits() {
        fun squareCoord(
            xDomain: DoubleSpan,// = DoubleSpan(0.0, 25.0),
            yDomain: DoubleSpan,// = DoubleSpan(0.0, 25.0),
            xLim: DoubleSpan? = null,
            yLim: DoubleSpan? = null,
            displaySize: DoubleVector = DoubleVector(800.0, 600.0)
        ): Pair<DoubleSpan, DoubleSpan> {
            return CoordProviders
                .fixed(1.0, xLim, yLim)
//                .adjustDomains(xDomain, yDomain, displaySize)
                // The `adjustDomains` has different meaning now!!!
                .adjustDomains(xDomain, yDomain)
        }

        fun squareCoord_0_25(
            xLim: DoubleSpan? = null,
            yLim: DoubleSpan? = null,
            displaySize: DoubleVector = DoubleVector(800.0, 600.0)
        ): Pair<DoubleSpan, DoubleSpan> {
            return squareCoord(
                DoubleSpan(0.0, 25.0),
                DoubleSpan(0.0, 25.0),
                xLim, yLim, displaySize
            )
        }


        // xLim
        run {
            // rectangular domain
            squareCoord(
                xDomain = DoubleSpan(0.0, 40.0),
                yDomain = DoubleSpan(0.0, 20.0),
                xLim = DoubleSpan(0.0, 10.0),
                displaySize = DoubleVector(40.0, 40.0)
            ).let { (adjustedXDomain, adjustedYDomain) ->
                assertEquals(DoubleSpan(-5.0, 15.0), adjustedXDomain) // adjusted to square
                assertEquals(DoubleSpan(0.0, 20.0), adjustedYDomain) // unchanged
            }

            // rectangular domain with wide range from negative to positive
            squareCoord(
                xDomain = DoubleSpan(-193.0, 195.0),
                yDomain = DoubleSpan(-61.0, 86.0),
                xLim = DoubleSpan(-130.0, 20.0),
                displaySize = DoubleVector(394.3, 291.0)
            ).let { (adjustedXDomain, adjustedYDomain) ->
                assertEquals(DoubleSpan(-154.59123711340206, 44.59123711340206), adjustedXDomain) // adjusted to square
                assertEquals(DoubleSpan(-61.0, 86.0), adjustedYDomain) // unchanged
            }

            // zero length y-domain
            squareCoord(
                xDomain = DoubleSpan(0.0, 20.0),
                yDomain = DoubleSpan(0.0, 0.0),
                xLim = DoubleSpan(5.0, 15.0)
            ).let { (adjustedXDomain, adjustedYDomain) ->
                assertEquals(DoubleSpan(5.0, 15.0), adjustedXDomain)  // lims
                assertEquals(DoubleSpan(0.0, 0.0), adjustedYDomain)   // unchanged
            }

            // limit larger than x-domain with zero length y-domain
            squareCoord(
                xDomain = DoubleSpan(100.0, 120.0),
                yDomain = DoubleSpan(0.0, 0.0),
                xLim = DoubleSpan(80.0, 140.0)
            ).let { (adjustedXDomain, adjustedYDomain) ->
                assertEquals(DoubleSpan(80.0, 140.0), adjustedXDomain) // lims
                assertEquals(DoubleSpan(0.0, 0.0), adjustedYDomain)   // unchanged
            }

            // limit larger than x-domain with non-zero length y-domain
            squareCoord(
                xDomain = DoubleSpan(100.0, 120.0),
                yDomain = DoubleSpan(-5.0, 5.0),
                xLim = DoubleSpan(80.0, 140.0)
            ).let { (adjustedXDomain, adjustedYDomain) ->
                assertEquals(DoubleSpan(80.0, 140.0), adjustedXDomain)  // lims
                assertEquals(DoubleSpan(-22.5, 22.5), adjustedYDomain)  // adjusted to square
            }

            // xLim in range
            squareCoord_0_25(xLim = DoubleSpan(1.0, 4.0))
                .let { (adjustedXDomain, adjustedYDomain) ->
                    assertEquals(
                        DoubleSpan(-14.166666666666664, 19.166666666666664),
                        adjustedXDomain
                    ) // adjusted to square
                    assertEquals(DoubleSpan(0.0, 25.0), adjustedYDomain) // unchanged
                }

            // xLim wider than range
            squareCoord_0_25(xLim = DoubleSpan(-3.0, 4.0))
                .let { (adjustedXDomain, adjustedYDomain) ->
                    assertEquals(
                        DoubleSpan(-16.166666666666664, 17.166666666666664),
                        adjustedXDomain
                    ) // adjusted to square
                    assertEquals(DoubleSpan(0.0, 25.0), adjustedYDomain) // unchanged
                }

            // xLim out of range
            squareCoord_0_25(xLim = DoubleSpan(-9.0, -4.0))
                .let { (adjustedXDomain, adjustedYDomain) ->
                    assertEquals(DoubleSpan(-23.166666666666664, 10.166666666666664), adjustedXDomain)
                    assertEquals(DoubleSpan(0.0, 25.0), adjustedYDomain) // unchanged
                }
        }

        // yLim
        run {
            // yLim in range
            squareCoord_0_25(yLim = DoubleSpan(1.0, 4.0))
                .let { (adjustedXDomain, adjustedYDomain) ->
                    assertEquals(DoubleSpan(0.0, 25.0), adjustedXDomain) // unchanged
                    assertEquals(DoubleSpan(-6.875, 11.875), adjustedYDomain)  // adjusted to square
                }

            // yLim wider than range
            squareCoord_0_25(yLim = DoubleSpan(-3.0, 6.0))
                .let { (adjustedXDomain, adjustedYDomain) ->
                    assertEquals(DoubleSpan(0.0, 25.0), adjustedXDomain) // unchanged
                    assertEquals(DoubleSpan(-7.875, 10.875), adjustedYDomain) // adjusted to square
                }

            // yLim out of range
            squareCoord_0_25(yLim = DoubleSpan(-9.0, -6.0))
                .let { (adjustedXDomain, adjustedYDomain) ->
                    assertEquals(DoubleSpan(0.0, 25.0), adjustedXDomain) // unchanged
                    assertEquals(DoubleSpan(-16.875, 1.875), adjustedYDomain)   // adjusted to square
                }
        }


        // xLim && yLim
        run {
            // intersecting, wider yLim wins
            squareCoord_0_25(xLim = DoubleSpan(10.0, 15.0), yLim = DoubleSpan(9.0, 16.0))
                .let { (adjustedXDomain, adjustedYDomain) ->
                    assertEquals(DoubleSpan(7.833, 17.166), adjustedXDomain)
                    assertEquals(DoubleSpan(9.0, 16.0), adjustedYDomain)
                }

            // intersecting, wider xLim wins
            squareCoord_0_25(xLim = DoubleSpan(9.0, 16.0), yLim = DoubleSpan(12.0, 14.0))
                .let { (adjustedXDomain, adjustedYDomain) ->
                    assertEquals(DoubleSpan(9.0, 16.0), adjustedXDomain)
                    assertEquals(DoubleSpan(10.375, 15.625), adjustedYDomain)
                }

            // non-intersecting, wider xLim wins
            squareCoord_0_25(xLim = DoubleSpan(9.0, 16.0), yLim = DoubleSpan(19.0, 22.0))
                .let { (adjustedXDomain, adjustedYDomain) ->
                    assertEquals(DoubleSpan(9.0, 16.0), adjustedXDomain)
                    assertEquals(DoubleSpan(17.875, 23.125), adjustedYDomain)
                }

            // non-intersecting, wider yLim wins
            squareCoord_0_25(xLim = DoubleSpan(15.0, 16.0), yLim = DoubleSpan(18.0, 22.0))
                .let { (adjustedXDomain, adjustedYDomain) ->
                    assertEquals(DoubleSpan(12.833, 18.166), adjustedXDomain)
                    assertEquals(DoubleSpan(18.0, 22.0), adjustedYDomain)
                }
        }
    }

    @Test
    fun adjustDomains() {
        // fixed ratio == 1 (equal X and Y)
        val dataBounds = dataBounds
        val rangeX = dataBounds.xRange()
        val rangeY = dataBounds.yRange()

        tryAdjustDomains(
            2.0,
            PROVIDER_EQUAL_XY, rangeX,
            expand(rangeY, 2.0)
        )
        tryAdjustDomains(
            0.5,
            PROVIDER_EQUAL_XY,
            expand(rangeX, 2.0), rangeY
        )

        // stretched Y
        run {
            // two ratios compensate
            val ratio = 2.0
            tryAdjustDomains(
                ratio,
                PROVIDER_2x_Y, rangeX, rangeY
            )
        }
        run {
            // two ratios multiply
            val ratio = .5
            val expectedX = expand(
                rangeX,
                2.0 * (1 / ratio)
            )  // coord system (2) + display (2)
            tryAdjustDomains(
                ratio,
                PROVIDER_2x_Y, expectedX, rangeY
            )
        }

        // stretched X
        run {
            // two ratios multiply
            val ratio = 2.0
            val expectedY = expand(
                rangeX,
                2.0 * ratio
            )  // coord system (2) + display (2)
            tryAdjustDomains(
                ratio,
                PROVIDER_2x_X, rangeX, expectedY
            )
        }
        run {
            // two ratios multiply
            val ratio = .5
            val expectedX = expand(
                rangeX,
                2.0 * (1 / ratio)
            ) // coord system (2) + display (2)
            tryAdjustDomains(
                ratio,
                PROVIDER_2x_Y, expectedX, rangeY
            )
        }
    }

    @Test
    fun applyScales() {
        // Square grid fit into the display
        tryApplyScales(
            2.0,
            PROVIDER_EQUAL_XY, DoubleVector(1.0, 1.0)
        )
        tryApplyScales(
            .5,
            PROVIDER_EQUAL_XY, DoubleVector(1.0, 1.0)
        )

        tryApplyScales(
            2.0,
            PROVIDER_2x_Y, DoubleVector(1.0, 2.0)
        )
        tryApplyScales(
            .5,
            PROVIDER_2x_Y, DoubleVector(.5, 1.0)
        )

        tryApplyScales(
            2.0,
            PROVIDER_2x_X, DoubleVector(1.0, .5)
        )
        tryApplyScales(
            .5,
            PROVIDER_2x_X, DoubleVector(2.0, 1.0)
        )
    }

    private fun tryApplyScales(ratio: Double, provider: CoordProvider, multiplier: DoubleVector) {
        val shortSide = shortSideOfDisplay(ratio)
        tryApplyScales(
            ratio, provider,
            DoubleVector(0.0, 0.0),
            DoubleVector(shortSide * multiplier.x, shortSide * multiplier.y),
            DoubleVector.ZERO
        )
    }

    private fun shortSideOfDisplay(ratio: Double): Double {
        val displaySize = unitDisplaySize(ratio)
        return min(displaySize.x, displaySize.y)
    }

    private fun DoubleSpan.equals(other: DoubleSpan, epsilon: Double = 0.00001): Boolean {
        fun doubleEquals(expected: Double, actual: Double, epsilon: Double) = abs(expected - actual) < epsilon

        return doubleEquals(lowerEnd, other.lowerEnd, epsilon) &&
                doubleEquals(upperEnd, other.upperEnd, epsilon)
    }

    private fun assertEquals(expected: DoubleSpan, actual: DoubleSpan, epsilon: Double = 0.001) {
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