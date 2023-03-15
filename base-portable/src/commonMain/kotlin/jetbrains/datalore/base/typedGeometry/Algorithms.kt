/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.typedGeometry

import jetbrains.datalore.base.algorithms.isClockwise
import jetbrains.datalore.base.algorithms.splitRings
import jetbrains.datalore.base.geometry.DoubleRectangles
import kotlin.math.sqrt

val <T> Vec<T>.length get() = sqrt(x * x + y * y)

fun <T> createMultiPolygon(points: List<Vec<T>>): MultiPolygon<T> {
    if (points.isEmpty()) {
        return MultiPolygon(emptyList())
    }

    val polygons = ArrayList<Polygon<T>>()
    var rings = ArrayList<Ring<T>>()

    for (ring in splitRings(points)) {
        if (rings.isNotEmpty() && isClockwise(ring, Vec<T>::x, Vec<T>::y)) {
            polygons.add(Polygon(rings))
            rings = ArrayList()
        }
        rings.add(Ring(ring))
    }

    if (rings.isNotEmpty()) {
        polygons.add(Polygon(rings))
    }

    return MultiPolygon(polygons)
}

fun <TypeT> Iterable<Vec<TypeT>>.boundingBox(): Rect<TypeT>? {
    return DoubleRectangles.calculateBoundingBox(this, Vec<*>::x, Vec<*>::y, Rect.Companion::LTRB)
}
