/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.tooltip.loc

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleRectangles
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.math.polygonContainsCoordinate
import org.jetbrains.letsPlot.commons.intern.typedGeometry.algorithms.calculateArea
import org.jetbrains.letsPlot.commons.intern.typedGeometry.algorithms.splitRings
import org.jetbrains.letsPlot.commons.intern.util.ClosestPointChecker
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.commons.geometry.PolylineSimplifier
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator.LookupSpace
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator.LookupStrategy
import kotlin.math.max
import kotlin.math.min

internal class PolygonTargetProjection(
    val points: List<DoubleVector>,
    val lookupSpace: LookupSpace
) : TargetProjection {
    private val rings by lazy { splitRings(points) }
    val x by lazy { mapToAxis(DoubleVector::x, rings) }
    val y by lazy { mapToAxis(DoubleVector::y, rings) }
    val xy by lazy { mapToXY(rings) }

    fun check(
        cursorCoord: DoubleVector,
        lookupStrategy: LookupStrategy,
        closestPointChecker: ClosestPointChecker
    ): Boolean {
        return when (lookupSpace) {
            LookupSpace.NONE -> false
            LookupSpace.X -> rangeBasedLookup(cursorCoord, lookupStrategy, closestPointChecker, x, byX = true)
            LookupSpace.Y -> rangeBasedLookup(cursorCoord, lookupStrategy, closestPointChecker, y, byX = false)
            LookupSpace.XY -> when (lookupStrategy) {
                LookupStrategy.NONE -> false
                LookupStrategy.NEAREST,
                LookupStrategy.HOVER -> xy.count { cursorCoord in it } % 2 != 0
            }
        }
    }

    companion object {
        private const val POINTS_COUNT_TO_SKIP_SIMPLIFICATION = 20.0
        private const val AREA_TOLERANCE_RATIO = 0.1
        private const val MAX_TOLERANCE = 40.0
        private const val RECT_X_NEAREST_EPSILON = 2.0

        fun create(points: List<DoubleVector>, lookupSpace: LookupSpace): PolygonTargetProjection {
            return PolygonTargetProjection(points, lookupSpace)
        }

        private fun mapToAxis(coordSelector: (DoubleVector) -> Double, rings: List<List<DoubleVector>>): DoubleSpan {
            var min = coordSelector(rings[0][0])
            var max = min
            for (ring in rings) {
                for (point in ring) {
                    min = min(min, coordSelector(point))
                    max = max(max, coordSelector(point))
                }
            }
            return DoubleSpan(min, max)
        }

        private fun mapToXY(rings: List<List<DoubleVector>>): List<RingXY> {
            val polygon = ArrayList<RingXY>()

            for (ring in rings) {
                if (ring.size < 4) {
                    continue
                }

                val bbox = DoubleRectangles.boundingBox(ring) ?: error("bbox should be not null - ring is not empty")
                val area = calculateArea(ring)

                val simplifiedRing: List<DoubleVector>

                if (ring.size > POINTS_COUNT_TO_SKIP_SIMPLIFICATION) {
                    val tolerance = min(area * AREA_TOLERANCE_RATIO, MAX_TOLERANCE)
                    simplifiedRing =
                        PolylineSimplifier.visvalingamWhyatt(ring).setWeightLimit(tolerance).points.single()

                    if (isLogEnabled) {
                        log(
                            "Simp: " + ring.size + " -> " + simplifiedRing.size +
                                    ", tolerance=" + tolerance +
                                    ", bbox=" + bbox +
                                    ", area=" + area
                        )
                    }
                } else {
                    if (isLogEnabled) {
                        log(
                            "Keep: size: " + ring.size +
                                    ", bbox=" + bbox +
                                    ", area=" + area
                        )
                    }
                    simplifiedRing = ring
                }

                if (simplifiedRing.size < 4) {
                    continue
                }

                polygon.add(RingXY(simplifiedRing, bbox))
            }

            return polygon
        }

        private fun rangeBasedLookup(
            cursor: DoubleVector,
            lookupStrategy: LookupStrategy,
            closestPointChecker: ClosestPointChecker,
            range: DoubleSpan,
            byX: Boolean
        ): Boolean {
            val coord = if (byX) {
                DoubleVector(range.lowerEnd + range.length / 2, cursor.y)
            } else {
                DoubleVector(cursor.x, range.lowerEnd + range.length / 2)
            }

            return when (lookupStrategy) {
                LookupStrategy.NONE -> false
                LookupStrategy.HOVER -> {
                    closestPointChecker.check(coord)
                    (if (byX) cursor.x else cursor.y) in range
                }

                LookupStrategy.NEAREST -> {
                    val cursorCoord = if (byX) cursor.x else cursor.y
                    if (range.contains(cursorCoord - RECT_X_NEAREST_EPSILON) || range.contains(cursorCoord + RECT_X_NEAREST_EPSILON)) {
                        closestPointChecker.check(coord)
                    } else {
                        false
                    }
                }
            }
        }

        private fun log(str: String) {
            println(str)
        }

        private const val isLogEnabled = false
    }

    internal class RingXY(
        private val edges: List<DoubleVector>,
        private val bbox: DoubleRectangle
    ) {
        operator fun contains(p: DoubleVector) = p in bbox && polygonContainsCoordinate(edges, p)
    }
}
