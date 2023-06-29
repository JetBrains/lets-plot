/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact.loc

import jetbrains.datalore.base.algorithms.splitRings
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.typedGeometry.Untyped
import jetbrains.datalore.base.typedGeometry.Vec
import jetbrains.datalore.base.typedGeometry.createMultiPolygon
import jetbrains.datalore.base.typedGeometry.explicitVec
import jetbrains.datalore.plot.base.aes.AestheticsBuilder
import jetbrains.datalore.plot.base.aes.AestheticsBuilder.Companion.list
import jetbrains.datalore.plot.base.geom.util.GeomUtil
import jetbrains.datalore.plot.base.interact.GeomTargetLocator
import jetbrains.datalore.plot.base.interact.GeomTargetLocator.LookupSpace
import jetbrains.datalore.plot.base.interact.GeomTargetLocator.LookupStrategy
import jetbrains.datalore.plot.builder.interact.TestUtil.assertEmpty
import jetbrains.datalore.plot.builder.interact.TestUtil.assertObjects
import jetbrains.datalore.plot.builder.interact.TestUtil.createLocator
import jetbrains.datalore.plot.builder.interact.TestUtil.first
import jetbrains.datalore.plot.builder.interact.TestUtil.last
import jetbrains.datalore.plot.builder.interact.TestUtil.multipolygon
import jetbrains.datalore.plot.builder.interact.TestUtil.point
import jetbrains.datalore.plot.builder.interact.TestUtil.polygon
import jetbrains.datalore.plot.builder.interact.TestUtil.polygonTarget
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PolygonEdgeCasesTest {

    private val polygonLocator: GeomTargetLocator
        get() = createLocator(
            LookupStrategy.HOVER, LookupSpace.XY,
            TARGET
        )

    @Test
    fun whenInside_AndControlSegmentGoesThroughSegmentJoints_ShouldFindPolygon() {

        val locator = polygonLocator

        assertObjects(locator, point(X_INSIDE, JOINT.y), POLYGON_KEY)
    }

    @Test
    fun whenOutside_AndControlSegmentGoesThroughSegmentJoints_ShouldNotFindPolygon() {
        val locator = polygonLocator

        assertEmpty(locator, point(X_OUTSIDE_RIGHT, JOINT.y))
    }

    @Test
    fun whenOnTheLeftSide_AndControlSegmentGoesThroughSegmentJoint_ShouldFindPolygon() {
        val locator = polygonLocator

        assertObjects(locator, point(LEFT_COORD, JOINT.y), POLYGON_KEY)
    }

    @Test
    fun whenOnTheRightSide_AndControlSegmentGoesThroughSegmentJoints_ShouldNotFindPolygon() {
        val locator = polygonLocator

        assertEmpty(locator, point(RIGHT_COORD, JOINT.y))
    }

    @Test
    fun whenOnSide_AndControlSegmentLiesOnSegment_ShouldNotFindPolygon() {
        val locator = polygonLocator

        assertEmpty(locator, point(X_OUTSIDE_RIGHT, BOTTOM_COORD))
        assertEmpty(locator, point(X_OUTSIDE_RIGHT, TOP_COORD))
    }

    @Test
    fun whenOutside_AndPointsAreNotClosed_ShouldFindPolygon() {
        val points = listOf(
            point(0.0, 0.0),
            point(0.0, 100.0),
            point(100.0, 100.0)
        )

        val locator = createLocator(LookupStrategy.HOVER, LookupSpace.XY, polygonTarget(POLYGON_KEY, points))
        assertEmpty(locator, point(55.0, 50.0))
        assertObjects(locator, point(40.0, 50.0), POLYGON_KEY)
    }

    @Test
    fun geomUtilSplitRingsFromPath_WhenRegionsAreClosed_ShouldAddRegions() {
        val ring1 = polygon(
            point(0.0, 0.0),
            point(5.0, 5.0),
            point(7.0, 7.0)
        )


        val ring2 = polygon(
            point(10.0, 10.0),
            point(15.0, 15.0),
            point(5.0, 5.0)
        )

        val ring3 = polygon(
            point(45.0, 45.0),
            point(90.0, 90.0),
            point(12.0, 12.0)
        )

        val list = multipolygon(ring1, ring2, ring3)

        val rings = splitRings(list)

        assertEquals(3, rings.size)
        assertEquals(ring1, rings[0])
        assertEquals(ring2, rings[1])
        assertEquals(ring3, rings[2])
    }

    @Test
    fun geomUtilSplitRingsFromPath_WhenRingIsNotClosed_ShouldCloseRing() {
        val openRing = listOf(
            point(0.0, 0.0),
            point(5.0, 5.0),
            point(7.0, 7.0)
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
        val startRing1 = point(0.0, 0.0)
        val startRing2 = point(0.0, 10.0)
        val startRing3 = point(0.5, 10.0)

        val polygon = multipolygon(
            polygon(
                startRing1,
                point(4.0, 0.0)
            ),
            polygon(
                startRing2,
                point(4.0, 10.0),
                point(0.2, 10.0)
            ),
            polygon(
                startRing3,
                point(4.0, 20.0)
            )
        )

        val aes = AestheticsBuilder(polygon.size)
            .x(list(polygon.map(DoubleVector::x)))
            .y(list(polygon.map(DoubleVector::y)))
            .build()

        val pathData = GeomUtil.createPathGroups(
            aes.dataPoints(),
            GeomUtil.TO_LOCATION_X_Y,
        )
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
        assertEquals(first(ring), last(ring))
        assertEquals(startPoint, first(ring))
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

        private val POLYGON = polygon(
            point(LEFT_COORD, BOTTOM_COORD),
            point(LEFT_COORD, JOINT.y),
            point(LEFT_COORD, TOP_COORD),
            point(RIGHT_COORD, TOP_COORD),
            point(RIGHT_COORD, JOINT.y),
            point(RIGHT_COORD, BOTTOM_COORD)
        )


        private const val POLYGON_KEY = 1
        private val TARGET = polygonTarget(POLYGON_KEY, POLYGON)

    }
}

