/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.tooltip.loc

import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator
import org.jetbrains.letsPlot.core.plot.builder.tooltip.TestUtil.assertEmpty
import org.jetbrains.letsPlot.core.plot.builder.tooltip.TestUtil.createLocator
import org.jetbrains.letsPlot.core.plot.builder.tooltip.TestUtil.point
import org.jetbrains.letsPlot.core.plot.builder.tooltip.TestUtil.polygon
import org.jetbrains.letsPlot.core.plot.builder.tooltip.TestUtil.polygonTarget
import kotlin.test.Test

class PolygonSawTeethUpTest {


    private val polygonLocator: GeomTargetLocator
        get() = createLocator(GeomTargetLocator.LookupStrategy.HOVER, GeomTargetLocator.LookupSpace.XY, TARGET)

    @Test
    fun whenBetweenTeeth_ShouldFindNothing() {
        val locator = polygonLocator

        assertEmpty(locator, point(MIDDLE_MIN, TOP))
    }

    @Test
    fun whenBeforeFirstTooth_ShouldFindNothing() {
        val locator = polygonLocator

        assertEmpty(locator, point(LEFT_MIN, TOP))
    }

    @Test
    fun whenAfterLastTooth_ShouldFindNothing() {
        val locator = polygonLocator

        assertEmpty(locator, point(RIGHT_MIN, TOP))
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

        private val POLYGON = polygon(
            point(0.0, BOTTOM),
            point(FIRST_TOOTH_PEEK_X, TOP),
            point(100.0, BOTTOM),
            point(SECOND_TOOTH_PEEK_X, TOP),
            point(200.0, BOTTOM)
        )

        private const val POLYGON_KEY = 1
        private val TARGET = polygonTarget(POLYGON_KEY, POLYGON)
    }
}
