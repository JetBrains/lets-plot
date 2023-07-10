/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.coord

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.junit.Ignore
import kotlin.test.BeforeTest
import kotlin.test.Test

internal class CoordCartesianTest : CoordTestBase() {

    @BeforeTest
    fun setUp() {
        dataBounds = DoubleRectangle(DoubleVector.ZERO, DoubleVector(10.0, 10.0))
    }

    @Test
    fun applyScales() {
        tryApplyScales(2.0)
        tryApplyScales(0.5)
    }

    private fun tryApplyScales(ratio: Double) {
        val displayMin = DoubleVector.ZERO
        val displayMax = displayMin.add(
            unitDisplaySize(
                ratio
            )
        )
        // data will fit to the display
        tryApplyScales(ratio,
            PROVIDER, displayMin, displayMax, DoubleVector.ZERO)
    }

    companion object {
        private val PROVIDER = CoordProviders.cartesian(null, null)
    }
}