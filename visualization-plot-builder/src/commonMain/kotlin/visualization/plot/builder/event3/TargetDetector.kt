package jetbrains.datalore.visualization.plot.builder.event3

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.plot.base.event3.GeomTargetLocator.LookupSpace
import jetbrains.datalore.visualization.plot.base.event3.GeomTargetLocator.LookupStrategy
import jetbrains.datalore.visualization.plot.builder.event3.MathUtil.ClosestPointChecker
import jetbrains.datalore.visualization.plot.builder.event3.MathUtil.ClosestPointChecker.COMPARE_RESULT
import jetbrains.datalore.visualization.plot.builder.event3.MathUtil.DoubleRange
import jetbrains.datalore.visualization.plot.builder.event3.TargetProjector.*
import jetbrains.datalore.visualization.plot.builder.event3.TargetProjector.PathTargetProjection.PathPoint

internal class TargetDetector(private val myLocatorLookupSpace: LookupSpace, private val myLocatorLookupStrategy: LookupStrategy) {

    fun checkPath(cursorCoord: DoubleVector, pathProjection: PathTargetProjection, closestPointChecker: ClosestPointChecker): PathPoint? {

        when (myLocatorLookupSpace) {

            LookupSpace.X -> {
                if (myLocatorLookupStrategy === LookupStrategy.NONE) {
                    return null
                }

                val pathPoints = pathProjection.points
                if (pathPoints.isEmpty()) {
                    return null
                }

                val resultIndex = binarySearch(
                        cursorCoord.x,
                        pathPoints.size,
                        { index -> pathPoints[index].projection().x() }
                )
                val bestPoint = pathPoints[resultIndex]

                when (myLocatorLookupStrategy) {
                    LookupStrategy.HOVER -> {
                        return if (cursorCoord.x < pathPoints[0].projection().x() || cursorCoord.x > pathPoints[pathPoints.size - 1].projection().x()) {
                            null
                        } else bestPoint
                    }

                    LookupStrategy.NEAREST -> return bestPoint

                    else -> throw IllegalStateException("Unknown lookup strategy: $myLocatorLookupStrategy")
                }
            }

            LookupSpace.XY -> {
                when (myLocatorLookupStrategy) {

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

                    LookupStrategy.NONE -> return null
                }
            }

            LookupSpace.NONE -> return null

            else -> throw IllegalStateException()
        }
    }

    fun checkPoint(cursorCoord: DoubleVector, pointProjection: PointTargetProjection, closestPointChecker: ClosestPointChecker): Boolean {
        when (myLocatorLookupSpace) {

            LookupSpace.X -> {
                val x = pointProjection.x()
                when (myLocatorLookupStrategy) {

                    LookupStrategy.HOVER -> return MathUtil.areEqual(x, cursorCoord.x, POINT_AREA_EPSILON)

                    LookupStrategy.NEAREST -> {
                        // Too far. Don't add this point into result list
                        return if (!MathUtil.areEqual(closestPointChecker.target.x, x, POINT_X_NEAREST_EPSILON)) {
                            false
                        } else closestPointChecker.check(DoubleVector(x, 0.0))

                    }

                    LookupStrategy.NONE -> return false

                    else -> throw IllegalStateException()
                }
            }

            LookupSpace.XY -> {
                val targetPointCoord = pointProjection.xy()
                when (myLocatorLookupStrategy) {

                    LookupStrategy.HOVER -> return MathUtil.areEqual(targetPointCoord, cursorCoord, POINT_AREA_EPSILON)

                    LookupStrategy.NEAREST -> return closestPointChecker.check(targetPointCoord)

                    LookupStrategy.NONE -> return false

                    else -> throw IllegalStateException()
                }
            }

            LookupSpace.NONE -> return false

            else -> throw IllegalStateException()
        }
    }

    fun checkRect(cursorCoord: DoubleVector, rectProjection: RectTargetProjection, closestPointChecker: ClosestPointChecker): Boolean {
        when (myLocatorLookupSpace) {

            LookupSpace.X -> {
                val range = rectProjection.x()
                return rangeBasedLookup(cursorCoord, closestPointChecker, range)
            }

            LookupSpace.XY -> {
                val rect = rectProjection.xy()
                when (myLocatorLookupStrategy) {

                    LookupStrategy.HOVER -> return rect.contains(cursorCoord)

                    LookupStrategy.NEAREST -> {
                        if (rect.contains(cursorCoord)) {
                            return closestPointChecker.check(cursorCoord)
                        }

                        var x = if (cursorCoord.x < rect.left) rect.left else rect.right
                        var y = if (cursorCoord.y < rect.top) rect.top else rect.bottom

                        x = if (rect.xRange().contains(cursorCoord.x)) cursorCoord.x else x
                        y = if (rect.yRange().contains(cursorCoord.y)) cursorCoord.y else y

                        return closestPointChecker.check(DoubleVector(x, y))
                    }

                    LookupStrategy.NONE -> return false

                    else -> throw IllegalStateException()
                }
            }

            LookupSpace.NONE -> return false

            else -> throw IllegalStateException()
        }
    }

    fun checkPolygon(cursorCoord: DoubleVector, polygonProjection: PolygonTargetProjection, closestPointChecker: ClosestPointChecker): Boolean {
        when (myLocatorLookupSpace) {

            LookupSpace.X -> {
                val range = polygonProjection.x()
                return rangeBasedLookup(cursorCoord, closestPointChecker, range)
            }

            LookupSpace.XY -> {
                val polygon = polygonProjection.xy()
                when (myLocatorLookupStrategy) {

                    LookupStrategy.HOVER, LookupStrategy.NEAREST -> {
                        // Doesn't support nearest strategy. Target can be found only by hovering a cursor above the polygon.
                        var counter = 0
                        for (ring in polygon) {
                            if (ring.bbox.contains(cursorCoord) && MathUtil.polygonContainsCoordinate(ring.edges, cursorCoord)) {
                                counter++
                            }
                        }
                        return counter % 2 != 0
                    }

                    LookupStrategy.NONE -> return false

                    else -> throw IllegalStateException()
                }
            }

            LookupSpace.NONE -> return false

            else -> throw IllegalStateException()
        }
    }

    private fun rangeBasedLookup(cursorCoord: DoubleVector, closestPointChecker: ClosestPointChecker, range: DoubleRange): Boolean {
        when (myLocatorLookupStrategy) {

            LookupStrategy.HOVER -> return range.contains(cursorCoord.x)

            LookupStrategy.NEAREST -> {
                //Too far
                return if (!range.contains(cursorCoord.x - RECT_X_NEAREST_EPSILON) && !range.contains(cursorCoord.x + RECT_X_NEAREST_EPSILON)) {
                    false
                } else closestPointChecker.compare(DoubleVector(range.start() + range.length() / 2, cursorCoord.y)) !== COMPARE_RESULT.NEW_FARTHER

            }

            LookupStrategy.NONE -> return false

            else -> throw IllegalStateException()
        }
    }

    companion object {
        private val POINT_AREA_EPSILON = 0.1
        private val POINT_X_NEAREST_EPSILON = 2.0
        private val RECT_X_NEAREST_EPSILON = 2.0

        private fun binarySearch(value: Double, length: Int, indexer: (Int) -> Double): Int {

            if (value < indexer(0)) {
                return 0
            }
            if (value > indexer(length - 1)) {
                return length - 1
            }

            var lo = 0
            var hi = length - 1

            while (lo <= hi) {
                val mid = (hi + lo) / 2
                val midValue = indexer(mid)

                if (value < midValue) {
                    hi = mid - 1
                } else if (value > midValue) {
                    lo = mid + 1
                } else {
                    return mid
                }
            }

            return if (indexer(lo) - value < value - indexer(hi)) {
                lo
            } else {
                hi
            }
        }
    }
}
