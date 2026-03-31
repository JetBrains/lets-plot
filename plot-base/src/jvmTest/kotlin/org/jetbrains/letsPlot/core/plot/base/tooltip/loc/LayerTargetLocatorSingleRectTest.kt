/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.tooltip.loc

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator.LookupSpace
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator.LookupStrategy
import org.jetbrains.letsPlot.core.plot.base.tooltip.TestUtil
import kotlin.test.Test


class LayerTargetLocatorSingleRectTest {

    @Test
    fun hoverXy() {
        val locator = createLocator(LookupStrategy.HOVER, LookupSpace.XY)

        TestUtil.assertObjects(locator, TestUtil.inside(RECT), RECT_KEY)

        // Not match
        TestUtil.assertEmpty(locator, TestUtil.outsideY(RECT))
        TestUtil.assertEmpty(locator, TestUtil.outsideX(RECT))
        TestUtil.assertEmpty(locator, TestUtil.outsideXY(RECT))
    }

    @Test
    fun nearestXy() {
        val locator = createLocator(LookupStrategy.NEAREST, LookupSpace.XY)

        TestUtil.assertObjects(locator, TestUtil.inside(RECT), RECT_KEY)
        TestUtil.assertObjects(locator, TestUtil.outsideY(RECT), RECT_KEY)
        TestUtil.assertObjects(locator, TestUtil.outsideX(RECT), RECT_KEY)
        TestUtil.assertObjects(locator, TestUtil.outsideXY(RECT), RECT_KEY)
    }

    @Test
    fun hoverXAndNearestXHaveSameBehaviour() {
        for (strategy in listOf(LookupStrategy.HOVER, LookupStrategy.NEAREST)) {
            val locator = createLocator(strategy, LookupSpace.X)
            TestUtil.assertObjects(locator, TestUtil.inside(RECT), RECT_KEY)
            TestUtil.assertObjects(locator, TestUtil.outsideY(RECT), RECT_KEY)

            TestUtil.assertEmpty(locator, TestUtil.outsideX(RECT))
            TestUtil.assertEmpty(locator, TestUtil.outsideXY(RECT))
        }
    }

    companion object {

        private val RECT = DoubleRectangle(0.0, 100.0, 20.0, 40.0)
        private const val RECT_KEY = 1
        private val TARGET = TestUtil.rectTarget(RECT_KEY, RECT)

        private fun createLocator(strategy: LookupStrategy, space: LookupSpace): GeomTargetLocator {
            return TestUtil.createLocator(strategy, space, TARGET)
        }
    }
}
