/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.algorithms

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.interval.IntSpan
import kotlin.math.abs

fun <T> reduce(points: List<T>, dropDistance: Double, distance: (T, T) -> Double): List<T> {
    return points.foldIndexed(mutableListOf()) { i, acc, el ->
        if (i == 0) acc.add(el)
        else if (i == points.lastIndex) acc.add(el)
        else if (distance(acc.last(), el) >= dropDistance) acc.add(el)

        acc
    }
}

inline fun <reified T> splitRings(points: List<T>): List<List<T>> = splitRings(points) { o1, o2 -> o1 == o2 }

fun <T> splitRings(points: List<T>, eq: (T, T) -> Boolean): List<List<T>> {
    val rings = findRingIntervals(points, eq).map(points::sublist).toMutableList()

    if (rings.isNotEmpty()) {
        if (!rings.last().isClosed()) {
            rings[rings.lastIndex] = makeClosed(rings.last())
        }
    }

    return rings
}

private fun <T> makeClosed(path: List<T>) = path.toMutableList() + path.first()

fun <T> List<T>.isClosed() = first() == last()

private fun <T> findRingIntervals(path: List<T>, eq: (T, T) -> Boolean): List<IntSpan> {
    val intervals = ArrayList<IntSpan>()
    var startIndex = 0

    var i = 0
    val n = path.size
    while (i < n) {
        if (startIndex != i && eq(path[startIndex], path[i])) {
            intervals.add(IntSpan(startIndex, i + 1))
            startIndex = i + 1
        }
        i++
    }

    if (startIndex != path.size) {
        intervals.add(IntSpan(startIndex, path.size))
    }
    return intervals
}

private fun <T> List<T>.sublist(range: IntSpan): List<T> {
    return this.subList(range.lowerEnd, range.upperEnd)
}


fun calculateArea(ring: List<DoubleVector>): Double {
    return calculateArea(ring, DoubleVector::x, DoubleVector::y)
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
