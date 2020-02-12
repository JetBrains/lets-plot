/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.coord

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import kotlin.math.min
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

internal class CoordFixedTest : jetbrains.datalore.plot.builder.coord.CoordTestBase() {

    @BeforeTest
    fun setUp() {
        dataBounds = DoubleRectangle(DoubleVector.ZERO, DATA_SPAN)
    }

    @Test
    fun limits() {
        val dataBounds = DoubleRectangle(0.0, 0.0, 5.0, 5.0)
        val screenSize = DoubleVector(800.0, 600.0)
        val (xDomain, yDomain) = CoordProviders
            .fixed(1.0, ClosedRange.closed(1.0, 4.0))
            .adjustDomains(dataBounds.xRange(), dataBounds.yRange(), screenSize)

        assertEquals(ClosedRange.closed(800.0 * 0.2, 800.0 * 0.8), xDomain)
        assertEquals(ClosedRange.closed(600.0 * 0.2, 600.0 * 0.8), yDomain)
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

    companion object {
        private val PROVIDER_EQUAL_XY = CoordProviders.fixed(1.0)
        private val PROVIDER_2x_Y = CoordProviders.fixed(2.0)
        private val PROVIDER_2x_X = CoordProviders.fixed(0.5)

        private val DATA_SPAN = DoubleVector(10.0, 10.0)
    }
}