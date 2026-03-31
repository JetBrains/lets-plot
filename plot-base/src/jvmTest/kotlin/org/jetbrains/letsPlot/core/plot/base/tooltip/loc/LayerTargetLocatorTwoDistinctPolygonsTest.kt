/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.tooltip.loc

import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator.LookupSpace
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator.LookupStrategy
import org.jetbrains.letsPlot.core.plot.base.tooltip.TestUtil
import kotlin.test.BeforeTest
import kotlin.test.Test

class LayerTargetLocatorTwoDistinctPolygonsTest {
    private lateinit var myLocator: GeomTargetLocator

    @BeforeTest
    fun setUp() {
        myLocator = TestUtil.createLocator(LookupStrategy.HOVER, LookupSpace.XY, FIRST_TARGET, SECOND_TARGET)
    }

    @Test
    fun pointInsideFirstPolygon_ShouldReturnFirstPolygonKey() {
        TestUtil.assertObjects(myLocator, FIRST_POLYGON_POINT_INSIDE, FIRST_POLYGON_KEY)
    }

    @Test
    fun pointInsideSecondPolygon_ShouldReturnSecondPolygonKey() {
        TestUtil.assertObjects(myLocator, SECOND_POLYGON_POINT_INSIDE, SECOND_POLYGON_KEY)
    }

    companion object {
        private val FIRST_POLYGON = TestUtil.polygon(
            TestUtil.point(0.0, 0.0),
            TestUtil.point(100.0, 0.0),
            TestUtil.point(100.0, 100.0),
            TestUtil.point(0.0, 100.0)
        )

        private const val FIRST_POLYGON_KEY = 1
        private val FIRST_TARGET = TestUtil.polygonTarget(FIRST_POLYGON_KEY, FIRST_POLYGON)
        private val FIRST_POLYGON_POINT_INSIDE = TestUtil.point(50.0, 50.0)

        private val SECOND_POLYGON = TestUtil.polygon(
            TestUtil.point(200.0, 200.0),
            TestUtil.point(300.0, 300.0),
            TestUtil.point(400.0, 200.0)
        )

        private const val SECOND_POLYGON_KEY = 2
        private val SECOND_TARGET = TestUtil.polygonTarget(SECOND_POLYGON_KEY, SECOND_POLYGON)
        private val SECOND_POLYGON_POINT_INSIDE = TestUtil.point(300.0, 250.0)
    }
}
