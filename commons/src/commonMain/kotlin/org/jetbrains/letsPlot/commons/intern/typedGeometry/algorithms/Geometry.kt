/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.typedGeometry.algorithms

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.interval.IntSpan
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
        if (!rings.last().isClosed(eq)) {
            rings[rings.lastIndex] = makeClosed(rings.last())
        }
    }

    //require(rings.sumOf { it.size } == points.size) { "Split rings error: ${rings.sumOf { it.size }} != ${points.size}" }
    return rings
}

private fun <T> makeClosed(path: List<T>) = path.toMutableList() + path.first()

fun <T> List<T>.isClosed(eq: (T, T) -> Boolean = { p1, p2 -> p1 == p2 }) = eq(first(), last())

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

fun <T> checkRingInvariants(ring: List<T>, eq: (T, T) -> Boolean) {
    require(ring.size >= 3) { "Ring should contain at least 3 points" }
    require(ring.isClosed(eq)) { "Ring should be closed" }
    require(isRingNormalized(ring, eq) ) { "PolygonData ring should be normalized" }
}

private fun <T> isRingNormalized(ring: List<T>, eq: (T, T) -> Boolean): Boolean {
    if (ring.size < 3) {
        return true
    }

    if (!ring.isClosed(eq)) {
        return false
    }

    val first = ring.first()
    if (eq(first, ring[1])) {
        return false
    }

    if (ring.takeLast(2).all { eq(it, first) }) {
        return false
    }

    //If ring has a not closed self-intersection in start point, then it's not normalized
    if (ring.count { eq(it, first) }.mod(2) != 0) {
        return false
    }
    if (ring.windowed(3).any { (a, b, c) -> eq(b, first) && !eq(a, first) && !eq(c, first) }) {
        return false
    }

    return true
}

// Normalize the ring by adding a duplicate of the first element if a self-intersection is found in the ring.
fun <T> normalizeRing(ring: List<T>, eq: (T, T) -> Boolean): List<T> {
    if (isRingNormalized(ring, eq)) return ring

    // Close the ring if it's not closed
    val closedRing = if (!ring.isClosed(eq)) makeClosed(ring) else ring

    // Trim the ring to remove the same points from the beginning and the end of the ring.
    val firstElement = closedRing.first()
    val lastElement = closedRing.last()

    val inner = closedRing.subList(1, closedRing.lastIndex)

    val startSkipCount = inner.indexOfFirst { !eq(it, firstElement) }
    val endSkipCount = inner.asReversed().indexOfFirst { !eq(it, firstElement) }

    // All items are the same - trim to two items
    if (startSkipCount == -1 || endSkipCount == -1) {
        return listOf(firstElement, lastElement)
    }

    val trimmedRing = closedRing.subList(startSkipCount, closedRing.size - endSkipCount)

    // Normalize the ring by adding a duplicate of the first element if a self-intersection is found in the ring.
    val normalizedRing = mutableListOf<T>()
    normalizedRing.add(trimmedRing.first())
    var isRingOpened = true
    trimmedRing.subList(1, trimmedRing.lastIndex).forEach {
        if (!eq(it, firstElement)) {
            if (!isRingOpened) {
                normalizedRing.add(firstElement)
                isRingOpened = true
            }
        } else {
            isRingOpened = !isRingOpened
        }
        normalizedRing.add(it)
    }
    normalizedRing.add(trimmedRing.last())

    return normalizedRing
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
