/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.tooltip.loc

import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator
import org.jetbrains.letsPlot.core.plot.base.tooltip.TestUtil
import kotlin.test.Test

class PolygonSawTeethDownTest {

    private val polygonLocator: GeomTargetLocator
        get() = TestUtil.createLocator(GeomTargetLocator.LookupStrategy.HOVER, GeomTargetLocator.LookupSpace.XY, TARGET)

    @Test
    fun whenBetweenTeeth_ShouldFindNothing() {
        val locator = polygonLocator

        TestUtil.assertEmpty(locator, TestUtil.point(MIDDLE_MAX, BOTTOM))
    }

    @Test
    fun whenBeforeFirstTooth_ShouldFindNothing() {
        val locator = polygonLocator

        TestUtil.assertEmpty(locator, TestUtil.point(LEFT_MAX, BOTTOM))
    }

    @Test
    fun whenAfterLastTooth_ShouldFindNothing() {
        val locator = polygonLocator

        TestUtil.assertEmpty(locator, TestUtil.point(RIGHT_MAX, BOTTOM))
    }

    @Test
    fun whenOnSecondToothPeekPoint_ShouldFindNothing() {
        val locator = polygonLocator

        TestUtil.assertEmpty(locator, TestUtil.point(SECOND_TOOTH_PEEK_X, BOTTOM))
    }

    @Test
    fun whenInsideSecondToothPeek_ShouldFindPolygon() {
        val locator = polygonLocator

        TestUtil.assertObjects(locator, TestUtil.point(SECOND_TOOTH_PEEK_X, BOTTOM + 1.0), POLYGON_KEY)
    }

    companion object {

        /*

    200 *-----*------*
         \    /\    /
          \  /  \  /
           \/    \/
      0     *     *
        0     100    200
  */

        private const val TOP = 200.0
        private const val BOTTOM = 0.0
        private const val FIRST_TOOTH_PEEK_X = 50.0
        private const val SECOND_TOOTH_PEEK_X = 150.0
        private const val LEFT_MAX = 0.0
        private const val RIGHT_MAX = 200.0
        private const val MIDDLE_MAX = 100.0

        private val POLYGON = TestUtil.polygon(
            TestUtil.point(0.0, TOP),
            TestUtil.point(FIRST_TOOTH_PEEK_X, BOTTOM),
            TestUtil.point(100.0, TOP),
            TestUtil.point(SECOND_TOOTH_PEEK_X, BOTTOM),
            TestUtil.point(200.0, TOP)
        )

        private const val POLYGON_KEY = 1
        private val TARGET = TestUtil.polygonTarget(POLYGON_KEY, POLYGON)
    }
}
