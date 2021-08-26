/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.spatial

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.spatial.LongitudeSegment.Companion.splitSegment
import jetbrains.datalore.base.typedGeometry.*
import kotlin.math.max
import kotlin.math.min

// Segment have direction, i.e. `start` can be less than `end` for the case
// of the antimeridian intersection.
// That's why we can't use ClosedRange class with lower <= upper invariant
typealias Segment = Pair<Double, Double>

val Segment.start get() = first
val Segment.end get() = second

class GeoBoundingBoxCalculator<TypeT>(
    private val myMapRect: Rect<TypeT>,
    private val myLoopX: Boolean,
    private val myLoopY: Boolean
) {
    fun calculateBoundingBox(xSegments: Sequence<Segment>, ySegments: Sequence<Segment>): Rect<TypeT> {
        val xRange = calculateBoundingRange(xSegments, myMapRect.xRange(), myLoopX)
        val yRange = calculateBoundingRange(ySegments, myMapRect.yRange(), myLoopY)
        return Rect(xRange.lowerEnd, yRange.lowerEnd, xRange.length(), yRange.length())
    }

    private fun calculateBoundingRange(
        segments: Sequence<Segment>,
        mapRange: ClosedRange<Double>,
        loop: Boolean
    ): ClosedRange<Double> {
        return if (loop) {
            calculateLoopLimitRange(segments, mapRange)
        } else {
            ClosedRange(
                segments.map(Segment::start).minOrNull()!!,
                segments.map(Segment::end).maxOrNull()!!
            )
        }
    }

    companion object {
        internal fun calculateLoopLimitRange(segments: Sequence<Segment>, mapRange: ClosedRange<Double>): ClosedRange<Double> {
            return segments
                .map { splitSegment(it.start, it.end, mapRange.lowerEnd, mapRange.upperEnd) }
                .flatten()
                .run { findMaxGapBetweenRanges(this, mapRange.length()) }
                .run { invertRange(this, mapRange.length()) }
                .run { normalizeCenter(this, mapRange) }
        }

        private fun normalizeCenter(range: ClosedRange<Double>, mapRange: ClosedRange<Double>): ClosedRange<Double> {
            return if (mapRange.contains((range.upperEnd + range.lowerEnd) / 2)) {
                range
            } else {
                ClosedRange(
                    range.lowerEnd - mapRange.length(),
                    range.upperEnd - mapRange.length()
                )
            }
        }

        private fun findMaxGapBetweenRanges(ranges: Sequence<ClosedRange<Double>>, width: Double): ClosedRange<Double> {
            val sortedRanges = ranges.sortedBy(ClosedRange<Double>::lowerEnd)
            var prevUpper = sortedRanges.maxByOrNull(ClosedRange<Double>::upperEnd)!!.upperEnd
            var nextLower = sortedRanges.first().lowerEnd
            val gapRight = max(width + nextLower, prevUpper)
            var maxGapRange = ClosedRange(prevUpper, gapRight)

            val it = sortedRanges.iterator()
            prevUpper = it.next().upperEnd

            while (it.hasNext()) {
                val range = it.next()

                nextLower = range.lowerEnd
                if (nextLower > prevUpper && nextLower - prevUpper > maxGapRange.length()) {
                    maxGapRange = ClosedRange(prevUpper, nextLower)
                }
                prevUpper = max(prevUpper, range.upperEnd)
            }
            return maxGapRange
        }

        private fun invertRange(range: ClosedRange<Double>, width: Double): ClosedRange<Double> {
            // Fix for rounding error for invertRange introduced by math with width.
            fun safeRange(first: Double, second: Double) = ClosedRange(min(first, second), max(first, second))

            return when {
                range.length() > width -> ClosedRange(range.lowerEnd, range.lowerEnd)
                range.upperEnd > width -> safeRange(range.upperEnd - width, range.lowerEnd)
                else -> safeRange(range.upperEnd, width + range.lowerEnd)
            }
        }

        private fun ClosedRange<Double>.length(): Double = upperEnd - lowerEnd
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
    require(xyCoords.size % 2 == 0) { "Longitude-Latitude list is not even-numbered." }
    val x: (Int) -> Double = { index -> xyCoords[2 * index] }
    val y: (Int) -> Double = { index -> xyCoords[2 * index + 1] }

    val i = xyCoords.size / 2
    return calculateBoundingBox(
        makeSegments(x, x, i),
        makeSegments(y, y, i)
    )
}

fun <T> GeoBoundingBoxCalculator<T>.union(rectangles: List<Rect<T>>): Rect<T> {
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

