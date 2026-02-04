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
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LayerTargetLocatorTwoOverlappedRectsTest {

    @BeforeTest
    fun setUp() {
        // Preconditions
        assertFalse(FIRST_RECT.contains(TestUtil.outsideY(SECOND_RECT)))
        assertFalse(FIRST_RECT.contains(TestUtil.inside(SECOND_RECT)))
        assertTrue(SECOND_RECT.contains(TestUtil.outsideY(FIRST_RECT)))
        assertTrue(SECOND_RECT.contains(TestUtil.inside(FIRST_RECT)))
    }

    @Test
    fun hoverXy() {
        val locator = createLocator(LookupStrategy.HOVER, LookupSpace.XY)

        TestUtil.assertObjects(locator, TestUtil.inside(FIRST_RECT), SECOND_RECT_KEY)
        TestUtil.assertObjects(locator, TestUtil.outsideY(FIRST_RECT), SECOND_RECT_KEY)
        TestUtil.assertObjects(locator, TestUtil.inside(SECOND_RECT), SECOND_RECT_KEY)

        TestUtil.assertEmpty(locator, TestUtil.outsideX(FIRST_RECT))
        TestUtil.assertEmpty(locator, TestUtil.outsideXY(FIRST_RECT))
        TestUtil.assertEmpty(locator, TestUtil.outsideY(SECOND_RECT))
        TestUtil.assertEmpty(locator, TestUtil.outsideX(SECOND_RECT))
        TestUtil.assertEmpty(locator, TestUtil.outsideXY(SECOND_RECT))
    }

    @Test
    fun nearestXy() {
        val locator = createLocator(LookupStrategy.NEAREST, LookupSpace.XY)

        TestUtil.assertObjects(locator, TestUtil.inside(FIRST_RECT), SECOND_RECT_KEY)
        TestUtil.assertObjects(locator, TestUtil.outsideY(FIRST_RECT), SECOND_RECT_KEY)
        TestUtil.assertObjects(locator, TestUtil.inside(SECOND_RECT), SECOND_RECT_KEY)
        TestUtil.assertObjects(locator, TestUtil.outsideX(FIRST_RECT), SECOND_RECT_KEY)
        TestUtil.assertObjects(locator, TestUtil.outsideXY(FIRST_RECT), SECOND_RECT_KEY)
        TestUtil.assertObjects(locator, TestUtil.outsideY(SECOND_RECT), SECOND_RECT_KEY)
        TestUtil.assertObjects(locator, TestUtil.outsideX(SECOND_RECT), SECOND_RECT_KEY)
        TestUtil.assertObjects(locator, TestUtil.outsideXY(SECOND_RECT), SECOND_RECT_KEY)
    }

    @Test
    fun hoverX() {
        val locator = createLocator(LookupStrategy.HOVER, LookupSpace.X)

        TestUtil.assertObjects(locator, TestUtil.inside(FIRST_RECT), FIRST_RECT_KEY, SECOND_RECT_KEY)
        TestUtil.assertObjects(locator, TestUtil.outsideY(FIRST_RECT), FIRST_RECT_KEY, SECOND_RECT_KEY)
        TestUtil.assertObjects(locator, TestUtil.outsideY(SECOND_RECT), FIRST_RECT_KEY, SECOND_RECT_KEY)
        TestUtil.assertObjects(locator, TestUtil.inside(SECOND_RECT), FIRST_RECT_KEY, SECOND_RECT_KEY)

        TestUtil.assertEmpty(locator, TestUtil.outsideX(FIRST_RECT))
        TestUtil.assertEmpty(locator, TestUtil.outsideXY(FIRST_RECT))
        TestUtil.assertEmpty(locator, TestUtil.outsideX(SECOND_RECT))
        TestUtil.assertEmpty(locator, TestUtil.outsideXY(SECOND_RECT))
    }

    @Test
    fun nearestX() {
        val locator = createLocator(LookupStrategy.NEAREST, LookupSpace.X)

        TestUtil.assertObjects(locator, TestUtil.inside(FIRST_RECT), FIRST_RECT_KEY, SECOND_RECT_KEY)
        TestUtil.assertObjects(locator, TestUtil.outsideY(FIRST_RECT), FIRST_RECT_KEY, SECOND_RECT_KEY)

        TestUtil.assertEmpty(locator, TestUtil.outsideX(SECOND_RECT))
        TestUtil.assertEmpty(locator, TestUtil.outsideX(FIRST_RECT))
        TestUtil.assertEmpty(locator, TestUtil.outsideXY(SECOND_RECT))
        TestUtil.assertEmpty(locator, TestUtil.outsideXY(FIRST_RECT))
    }

    private fun createLocator(strategy: LookupStrategy, space: LookupSpace): GeomTargetLocator {
        return TestUtil.createLocator(strategy, space, FIRST_TARGET, SECOND_TARGET)
    }

    companion object {
        private val FIRST_RECT = DoubleRectangle(0.0, 0.0, 20.0, 40.0)
        private const val FIRST_RECT_KEY = 1
        private val FIRST_TARGET = TestUtil.rectTarget(FIRST_RECT_KEY, FIRST_RECT)

        private const val SECOND_RECT_KEY = 2
        private val SECOND_RECT = DoubleRectangle(0.0, 0.0, 20.0, 300.0)
        private val SECOND_TARGET = TestUtil.rectTarget(SECOND_RECT_KEY, SECOND_RECT)
    }
}
