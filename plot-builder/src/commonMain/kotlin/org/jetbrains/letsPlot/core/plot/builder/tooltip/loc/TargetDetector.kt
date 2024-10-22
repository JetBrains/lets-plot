/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.tooltip.loc

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.math.isOnSegment
import org.jetbrains.letsPlot.commons.intern.math.projection
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator.LookupSpace
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator.LookupStrategy
import org.jetbrains.letsPlot.core.plot.builder.tooltip.MathUtil
import org.jetbrains.letsPlot.core.plot.builder.tooltip.MathUtil.ClosestPointChecker
import org.jetbrains.letsPlot.core.plot.builder.tooltip.MathUtil.ClosestPointChecker.COMPARISON_RESULT
import org.jetbrains.letsPlot.core.plot.builder.tooltip.loc.PathTargetProjection.PathPoint

internal class TargetDetector(
    private val locatorLookupSpace: LookupSpace,
    private val locatorLookupStrategy: LookupStrategy
) {
    fun checkPath(
        cursorCoord: DoubleVector,
        pathProjection: PathTargetProjection,
        closestPointChecker: ClosestPointChecker
    ): Pair<PathPoint, DoubleVector?>? {
        if (pathProjection.points.isEmpty()) {
            return null
        }

        val lookupResult: Pair<PathPoint, DoubleVector?>? = when (locatorLookupSpace) {
            LookupSpace.NONE -> null
            LookupSpace.X -> when (locatorLookupStrategy) {
                LookupStrategy.NONE -> null
                LookupStrategy.NEAREST -> searchNearest(cursorCoord.x, pathProjection.points) { it.projection().x() } to null
                LookupStrategy.HOVER ->
                    if (cursorCoord.x < pathProjection.points.first().projection().x() ||
                        cursorCoord.x > pathProjection.points.last().projection().x()
                    ) {
                        null
                    } else {
                        searchNearest(cursorCoord.x, pathProjection.points) { it.projection().x() } to null
                    }
            }

            LookupSpace.Y -> when (locatorLookupStrategy) {
                LookupStrategy.NONE -> null
                LookupStrategy.NEAREST -> searchNearest(cursorCoord.y, pathProjection.points) { it.projection().y() } to null
                LookupStrategy.HOVER ->
                    if (cursorCoord.y < pathProjection.points.first().projection().y() ||
                        cursorCoord.y > pathProjection.points.last().projection().y()
                    ) {
                        null
                    } else {
                        searchNearest(cursorCoord.y, pathProjection.points) { it.projection().y() } to null
                    }
            }

            LookupSpace.XY -> when (locatorLookupStrategy) {
                LookupStrategy.NONE -> null
                LookupStrategy.HOVER -> {
                    var candidate: Pair<PathPoint, DoubleVector>? = null

                    pathProjection.points.asSequence().windowed(2).forEach() {
                        val p1 = it[0].projection().xy()
                        val p2 = it[1].projection().xy()

                        if (isOnSegment(cursorCoord, p1, p2)) {
                            val targetPointCoord = projection(cursorCoord, p1, p2)
                            if (closestPointChecker.check(targetPointCoord)) {
                                candidate = it[0] to targetPointCoord
                            }
                        } else if (closestPointChecker.check(p1)) {
                            candidate = it[0] to p1
                        }
                    }

                    candidate
                }

                LookupStrategy.NEAREST -> {
                    var candidate: PathPoint? = null
                    for (pathPoint in pathProjection.points) {
                        val targetPointCoord = pathPoint.projection().xy()
                        if (closestPointChecker.check(targetPointCoord)) {
                            candidate = pathPoint
                        }
                    }
                    candidate?.let { it to null }
                }
            }
        }

        return lookupResult
    }

    fun checkPoint(
        cursorCoord: DoubleVector,
        pointProjection: PointTargetProjection,
        closestPointChecker: ClosestPointChecker
    ): Boolean {
        return when (locatorLookupSpace) {
            LookupSpace.NONE -> false
            LookupSpace.X -> when (locatorLookupStrategy) {
                LookupStrategy.NONE -> false
                LookupStrategy.HOVER -> MathUtil.areEqual(pointProjection.x(), cursorCoord.x, pointProjection.radius + POINT_AREA_EPSILON)
                LookupStrategy.NEAREST -> closestPointChecker.check(DoubleVector(pointProjection.x(), 0.0))
            }

            LookupSpace.Y -> when (locatorLookupStrategy) {
                LookupStrategy.NONE -> false
                LookupStrategy.HOVER -> MathUtil.areEqual(pointProjection.y(), cursorCoord.y, pointProjection.radius + POINT_AREA_EPSILON)
                LookupStrategy.NEAREST -> closestPointChecker.check(DoubleVector(0.0, pointProjection.y()))
            }

            LookupSpace.XY -> when (locatorLookupStrategy) {
                LookupStrategy.NONE -> false
                LookupStrategy.HOVER -> MathUtil.areEqual(pointProjection.xy(), cursorCoord, pointProjection.radius + POINT_AREA_EPSILON)
                LookupStrategy.NEAREST -> closestPointChecker.check(pointProjection.xy())
            }
        }
    }

    fun checkRect(
        cursorCoord: DoubleVector,
        rectProjection: RectTargetProjection,
        closestPointChecker: ClosestPointChecker
    ): Boolean {
        return when (locatorLookupSpace) {
            LookupSpace.NONE -> false
            LookupSpace.X -> rangeBasedLookup(cursorCoord, closestPointChecker, rectProjection.x(), byX = true)
            LookupSpace.Y -> rangeBasedLookup(cursorCoord, closestPointChecker, rectProjection.y(), byX = false)
            LookupSpace.XY -> {
                val rect = rectProjection.xy()
                when (locatorLookupStrategy) {
                    LookupStrategy.NONE -> false
                    LookupStrategy.HOVER -> cursorCoord in rect
                    LookupStrategy.NEAREST -> if (cursorCoord in rect) {
                        closestPointChecker.check(cursorCoord)
                    } else {
                        var x = if (cursorCoord.x < rect.left) rect.left else rect.right
                        var y = if (cursorCoord.y < rect.top) rect.top else rect.bottom

                        x = if (rect.xRange().contains(cursorCoord.x)) cursorCoord.x else x
                        y = if (rect.yRange().contains(cursorCoord.y)) cursorCoord.y else y

                        closestPointChecker.check(DoubleVector(x, y))
                    }
                }
            }
        }
    }

    fun checkPolygon(
        cursorCoord: DoubleVector,
        polygonProjection: PolygonTargetProjection,
        closestPointChecker: ClosestPointChecker
    ): Boolean {
        return when (locatorLookupSpace) {
            LookupSpace.NONE -> false
            LookupSpace.X -> rangeBasedLookup(cursorCoord, closestPointChecker, polygonProjection.x(), byX = true)
            LookupSpace.Y -> rangeBasedLookup(cursorCoord, closestPointChecker, polygonProjection.y(), byX = false)
            LookupSpace.XY -> when (locatorLookupStrategy) {
                LookupStrategy.NONE -> false
                LookupStrategy.NEAREST, // Doesn't support nearest strategy. Target can be found only by hovering a cursor above the polygon.
                LookupStrategy.HOVER -> polygonProjection.xy().count { cursorCoord in it } % 2 != 0
            }
        }
    }

    private fun rangeBasedLookup(
        cursor: DoubleVector,
        closestPointChecker: ClosestPointChecker,
        range: DoubleSpan,
        byX: Boolean
    ): Boolean {
        return when (locatorLookupStrategy) {
            LookupStrategy.NONE -> false
            LookupStrategy.HOVER -> (if (byX) cursor.x else cursor.y) in range
            LookupStrategy.NEAREST -> {
                val cursorCoord = if (byX) cursor.x else cursor.y
                //Too far
                if (range.contains(cursorCoord - RECT_X_NEAREST_EPSILON) || range.contains(cursorCoord + RECT_X_NEAREST_EPSILON)) {
                    val coord = if (byX) {
                        DoubleVector(range.lowerEnd + range.length / 2, cursor.y)
                    } else {
                        DoubleVector(cursor.x, range.lowerEnd + range.length / 2)
                    }
                    closestPointChecker.compare(coord) != COMPARISON_RESULT.NEW_FARTHER
                } else {
                    false
                }
            }
        }
    }

    companion object {
        private const val POINT_AREA_EPSILON = 5.1
        private const val RECT_X_NEAREST_EPSILON = 2.0

        private fun <T> searchNearest(value: Double, items: List<T>, mapper: (T) -> Double): T {
            if (value < mapper(items.first())) {
                return items.first()
            }
            if (value > mapper(items.last())) {
                return items.last()
            }

            var lo = 0
            var hi = items.lastIndex

            while (lo <= hi) {
                val mid = (hi + lo) / 2
                val midValue = mapper(items[mid])

                when {
                    value < midValue -> hi = mid - 1
                    value > midValue -> lo = mid + 1
                    else -> return items[mid]
                }
            }

            return if (mapper(items[lo]) - value < value - mapper(items[hi])) {
                items[lo]
            } else {
                items[hi]
            }
        }
    }
}
