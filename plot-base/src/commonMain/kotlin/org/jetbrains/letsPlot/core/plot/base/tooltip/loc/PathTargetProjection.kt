/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.tooltip.loc

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.math.isOnSegment
import org.jetbrains.letsPlot.commons.intern.math.projection
import org.jetbrains.letsPlot.commons.intern.util.ClosestPointChecker
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator.LookupSpace
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator.LookupStrategy

internal class PathTargetProjection(
    val path: List<PathPoint>,
    val lookupSpace: LookupSpace
) : TargetProjection {
    val points = when (lookupSpace) {
        LookupSpace.X -> path.sortedBy { it.x }
        LookupSpace.Y -> path.sortedBy { it.y }
        else -> path
    }

    internal class PathPoint(
        val originalCoord: DoubleVector,
        val index: Int
    ) {
        val x = originalCoord.x
        val y = originalCoord.y
        val xy = originalCoord
    }

    fun check(
        cursorCoord: DoubleVector,
        lookupStrategy: LookupStrategy,
        closestPointChecker: ClosestPointChecker
    ): Pair<PathPoint, DoubleVector?>? {
        if (points.isEmpty()) {
            return null
        }

        return when (lookupSpace) {
            LookupSpace.NONE -> null
            LookupSpace.X -> when (lookupStrategy) {
                LookupStrategy.NONE -> null
                LookupStrategy.NEAREST -> searchNearest(cursorCoord.x, points) { it.x } to null
                LookupStrategy.HOVER ->
                    if (cursorCoord.x < points.first().x || cursorCoord.x > points.last().x) {
                        null
                    } else {
                        val nearest = searchNearest(cursorCoord.x, points) { it.x }
                        closestPointChecker.check(nearest.originalCoord, 0.0)
                        nearest to null
                    }
            }

            LookupSpace.Y -> when (lookupStrategy) {
                LookupStrategy.NONE -> null
                LookupStrategy.NEAREST -> searchNearest(cursorCoord.y, points) { it.y } to null
                LookupStrategy.HOVER ->
                    if (cursorCoord.y < points.first().y || cursorCoord.y > points.last().y) {
                        null
                    } else {
                        val nearest = searchNearest(cursorCoord.y, points) { it.y }
                        closestPointChecker.check(nearest.originalCoord)
                        nearest to null
                    }
            }

            LookupSpace.XY -> when (lookupStrategy) {
                LookupStrategy.NONE -> null
                LookupStrategy.HOVER -> {
                    var candidate: Pair<PathPoint, DoubleVector>? = null

                    points.asSequence().windowed(2).forEach {
                        val p1 = it[0].xy
                        val p2 = it[1].xy

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
                    for (pathPoint in points) {
                        if (closestPointChecker.check(pathPoint.xy)) {
                            candidate = pathPoint
                        }
                    }
                    candidate?.let { it to null }
                }
            }
        }
    }

    companion object {
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
