/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.spatial

import jetbrains.datalore.base.gcommon.base.Preconditions
import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.spatial.LongitudeSegment.Companion.splitSegment
import jetbrains.datalore.base.typedGeometry.*
import kotlin.math.max
import kotlin.math.min

// Segment have direction, i.e. `start` can be less than `end` for the case
// of the antimeridian intersection.
// Thats why we can't use ClosedRange class with lower <= upper invariant
typealias Segment = Pair<Double, Double>
val Segment.start get() = first
val Segment.end get() = second

class GeoBoundingBoxCalculator<TypeT>(
    private val myMapRect: Rect<TypeT>,
    private val myLoopX: Boolean,
    private val myLoopY: Boolean
) {


    fun calculateBoundingBox(
        xSegments: Sequence<Segment>,
        ySegments: Sequence<Segment>
    ): Rect<TypeT> {
        val xRange = calculateBoundingRange(
            xSegments,
            myMapRect.xRange(),
            myLoopX
        )
        val yRange = calculateBoundingRange(
            ySegments,
            myMapRect.yRange(),
            myLoopY
        )
        return Rect(
            xRange.lowerEndpoint(),
            yRange.lowerEndpoint(),
            xRange.length(),
            yRange.length()
        )
    }

    private fun calculateBoundingRange(
        segments: Sequence<Segment>,
        mapRange: ClosedRange<Double>,
        loop: Boolean
    ): ClosedRange<Double> {
        return if (loop) {
            calculateLoopLimitRange(segments, mapRange)
        } else {
            ClosedRange.closed(
                segments.map(Segment::start).min()!!,
                segments.map(Segment::end).max()!!
            )
        }
    }

    companion object {
        internal fun calculateLoopLimitRange(segments: Sequence<Segment>, mapRange: ClosedRange<Double>): ClosedRange<Double> {
            return segments
                .map {
                    splitSegment(
                        it.start, it.end,
                        mapRange.lowerEndpoint(),
                        mapRange.upperEndpoint()
                    )
                }
                .flatten()
                .run { findMaxGapBetweenRanges(this, mapRange.length()) }
                .run { invertRange(this, mapRange.length()) }
                .run { normalizeCenter(this, mapRange) }
        }

        private fun normalizeCenter(range: ClosedRange<Double>, mapRange: ClosedRange<Double>): ClosedRange<Double> {
            return if (mapRange.contains((range.upperEndpoint() + range.lowerEndpoint()) / 2)) {
                range
            } else {
                ClosedRange.closed(
                    range.lowerEndpoint() - mapRange.length(),
                    range.upperEndpoint() - mapRange.length()
                )
            }
        }

        private fun findMaxGapBetweenRanges(ranges: Sequence<ClosedRange<Double>>, width: Double): ClosedRange<Double> {
            val sortedRanges = ranges.sortedBy(ClosedRange<Double>::lowerEndpoint)
            var prevUpper = sortedRanges.maxBy(ClosedRange<Double>::upperEndpoint)!!.upperEndpoint()
            var nextLower = sortedRanges.first().lowerEndpoint()
            val gapRight = max(width + nextLower, prevUpper)
            var maxGapRange = ClosedRange.closed(prevUpper, gapRight)

            val it = sortedRanges.iterator()
            prevUpper = it.next().upperEndpoint()

            while (it.hasNext()) {
                val range = it.next()

                nextLower = range.lowerEndpoint()
                if (nextLower > prevUpper && nextLower - prevUpper > maxGapRange.length()) {
                    maxGapRange = ClosedRange.closed(prevUpper, nextLower)
                }
                prevUpper = max(prevUpper, range.upperEndpoint())
            }
            return maxGapRange
        }

        private fun invertRange(range: ClosedRange<Double>, width: Double): ClosedRange<Double> {
            // Fix for rounding error for invertRange introduced by math with width.
            fun safeRange(first: Double, second: Double) = ClosedRange.closed(min(first, second), max(first, second))

            return when {
                range.length() > width ->
                    ClosedRange.closed(range.lowerEndpoint(), range.lowerEndpoint())
                range.upperEndpoint() > width ->
                    safeRange(range.upperEndpoint() - width, range.lowerEndpoint())
                else ->
                    safeRange(range.upperEndpoint(), width + range.lowerEndpoint())
            }
        }

        private fun ClosedRange<Double>.length(): Double {
            return upperEndpoint() - lowerEndpoint()
        }
    }
}

fun makeSegments(start: (Int) -> Double, end: (Int) -> Double, size: Int): Sequence<Segment> {
    return (0 until size).asSequence().map { Segment(start(it), end(it)) }
}

fun <T> GeoBoundingBoxCalculator<T>.geoRectsBBox(rectangles: List<GeoRectangle>): Rect<T> {
    return calculateBoundingBox(
        makeSegments(
            { rectangles[it].startLongitude() },
            { rectangles[it].endLongitude() },
            rectangles.size
        ),
        makeSegments(
            { rectangles[it].minLatitude() },
            { rectangles[it].maxLatitude() },
            rectangles.size
        )
    )
}

fun <T> GeoBoundingBoxCalculator<T>.pointsBBox(xyCoords: List<Double>): Rect<T> {
    Preconditions.checkArgument(xyCoords.size % 2 == 0, "Longitude-Latitude list is not even-numbered.")
    val x: (Int) -> Double = { index -> xyCoords[2 * index] }
    val y: (Int) -> Double = { index -> xyCoords[2 * index + 1] }

    val i = xyCoords.size / 2
    return calculateBoundingBox(
        makeSegments(x, x, i),
        makeSegments(y, y, i)
    )
}

fun <T> GeoBoundingBoxCalculator<T>.rectsBBox(rectangles: List<Rect<T>>): Rect<T> {
    return calculateBoundingBox(
        makeSegments(
            { rectangles[it].left },
            { rectangles[it].right },
            rectangles.size
        ),
        makeSegments(
            { rectangles[it].top },
            { rectangles[it].bottom },
            rectangles.size
        )
    )
}

