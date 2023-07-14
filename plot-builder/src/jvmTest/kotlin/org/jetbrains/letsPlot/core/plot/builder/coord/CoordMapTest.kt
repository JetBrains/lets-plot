/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.coord

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import kotlin.test.BeforeTest
import kotlin.test.Test

internal class CoordMapTest : CoordTestBase() {

    @BeforeTest
    fun setUp() {
        dataBounds = DoubleRectangle(DoubleVector.ZERO, DoubleVector(50.0, 50.0))
    }

    @Test
    fun applyScales() {
        // Map coord translates a square to a rectangle.
        run {
            val ratio = 2.0
            tryApplyScales(
                ratio, PROVIDER,
                expectedMin = DoubleVector(0.0, 0.0),
                expectedMax = DoubleVector(1.0, 1.1581),
                accuracy = DoubleVector(1.0E-10, 0.0001)
            )
        }
        run {
            val ratio = 0.5
            tryApplyScales(
                ratio, PROVIDER,
                expectedMin = DoubleVector(0.0, 0.0),
                expectedMax = DoubleVector(0.8634, 1.0),
                accuracy = DoubleVector(0.0001, 1.0E-10)
            )
        }
    }

    companion object {
        private val PROVIDER = CoordProviders.map()
    }
}