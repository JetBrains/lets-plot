/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.coord

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import kotlin.math.min
import kotlin.test.BeforeTest
import kotlin.test.Ignore
import kotlin.test.Test

@org.junit.Ignore
internal class CoordMapTest : jetbrains.datalore.plot.builder.coord.CoordTestBase() {

    @BeforeTest
    fun setUp() {
        dataBounds = DoubleRectangle(DoubleVector.ZERO,
            DATA_SPAN
        )
    }

    @Test
    @Ignore("coord_map aspect ratio is variable (not 1.0): test need to be updated")
    fun adjustDomains() {
        // Coord Map keeps fixed ratio == 1 (equal X and Y)
        val dataBounds = dataBounds
        tryAdjustDomains(2.0,
            PROVIDER, dataBounds.xRange(),
            expand(dataBounds.yRange(), 2.0)
        )
        tryAdjustDomains(0.5,
            PROVIDER,
            expand(dataBounds.xRange(), 2.0), dataBounds.yRange())
    }

    @Test
    @Ignore("coord_map aspect ratio is variable (not 1.0): test need to be updated")
    fun applyScales() {
        // Map coord tries to keep grid square regardless of the display form factor
        run {
            val ratio = 2.0
            val shortSide = shortSideOfDisplay(ratio)
            tryApplyScales(ratio, PROVIDER,
                    DoubleVector(0.0, 0.0), DoubleVector(shortSide, shortSide), DoubleVector(0.0, 1.0E-2))
        }
        run {
            val ratio = 0.5
            val shortSide = shortSideOfDisplay(ratio)
            tryApplyScales(ratio, PROVIDER,
                    DoubleVector(0.0, 0.0), DoubleVector(shortSide, shortSide), DoubleVector(0.0, 1.0E-5))
        }
    }

    private fun shortSideOfDisplay(ratio: Double): Double {
        val displaySize = unitDisplaySize(ratio)
        return min(displaySize.x, displaySize.y)
    }

    companion object {
        private val PROVIDER = CoordProviders.map()

        private val DATA_SPAN = DoubleVector(10.0, 10.0)
    }
}