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

class LayerTargetLocatorTwoPointsTest {

    @Test
    fun hoverXy() {
        val locator = createLocator(LookupStrategy.HOVER, LookupSpace.XY)

        TestUtil.assertObjects(locator, FIRST_POINT, FIRST_POINT_KEY)
        TestUtil.assertObjects(locator, SECOND_POINT, SECOND_POINT_KEY)

        // Not match
        TestUtil.assertEmpty(locator, TestUtil.offsetX(FIRST_POINT))
        TestUtil.assertEmpty(locator, TestUtil.offsetX(SECOND_POINT))
    }


    @Test
    fun hoverX() {
        val locator = createLocator(LookupStrategy.HOVER, LookupSpace.X)

        TestUtil.assertObjects(locator, FIRST_POINT, FIRST_POINT_KEY)
        TestUtil.assertObjects(locator, TestUtil.offsetY(FIRST_POINT), FIRST_POINT_KEY)
        TestUtil.assertObjects(locator, SECOND_POINT, SECOND_POINT_KEY)
        TestUtil.assertObjects(locator, TestUtil.offsetY(SECOND_POINT), SECOND_POINT_KEY)

        // Not match
        TestUtil.assertEmpty(locator, TestUtil.offsetX(FIRST_POINT))
        TestUtil.assertEmpty(locator, TestUtil.offsetX(SECOND_POINT))
    }

    @Test
    fun nearestXy() {
        val locator = createLocator(LookupStrategy.NEAREST, LookupSpace.XY)

        TestUtil.assertObjects(locator, FIRST_POINT, FIRST_POINT_KEY)
        TestUtil.assertObjects(locator, SECOND_POINT, SECOND_POINT_KEY)
        TestUtil.assertObjects(locator, TestUtil.offsetXY(FIRST_POINT), FIRST_POINT_KEY)
        TestUtil.assertObjects(locator, TestUtil.offsetXY(SECOND_POINT), SECOND_POINT_KEY)
    }

    @Test
    fun nearestX() {
        val locator = createLocator(LookupStrategy.NEAREST, LookupSpace.X)

        TestUtil.assertObjects(locator, FIRST_POINT, FIRST_POINT_KEY)
        TestUtil.assertObjects(locator, SECOND_POINT, SECOND_POINT_KEY)
        TestUtil.assertObjects(locator, TestUtil.offsetXY(FIRST_POINT), FIRST_POINT_KEY)
        TestUtil.assertObjects(locator, TestUtil.offsetXY(SECOND_POINT), SECOND_POINT_KEY)
    }

    @Test
    fun nearestXCloseToEachOther() {
        val firstTarget = 1
        val secondTarget = 2

        val locator = TestUtil.createLocator(
            LookupStrategy.NEAREST,
            LookupSpace.X,
            TestUtil.pointTarget(firstTarget, TestUtil.point(10.0, 10.0)),
            TestUtil.pointTarget(secondTarget, TestUtil.point(15.0, 10.0))
        )

        val closerToFirst = TestUtil.point(11.0, 10.0)
        TestUtil.assertObjects(locator, closerToFirst, firstTarget)

        val closerToSecond = TestUtil.point(14.0, 10.0)
        TestUtil.assertObjects(locator, closerToSecond, secondTarget)
    }

    @Test
    fun `nearestX - points are equidistant from cursor`() {
        val firstTarget = 1
        val secondTarget = 2

        val locator = TestUtil.createLocator(
            LookupStrategy.NEAREST,
            LookupSpace.X,
            TestUtil.pointTarget(firstTarget, TestUtil.point(10.0, 10.0)),
            TestUtil.pointTarget(secondTarget, TestUtil.point(16.0, 10.0))
        )

        val closeToBoth = TestUtil.point(13.0, 10.0)
        TestUtil.assertObjects(locator, closeToBoth, firstTarget, secondTarget)
    }

    @Test
    fun `nearestX - points with the same X`() {
        val firstTarget = 1
        val secondTarget = 2

        val locator = TestUtil.createLocator(
            LookupStrategy.NEAREST,
            LookupSpace.X,
            TestUtil.pointTarget(firstTarget, TestUtil.point(10.0, 10.0)),
            TestUtil.pointTarget(secondTarget, TestUtil.point(10.0, 16.0))
        )

        val closeToBoth = TestUtil.point(10.0, 10.0)
        TestUtil.assertObjects(locator, closeToBoth, firstTarget, secondTarget)
    }

    private fun createLocator(strategy: LookupStrategy, space: LookupSpace): GeomTargetLocator {
        return TestUtil.createLocator(strategy, space, FIRST_TARGET, SECOND_TARGET)
    }

    companion object {
        private const val FIRST_POINT_KEY = 1
        private val FIRST_POINT = TestUtil.point(10.0, 10.0)
        private val FIRST_TARGET = TestUtil.pointTarget(FIRST_POINT_KEY, FIRST_POINT)
        private const val SECOND_POINT_KEY = 2
        private val SECOND_POINT = TestUtil.point(40.0, 10.0)
        private val SECOND_TARGET = TestUtil.pointTarget(SECOND_POINT_KEY, SECOND_POINT)
    }
}
