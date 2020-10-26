/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact.loc

import jetbrains.datalore.base.algorithms.calculateArea
import jetbrains.datalore.base.algorithms.splitRings
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleRectangles.boundingBox
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.interact.GeomTargetLocator.LookupSpace
import jetbrains.datalore.plot.base.interact.GeomTargetLocator.LookupSpace.*
import jetbrains.datalore.plot.builder.interact.MathUtil.DoubleRange
import jetbrains.datalore.plot.common.geometry.PolylineSimplifier
import kotlin.math.max
import kotlin.math.min


internal open class TargetProjection

internal class PointTargetProjection private constructor(val data: Any) : TargetProjection() {

    fun x(): Double {
        return data as Double
    }

    fun xy(): DoubleVector {
        return data as DoubleVector
    }

    companion object {
        fun create(p: DoubleVector, lookupSpace: LookupSpace): PointTargetProjection {
            return when (lookupSpace) {
                X -> PointTargetProjection(p.x)
                XY -> PointTargetProjection(p)
                NONE -> undefinedLookupSpaceError()
            }
        }
    }
}

internal class RectTargetProjection private constructor(val data: Any) : TargetProjection() {

    fun x(): DoubleRange {
        return data as DoubleRange
    }

    fun xy(): DoubleRectangle {
        return data as DoubleRectangle
    }

    companion object {
        fun create(rect: DoubleRectangle, lookupSpace: LookupSpace): RectTargetProjection {
            return when (lookupSpace) {
                X -> RectTargetProjection(DoubleRange.withStartAndEnd(rect.left, rect.right))
                XY -> RectTargetProjection(rect)
                NONE -> undefinedLookupSpaceError()
            }
        }
    }
}

internal class PolygonTargetProjection private constructor(val data: Any) : TargetProjection() {

    fun x(): DoubleRange {
        return data as DoubleRange
    }

    fun xy(): List<RingXY> {
        @Suppress("UNCHECKED_CAST")
        return data as List<RingXY>
    }

    companion object {
        private const val AREA_LIMIT_TO_REMOVE_POLYGON = 25.0
        private const val POINTS_COUNT_TO_SKIP_SIMPLIFICATION = 20.0
        private const val AREA_TOLERANCE_RATIO = 0.1
        private const val MAX_TOLERANCE = 40.0

        fun create(points: List<DoubleVector>, lookupSpace: LookupSpace): PolygonTargetProjection {
            val rings = splitRings(points)

            return when (lookupSpace) {
                X -> PolygonTargetProjection(mapToX(rings))
                XY -> PolygonTargetProjection(mapToXY(rings))
                NONE -> undefinedLookupSpaceError()
            }
        }

        private fun mapToX(rings: List<List<DoubleVector>>): DoubleRange {
            var min = rings[0][0].x
            var max = min
            for (ring in rings) {
                for (point in ring) {
                    min = min(min, point.x)
                    max = max(max, point.x)
                }
            }
            return DoubleRange.withStartAndEnd(min, max)
        }

        private fun mapToXY(rings: List<List<DoubleVector>>): List<RingXY> {
            val polygon = ArrayList<RingXY>()

            for (ring in rings) {
                if (ring.size < 4) {
                    continue
                }


                val bbox = boundingBox(ring)
                val area = calculateArea(ring)

                if (area < AREA_LIMIT_TO_REMOVE_POLYGON) {
                    @Suppress("ConstantConditionIf")
                    if (isLogEnabled) {
                        log("Rem.: size=" + ring.size +
                                ", bbox=" + bbox +
                                ", area=" + area
                        )
                    }
                    continue
                }

                val simplifiedRing: List<DoubleVector>

                if (ring.size > POINTS_COUNT_TO_SKIP_SIMPLIFICATION) {
                    val tolerance = min(area * AREA_TOLERANCE_RATIO, MAX_TOLERANCE)
                    simplifiedRing = PolylineSimplifier.visvalingamWhyatt(ring).setWeightLimit(tolerance).points

                    @Suppress("ConstantConditionIf")
                    if (isLogEnabled) {
                        log("Simp: " + ring.size + " -> " + simplifiedRing.size +
                                ", tolerance=" + tolerance +
                                ", bbox=" + bbox +
                                ", area=" + area
                        )
                    }
                } else {
                    @Suppress("ConstantConditionIf")
                    if (isLogEnabled) {
                        log("Keep: size: " + ring.size +
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

    internal class RingXY(val edges: List<DoubleVector>, val bbox: DoubleRectangle)
}

internal class PathTargetProjection(val data: List<PathPoint>) : TargetProjection() {

    val points: List<PathPoint> = data

    internal class PathPoint private constructor(
            private val myPointTargetProjection: PointTargetProjection,
            val originalCoord: DoubleVector,
            val index: Int) {

        fun projection(): PointTargetProjection {
            return myPointTargetProjection
        }

        companion object {
            fun create(p: DoubleVector, index: Int, lookupSpace: LookupSpace): PathPoint {
                return when (lookupSpace) {
                    X -> PathPoint(PointTargetProjection.create(p, lookupSpace), p, index)
                    XY -> PathPoint(PointTargetProjection.create(p, lookupSpace), p, index)
                    NONE -> undefinedLookupSpaceError()
                }
            }
        }
    }

    companion object {
        fun create(points: List<DoubleVector>, indexMapper: (Int) -> Int, lookupSpace: LookupSpace): PathTargetProjection {
            val pointsLocation = ArrayList<PathPoint>()
            for ((i, point) in points.withIndex()) {
                pointsLocation.add(PathPoint.create(point, indexMapper(i), lookupSpace))
            }

            return PathTargetProjection(pointsLocation)
        }
    }
}

private fun undefinedLookupSpaceError(): Nothing {
    throw IllegalStateException("Undefined geom lookup space")
}
