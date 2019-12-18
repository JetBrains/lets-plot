package jetbrains.datalore.plot.config.geo

import jetbrains.datalore.base.spatial.GeoRectangle
import jetbrains.datalore.base.spatial.LonLat
import jetbrains.datalore.base.typedGeometry.*
import jetbrains.datalore.plot.builder.map.GeoPositionField.POINT_X
import jetbrains.datalore.plot.builder.map.GeoPositionField.POINT_Y
import jetbrains.datalore.plot.builder.map.GeoPositionField.RECT_XMAX
import jetbrains.datalore.plot.builder.map.GeoPositionField.RECT_XMIN
import jetbrains.datalore.plot.builder.map.GeoPositionField.RECT_YMAX
import jetbrains.datalore.plot.builder.map.GeoPositionField.RECT_YMIN
import jetbrains.datalore.plot.config.GeoPositionsDataUtil.GeoDataKind.*
import jetbrains.datalore.plot.config.GeoPositionsDataUtil.MAP_COLUMN_JOIN_KEY
import kotlin.test.Test
import kotlin.test.assertEquals


class GeoPositionsDataCombinerTest {

    @Test
    fun shouldNotAddPointForPolygonMode() {
        val pointCombiner = createBoundaryCombiner()
        pointCombiner.addPoint(OBJECT_ID, POINT)

        assertEquals(
            ExpectedDataBuilder().buildPoints(),
            pointCombiner.data
        )
    }

    @Test
    fun shouldAddPointForPathMode() {
        val pointCombiner = createCentroidCombiner()
        pointCombiner.addPoint(OBJECT_ID, POINT)

        assertEquals(
            ExpectedDataBuilder()
                .addPoint(OBJECT_ID, POINT)
                .buildPoints(),
            pointCombiner.data
        )
    }

    @Test
    fun shouldClosePathForPolygonMode() {
        val polygonCombiner = createBoundaryCombiner()
        polygonCombiner.addBoundary(OBJECT_ID, MultiPolygon(listOf(Polygon(listOf(OPEN_PATH)))))

        assertEquals(
            ExpectedDataBuilder()
                .addPoints(OBJECT_ID, OPEN_PATH)
                .addPoint(OBJECT_ID, OPEN_PATH.get(0))
                .buildPoints(),
            polygonCombiner.data
        )
    }

    @Test
    fun shouldNotClosePathForNotPolygonMode() {
        val pointCombiner = createCentroidCombiner()
        pointCombiner.addBoundary(OBJECT_ID, MultiPolygon(listOf(Polygon(listOf(OPEN_PATH)))))

        assertEquals(
            ExpectedDataBuilder()
                .addPoints(OBJECT_ID, OPEN_PATH)
                .buildPoints(),
            pointCombiner.data
        )
    }

    @Test
    fun whenGeoRectangleCrossAntiMeridian_itShouldBeCut() {
        val rectCombiner = createLimitCombiner()
        rectCombiner.addGeoRectangle(OBJECT_ID, RECT_CROSSED_ANTI_MERIDIAN)

        assertEquals(
            ExpectedDataBuilder()
                .addRect(OBJECT_ID, POSITIVE_LON, 180.0, NEGATIVE_LAT, POSITIVE_LAT)
                .addRect(OBJECT_ID, -180.0, NEGATIVE_LON, NEGATIVE_LAT, POSITIVE_LAT)
                .buildLimits(),
            rectCombiner.data
        )
    }

    @Test
    fun whenGeoRectangleNotCrossAntiMeridian_itShouldNotBeCut() {
        val rectCombiner = createLimitCombiner()
        rectCombiner.addGeoRectangle(OBJECT_ID, RECT_NOT_CROSSED_ANTI_MERIDIAN)

        assertEquals(
            ExpectedDataBuilder()
                .addRect(OBJECT_ID, NEGATIVE_LON, POSITIVE_LON, NEGATIVE_LAT, POSITIVE_LAT)
                .buildLimits(),
            rectCombiner.data
        )
    }

    @Test
    fun shouldCalculateBBoxForBoundary() {
        val rectCombiner = createLimitCombiner()
        rectCombiner.addBoundary(
            OBJECT_ID, MultiPolygon(listOf(
                Polygon(listOf(
                    Ring(listOf(
                        point(NEGATIVE_LON, ZERO),
                        point(POSITIVE_LON, ZERO),
                        point(ZERO, NEGATIVE_LAT),
                        point(ZERO, POSITIVE_LAT)
                    ))
                ))
            ))
        )

        assertEquals(
            ExpectedDataBuilder()
                .addRect(OBJECT_ID, NEGATIVE_LON, POSITIVE_LON, NEGATIVE_LAT, POSITIVE_LAT)
                .buildLimits(),
            rectCombiner.data
        )
    }

    @Test
    fun shouldNotAddPoint() {
        val rectCombiner = createLimitCombiner()
        rectCombiner.addPoint(OBJECT_ID, point(POSITIVE_LON, POSITIVE_LAT))

        assertEquals(
            ExpectedDataBuilder().buildLimits(),
            rectCombiner.data
        )
    }

    private class ExpectedDataBuilder {
        private val myIdList = ArrayList<String>()
        private val myXList = ArrayList<Double>()
        private val myYList = ArrayList<Double>()
        private val myXMinList = ArrayList<Double>()
        private val myXMaxList = ArrayList<Double>()
        private val myYMinList = ArrayList<Double>()
        private val myYMaxList = ArrayList<Double>()

        internal fun addRect(
            id: String,
            xmin: Double,
            xmax: Double,
            ymin: Double,
            ymax: Double
        ): ExpectedDataBuilder {
            myIdList.add(id)
            myXMinList.add(xmin)
            myXMaxList.add(xmax)
            myYMinList.add(ymin)
            myYMaxList.add(ymax)
            return this
        }

        internal fun addPoints(id: String, coords: List<Vec<*>>): ExpectedDataBuilder {
            coords.forEach { coord -> addPoint(id, coord) }
            return this
        }

        internal fun addPoint(id: String, coord: Vec<*>): ExpectedDataBuilder {
            myIdList.add(id)
            myXList.add(coord.x)
            myYList.add(coord.y)
            return this
        }

        internal fun buildPoints(): Map<String, Any> {
            return mapOf(
                MAP_COLUMN_JOIN_KEY to myIdList,
                POINT_X to myXList,
                POINT_Y to myYList
            )
        }

        internal fun buildLimits(): Map<String, Any> {
            return mapOf(
                MAP_COLUMN_JOIN_KEY to myIdList,
                RECT_XMIN to myXMinList,
                RECT_XMAX to myXMaxList,
                RECT_YMIN to myYMinList,
                RECT_YMAX to myYMaxList
            )
        }
    }

    companion object {
        private val OBJECT_ID = "42"
        private val POINT = point(15.0, 27.0)
        private val OPEN_PATH = Ring<LonLat>(listOf(
            point(0.0, 0.0),
            point(0.0, 1.0),
            point(1.0, 1.0)
        )
        )
        private val NEGATIVE_LON = -90.0
        private val POSITIVE_LON = 150.0
        private val NEGATIVE_LAT = -10.0
        private val POSITIVE_LAT = 30.0
        private val ZERO = 0.0
        private val RECT_CROSSED_ANTI_MERIDIAN = GeoRectangle(POSITIVE_LON, NEGATIVE_LAT, NEGATIVE_LON, POSITIVE_LAT)
        private val RECT_NOT_CROSSED_ANTI_MERIDIAN =
            GeoRectangle(NEGATIVE_LON, NEGATIVE_LAT, POSITIVE_LON, POSITIVE_LAT)

        private fun point(x: Double, y: Double): Vec<LonLat> {
            return explicitVec(x, y)
        }

        private fun createBoundaryCombiner(): GeoPositionsDataCombiner {
            return GeoPositionsDataCombiner(BOUNDARY)
        }

        private fun createCentroidCombiner(): GeoPositionsDataCombiner {
            return GeoPositionsDataCombiner(CENTROID)
        }

        private fun createLimitCombiner(): GeoPositionsDataCombiner {
            return GeoPositionsDataCombiner(LIMIT)
        }
    }
}