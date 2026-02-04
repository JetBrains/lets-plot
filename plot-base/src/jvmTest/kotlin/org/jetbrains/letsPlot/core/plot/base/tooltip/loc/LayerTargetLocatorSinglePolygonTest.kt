/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.tooltip.loc

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator.LookupSpace
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator.LookupStrategy
import org.jetbrains.letsPlot.core.plot.base.tooltip.TestUtil
import kotlin.test.Test

class LayerTargetLocatorSinglePolygonTest {
    private lateinit var locator: GeomTargetLocator

    @Test
    fun pointInsidePolygon_ShouldReturnPolygonKey() {
        locator = TestUtil.createLocator(LookupStrategy.HOVER, LookupSpace.XY, TARGET)
        TestUtil.assertObjects(locator, TestUtil.point(50.0, 50.0), POLYGON_KEY)
    }


    @Test
    fun pointInside_WithNearestStrategy_ShouldReturnZeroDistance() {
        locator = TestUtil.createLocator(LookupStrategy.NEAREST, LookupSpace.XY, TARGET)
        assertThat(distanceFor(TestUtil.point(50.0, 50.0))).isZero()
    }

    @Test
    fun pointOutside_WithNearestStrategy_ShouldReturnNoTargets() {
        locator = TestUtil.createLocator(LookupStrategy.NEAREST, LookupSpace.XY, TARGET)
        TestUtil.assertEmpty(locator, TestUtil.point(150.0, 0.0))
    }

    @Test
    fun pointInside_WithHoverStrategy_ShouldReturnZeroDistance() {
        locator = TestUtil.createLocator(LookupStrategy.HOVER, LookupSpace.XY, TARGET)
        assertThat(distanceFor(TestUtil.point(50.0, 50.0))).isZero()
    }

    private fun distanceFor(coord: DoubleVector): Double {
        return locator.search(coord)!!.distance
    }

    companion object {
        private val POLYGON = TestUtil.polygon(
            TestUtil.point(0.0, 0.0),
            TestUtil.point(100.0, 0.0),
            TestUtil.point(100.0, 100.0),
            TestUtil.point(0.0, 100.0)
        )

        private const val POLYGON_KEY = 1
        private val TARGET = TestUtil.polygonTarget(POLYGON_KEY, POLYGON)
    }
}
