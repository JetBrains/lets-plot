/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.spatial


import jetbrains.datalore.base.function.Consumer
import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.spatial.GeoBoundingBoxCalculator.Companion.MAX_LATITUDE_GETTER
import jetbrains.datalore.base.spatial.GeoBoundingBoxCalculator.Companion.MAX_LONGITUDE_GETTER
import jetbrains.datalore.base.spatial.GeoBoundingBoxCalculator.Companion.MIN_LATITUDE_GETTER
import jetbrains.datalore.base.spatial.GeoBoundingBoxCalculator.Companion.MIN_LONGITUDE_GETTER
import jetbrains.datalore.base.spatial.GeoBoundingBoxCalculator.Companion.calculateLoopLimitRange
import jetbrains.datalore.base.spatial.GeoRectangleTestHelper.assertDoubleEquals
import jetbrains.datalore.base.spatial.GeoRectangleTestHelper.point
import jetbrains.datalore.base.spatial.GeoRectangleTestHelper.rectangle
import jetbrains.datalore.base.spatial.GeoUtils.BBOX_CALCULATOR
import jetbrains.datalore.base.spatial.GeoUtils.convertToGeoRectangle
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class GeoBoundingBoxCalculatorTest {

    @Test
    fun whenPointsCloseToAntiMeridian_ShouldBoundingBoxCrossesAntiMeridian() {
        checkLocationsWhichCalculatedFromPoints(
                longitudeLimitEqualsChecker(FIRST_POINT, SECOND_POINT),
                FIRST_POINT, SECOND_POINT)
    }

    @Test
    fun whenPointsFarFromAntiMeridian_ShouldBoundingBoxNotCrossAntiMeridian() {
        checkLocationsWhichCalculatedFromPoints(
                longitudeLimitEqualsChecker(FOURTH_POINT, THIRD_POINT),
                THIRD_POINT, FOURTH_POINT)
    }

    @Test
    fun whenRectanglesCloseToAntiMeridian_ShouldBoundingBoxCrossesAntiMeridian() {
        checkLocationsWhichCalculatedFromRectangles(
                longitudeLimitEqualsChecker(FIRST_RECTANGLE, SECOND_RECTANGLE),
                FIRST_RECTANGLE, SECOND_RECTANGLE)
    }

    @Test
    fun whenRectangleCrossAntiMeridian_ShouldBoundingBoxCrossesAntiMeridian() {
        checkLocationsWhichCalculatedFromRectangles(
                longitudeLimitEqualsChecker(FIRST_RECTANGLE, SECOND_RECTANGLE),
                FIRST_RECTANGLE, SECOND_RECTANGLE, THIRD_RECTANGLE)
    }

    @Test
    fun simpleCalculateLatitudesForPoints() {
        checkLocationsWhichCalculatedFromPoints(
                latitudeLimitEqualsChecker(THIRD_POINT, FOURTH_POINT),
                FIRST_POINT, SECOND_POINT, THIRD_POINT, FOURTH_POINT)
    }

    @Test
    fun simpleCalculateLatitudesForRectangles() {
        checkLocationsWhichCalculatedFromRectangles(
                latitudeLimitEqualsChecker(FIRST_RECTANGLE, THIRD_RECTANGLE),
                FIRST_RECTANGLE, SECOND_RECTANGLE, THIRD_RECTANGLE)
    }

    @Test
    fun simpleCalculateLocationForEqualPoints() {
        checkLocationsWhichCalculatedFromPoints(
                boundingBoxEqualsChecker(FIRST_POINT),
                FIRST_POINT, FIRST_POINT)
    }

    @Test
    fun simpleCalculateLocationForEqualRectangles() {
        checkLocationsWhichCalculatedFromRectangles(
                boundingBoxEqualsChecker(FIRST_RECTANGLE),
                FIRST_RECTANGLE, FIRST_RECTANGLE)
    }

    @Test
    fun simpleCalculateLocationForZeroRectangle() {
        checkLocationsWhichCalculatedFromRectangles(
                boundingBoxEqualsChecker(SECOND_POINT),
                emptyRectangle(SECOND_POINT))
    }

    @Test
    fun simpleCalculateLocationForEmptyPoints() {
        expectNoCoordinatesException {
            checkLocationsWhichCalculatedFromPoints(NO_CHECKER)
        }
    }

    @Test
    fun simpleCalculateLocationForEmptyRectangles() {
        expectNoCoordinatesException {
            checkLocationsWhichCalculatedFromRectangles(NO_CHECKER)
        }
    }

    @Test
    fun calculateLoopLimitRangeForSingleRange() {
        val mapRange = ClosedRange.closed(0.0, 255.99999999999997)
        val range = ClosedRange.closed(100.0, 100.0)
        val coordinateHelper = createCoordinateHelper(listOf(range))

        val limitRange = calculateLoopLimitRange(coordinateHelper, mapRange)
        assertDoubleEquals(range.lowerEndpoint(), limitRange.lowerEndpoint())
        assertDoubleEquals(range.upperEndpoint(), limitRange.upperEndpoint())
    }

    private fun expectNoCoordinatesException(block: () -> Unit) {
        val expectMessage = "No coordinates for bounding box calculation."
        assertFailsWith(RuntimeException::class, expectMessage, block)
    }

    companion object {
        private val FIRST_POINT = point(97.0, -41.0)
        private val SECOND_POINT = point(-92.0, 62.0)
        private val THIRD_POINT = point(87.0, -43.0)
        private val FOURTH_POINT = point(-82.0, 64.0)
        private val FIRST_RECTANGLE = rectangle(125.0, -65.0, 145.0, -44.0)
        private val SECOND_RECTANGLE = rectangle(-145.0, 30.0, -105.0, 60.0)
        private val THIRD_RECTANGLE = rectangle(175.0, 35.0, -140.0, 65.0)

        internal val X_GETTER = { point: DoubleVector -> point.x }

        internal val Y_GETTER = { point: DoubleVector -> point.y }

        private val EMPTY_RECTANGLE_CHECKER = { location: GeoRectangle -> assertTrue(location.isEmpty) }

        private val NO_CHECKER = { _: GeoRectangle -> }

        private fun emptyRectangle(point: DoubleVector): GeoRectangle {
            return rectangle(point.x, point.y, point.x, point.y)
        }

        private fun longitudeLimitEqualsChecker(expectedLeft: Double, expectedRight: Double): Consumer<GeoRectangle> {
            return { location: GeoRectangle ->
                assertDoubleEquals(expectedLeft, location.minLongitude())
                assertDoubleEquals(expectedRight, location.maxLongitude())
            }
        }

        private fun longitudeLimitEqualsChecker(expectedLeft: DoubleVector, expectedRight: DoubleVector): Consumer<GeoRectangle> {
            return longitudeLimitEqualsChecker(expectedLeft.x, expectedRight.x)
        }

        private fun longitudeLimitEqualsChecker(expectedLeft: GeoRectangle, expectedRight: GeoRectangle): Consumer<GeoRectangle> {
            return longitudeLimitEqualsChecker(expectedLeft.minLongitude(), expectedRight.maxLongitude())
        }

        private fun latitudeLimitEqualsChecker(expectedBottom: Double, expectedTop: Double): Consumer<GeoRectangle> {
            return { location: GeoRectangle ->
                assertDoubleEquals(expectedBottom, location.minLatitude())
                assertDoubleEquals(expectedTop, location.maxLatitude())
            }
        }
    }

    private fun latitudeLimitEqualsChecker(expectedBottom: DoubleVector, expectedTop: DoubleVector): Consumer<GeoRectangle> {
        return latitudeLimitEqualsChecker(expectedBottom.y, expectedTop.y)
    }

    private fun latitudeLimitEqualsChecker(expectedBottom: GeoRectangle, expectedTop: GeoRectangle): Consumer<GeoRectangle> {
        return latitudeLimitEqualsChecker(expectedBottom.minLatitude(), expectedTop.maxLatitude())
    }

    private fun boundingBoxEqualsChecker(expected: DoubleVector): Consumer<GeoRectangle> {
        return { value: GeoRectangle ->
            EMPTY_RECTANGLE_CHECKER(value)
            longitudeLimitEqualsChecker(expected, expected)
            latitudeLimitEqualsChecker(expected, expected)
        }
    }

    private fun boundingBoxEqualsChecker(expected: GeoRectangle): Consumer<GeoRectangle> {
        return {
            longitudeLimitEqualsChecker(expected, expected)
            latitudeLimitEqualsChecker(expected, expected)
        }
    }

    private fun <T> asCoordinateList(geometries: List<T>, getCoordinate: (T) -> Double): List<Double> {
        val coordinates = ArrayList<Double>(geometries.size)
        for (geometry in geometries) {
            coordinates.add(getCoordinate(geometry))
        }
        return coordinates
    }

    private fun calculateLocationFromCoordinateArray(vararg points: DoubleVector): GeoRectangle {
        val lonLats = ArrayList<Double>(points.size * 2)
        for (point in points) {
            lonLats.add(point.x)
            lonLats.add(point.y)
        }
        return convertToGeoRectangle(BBOX_CALCULATOR.calculateBoundingBox(lonLats))
    }

    private fun calculateLocationFromLonLatArrays(vararg points: DoubleVector): GeoRectangle {
        val pointList = listOf(*points)
        val longitudes = asCoordinateList(pointList, X_GETTER)
        val latitudes = asCoordinateList(pointList, Y_GETTER)

        return convertToGeoRectangle(BBOX_CALCULATOR.calculateBoundingBox(longitudes, latitudes))
    }

    private fun calculateLocationFromLimitArrays(vararg rectangles: GeoRectangle): GeoRectangle {
        val rectangleList = listOf(*rectangles)
        val minLongitudes = asCoordinateList(rectangleList, MIN_LONGITUDE_GETTER)
        val minLatitudes = asCoordinateList(rectangleList, MIN_LATITUDE_GETTER)
        val maxLongitudes = asCoordinateList(rectangleList, MAX_LONGITUDE_GETTER)
        val maxLatitudes = asCoordinateList(rectangleList, MAX_LATITUDE_GETTER)

        return convertToGeoRectangle(BBOX_CALCULATOR.calculateBoundingBox(minLongitudes, minLatitudes, maxLongitudes, maxLatitudes))
    }

    private fun calculateLocationFromBoundingBoxArrays(vararg rectangles: GeoRectangle): GeoRectangle {
        return convertToGeoRectangle(BBOX_CALCULATOR.calculateBoundingBoxFromGeoRectangles(listOf(*rectangles)))
    }

    private fun checkLocationsWhichCalculatedFromPoints(locationChecker: Consumer<GeoRectangle>, vararg points: DoubleVector) {
        locationChecker(calculateLocationFromCoordinateArray(*points))
        locationChecker(calculateLocationFromLonLatArrays(*points))
    }

    private fun checkLocationsWhichCalculatedFromRectangles(locationChecker: Consumer<GeoRectangle>, vararg rectangles: GeoRectangle) {
        locationChecker(calculateLocationFromLimitArrays(*rectangles))
        locationChecker(calculateLocationFromBoundingBoxArrays(*rectangles))
    }

    private fun createCoordinateHelper(ranges: List<ClosedRange<Double>>): GeoBoundingBoxCalculator.CoordinateHelper {
        return object : GeoBoundingBoxCalculator.CoordinateHelper {
            override fun minCoord(index: Int): Double {
                return ranges[index].lowerEndpoint()
            }

            override fun maxCoord(index: Int): Double {
                return ranges[index].upperEndpoint()
            }

            override fun size(): Int {
                return ranges.size
            }
        }
    }
}
