/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.tooltip.loc

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Untyped
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Vec
import org.jetbrains.letsPlot.commons.intern.typedGeometry.algorithms.splitRings
import org.jetbrains.letsPlot.commons.intern.typedGeometry.createMultiPolygon
import org.jetbrains.letsPlot.commons.intern.typedGeometry.explicitVec
import org.jetbrains.letsPlot.core.plot.base.aes.AestheticsBuilder
import org.jetbrains.letsPlot.core.plot.base.aes.AestheticsBuilder.Companion.list
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomUtil
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator.LookupSpace
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator.LookupStrategy
import org.jetbrains.letsPlot.core.plot.base.tooltip.TestUtil
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PolygonEdgeCasesTest {

    private val polygonLocator: GeomTargetLocator
        get() = TestUtil.createLocator(
            LookupStrategy.HOVER, LookupSpace.XY,
            TARGET
        )

    @Test
    fun whenInside_AndControlSegmentGoesThroughSegmentJoints_ShouldFindPolygon() {

        val locator = polygonLocator

        TestUtil.assertObjects(locator, TestUtil.point(X_INSIDE, JOINT.y), POLYGON_KEY)
    }

    @Test
    fun whenOutside_AndControlSegmentGoesThroughSegmentJoints_ShouldNotFindPolygon() {
        val locator = polygonLocator

        TestUtil.assertEmpty(locator, TestUtil.point(X_OUTSIDE_RIGHT, JOINT.y))
    }

    @Test
    fun whenOnTheLeftSide_AndControlSegmentGoesThroughSegmentJoint_ShouldFindPolygon() {
        val locator = polygonLocator

        TestUtil.assertObjects(locator, TestUtil.point(LEFT_COORD, JOINT.y), POLYGON_KEY)
    }

    @Test
    fun whenOnTheRightSide_AndControlSegmentGoesThroughSegmentJoints_ShouldNotFindPolygon() {
        val locator = polygonLocator

        TestUtil.assertEmpty(locator, TestUtil.point(RIGHT_COORD, JOINT.y))
    }

    @Test
    fun whenOnSide_AndControlSegmentLiesOnSegment_ShouldNotFindPolygon() {
        val locator = polygonLocator

        TestUtil.assertEmpty(locator, TestUtil.point(X_OUTSIDE_RIGHT, BOTTOM_COORD))
        TestUtil.assertEmpty(locator, TestUtil.point(X_OUTSIDE_RIGHT, TOP_COORD))
    }

    @Test
    fun whenOutside_AndPointsAreNotClosed_ShouldFindPolygon() {
        val points = listOf(
            TestUtil.point(0.0, 0.0),
            TestUtil.point(0.0, 100.0),
            TestUtil.point(100.0, 100.0)
        )

        val locator =
            TestUtil.createLocator(LookupStrategy.HOVER, LookupSpace.XY, TestUtil.polygonTarget(POLYGON_KEY, points))
        TestUtil.assertEmpty(locator, TestUtil.point(55.0, 50.0))
        TestUtil.assertObjects(locator, TestUtil.point(40.0, 50.0), POLYGON_KEY)
    }

    @Test
    fun geomUtilSplitRingsFromPath_WhenRegionsAreClosed_ShouldAddRegions() {
        val ring1 = TestUtil.polygon(
            TestUtil.point(0.0, 0.0),
            TestUtil.point(5.0, 5.0),
            TestUtil.point(7.0, 7.0)
        )


        val ring2 = TestUtil.polygon(
            TestUtil.point(10.0, 10.0),
            TestUtil.point(15.0, 15.0),
            TestUtil.point(5.0, 5.0)
        )

        val ring3 = TestUtil.polygon(
            TestUtil.point(45.0, 45.0),
            TestUtil.point(90.0, 90.0),
            TestUtil.point(12.0, 12.0)
        )

        val list = TestUtil.multipolygon(ring1, ring2, ring3)

        val rings = splitRings(list)

        assertEquals(3, rings.size)
        assertEquals(ring1, rings[0])
        assertEquals(ring2, rings[1])
        assertEquals(ring3, rings[2])
    }

    @Test
    fun geomUtilSplitRingsFromPath_WhenRingIsNotClosed_ShouldCloseRing() {
        val openRing = listOf(
            TestUtil.point(0.0, 0.0),
            TestUtil.point(5.0, 5.0),
            TestUtil.point(7.0, 7.0)
        )

        val rings = splitRings(openRing)

        val expectedRing = ArrayList(openRing)
        expectedRing.add(openRing[0])

        assertEquals(1, rings.size)
        assertEquals(expectedRing, rings[0])
    }

    @Test
    fun geomUtilSplitRingsFromPath_WhenPathIsEmpty_ShouldReturnEmptyList() {
        val rings = splitRings(emptyList<Any>())

        assertTrue(rings.isEmpty())
    }

    @Test
    fun pathDataConstructor_ShouldNotReduceRingControlPoints() {
        val startRing1 = TestUtil.point(0.0, 0.0)
        val startRing2 = TestUtil.point(0.0, 10.0)
        val startRing3 = TestUtil.point(0.5, 10.0)

        val polygon = TestUtil.multipolygon(
            TestUtil.polygon(
                            startRing1,
                            TestUtil.point(4.0, 0.0)
                        ),
            TestUtil.polygon(
                            startRing2,
                            TestUtil.point(4.0, 10.0),
                            TestUtil.point(0.2, 10.0)
                        ),
            TestUtil.polygon(
                            startRing3,
                            TestUtil.point(4.0, 20.0)
                        )
        )

        val aes = AestheticsBuilder(polygon.size)
            .x(list(polygon.map(DoubleVector::x)))
            .y(list(polygon.map(DoubleVector::y)))
            .build()

        val pathData = GeomUtil.createPaths(aes.dataPoints(), GeomUtil.TO_LOCATION_X_Y, sorted = true) {}
        val rings = splitRings(pathData[0].coordinates)

        assertEquals(3, rings.size)
        assertRing(rings[0], startRing1)
        assertRing(rings[1], startRing2)
        assertRing(rings[2], startRing3)
    }

    @Test
    fun geomUtilCreateMultipolygon_fromThreeRing() {
        fun vec(x: Double, y: Double) = explicitVec<Untyped>(x, y)

        val points = ArrayList<Vec<Untyped>>()
        points.addAll(
            listOf(
                vec(0.0, 0.0),
                vec(0.0, 50.0),
                vec(50.0, 50.0),
                vec(50.0, 0.0),
                vec(0.0, 0.0)
            )
        )

        points.addAll(
            listOf(
                vec(100.0, 0.0),
                vec(100.0, 50.0),
                vec(150.0, 50.0),
                vec(150.0, 0.0),
                vec(100.0, 0.0)
            )
        )

        points.addAll(
            listOf(
                vec(110.0, 10.0),
                vec(140.0, 10.0),
                vec(140.0, 40.0),
                vec(110.0, 40.0),
                vec(110.0, 10.0)
            )
        )

        val multipolygon = createMultiPolygon(points)

        assertEquals(2, multipolygon.size)
        assertEquals(1, multipolygon[0].size)
        assertEquals(2, multipolygon[1].size)
    }

    private fun assertRing(ring: List<DoubleVector>, startPoint: DoubleVector) {
        assertEquals(TestUtil.first(ring), TestUtil.last(ring))
        assertEquals(startPoint, TestUtil.first(ring))
    }

    companion object {

        private const val LEFT_COORD = 0.0
        private const val RIGHT_COORD = 200.0
        private const val X_INSIDE = 50.0
        private const val X_OUTSIDE_RIGHT = 300.0

        private const val TOP_COORD = 200.0
        private const val BOTTOM_COORD = 0.0
        private val JOINT = DoubleVector(100.0, 100.0)

        /*
    200 *-------*
        |       |
    100 *       *
        |       |
      0 *-------*
        0   100 200
  */

        private val POLYGON = TestUtil.polygon(
            TestUtil.point(LEFT_COORD, BOTTOM_COORD),
            TestUtil.point(LEFT_COORD, JOINT.y),
            TestUtil.point(LEFT_COORD, TOP_COORD),
            TestUtil.point(RIGHT_COORD, TOP_COORD),
            TestUtil.point(RIGHT_COORD, JOINT.y),
            TestUtil.point(RIGHT_COORD, BOTTOM_COORD)
        )


        private const val POLYGON_KEY = 1
        private val TARGET = TestUtil.polygonTarget(POLYGON_KEY, POLYGON)

    }
}

