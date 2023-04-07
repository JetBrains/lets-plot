/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact.loc

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.plot.base.interact.GeomTargetLocator.LookupSpace
import jetbrains.datalore.plot.base.interact.GeomTargetLocator.LookupStrategy
import jetbrains.datalore.plot.builder.interact.MathUtil
import jetbrains.datalore.plot.builder.interact.MathUtil.ClosestPointChecker
import jetbrains.datalore.plot.builder.interact.MathUtil.ClosestPointChecker.COMPARISON_RESULT
import jetbrains.datalore.plot.builder.interact.loc.PathTargetProjection.PathPoint

internal class TargetDetector(
    private val locatorLookupSpace: LookupSpace,
    private val locatorLookupStrategy: LookupStrategy
) {
    fun checkPath(
        cursorCoord: DoubleVector,
        pathProjection: PathTargetProjection,
        closestPointChecker: ClosestPointChecker
    ): PathPoint? {
        if (pathProjection.points.isEmpty()) {
            return null
        }

        return when (locatorLookupSpace) {
            LookupSpace.NONE -> null
            LookupSpace.X, LookupSpace.Y -> {
                val cursor = if (locatorLookupSpace == LookupSpace.X) cursorCoord.x else cursorCoord.y
                when (locatorLookupStrategy) {
                    LookupStrategy.NONE -> null
                    LookupStrategy.NEAREST -> searchNearest(cursor, pathProjection.points) { it.projection().double() }
                    LookupStrategy.HOVER ->
                        if (cursor < pathProjection.points.first().projection().double() ||
                            cursor > pathProjection.points.last().projection().double()
                        ) {
                            null
                        } else {
                            searchNearest(cursor, pathProjection.points) { it.projection().double() }
                        }
                }
            }
            LookupSpace.XY -> when (locatorLookupStrategy) {
                LookupStrategy.NONE -> return null
                LookupStrategy.HOVER -> {
                    for (pathPoint in pathProjection.points) {
                        val targetPointCoord = pathPoint.projection().xy()
                        if (MathUtil.areEqual(targetPointCoord, cursorCoord, POINT_AREA_EPSILON)) {
                            return pathPoint
                        }
                    }
                    return null
                }
                LookupStrategy.NEAREST -> {
                    var nearestPoint: PathPoint? = null
                    for (pathPoint in pathProjection.points) {
                        val targetPointCoord = pathPoint.projection().xy()
                        if (closestPointChecker.check(targetPointCoord)) {
                            nearestPoint = pathPoint
                        }
                    }
                    return nearestPoint
                }
            }
        }
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
                LookupStrategy.HOVER -> MathUtil.areEqual(pointProjection.double(), cursorCoord.x, POINT_AREA_EPSILON)
                LookupStrategy.NEAREST -> closestPointChecker.check(DoubleVector(pointProjection.double(), 0.0))
            }
            LookupSpace.Y -> when (locatorLookupStrategy) {
                LookupStrategy.NONE -> false
                LookupStrategy.HOVER -> MathUtil.areEqual(pointProjection.double(), cursorCoord.y, POINT_AREA_EPSILON)
                LookupStrategy.NEAREST -> closestPointChecker.check(DoubleVector(0.0, pointProjection.double()))
            }
            LookupSpace.XY -> when (locatorLookupStrategy) {
                LookupStrategy.NONE -> false
                LookupStrategy.HOVER -> MathUtil.areEqual(pointProjection.xy(), cursorCoord, POINT_AREA_EPSILON)
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
            LookupSpace.X, LookupSpace.Y -> rangeBasedLookup(
                cursorCoord,
                closestPointChecker,
                rectProjection.range(),
                byX = locatorLookupSpace == LookupSpace.X
            )
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
            LookupSpace.X, LookupSpace.Y -> rangeBasedLookup(
                cursorCoord,
                closestPointChecker,
                polygonProjection.range(),
                byX = locatorLookupSpace == LookupSpace.X
            )
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
                }
                else {
                    false
                }
            }
        }
    }

    companion object {
        private const val POINT_AREA_EPSILON = 0.1
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
