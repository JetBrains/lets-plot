/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.tooltip.loc

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator
import jetbrains.datalore.plot.builder.tooltip.TestUtil
import jetbrains.datalore.plot.builder.tooltip.TestUtil.assertEmpty
import jetbrains.datalore.plot.builder.tooltip.TestUtil.assertObjects
import jetbrains.datalore.plot.builder.tooltip.TestUtil.between
import jetbrains.datalore.plot.builder.tooltip.TestUtil.createLocator
import jetbrains.datalore.plot.builder.tooltip.TestUtil.inside
import jetbrains.datalore.plot.builder.tooltip.TestUtil.outsideX
import jetbrains.datalore.plot.builder.tooltip.TestUtil.point
import jetbrains.datalore.plot.builder.tooltip.TestUtil.polygon
import jetbrains.datalore.plot.builder.tooltip.TestUtil.polygonTarget
import kotlin.test.Test

class MultiPolygonTest {

    private val polygonLocator: GeomTargetLocator
        get() = createLocator(GeomTargetLocator.LookupStrategy.HOVER, GeomTargetLocator.LookupSpace.XY, FIRST_TARGET)

    @Test
    fun pointInsideFirst_NotInHole_ShouldFindPolygon() {
        val locator = polygonLocator

        assertObjects(locator, between(FIRST_POLYGON_RECT, HOLE_RECT), FIRST_POLYGON_KEY)
    }

    @Test
    fun pointInsideFirst_InsideHole_ShouldFindNothing() {
        val locator = polygonLocator

        assertEmpty(locator, inside(HOLE_RECT))
    }

    @Test
    fun pointInsideSecond_ShouldFindPolygon() {
        val locator = polygonLocator

        assertObjects(locator, inside(SECOND_POLYGON_RECT), FIRST_POLYGON_KEY)
    }

    @Test
    fun pointRightFromSecond_ShouldFindNothing() {
        val locator = polygonLocator

        assertEmpty(locator, outsideX(SECOND_POLYGON_RECT))
    }

    companion object {

        /*
    200 *-------*
    150 |  *-*  |       *----*  150
        |  | |  |       |    |
     50 |  *-*  |       *----*  50
      0 *-------*
        0       200     400  500
  */

        private val HOLE_RECT = DoubleRectangle(50.0, 50.0, 100.0, 100.0)
        private val SECOND_POLYGON_RECT = DoubleRectangle(400.0, 150.0, 100.0, 100.0)
        private val FIRST_POLYGON_RECT = DoubleRectangle(0.0, 0.0, 200.0, 200.0)

        private val FIRST_POLYGON = TestUtil.multipolygon(
            polygonFromRect(HOLE_RECT),
            polygonFromRect(SECOND_POLYGON_RECT),
            polygonFromRect(FIRST_POLYGON_RECT)
        )

        private const val FIRST_POLYGON_KEY = 1
        private val FIRST_TARGET = polygonTarget(FIRST_POLYGON_KEY, FIRST_POLYGON)

        private fun polygonFromRect(rect: DoubleRectangle): MutableList<DoubleVector> {

            return polygon(
                    point(rect.left, rect.top),
                    point(rect.left, rect.bottom),
                    point(rect.right, rect.bottom),
                    point(rect.right, rect.top))
        }
    }

}
