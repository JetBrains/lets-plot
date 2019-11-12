/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact.loc

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.projectionGeometry.GeoUtils.createMultiPolygon
import jetbrains.datalore.base.projectionGeometry.GeoUtils.createRingsFromPoints
import jetbrains.datalore.base.values.Pair
import jetbrains.datalore.plot.base.aes.AestheticsBuilder
import jetbrains.datalore.plot.base.aes.AestheticsBuilder.Companion.collection
import jetbrains.datalore.plot.base.geom.util.GeomUtil
import jetbrains.datalore.plot.base.geom.util.GeomUtil.rectToGeometry
import jetbrains.datalore.plot.base.geom.util.MultiPointDataConstructor
import jetbrains.datalore.plot.base.geom.util.MultiPointDataConstructor.collector
import jetbrains.datalore.plot.base.geom.util.MultiPointDataConstructor.multiPointAppender
import jetbrains.datalore.plot.base.geom.util.MultiPointDataConstructor.reducer
import jetbrains.datalore.plot.base.geom.util.MultiPointDataConstructor.singlePointAppender
import jetbrains.datalore.plot.base.interact.GeomTargetLocator
import jetbrains.datalore.plot.base.interact.GeomTargetLocator.LookupSpace
import jetbrains.datalore.plot.base.interact.GeomTargetLocator.LookupStrategy
import jetbrains.datalore.plot.builder.interact.TestUtil.assertEmpty
import jetbrains.datalore.plot.builder.interact.TestUtil.assertObjects
import jetbrains.datalore.plot.builder.interact.TestUtil.createLocator
import jetbrains.datalore.plot.builder.interact.TestUtil.first
import jetbrains.datalore.plot.builder.interact.TestUtil.last
import jetbrains.datalore.plot.builder.interact.TestUtil.map
import jetbrains.datalore.plot.builder.interact.TestUtil.multipolygon
import jetbrains.datalore.plot.builder.interact.TestUtil.point
import jetbrains.datalore.plot.builder.interact.TestUtil.polygon
import jetbrains.datalore.plot.builder.interact.TestUtil.polygonTarget
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PolygonEdgeCasesTest {

    private val polygonLocator: GeomTargetLocator
        get() = createLocator(LookupStrategy.HOVER, LookupSpace.XY,
            jetbrains.datalore.plot.builder.interact.loc.PolygonEdgeCasesTest.Companion.TARGET
        )

    @Test
    fun whenInside_AndControlSegmentGoesThroughSegmentJoints_ShouldFindPolygon() {

        val locator = polygonLocator

        assertObjects(locator, point(jetbrains.datalore.plot.builder.interact.loc.PolygonEdgeCasesTest.Companion.X_INSIDE, jetbrains.datalore.plot.builder.interact.loc.PolygonEdgeCasesTest.Companion.JOINT.y),
            jetbrains.datalore.plot.builder.interact.loc.PolygonEdgeCasesTest.Companion.POLYGON_KEY
        )
    }

    @Test
    fun whenOutside_AndControlSegmentGoesThroughSegmentJoints_ShouldNotFindPolygon() {
        val locator = polygonLocator

        assertEmpty(locator, point(jetbrains.datalore.plot.builder.interact.loc.PolygonEdgeCasesTest.Companion.X_OUTSIDE_RIGHT, jetbrains.datalore.plot.builder.interact.loc.PolygonEdgeCasesTest.Companion.JOINT.y))
    }

    @Test
    fun whenOnTheLeftSide_AndControlSegmentGoesThroughSegmentJoint_ShouldFindPolygon() {
        val locator = polygonLocator

        assertObjects(locator, point(jetbrains.datalore.plot.builder.interact.loc.PolygonEdgeCasesTest.Companion.LEFT_COORD, jetbrains.datalore.plot.builder.interact.loc.PolygonEdgeCasesTest.Companion.JOINT.y),
            jetbrains.datalore.plot.builder.interact.loc.PolygonEdgeCasesTest.Companion.POLYGON_KEY
        )
    }

    @Test
    fun whenOnTheRightSide_AndControlSegmentGoesThroughSegmentJoints_ShouldNotFindPolygon() {
        val locator = polygonLocator

        assertEmpty(locator, point(jetbrains.datalore.plot.builder.interact.loc.PolygonEdgeCasesTest.Companion.RIGHT_COORD, jetbrains.datalore.plot.builder.interact.loc.PolygonEdgeCasesTest.Companion.JOINT.y))
    }

    @Test
    fun whenOnSide_AndControlSegmentLiesOnSegment_ShouldNotFindPolygon() {
        val locator = polygonLocator

        assertEmpty(locator, point(
            jetbrains.datalore.plot.builder.interact.loc.PolygonEdgeCasesTest.Companion.X_OUTSIDE_RIGHT,
            jetbrains.datalore.plot.builder.interact.loc.PolygonEdgeCasesTest.Companion.BOTTOM_COORD
        ))
        assertEmpty(locator, point(
            jetbrains.datalore.plot.builder.interact.loc.PolygonEdgeCasesTest.Companion.X_OUTSIDE_RIGHT,
            jetbrains.datalore.plot.builder.interact.loc.PolygonEdgeCasesTest.Companion.TOP_COORD
        ))
    }

    @Test
    fun whenOutside_AndPointsAreNotClosed_ShouldFindPolygon() {
        val points = listOf(
                point(0.0, 0.0),
                point(0.0, 100.0),
                point(100.0, 100.0)
        )

        val locator = createLocator(LookupStrategy.HOVER, LookupSpace.XY, polygonTarget(jetbrains.datalore.plot.builder.interact.loc.PolygonEdgeCasesTest.Companion.POLYGON_KEY, points))
        assertEmpty(locator, point(55.0, 50.0))
        assertObjects(locator, point(40.0, 50.0),
            jetbrains.datalore.plot.builder.interact.loc.PolygonEdgeCasesTest.Companion.POLYGON_KEY
        )
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

        val rings = createRingsFromPoints(list)

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

        val rings = createRingsFromPoints(openRing)

        val expectedRing = ArrayList(openRing)
        expectedRing.add(openRing[0])

        assertEquals(1, rings.size)
        assertEquals(expectedRing, rings[0])
    }

    @Test
    fun geomUtilSplitRingsFromPath_WhenPathIsEmpty_ShouldReturnEmptyList() {
        val rings = createRingsFromPoints(emptyList<Any>())

        assertTrue(rings.isEmpty())
    }

    @Test
    fun multiPointDataConstructor_ShouldNotReduceRingControlPoints() {
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
                .x(collection(map(polygon) { point -> point.x }))
                .y(collection(map(polygon) { point -> point.y }))
                .build()

        val multiPointDataList = MultiPointDataConstructor.createMultiPointDataByGroup(
                aes.dataPoints(),
                singlePointAppender(GeomUtil.TO_LOCATION_X_Y),
                reducer(1.0, true)
        )
        val rings = createRingsFromPoints(multiPointDataList[0].points)

        assertEquals(3, rings.size)
        assertRing(rings[0], startRing1)
        assertRing(rings[1], startRing2)
        assertRing(rings[2], startRing3)
    }

    @Test
    fun multiPointDataConstructor_ShouldGroupRectangles() {
        val leftFromAntiMeridian = DoubleRectangle.span(point(120.0, 50.0), point(180.0, 20.0))
        val rightFromAntiMeridian = DoubleRectangle.span(point(-180.0, 50.0), point(-90.0, 20.0))

        val rects = listOf(
                Pair(1, leftFromAntiMeridian),
                Pair(2, DoubleRectangle.span(point(30.0, 40.0), point(20.0, 10.0))),
                Pair(1, rightFromAntiMeridian))

        val aes = AestheticsBuilder(rects.size)
                .xmin { i -> rects[i].second.left }
                .xmax { i -> rects[i].second.right }
                .ymin { i -> rects[i].second.bottom }
                .ymax { i -> rects[i].second.top }
                .group { i -> rects[i].first }
                .build()

        val multiPointDataList = MultiPointDataConstructor.createMultiPointDataByGroup(
                aes.dataPoints(),
                multiPointAppender(GeomUtil.TO_RECTANGLE),
                collector()
        )

        assertEquals(2, multiPointDataList.size)

        val rings = createRingsFromPoints(multiPointDataList[0].points)
        assertEquals(2, rings.size)
        assertEquals(
            jetbrains.datalore.plot.builder.interact.loc.PolygonEdgeCasesTest.Companion.polygonFromRect(
                leftFromAntiMeridian
            ), rings[0])
        assertEquals(
            jetbrains.datalore.plot.builder.interact.loc.PolygonEdgeCasesTest.Companion.polygonFromRect(
                rightFromAntiMeridian
            ), rings[1])
    }

    @Test
    fun geomUtilCreateMultipolygon_fromThreeRing() {
        val lonLatPoints = multipolygon(
                polygon(
                        point(0.0, 0.0),
                        point(0.0, 50.0),
                        point(50.0, 50.0),
                        point(50.0, 0.0)
                ),
                polygon(
                        point(100.0, 0.0),
                        point(100.0, 50.0),
                        point(150.0, 50.0),
                        point(150.0, 0.0)
                ),
                polygon(
                        point(110.0, 10.0),
                        point(140.0, 10.0),
                        point(140.0, 40.0),
                        point(110.0, 40.0)
                )
        )

        val multipolygon = createMultiPolygon(lonLatPoints)

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
                point(
                    jetbrains.datalore.plot.builder.interact.loc.PolygonEdgeCasesTest.Companion.LEFT_COORD,
                    jetbrains.datalore.plot.builder.interact.loc.PolygonEdgeCasesTest.Companion.BOTTOM_COORD
                ),
                point(jetbrains.datalore.plot.builder.interact.loc.PolygonEdgeCasesTest.Companion.LEFT_COORD, jetbrains.datalore.plot.builder.interact.loc.PolygonEdgeCasesTest.Companion.JOINT.y),
                point(
                    jetbrains.datalore.plot.builder.interact.loc.PolygonEdgeCasesTest.Companion.LEFT_COORD,
                    jetbrains.datalore.plot.builder.interact.loc.PolygonEdgeCasesTest.Companion.TOP_COORD
                ),
                point(
                    jetbrains.datalore.plot.builder.interact.loc.PolygonEdgeCasesTest.Companion.RIGHT_COORD,
                    jetbrains.datalore.plot.builder.interact.loc.PolygonEdgeCasesTest.Companion.TOP_COORD
                ),
                point(jetbrains.datalore.plot.builder.interact.loc.PolygonEdgeCasesTest.Companion.RIGHT_COORD, jetbrains.datalore.plot.builder.interact.loc.PolygonEdgeCasesTest.Companion.JOINT.y),
                point(
                    jetbrains.datalore.plot.builder.interact.loc.PolygonEdgeCasesTest.Companion.RIGHT_COORD,
                    jetbrains.datalore.plot.builder.interact.loc.PolygonEdgeCasesTest.Companion.BOTTOM_COORD
                )
        )


        private const val POLYGON_KEY = 1
        private val TARGET = polygonTarget(
            jetbrains.datalore.plot.builder.interact.loc.PolygonEdgeCasesTest.Companion.POLYGON_KEY,
            jetbrains.datalore.plot.builder.interact.loc.PolygonEdgeCasesTest.Companion.POLYGON
        )

        private fun polygonFromRect(rect: DoubleRectangle): List<DoubleVector> {
            return rectToGeometry(rect.left, rect.bottom, rect.right, rect.top)
        }
    }
}
