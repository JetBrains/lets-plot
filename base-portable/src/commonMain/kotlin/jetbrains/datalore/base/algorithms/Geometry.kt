/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.algorithms

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.projectionGeometry.MultiPolygon
import jetbrains.datalore.base.projectionGeometry.Polygon
import jetbrains.datalore.base.projectionGeometry.Ring
import jetbrains.datalore.base.projectionGeometry.Vec
import kotlin.math.abs

fun <T> splitRings(points: List<T>): List<List<T>> {
    val rings = findRingIntervals(points).map { points.sublist(it) }.toMutableList()

    if (rings.isNotEmpty()) {
        if (!rings.last().isClosed()) {
            rings.set(rings.lastIndex, makeClosed(rings.last()))
        }
    }

    return rings
}

private fun <T> makeClosed(path: List<T>) = path.toMutableList() + path.first()

fun <T> List<T>.isClosed() = first() == last()

private fun <T> findRingIntervals(path: List<T>): List<ClosedRange<Int>> {
    val intervals = ArrayList<ClosedRange<Int>>()
    var startIndex = 0

    var i = 0
    val n = path.size
    while (i < n) {
        if (startIndex != i && path[startIndex] == path[i]) {
            intervals.add(ClosedRange.closed(startIndex, i + 1))
            startIndex = i + 1
        }
        i++
    }

    if (startIndex != path.size) {
        intervals.add(ClosedRange.closed(startIndex, path.size))
    }
    return intervals
}

private fun <T> List<T>.sublist(range: ClosedRange<Int>): List<T> {
    return this.subList(range.lowerEndpoint(), range.upperEndpoint())
}


fun calculateArea(ring: List<DoubleVector>): Double {
    return calculateArea(ring, DoubleVector::x, DoubleVector::y)
}

private fun <T> isClockwise(ring: List<Vec<T>>): Boolean {
    return isClockwise(ring, Vec<T>::x, Vec<T>::y)
}

fun <T> createMultiPolygon(points: List<Vec<T>>): MultiPolygon<T> {
    if (points.isEmpty()) {
        return MultiPolygon(emptyList())
    }

    val polygons = ArrayList<Polygon<T>>()
    var rings = ArrayList<Ring<T>>()

    for (ring in splitRings(points)) {
        if (rings.isNotEmpty() && isClockwise(ring)) {
            polygons.add(Polygon(rings))
            rings = ArrayList()
        }
        rings.add(Ring(ring))
    }

    if (rings.isNotEmpty()) {
        polygons.add(Polygon(rings))
    }

    return MultiPolygon(polygons)
}

fun <T> isClockwise(ring: List<T>, x: (T) -> Double, y: (T) -> Double): Boolean {
    check(ring.isNotEmpty()) { "Ring shouldn't be empty to calculate clockwise" }

    var sum = 0.0
    var prev = ring[ring.size - 1]
    for (point in ring) {
        sum += x(prev) * y(point) - x(point) * y(prev)
        prev = point
    }
    return sum < 0.0
}

fun <T> calculateArea(ring: List<T>, x: T.() -> Double, y: T.() -> Double): Double {
    var area = 0.0

    var j = ring.size - 1

    for (i in ring.indices) {
        val p1 = ring[i]
        val p2 = ring[j]

        area += (p2.x() + p1.x()) * (p2.y() - p1.y())
        j = i
    }

    return abs(area / 2)
}
