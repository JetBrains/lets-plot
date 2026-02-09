/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.tooltip.loc

import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator
import org.jetbrains.letsPlot.core.plot.base.tooltip.TestUtil
import kotlin.test.Test

class PolygonSawTeethUpTest {


    private val polygonLocator: GeomTargetLocator
        get() = TestUtil.createLocator(GeomTargetLocator.LookupStrategy.HOVER, GeomTargetLocator.LookupSpace.XY, TARGET)

    @Test
    fun whenBetweenTeeth_ShouldFindNothing() {
        val locator = polygonLocator

        TestUtil.assertEmpty(locator, TestUtil.point(MIDDLE_MIN, TOP))
    }

    @Test
    fun whenBeforeFirstTooth_ShouldFindNothing() {
        val locator = polygonLocator

        TestUtil.assertEmpty(locator, TestUtil.point(LEFT_MIN, TOP))
    }

    @Test
    fun whenAfterLastTooth_ShouldFindNothing() {
        val locator = polygonLocator

        TestUtil.assertEmpty(locator, TestUtil.point(RIGHT_MIN, TOP))
    }

    companion object {

        /*
    200    *      *
          / \    / \
         /   \  /   \
        /     \/     \
      0 *-----*------*
        0     100    200
  */

        private const val TOP = 200.0
        private const val BOTTOM = 0.0
        private const val FIRST_TOOTH_PEEK_X = 50.0
        private const val SECOND_TOOTH_PEEK_X = 150.0
        private const val LEFT_MIN = 0.0
        private const val RIGHT_MIN = 200.0
        private const val MIDDLE_MIN = 100.0

        private val POLYGON = TestUtil.polygon(
            TestUtil.point(0.0, BOTTOM),
            TestUtil.point(FIRST_TOOTH_PEEK_X, TOP),
            TestUtil.point(100.0, BOTTOM),
            TestUtil.point(SECOND_TOOTH_PEEK_X, TOP),
            TestUtil.point(200.0, BOTTOM)
        )

        private const val POLYGON_KEY = 1
        private val TARGET = TestUtil.polygonTarget(POLYGON_KEY, POLYGON)
    }
}
