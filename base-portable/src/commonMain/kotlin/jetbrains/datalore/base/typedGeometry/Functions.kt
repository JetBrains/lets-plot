/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.typedGeometry

import jetbrains.datalore.base.typedGeometry.algorithms.isClockwise
import jetbrains.datalore.base.typedGeometry.algorithms.splitRings
import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleRectangles
import org.jetbrains.letsPlot.commons.geometry.DoubleVector

class Untyped

@Suppress("UNCHECKED_CAST")
fun <TypeT> Vec<Untyped>.reinterpret(): Vec<TypeT> = this as Vec<TypeT>

@Suppress("UNCHECKED_CAST")
fun <TypeT> MultiPoint<Untyped>.reinterpret(): MultiPoint<TypeT> = this as MultiPoint<TypeT>

@Suppress("UNCHECKED_CAST")
fun <TypeT> LineString<Untyped>.reinterpret(): LineString<TypeT> = this as LineString<TypeT>

@Suppress("UNCHECKED_CAST")
fun <TypeT> MultiLineString<Untyped>.reinterpret(): MultiLineString<TypeT> = this as MultiLineString<TypeT>

@Suppress("UNCHECKED_CAST")
fun <TypeT> Polygon<Untyped>.reinterpret(): Polygon<TypeT> = this as Polygon<TypeT>

@Suppress("UNCHECKED_CAST")
fun <TypeT> MultiPolygon<Untyped>.reinterpret(): MultiPolygon<TypeT> = this as MultiPolygon<TypeT>


fun Vec<*>.toDoubleVector() = DoubleVector(x, y)
fun <T> DoubleVector.toVec() = Vec<T>(x, y)
fun <T> DoubleRectangle.toRect() = Rect.XYWH<T>(left, top, width, height)
fun <T> Rect<T>.toDoubleRectangle() = DoubleRectangle(left, top, width, height)

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
