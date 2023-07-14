/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.tooltip.loc

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator.LookupSpace
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator.LookupStrategy
import org.jetbrains.letsPlot.core.plot.builder.tooltip.TestUtil
import org.jetbrains.letsPlot.core.plot.builder.tooltip.TestUtil.assertEmpty
import org.jetbrains.letsPlot.core.plot.builder.tooltip.TestUtil.assertObjects
import org.jetbrains.letsPlot.core.plot.builder.tooltip.TestUtil.inside
import org.jetbrains.letsPlot.core.plot.builder.tooltip.TestUtil.outsideX
import org.jetbrains.letsPlot.core.plot.builder.tooltip.TestUtil.outsideXY
import org.jetbrains.letsPlot.core.plot.builder.tooltip.TestUtil.outsideY
import org.jetbrains.letsPlot.core.plot.builder.tooltip.TestUtil.rectTarget
import kotlin.test.Test


class LayerTargetLocatorSingleRectTest {

    @Test
    fun hoverXy() {
        val locator = createLocator(LookupStrategy.HOVER, LookupSpace.XY)

        assertObjects(locator, inside(RECT), RECT_KEY)

        // Not match
        assertEmpty(locator, outsideY(RECT))
        assertEmpty(locator, outsideX(RECT))
        assertEmpty(locator, outsideXY(RECT))
    }

    @Test
    fun nearestXy() {
        val locator = createLocator(LookupStrategy.NEAREST, LookupSpace.XY)

        assertObjects(locator, inside(RECT), RECT_KEY)
        assertObjects(locator, outsideY(RECT), RECT_KEY)
        assertObjects(locator, outsideX(RECT), RECT_KEY)
        assertObjects(locator, outsideXY(RECT), RECT_KEY)
    }

    @Test
    fun hoverXAndNearestXHaveSameBehaviour() {
        for (strategy in listOf(LookupStrategy.HOVER, LookupStrategy.NEAREST)) {
            val locator = createLocator(strategy, LookupSpace.X)
            assertObjects(locator, inside(RECT), RECT_KEY)
            assertObjects(locator, outsideY(RECT), RECT_KEY)

            assertEmpty(locator, outsideX(RECT))
            assertEmpty(locator, outsideXY(RECT))
        }
    }

    companion object {

        private val RECT = DoubleRectangle(0.0, 100.0, 20.0, 40.0)
        private const val RECT_KEY = 1
        private val TARGET = rectTarget(RECT_KEY, RECT)

        private fun createLocator(strategy: LookupStrategy, space: LookupSpace): GeomTargetLocator {
            return TestUtil.createLocator(strategy, space, TARGET)
        }
    }
}
