/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.tooltip.loc

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleRectangles.boundingBox
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.typedGeometry.algorithms.calculateArea
import org.jetbrains.letsPlot.commons.intern.typedGeometry.algorithms.splitRings
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.commons.geometry.PolylineSimplifier
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator.LookupSpace
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator.LookupSpace.*
import org.jetbrains.letsPlot.core.plot.builder.tooltip.MathUtil.polygonContainsCoordinate
import kotlin.math.max
import kotlin.math.min


internal open class TargetProjection

internal class PointTargetProjection private constructor(
    val data: Any,
    val radius: Double
) : TargetProjection() {
    fun x() = data as Double
    fun y() = data as Double
    fun xy() = data as DoubleVector

    companion object {
        fun create(p: DoubleVector, radius: Double, lookupSpace: LookupSpace): PointTargetProjection {
            return when (lookupSpace) {
                X -> PointTargetProjection(p.x, radius)
                Y -> PointTargetProjection(p.y, radius)
                XY -> PointTargetProjection(p, radius)
                NONE -> undefinedLookupSpaceError()
            }
        }
    }
}

internal class RectTargetProjection private constructor(val data: Any) : TargetProjection() {
    fun x() = data as DoubleSpan
    fun y() = data as DoubleSpan
    fun xy() = data as DoubleRectangle

    companion object {
        fun create(rect: DoubleRectangle, lookupSpace: LookupSpace): RectTargetProjection {
            return when (lookupSpace) {
                X -> RectTargetProjection(rect.xRange())
                Y -> RectTargetProjection(rect.yRange())
                XY -> RectTargetProjection(rect)
                NONE -> undefinedLookupSpaceError()
            }
        }
    }
}

internal class PolygonTargetProjection private constructor(val data: Any) : TargetProjection() {
    fun x() = data as DoubleSpan
    fun y() = data as DoubleSpan
    fun xy(): List<RingXY> {
        @Suppress("UNCHECKED_CAST")
        return data as List<RingXY>
    }

    companion object {
        private const val POINTS_COUNT_TO_SKIP_SIMPLIFICATION = 20.0
        private const val AREA_TOLERANCE_RATIO = 0.1
        private const val MAX_TOLERANCE = 40.0

        fun create(points: List<DoubleVector>, lookupSpace: LookupSpace): PolygonTargetProjection {
            val rings = splitRings(points)

            return when (lookupSpace) {
                X -> PolygonTargetProjection(mapToAxis(DoubleVector::x, rings))
                Y -> PolygonTargetProjection(mapToAxis(DoubleVector::y, rings))
                XY -> PolygonTargetProjection(mapToXY(rings))
                NONE -> undefinedLookupSpaceError()
            }
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

                val bbox = boundingBox(ring) ?: error("bbox should be not null - ring is not empty")
                val area = calculateArea(ring)

                val simplifiedRing: List<DoubleVector>

                if (ring.size > POINTS_COUNT_TO_SKIP_SIMPLIFICATION) {
                    val tolerance = min(area * AREA_TOLERANCE_RATIO, MAX_TOLERANCE)
                    simplifiedRing = PolylineSimplifier.visvalingamWhyatt(ring).setWeightLimit(tolerance).points.single()

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

internal class PathTargetProjection(val data: List<PathPoint>) : TargetProjection() {
    val points: List<PathPoint> = data

    internal class PathPoint private constructor(
        private val myPointTargetProjection: PointTargetProjection,
        val originalCoord: DoubleVector,
        val index: Int
    ) {
        fun projection() = myPointTargetProjection

        companion object {
            internal fun create(p: DoubleVector, index: Int, lookupSpace: LookupSpace): PathPoint {
                return when (lookupSpace) {
                    X -> PathPoint(PointTargetProjection.create(p, radius = 0.0, lookupSpace), p, index)
                    Y -> PathPoint(PointTargetProjection.create(p, radius = 0.0, lookupSpace), p, index)
                    XY -> PathPoint(PointTargetProjection.create(p, radius = 0.0, lookupSpace), p, index)
                    NONE -> undefinedLookupSpaceError()
                }
            }
        }
    }

    companion object {
        fun create(
            points: List<DoubleVector>,
            indexMapper: (Int) -> Int,
            lookupSpace: LookupSpace
        ): PathTargetProjection {
            val pointsLocation = ArrayList<PathPoint>()
            for ((i, point) in points.withIndex()) {
                pointsLocation.add(PathPoint.create(point, indexMapper(i), lookupSpace))
            }

            // Sort for fast search
            if (lookupSpace == X) {
                pointsLocation.sortBy { it.projection().x() }
            } else if (lookupSpace == Y) {
                pointsLocation.sortBy { it.projection().y() }
            }

            return PathTargetProjection(pointsLocation)
        }
    }
}

private fun undefinedLookupSpaceError(): Nothing {
    throw IllegalStateException("Undefined geom lookup space")
}
