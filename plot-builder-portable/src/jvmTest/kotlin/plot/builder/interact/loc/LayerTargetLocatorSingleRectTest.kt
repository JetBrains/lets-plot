/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact.loc

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.core.plot.base.interact.GeomTargetLocator
import org.jetbrains.letsPlot.core.plot.base.interact.GeomTargetLocator.LookupSpace
import org.jetbrains.letsPlot.core.plot.base.interact.GeomTargetLocator.LookupStrategy
import jetbrains.datalore.plot.builder.interact.TestUtil
import jetbrains.datalore.plot.builder.interact.TestUtil.assertEmpty
import jetbrains.datalore.plot.builder.interact.TestUtil.assertObjects
import jetbrains.datalore.plot.builder.interact.TestUtil.inside
import jetbrains.datalore.plot.builder.interact.TestUtil.outsideX
import jetbrains.datalore.plot.builder.interact.TestUtil.outsideXY
import jetbrains.datalore.plot.builder.interact.TestUtil.outsideY
import jetbrains.datalore.plot.builder.interact.TestUtil.rectTarget
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
