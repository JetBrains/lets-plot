/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.tooltip.loc

import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator.LookupSpace
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator.LookupStrategy
import jetbrains.datalore.plot.builder.tooltip.TestUtil
import jetbrains.datalore.plot.builder.tooltip.TestUtil.assertEmpty
import jetbrains.datalore.plot.builder.tooltip.TestUtil.assertObjects
import jetbrains.datalore.plot.builder.tooltip.TestUtil.offsetX
import jetbrains.datalore.plot.builder.tooltip.TestUtil.offsetY
import jetbrains.datalore.plot.builder.tooltip.TestUtil.point
import jetbrains.datalore.plot.builder.tooltip.TestUtil.pointTarget
import kotlin.test.Test

class LayerTargetLocatorSinglePointTest {

    @Test
    fun hoverXy() {
        val locator = createLocator(LookupStrategy.HOVER, LookupSpace.XY)

        assertObjects(locator, POINT, POINT_KEY)
        assertEmpty(locator, offsetX(POINT))
    }

    @Test
    fun nearestXy() {
        val locator = createLocator(LookupStrategy.NEAREST, LookupSpace.XY)

        assertObjects(locator, POINT, POINT_KEY)
        assertObjects(locator, offsetX(POINT), POINT_KEY)
    }

    @Test
    fun hoverX() {
        val locator = createLocator(LookupStrategy.HOVER, LookupSpace.X)

        assertObjects(locator, POINT, POINT_KEY)
        assertObjects(locator, offsetY(POINT), POINT_KEY)

        assertEmpty(locator, offsetX(POINT))
    }

    @Test
    fun nearestX() {
        val locator = createLocator(LookupStrategy.NEAREST, LookupSpace.X)

        assertObjects(locator, POINT, POINT_KEY)
        assertObjects(locator, offsetY(POINT), POINT_KEY)
        assertObjects(locator, offsetX(POINT), POINT_KEY)
    }

    private fun createLocator(strategy: LookupStrategy, space: LookupSpace): GeomTargetLocator {
        return TestUtil.createLocator(
            strategy, space,
            TARGET
        )
    }

    companion object {
        private val POINT = point(100.0, 100.0)
        private const val POINT_KEY = 1
        private val TARGET = pointTarget(
            POINT_KEY,
            POINT
        )
    }
}
