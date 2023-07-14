/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.coord

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import kotlin.math.abs
import kotlin.math.min
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.fail

internal class CoordFixedTest : CoordTestBase() {

    @BeforeTest
    fun setUp() {
        dataBounds = DoubleRectangle(DoubleVector.ZERO, DATA_SPAN)
    }

    @Test
    fun limits() {
        fun squareCoord(
            xDomain: DoubleSpan,
            yDomain: DoubleSpan,
            xLim: DoubleSpan? = null,
            yLim: DoubleSpan? = null,
            displaySize: DoubleVector
        ): DoubleVector {
            val coordProvider = CoordProviders.fixed(1.0, xLim, yLim)
            val adjustedDomain = coordProvider.adjustDomain(DoubleRectangle(xDomain, yDomain))
            return coordProvider.adjustGeomSize(
                adjustedDomain.xRange(),
                adjustedDomain.yRange(),
                displaySize
            )
        }

        fun squareCoord_0_25(
            xLim: DoubleSpan? = null,
            yLim: DoubleSpan? = null,
            displaySize: DoubleVector
        ): DoubleVector {
            return squareCoord(
                DoubleSpan(0.0, 25.0),
                DoubleSpan(0.0, 25.0),
                xLim, yLim, displaySize
            )
        }

        // xLim
        run {
            squareCoord(
                xDomain = DoubleSpan(0.0, 40.0),
                yDomain = DoubleSpan(0.0, 20.0),
                displaySize = DoubleVector(40.0, 40.0)
            ).let { geomSize ->
                assertEquals(DoubleVector(40.0, 20.0), geomSize)
            }

            squareCoord(
                xDomain = DoubleSpan(0.0, 40.0),
                yDomain = DoubleSpan(0.0, 20.0),
                xLim = DoubleSpan(0.0, 10.0),
                displaySize = DoubleVector(40.0, 40.0)
            ).let { geomSize ->
                assertEquals(DoubleVector(20.0, 40.0), geomSize)
            }

            squareCoord(
                xDomain = DoubleSpan(-20.0, 20.0),
                yDomain = DoubleSpan(-10.0, 10.0),
                displaySize = DoubleVector(40.0, 40.0)
            ).let { geomSize ->
                assertEquals(DoubleVector(40.0, 20.0), geomSize)
            }

            // zero length y-domain
// zero-length domains are not supported.
//            squareCoord(
//                xDomain = DoubleSpan(0.0, 20.0),
//                yDomain = DoubleSpan(0.0, 0.0),
//                displaySize = DoubleVector(40.0, 40.0)
//            ).let { geomSize ->
//                assertEquals(DoubleVector(40.0, 0.0), geomSize)
//            }

            // limit larger than x-domain with zero length y-domain
// zero-length domains are not supported.
//            squareCoord(
//                xDomain = DoubleSpan(100.0, 120.0),
//                yDomain = DoubleSpan(0.0, 0.0),
//                xLim = DoubleSpan(80.0, 140.0),
//                displaySize = DoubleVector(40.0, 40.0)
//            ).let { geomSize ->
//                assertEquals(DoubleVector(40.0, 0.0), geomSize)
//            }

            // limit larger than x-domain
            squareCoord(
                xDomain = DoubleSpan(1.0, 2.0),
                yDomain = DoubleSpan(-10.0, 10.0),
                xLim = DoubleSpan(0.0, 40.0),
                displaySize = DoubleVector(40.0, 40.0)
            ).let { geomSize ->
                assertEquals(DoubleVector(40.0, 20.0), geomSize)
            }

            // xLim in range
            squareCoord_0_25(
                xLim = DoubleSpan(1.0, 6.0),
                displaySize = DoubleVector(10.0, 10.0)
            ).let { geomSize ->
                assertEquals(DoubleVector(2.0, 10.0), geomSize)
            }

            // xLim wider than range
            squareCoord_0_25(
                xLim = DoubleSpan(-15.0, 10.0),
                displaySize = DoubleVector(10.0, 10.0)
            ).let { geomSize ->
                assertEquals(DoubleVector(10.0, 10.0), geomSize)
            }

            // xLim out of range
            squareCoord_0_25(
                xLim = DoubleSpan(-30.0, -5.0),
                displaySize = DoubleVector(10.0, 10.0)
            ).let { geomSize ->
                assertEquals(DoubleVector(10.0, 10.0), geomSize)
            }
        }

        // yLim
        run {
            // yLim in range
            squareCoord_0_25(
                yLim = DoubleSpan(1.0, 6.0),
                displaySize = DoubleVector(10.0, 10.0)
            ).let { geomSize ->
                assertEquals(DoubleVector(10.0, 2.0), geomSize)
            }

            // yLim wider than range
            squareCoord_0_25(
                yLim = DoubleSpan(-15.0, 10.0),
                displaySize = DoubleVector(10.0, 10.0)
            ).let { geomSize ->
                assertEquals(DoubleVector(10.0, 10.0), geomSize)
            }

            // yLim out of range
            squareCoord_0_25(
                yLim = DoubleSpan(-30.0, -5.0),
                displaySize = DoubleVector(10.0, 10.0)
            ).let { geomSize ->
                assertEquals(DoubleVector(10.0, 10.0), geomSize)
            }
        }


        // xLim && yLim
        run {
            // wider yLim
            squareCoord_0_25(
                xLim = DoubleSpan(10.0, 15.0),
                yLim = DoubleSpan(10.0, 20.0),
                displaySize = DoubleVector(10.0, 10.0)
            ).let { geomSize ->
                assertEquals(DoubleVector(5.0, 10.0), geomSize)
            }

            // wider xLim
            squareCoord_0_25(
                xLim = DoubleSpan(10.0, 20.0),
                yLim = DoubleSpan(10.0, 15.0),
                displaySize = DoubleVector(10.0, 10.0)
            ).let { geomSize ->
                assertEquals(DoubleVector(10.0, 5.0), geomSize)
            }
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

    private fun DoubleVector.equals(other: DoubleVector, epsilon: Double = 0.00001): Boolean {
        fun doubleEquals(expected: Double, actual: Double, epsilon: Double) = abs(expected - actual) < epsilon

        return doubleEquals(x, other.x, epsilon) &&
                doubleEquals(y, other.y, epsilon)
    }

    private fun assertEquals(expected: DoubleSpan, actual: DoubleSpan, epsilon: Double = 0.001) {
        if (!expected.equals(actual, epsilon)) {
            fail("$expected != $actual")
        }
    }

    private fun assertEquals(expected: DoubleVector, actual: DoubleVector, epsilon: Double = 0.001) {
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