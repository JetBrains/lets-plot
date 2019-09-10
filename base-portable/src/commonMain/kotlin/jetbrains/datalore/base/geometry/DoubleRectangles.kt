package jetbrains.datalore.base.geometry

import kotlin.math.max
import kotlin.math.min

object DoubleRectangles {
    fun boundingBox(points: Iterable<DoubleVector>): DoubleRectangle {
        val first = points.iterator().next()
        var minLon = first.x
        var minLat = first.y
        var maxLon = minLon
        var maxLat = minLat

        for (point in points) {
            minLon = min(minLon, point.x)
            maxLon = max(maxLon, point.x)
            minLat = min(minLat, point.y)
            maxLat = max(maxLat, point.y)
        }

        return DoubleRectangle.span(
                DoubleVector(minLon, minLat),
                DoubleVector(maxLon, maxLat))
    }
}
