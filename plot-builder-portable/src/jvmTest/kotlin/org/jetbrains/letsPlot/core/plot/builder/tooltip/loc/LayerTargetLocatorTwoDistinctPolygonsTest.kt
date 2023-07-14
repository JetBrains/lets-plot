/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.tooltip.loc

import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator.LookupSpace
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator.LookupStrategy
import org.jetbrains.letsPlot.core.plot.builder.tooltip.TestUtil.assertObjects
import org.jetbrains.letsPlot.core.plot.builder.tooltip.TestUtil.createLocator
import org.jetbrains.letsPlot.core.plot.builder.tooltip.TestUtil.point
import org.jetbrains.letsPlot.core.plot.builder.tooltip.TestUtil.polygon
import org.jetbrains.letsPlot.core.plot.builder.tooltip.TestUtil.polygonTarget
import kotlin.test.BeforeTest
import kotlin.test.Test

class LayerTargetLocatorTwoDistinctPolygonsTest {
    private lateinit var myLocator: GeomTargetLocator

    @BeforeTest
    fun setUp() {
        myLocator = createLocator(LookupStrategy.HOVER, LookupSpace.XY, FIRST_TARGET, SECOND_TARGET)
    }

    @Test
    fun pointInsideFirstPolygon_ShouldReturnFirstPolygonKey() {
        assertObjects(myLocator, FIRST_POLYGON_POINT_INSIDE, FIRST_POLYGON_KEY)
    }

    @Test
    fun pointInsideSecondPolygon_ShouldReturnSecondPolygonKey() {
        assertObjects(myLocator, SECOND_POLYGON_POINT_INSIDE, SECOND_POLYGON_KEY)
    }

    companion object {
        private val FIRST_POLYGON = polygon(
            point(0.0, 0.0),
            point(100.0, 0.0),
            point(100.0, 100.0),
            point(0.0, 100.0)
        )

        private const val FIRST_POLYGON_KEY = 1
        private val FIRST_TARGET = polygonTarget(FIRST_POLYGON_KEY, FIRST_POLYGON)
        private val FIRST_POLYGON_POINT_INSIDE = point(50.0, 50.0)

        private val SECOND_POLYGON = polygon(
            point(200.0, 200.0),
            point(300.0, 300.0),
            point(400.0, 200.0)
        )

        private const val SECOND_POLYGON_KEY = 2
        private val SECOND_TARGET = polygonTarget(SECOND_POLYGON_KEY, SECOND_POLYGON)
        private val SECOND_POLYGON_POINT_INSIDE = point(300.0, 250.0)
    }
}
