/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.commons.geometry

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.commons.geometry.PolylineSimplifier.RankingStrategy
import kotlin.math.abs


// Reference: https://bost.ocks.org/mike/simplify/
internal class VisvalingamWhyattSimplification : RankingStrategy {

    override fun computeWeights(points: List<DoubleVector>): List<Double> {
        if (points.size < 3) {
            return MutableList(points.size) { INITIAL_AREA }
        }

        val sortedTriangles = initTriangles(points)
        val weights = MutableList(points.size) { INITIAL_AREA }
        var lastRemovedVertexArea = 0.0
        while (!sortedTriangles.isEmpty()) {
            val triangle = sortedTriangles.poll()

            lastRemovedVertexArea = maxOf(triangle.area, lastRemovedVertexArea)

            weights[triangle.currentVertex] = lastRemovedVertexArea

            triangle.next?.let {
                it.takePrevFrom(triangle)
                sortedTriangles.reposition(it)
            }

            triangle.prev?.let {
                it.takeNextFrom(triangle)
                sortedTriangles.reposition(it)
            }
        }

        return weights
    }

    private fun initTriangles(points: List<DoubleVector>): SortedList<Triangle> {
        val triangles = (1..<points.lastIndex).map { i -> Triangle(i, points) }
        triangles.windowed(3).map { (prev, current, next) ->
            prev.next = current
            current.prev = prev
            current.next = next
            next.prev = current
        }

        return SortedList(compareBy(Triangle::area)).apply {
            triangles.forEach(::add)
        }
    }

    private class Triangle(
        val currentVertex: Int,
        private val points: List<DoubleVector>
    ) {
        var area: Double = 0.toDouble()
            private set
        private var prevVertex: Int = 0
        private var nextVertex: Int = 0
        var prev: Triangle? = null
        var next: Triangle? = null

        init {
            prevVertex = currentVertex - 1
            nextVertex = currentVertex + 1
            area = calculateArea()
        }

        fun takeNextFrom(triangle: Triangle) {
            next = triangle.next
            nextVertex = triangle.nextVertex
            area = calculateArea(min = triangle.area)
        }

        fun takePrevFrom(triangle: Triangle) {
            prev = triangle.prev
            prevVertex = triangle.prevVertex
            area = calculateArea(min = triangle.area)
        }

        private fun calculateArea(min: Double = 0.0): Double {
            val (x1, y1) = points[prevVertex]
            val (x2, y2) = points[currentVertex]
            val (x3, y3) = points[nextVertex]

            val area = abs((x1 * (y2 - y3) + x2 * (y3 - y1) + x3 * (y1 - y2)) / 2.0)
            return area.takeIf { it > min } ?: (min + Double.MIN_VALUE)
        }

        override fun toString(): String {
            return "Triangle{" +
                    "prevVertex=" + prevVertex +
                    ", currentVertex=" + currentVertex +
                    ", nextVertex=" + nextVertex +
                    ", area=" + area +
                    '}'
        }
    }

    class SortedList<T>(
        private val comparator: Comparator<T>
    ) {
        fun isEmpty(): Boolean {
            return elements.isEmpty()
        }

        internal fun peek(): T {
            return elements[0]
        }

        internal fun poll(): T {
            val el = peek()
            elements.remove(el)
            return el
        }

        private fun getIndex(el: T): Int {
            var index = elements.binarySearch(el, comparator)
            if (index < 0) {
                index = index.inv()
            }
            return index
        }

        fun add(el: T) {
            val index = getIndex(el)
            elements.add(index, el)
        }

        fun remove(el: T) {
            elements.remove(el)
        }

        fun reposition(el: T) {
            remove(el)
            add(el)
        }

        private val elements = mutableListOf<T>()
    }


    companion object {

        private const val INITIAL_AREA = Double.MAX_VALUE
    }
}
