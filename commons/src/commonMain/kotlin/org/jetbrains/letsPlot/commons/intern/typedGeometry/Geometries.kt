/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.typedGeometry

import org.jetbrains.letsPlot.commons.intern.typedGeometry.algorithms.isClockwise

open class AbstractGeometryList<T>(
    private val myGeometry: List<T>
) : AbstractList<T>() {
    override fun get(index: Int) = myGeometry[index]

    override val size: Int
        get() = myGeometry.size
}


class LineString<TypeT>(
    points: List<Vec<TypeT>>
) : AbstractGeometryList<Vec<TypeT>>(points) {
    val bbox: Rect<TypeT>? by lazy(this::boundingBox)

    companion object {
        fun <TypeT> of(vararg points: Vec<TypeT>) = LineString(points.asList())
    }
}


class MultiLineString<TypeT>(
    lineStrings: List<LineString<TypeT>>
) : AbstractGeometryList<LineString<TypeT>>(lineStrings) {
    constructor(linString: LineString<TypeT>) : this(listOf(linString))
    val bbox: Rect<TypeT>? by lazy(lineStrings.map(LineString<TypeT>::bbox)::union)
}


class Ring<TypeT>(
    points: List<Vec<TypeT>>
) : AbstractGeometryList<Vec<TypeT>>(points) {
    val bbox: Rect<TypeT>? by lazy(this::boundingBox)
    val isClockwise: Boolean by lazy { isClockwise(this, Vec<TypeT>::x, Vec<TypeT>::y) }

    companion object {
        fun <TypeT> of(vararg points: Vec<TypeT>) = Ring(points.asList())
    }
}


class Polygon<TypeT>(
    rings: List<Ring<TypeT>>
) : AbstractGeometryList<Ring<TypeT>>(rings) {
    constructor(ring: Ring<TypeT>) : this(listOf(ring))
    val bbox: Rect<TypeT>? by lazy(rings.map(Ring<TypeT>::bbox)::union)
}


class MultiPolygon<TypeT>(
    polygons: List<Polygon<TypeT>>
) : AbstractGeometryList<Polygon<TypeT>>(polygons) {
    constructor(polygon: Polygon<TypeT>) : this(listOf(polygon))
    val bbox: Rect<TypeT>? by lazy(polygons.map(Polygon<TypeT>::bbox)::union)
}


class MultiPoint<TypeT>(
    points: List<Vec<TypeT>>
) : AbstractGeometryList<Vec<TypeT>>(points) {
    constructor(point: Vec<TypeT>) : this(listOf(point))
    val bbox: Rect<TypeT>? by lazy(this::boundingBox)
}
