/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.tooltip

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator.LookupSpace
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator.LookupStrategy
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue


class HelperTest {

    @Test
    fun placementMethodsTest() {
        val r = DoubleRectangle(0.0, 100.0, 50.0, 50.0)
        run {
            val outsideX = TestUtil.outsideX(r)
            assertTrue(outsideX.x > r.right)
            assertTrue(outsideX.y > r.top && outsideX.y < r.bottom)
        }

        run {
            val outsideY = TestUtil.outsideY(r)
            assertTrue(outsideY.x > r.left && outsideY.x < r.right)
            assertTrue(outsideY.y > r.top)
        }

        run {
            val outsideXY = TestUtil.outsideXY(r)
            assertTrue(outsideXY.x > r.right)
            assertTrue(outsideXY.y > r.bottom)
        }

        run {
            val inside = TestUtil.inside(r)
            assertTrue(inside.x > r.left && inside.x < r.right)
            assertTrue(inside.y > r.top && inside.y < r.bottom)
        }
    }

    @Test
    fun pathIndexMapperTest() {
        val y = 100.0
        val path1 = TestUtil.horizontalPath(y, 0.0, 1.0, 2.0, 3.0)

        val target = TestUtil.pathTarget(path1)
        val locator = TestUtil.createLocator(LookupStrategy.NEAREST, LookupSpace.X, target)

        assertEquals(0, TestUtil.findTargets(locator, TestUtil.point(0.0, y))[0].hitIndex)
        assertEquals(1, TestUtil.findTargets(locator, TestUtil.point(1.0, y))[0].hitIndex)
        assertEquals(2, TestUtil.findTargets(locator, TestUtil.point(2.0, y))[0].hitIndex)
        assertEquals(3, TestUtil.findTargets(locator, TestUtil.point(3.0, y))[0].hitIndex)
    }

}