/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.tooltip.loc

import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator.LookupSpace
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator.LookupStrategy
import org.jetbrains.letsPlot.core.plot.base.tooltip.TestUtil
import kotlin.test.Test

class LayerTargetLocatorSinglePointTest {

    @Test
    fun `issue1214 - coord_polar geom_point tooltips should take in account point size`() {
        val pointKey = 1
        val pointCenter = 50.0
        val pointRadius = 20.0

        val locator = TestUtil.createLocator(
            LookupStrategy.HOVER,
            LookupSpace.XY,
            TestUtil.pointTarget(pointKey, TestUtil.point(pointCenter, pointCenter), pointRadius)
        )

        val locatorExtraDistance = 5.1 // see POINT_AREA_EPSILON

        // exceeds the radius
        TestUtil.assertEmpty(
            locator,
            TestUtil.point(pointCenter - pointRadius - locatorExtraDistance - 1.0, pointCenter)
        )

        // within the radius
        TestUtil.assertObjects(
            locator,
            TestUtil.point(pointCenter - pointRadius - locatorExtraDistance + 1.0, pointCenter),
            pointKey
        )
    }

    @Test
    fun hoverXy() {
        val locator = createLocator(LookupStrategy.HOVER, LookupSpace.XY)

        TestUtil.assertObjects(locator, POINT, POINT_KEY)
        TestUtil.assertEmpty(locator, TestUtil.offsetX(POINT))
    }

    @Test
    fun nearestXy() {
        val locator = createLocator(LookupStrategy.NEAREST, LookupSpace.XY)

        TestUtil.assertObjects(locator, POINT, POINT_KEY)
        TestUtil.assertObjects(locator, TestUtil.offsetX(POINT), POINT_KEY)
    }

    @Test
    fun hoverX() {
        val locator = createLocator(LookupStrategy.HOVER, LookupSpace.X)

        TestUtil.assertObjects(locator, POINT, POINT_KEY)
        TestUtil.assertObjects(locator, TestUtil.offsetY(POINT), POINT_KEY)

        TestUtil.assertEmpty(locator, TestUtil.offsetX(POINT))
    }

    @Test
    fun nearestX() {
        val locator = createLocator(LookupStrategy.NEAREST, LookupSpace.X)

        TestUtil.assertObjects(locator, POINT, POINT_KEY)
        TestUtil.assertObjects(locator, TestUtil.offsetY(POINT), POINT_KEY)
        TestUtil.assertObjects(locator, TestUtil.offsetX(POINT), POINT_KEY)
    }

    private fun createLocator(strategy: LookupStrategy, space: LookupSpace): GeomTargetLocator {
        return TestUtil.createLocator(
            strategy, space,
            TARGET
        )
    }

    companion object {
        private val POINT = TestUtil.point(100.0, 100.0)
        private const val POINT_KEY = 1
        private val TARGET = TestUtil.pointTarget(
            POINT_KEY,
            POINT
        )
    }
}
