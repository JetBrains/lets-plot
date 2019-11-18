/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.spatial

import jetbrains.datalore.base.gcommon.base.Preconditions.checkArgument
import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.projectionGeometry.*
import jetbrains.datalore.base.spatial.LongitudeRange.Companion.splitRange
import kotlin.math.max
import kotlin.math.min

class GeoBoundingBoxCalculator<TypeT>(
    private val myMapRect: Rect<TypeT>,
    private val myLoopX: Boolean,
    private val myLoopY: Boolean) {

    fun calculateBoundingBox(xyCoords: List<Double>): Rect<TypeT> {
        checkArgument(xyCoords.size % 2 == 0, "Longitude-Latitude list is not even-numbered.")

        return calculateBoundingBox(evenItemGetter(xyCoords), oddItemGetter(xyCoords), xyCoords.size / 2)
    }

    fun calculateBoundingBox(xCoords: List<Double>, yCoords: List<Double>): Rect<TypeT> {
        checkArgument(xCoords.size == yCoords.size, "Longitude list count is not equal Latitude list count.")

        return calculateBoundingBox(itemGetter(xCoords), itemGetter(yCoords), yCoords.size)
    }

    fun calculateBoundingBox(
            minXCoords: List<Double>,
            minYCoords: List<Double>,
            maxXCoords: List<Double>,
            maxYCoords: List<Double>
    ): Rect<TypeT> {
        val count = minXCoords.size
        checkArgument(minYCoords.size == count && maxXCoords.size == count && maxYCoords.size == count,
                "Counts of 'minLongitudes', 'minLatitudes', 'maxLongitudes', 'maxLatitudes' lists are not equal.")

        return calculateBoundingBox(
                itemGetter(minXCoords),
                itemGetter(maxXCoords),
                itemGetter(minYCoords),
                itemGetter(maxYCoords),
                count
        )
    }

    fun calculateBoundingBoxFromGeoRectangles(rectangles: List<GeoRectangle>): Rect<TypeT> {
        val geoRectGetter = itemGetter(rectangles)
        return calculateBoundingBox(
                { x: Int -> MIN_LONGITUDE_GETTER(geoRectGetter(x)) },
                { x: Int -> MAX_LONGITUDE_GETTER(geoRectGetter(x)) },
                { x: Int -> MIN_LATITUDE_GETTER(geoRectGetter(x)) },
                { x: Int -> MAX_LATITUDE_GETTER(geoRectGetter(x)) },
                rectangles.size
        )
    }

    fun calculateBoundingBoxFromRectangles(rectangles: List<Rect<TypeT>>): Rect<TypeT> {
        val rectGetter = itemGetter(rectangles)
        return calculateBoundingBox(
                { x: Int -> LEFT_RECT_GETTER(rectGetter(x)) },
                { x: Int -> RIGHT_RECT_GETTER(rectGetter(x)) },
                { x: Int -> TOP_RECT_GETTER(rectGetter(x)) },
                { x: Int -> BOTTOM_RECT_GETTER(rectGetter(x)) },
                rectangles.size
        )
    }

    private fun calculateBoundingBox(x: (Int) -> Double, y: (Int) -> Double, size: Int): Rect<TypeT> {
        return calculateBoundingBox(x, x, y, y, size)
    }

    private fun calculateBoundingBox(
            minX: (Int) -> Double, maxX: (Int) -> Double,
            minY: (Int) -> Double, maxY: (Int) -> Double,
            size: Int
    ): Rect<TypeT> {
        val xRange = calculateBoundingRange(CoordinateHelperImpl(minX, maxX, size), myMapRect.xRange(), myLoopX)
        val yRange = calculateBoundingRange(CoordinateHelperImpl(minY, maxY, size), myMapRect.yRange(), myLoopY)
        return Rect(
            xRange.lowerEndpoint(),
            yRange.lowerEndpoint(),
            length(xRange),
            length(yRange)
        )
    }

    private fun calculateBoundingRange(helper: CoordinateHelper, mapRange: ClosedRange<Double>, loop: Boolean): ClosedRange<Double> {
        return if (loop) calculateLoopLimitRange(helper, mapRange) else calculateLimitRange(helper)
    }

    private fun calculateLimitRange(helper: CoordinateHelper): ClosedRange<Double> {
        var min = helper.minCoord(0)
        var max = helper.maxCoord(0)
        for (i in 1 until helper.size()) {
            min = min(min, helper.minCoord(i))
            max = max(max, helper.maxCoord(i))
        }
        return ClosedRange.closed(min, max)
    }

    internal interface CoordinateHelper {
        fun minCoord(index: Int): Double
        fun maxCoord(index: Int): Double
        fun size(): Int
    }

    private class CoordinateHelperImpl internal constructor(
        private val myMinCoord: (Int) -> Double,
        private val myMaxCoord: (Int) -> Double,
        private val mySize: Int
    ) : CoordinateHelper {

        override fun minCoord(index: Int) = myMinCoord(index)
        override fun maxCoord(index: Int) = myMaxCoord(index)
        override fun size() = mySize
    }

    companion object {
        internal val MIN_LONGITUDE_GETTER = { rectangle: GeoRectangle -> rectangle.minLongitude() }

        internal val MAX_LONGITUDE_GETTER = { rectangle: GeoRectangle -> rectangle.maxLongitude() }

        internal val MIN_LATITUDE_GETTER = { rectangle: GeoRectangle -> rectangle.minLatitude() }

        internal val MAX_LATITUDE_GETTER = { rectangle: GeoRectangle -> rectangle.maxLatitude() }

        internal val LEFT_GETTER = { rectangle: DoubleRectangle -> rectangle.left }

        internal val RIGHT_GETTER = { rectangle: DoubleRectangle -> rectangle.right }

        internal val TOP_GETTER = { rectangle: DoubleRectangle -> rectangle.top }

        internal val BOTTOM_GETTER = { rectangle: DoubleRectangle -> rectangle.bottom }

        internal val LEFT_RECT_GETTER = { rectangle: Rect<*> -> rectangle.left }

        internal val RIGHT_RECT_GETTER = { rectangle: Rect<*> -> rectangle.right }

        internal val TOP_RECT_GETTER = { rectangle: Rect<*> -> rectangle.top }

        internal val BOTTOM_RECT_GETTER = { rectangle: Rect<*> -> rectangle.bottom }

        private val LOWER_ENDPOINT_GETTER = { range: ClosedRange<Double> -> range.lowerEndpoint() }

        private val UPPER_ENDPOINT_GETTER = { range: ClosedRange<Double> -> range.upperEndpoint() }

        private fun rangeComparator(valueGetter: (ClosedRange<Double>) -> Double): Comparator<ClosedRange<Double>> {
            return object : Comparator<ClosedRange<Double>> {
                override fun compare(a: ClosedRange<Double>, b: ClosedRange<Double>): Int {
                    val v1 = valueGetter(a)
                    val v2 = valueGetter(b)
                    return if (v1 == v2) 0 else if (v1 < v2) -1 else 1
                }
            }
        }

        private fun <T> itemGetter(values: List<T>): (Int) -> T {
            return { index -> values[index] }
        }

        private fun evenItemGetter(values: List<Double>): (Int) -> Double {
            return { index -> values[2 * index] }
        }

        private fun oddItemGetter(values: List<Double>): (Int) -> Double {
            return { index -> values[2 * index + 1] }
        }

        internal fun calculateLoopLimitRange(helper: CoordinateHelper, mapRange: ClosedRange<Double>): ClosedRange<Double> {
            if (helper.size() == 0) {
                throw RuntimeException("No coordinates for bounding box calculation.")
            }

            val coordRanges = combineCoordRanges(helper, mapRange.lowerEndpoint(), mapRange.upperEndpoint())
            val maxGapRange = findMaxGapBetweenRanges(coordRanges, length(mapRange))
            return normalizeCenter(invertRange(maxGapRange, length(mapRange)), mapRange)
        }

        private fun normalizeCenter(range: ClosedRange<Double>, mapRange: ClosedRange<Double>): ClosedRange<Double> {
            return if (mapRange.contains((range.upperEndpoint() + range.lowerEndpoint()) / 2)) {
                range
            } else {
                ClosedRange.closed(
                        range.lowerEndpoint() - length(mapRange),
                        range.upperEndpoint() - length(mapRange)
                )
            }
        }

        private fun combineCoordRanges(helper: CoordinateHelper, min: Double, max: Double): List<ClosedRange<Double>> {
            val coordRanges = ArrayList<ClosedRange<Double>>()

            for (i in 0 until helper.size()) {
                splitRange(helper.minCoord(i), helper.maxCoord(i), min, max, coordRanges)
            }
            return coordRanges
        }

        private fun findMaxGapBetweenRanges(ranges: List<ClosedRange<Double>>, width: Double): ClosedRange<Double> {
            val sortedRanges = ranges.sortedBy(LOWER_ENDPOINT_GETTER)
            var prevUpper = sortedRanges.maxBy(UPPER_ENDPOINT_GETTER)!!.upperEndpoint()
            var nextLower = sortedRanges.first().lowerEndpoint()
            val gapRight = max(width + nextLower, prevUpper)
            var maxGapRange = ClosedRange.closed(prevUpper, gapRight)

            val it = sortedRanges.iterator()
            prevUpper = it.next().upperEndpoint()

            while (it.hasNext()) {
                val range = it.next()

                nextLower = range.lowerEndpoint()
                if (nextLower > prevUpper && nextLower - prevUpper > length(maxGapRange)) {
                    maxGapRange = ClosedRange.closed(prevUpper, nextLower)
                }
                prevUpper = max(prevUpper, range.upperEndpoint())
            }
            return maxGapRange
        }

        private fun invertRange(range: ClosedRange<Double>, width: Double): ClosedRange<Double> {
            return when {
                length(range) > width ->
                    ClosedRange.closed(range.lowerEndpoint(), range.lowerEndpoint())
                range.upperEndpoint() > width ->
                    ClosedRange.closed(range.upperEndpoint() - width, range.lowerEndpoint())
                else ->
                    ClosedRange.closed(range.upperEndpoint(), width + range.lowerEndpoint())
            }
        }

        private fun length(range: ClosedRange<Double>): Double {
            return range.upperEndpoint() - range.lowerEndpoint()
        }
    }
}
