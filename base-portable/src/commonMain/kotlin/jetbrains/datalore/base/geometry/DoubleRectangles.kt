package jetbrains.datalore.base.geometry

import jetbrains.datalore.base.projectionGeometry.AnyPoint
import jetbrains.datalore.base.projectionGeometry.Rect
import jetbrains.datalore.base.projectionGeometry.Vec
import jetbrains.datalore.base.projectionGeometry.newSpanRectangle
import kotlin.math.max
import kotlin.math.min

object DoubleRectangles {
    private val GET_DOUBLE_VECTOR_X = { p: DoubleVector -> p.x }
    private val GET_DOUBLE_VECTOR_Y = { p: DoubleVector -> p.y }
    private val GET_POINT_X = { p: AnyPoint -> p.x }
    private val GET_POINT_Y = { p: AnyPoint -> p.y }

    fun boundingBox(points: Iterable<DoubleVector>): DoubleRectangle {
        return calculateBoundingBox(points, GET_DOUBLE_VECTOR_X, GET_DOUBLE_VECTOR_Y)
        { minX, minY, maxX, maxY ->
            return@calculateBoundingBox DoubleRectangle.span(
                DoubleVector(minX, minY),
                DoubleVector(maxX, maxY)
            )
        }
    }

    fun <TypeT> boundingBox(points: Iterable<Vec<TypeT>>): Rect<TypeT> {
        return calculateBoundingBox(points, GET_POINT_X, GET_POINT_Y)
        { minX, minY, maxX, maxY ->
            newSpanRectangle(
                Vec(minX, minY),
                Vec(maxX, maxY)
            )
        }
    }

    private fun <PointT, BoxT> calculateBoundingBox(
        points: Iterable<PointT>,
        getX: (PointT) -> Double,
        getY: (PointT) -> Double,
        factory: (minX: Double, minY: Double, maxX: Double, maxY: Double) -> BoxT
    ): BoxT {
        val first = points.iterator().next()
        var minLon = getX(first)
        var minLat = getY(first)
        var maxLon = minLon
        var maxLat = minLat

        for (point in points) {
            minLon = min(minLon, getX(point))
            maxLon = max(maxLon, getX(point))
            minLat = min(minLat, getY(point))
            maxLat = max(maxLat, getY(point))
        }

        return factory(minLon, minLat, maxLon, maxLat)
    }
}
