/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.geometry

import kotlin.math.max
import kotlin.math.min

object DoubleRectangles {

    /*
    Returns null if point collection is empty
    */
    fun boundingBox(points: Iterable<DoubleVector>): DoubleRectangle? {
        return calculateBoundingBox(points, DoubleVector::x, DoubleVector::y)
        { minX, minY, maxX, maxY ->
            DoubleRectangle.span(
                DoubleVector(minX, minY),
                DoubleVector(maxX, maxY)
            )
        }
    }

    fun <PointT, BoxT> calculateBoundingBox(
        points: Iterable<PointT>,
        getX: (PointT) -> Double,
        getY: (PointT) -> Double,
        factory: (minX: Double, minY: Double, maxX: Double, maxY: Double) -> BoxT
    ): BoxT? {
        if (!points.iterator().hasNext()) {
            return null
        }
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
