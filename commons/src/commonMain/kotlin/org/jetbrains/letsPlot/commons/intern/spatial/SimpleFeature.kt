/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.spatial

import org.jetbrains.letsPlot.commons.intern.typedGeometry.*
import org.jetbrains.letsPlot.commons.intern.typedGeometry.*

object SimpleFeature {
    interface GeometryConsumer<T> {
        fun onPoint(point: Vec<T>): Unit = error("Point isn't supported")
        fun onLineString(lineString: LineString<T>): Unit = error("LineString isn't supported")
        fun onPolygon(polygon: Polygon<T>): Unit = error("Polygon isn't supported")
        fun onMultiPoint(multiPoint: MultiPoint<T>): Unit = error("MultiPoint isn't supported")
        fun onMultiLineString(multiLineString: MultiLineString<T>): Unit = error("MultiLineString isn't supported")
        fun onMultiPolygon(multipolygon: MultiPolygon<T>): Unit = error("MultiPolygon isn't supported")
    }

    class Consumer<T>(
        var onPoint: (Vec<T>) -> Unit = { error("Point isn't supported") },
        var onLineString: (LineString<T>) -> Unit = { error("LineString isn't supported") },
        var onPolygon: (Polygon<T>) -> Unit = { error("Polygon isn't supported") },
        var onMultiPoint: (MultiPoint<T>) -> Unit = { error("MultiPoint isn't supported") },
        var onMultiLineString: (MultiLineString<T>) -> Unit = { error("MultiLineString isn't supported") },
        var onMultiPolygon: (MultiPolygon<T>) -> Unit = { error("MultiPolygon isn't supported") }
    ) : GeometryConsumer<T> {
        override fun onPoint(point: Vec<T>): Unit = (onPoint)(point)
        override fun onLineString(lineString: LineString<T>): Unit = (onLineString)(lineString)
        override fun onPolygon(polygon: Polygon<T>): Unit = (onPolygon)(polygon)
        override fun onMultiPoint(multiPoint: MultiPoint<T>): Unit = (onMultiPoint)(multiPoint)
        override fun onMultiLineString(multiLineString: MultiLineString<T>): Unit = (onMultiLineString)(multiLineString)
        override fun onMultiPolygon(multipolygon: MultiPolygon<T>): Unit = (onMultiPolygon)(multipolygon)
    }

    enum class GeometryType {
        POINT,
        LINE_STRING,
        POLYGON,
        MULTI_POINT,
        MULTI_LINE_STRING,
        MULTI_POLYGON,
        GEOMETRY_COLLECTION;
    }
}
