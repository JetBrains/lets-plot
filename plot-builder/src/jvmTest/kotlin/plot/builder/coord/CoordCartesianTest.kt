/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.coord

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import org.junit.Ignore
import kotlin.test.BeforeTest
import kotlin.test.Test

@Ignore
internal class CoordCartesianTest : jetbrains.datalore.plot.builder.coord.CoordTestBase() {

    @BeforeTest
    fun setUp() {
        dataBounds = DoubleRectangle(DoubleVector.ZERO, DoubleVector(10.0, 10.0))
    }

    @Test
    fun adjustDomains() {
        val dataBounds = dataBounds
        // domains not changed
        tryAdjustDomains(2.0,
            PROVIDER, dataBounds.xRange(), dataBounds.yRange())
        tryAdjustDomains(0.5,
            PROVIDER, dataBounds.xRange(), dataBounds.yRange())
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