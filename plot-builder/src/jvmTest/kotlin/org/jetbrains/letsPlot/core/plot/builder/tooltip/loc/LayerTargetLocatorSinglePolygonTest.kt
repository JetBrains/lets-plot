/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.tooltip.loc

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator.LookupSpace
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator.LookupStrategy
import org.jetbrains.letsPlot.core.plot.builder.tooltip.TestUtil.assertEmpty
import org.jetbrains.letsPlot.core.plot.builder.tooltip.TestUtil.assertObjects
import org.jetbrains.letsPlot.core.plot.builder.tooltip.TestUtil.createLocator
import org.jetbrains.letsPlot.core.plot.builder.tooltip.TestUtil.point
import org.jetbrains.letsPlot.core.plot.builder.tooltip.TestUtil.polygon
import org.jetbrains.letsPlot.core.plot.builder.tooltip.TestUtil.polygonTarget
import kotlin.test.Test

class LayerTargetLocatorSinglePolygonTest {
    private lateinit var locator: GeomTargetLocator

    @Test
    fun pointInsidePolygon_ShouldReturnPolygonKey() {
        locator = createLocator(LookupStrategy.HOVER, LookupSpace.XY, TARGET)
        assertObjects(locator, point(50.0, 50.0), POLYGON_KEY)
    }


    @Test
    fun pointInside_WithNearestStrategy_ShouldReturnZeroDistance() {
        locator = createLocator(LookupStrategy.NEAREST, LookupSpace.XY, TARGET)
        assertThat(distanceFor(point(50.0, 50.0))).isZero()
    }

    @Test
    fun pointOutside_WithNearestStrategy_ShouldReturnNoTargets() {
        locator = createLocator(LookupStrategy.NEAREST, LookupSpace.XY, TARGET)
        assertEmpty(locator, point(150.0, 0.0))
    }

    @Test
    fun pointInside_WithHoverStrategy_ShouldReturnZeroDistance() {
        locator = createLocator(LookupStrategy.HOVER, LookupSpace.XY, TARGET)
        assertThat(distanceFor(point(50.0, 50.0))).isZero()
    }

    private fun distanceFor(coord: DoubleVector): Double {
        return locator.search(coord)!!.distance
    }

    companion object {
        private val POLYGON = polygon(
            point(0.0, 0.0),
            point(100.0, 0.0),
            point(100.0, 100.0),
            point(0.0, 100.0)
        )

        private const val POLYGON_KEY = 1
        private val TARGET = polygonTarget(POLYGON_KEY, POLYGON)
    }
}
