/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.tooltip

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator.LookupSpace
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator.LookupStrategy
import jetbrains.datalore.plot.builder.tooltip.TestUtil.createLocator
import jetbrains.datalore.plot.builder.tooltip.TestUtil.findTargets
import jetbrains.datalore.plot.builder.tooltip.TestUtil.horizontalPath
import jetbrains.datalore.plot.builder.tooltip.TestUtil.inside
import jetbrains.datalore.plot.builder.tooltip.TestUtil.outsideX
import jetbrains.datalore.plot.builder.tooltip.TestUtil.outsideXY
import jetbrains.datalore.plot.builder.tooltip.TestUtil.outsideY
import jetbrains.datalore.plot.builder.tooltip.TestUtil.pathTarget
import jetbrains.datalore.plot.builder.tooltip.TestUtil.point
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue


class HelperTest {

    @Test
    fun placementMethodsTest() {
        val r = DoubleRectangle(0.0, 100.0, 50.0, 50.0)
        run {
            val outsideX = outsideX(r)
            assertTrue(outsideX.x > r.right)
            assertTrue(outsideX.y > r.top && outsideX.y < r.bottom)
        }

        run {
            val outsideY = outsideY(r)
            assertTrue(outsideY.x > r.left && outsideY.x < r.right)
            assertTrue(outsideY.y > r.top)
        }

        run {
            val outsideXY = outsideXY(r)
            assertTrue(outsideXY.x > r.right)
            assertTrue(outsideXY.y > r.bottom)
        }

        run {
            val inside = inside(r)
            assertTrue(inside.x > r.left && inside.x < r.right)
            assertTrue(inside.y > r.top && inside.y < r.bottom)
        }
    }

    @Test
    fun pathIndexMapperTest() {
        val y = 100.0
        val path1 = horizontalPath(y, 0.0, 1.0, 2.0, 3.0)

        val target = pathTarget(path1)
        val locator = createLocator(LookupStrategy.NEAREST, LookupSpace.X, target)

        assertEquals(0, findTargets(locator, point(0.0, y))[0].hitIndex)
        assertEquals(1, findTargets(locator, point(1.0, y))[0].hitIndex)
        assertEquals(2, findTargets(locator, point(2.0, y))[0].hitIndex)
        assertEquals(3, findTargets(locator, point(3.0, y))[0].hitIndex)
    }

}