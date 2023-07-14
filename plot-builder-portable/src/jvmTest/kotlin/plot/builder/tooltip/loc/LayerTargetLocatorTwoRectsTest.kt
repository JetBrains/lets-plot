/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.tooltip.loc

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator.LookupSpace
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator.LookupStrategy
import jetbrains.datalore.plot.builder.tooltip.TestUtil
import jetbrains.datalore.plot.builder.tooltip.TestUtil.assertEmpty
import jetbrains.datalore.plot.builder.tooltip.TestUtil.assertObjects
import jetbrains.datalore.plot.builder.tooltip.TestUtil.inside
import jetbrains.datalore.plot.builder.tooltip.TestUtil.outsideX
import jetbrains.datalore.plot.builder.tooltip.TestUtil.outsideXY
import jetbrains.datalore.plot.builder.tooltip.TestUtil.outsideY
import jetbrains.datalore.plot.builder.tooltip.TestUtil.rectTarget
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse

class LayerTargetLocatorTwoRectsTest {

    @BeforeTest
    fun setUp() {
        // Preconditions
        assertFalse(FIRST_RECT.contains(outsideY(SECOND_RECT)))
        assertFalse(FIRST_RECT.contains(inside(SECOND_RECT)))
        assertFalse(SECOND_RECT.contains(outsideY(FIRST_RECT)))
        assertFalse(SECOND_RECT.contains(inside(FIRST_RECT)))
    }

    @Test
    fun hoverX() {
        val locator = createLocator(LookupStrategy.HOVER, LookupSpace.X)

        assertCoordInsideXRangeIgnoresY(locator)
    }

    @Test
    fun nearestX() {
        val locator = createLocator(LookupStrategy.NEAREST, LookupSpace.X)

        assertCoordInsideXRangeIgnoresY(locator)
    }

    private fun assertCoordInsideXRangeIgnoresY(locator: GeomTargetLocator) {
        assertObjects(locator, inside(FIRST_RECT), FIRST_RECT_KEY)
        assertObjects(locator, outsideY(FIRST_RECT), FIRST_RECT_KEY)
        assertObjects(locator, outsideY(SECOND_RECT), SECOND_RECT_KEY)
        assertObjects(locator, inside(SECOND_RECT), SECOND_RECT_KEY)


        assertEmpty(locator, outsideX(FIRST_RECT))
        assertEmpty(locator, outsideXY(FIRST_RECT))
        assertEmpty(locator, outsideX(SECOND_RECT))
        assertEmpty(locator, outsideXY(SECOND_RECT))
    }

    private fun createLocator(strategy: LookupStrategy, space: LookupSpace): GeomTargetLocator {
        return TestUtil.createLocator(strategy, space, FIRST_TARGET, SECOND_TARGET)
    }

    companion object {
        private const val FIRST_RECT_KEY = 1
        private val FIRST_RECT = DoubleRectangle(0.0, 0.0, 20.0, 40.0)
        private val FIRST_TARGET = rectTarget(FIRST_RECT_KEY, FIRST_RECT)
        private const val SECOND_RECT_KEY = 2
        private val SECOND_RECT = DoubleRectangle(80.0, 0.0, 20.0, 300.0)
        private val SECOND_TARGET = rectTarget(SECOND_RECT_KEY, SECOND_RECT)
    }

}
