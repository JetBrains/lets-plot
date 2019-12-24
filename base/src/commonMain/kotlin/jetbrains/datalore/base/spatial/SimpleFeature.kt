/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.spatial

import jetbrains.datalore.base.typedGeometry.*

object SimpleFeature {
    interface GeometryConsumer {
        fun onPoint(point: Vec<Generic>): Unit = error("Point isn't supported")
        fun onLineString(lineString: LineString<Generic>): Unit = error("LineString isn't supported")
        fun onPolygon(polygon: Polygon<Generic>): Unit = error("Polygon isn't supported")
        fun onMultiPoint(multiPoint: MultiPoint<Generic>): Unit = error("MultiPoint isn't supported")
        fun onMultiLineString(multiLineString: MultiLineString<Generic>): Unit = error("MultiLineString isn't supported")
        fun onMultiPolygon(multipolygon: MultiPolygon<Generic>): Unit = error("MultiPolygon isn't supported")
    }

    class Consumer<T>(
        var point: (Vec<T>) -> Unit = { error("Point isn't supported") },
        var lineString: (LineString<T>) -> Unit = { error("LineString isn't supported") },
        var polygon: (Polygon<T>) -> Unit = { error("Polygon isn't supported") },
        var multiPoint: (MultiPoint<T>) -> Unit = { error("MultiPoint isn't supported") },
        var multiLineString: (MultiLineString<T>) -> Unit = { error("MultiLineString isn't supported") },
        var multiPolygon: (MultiPolygon<T>) -> Unit = { error("MultiPolygon isn't supported") }
    ) : GeometryConsumer {
        override fun onPoint(point: Vec<Generic>): Unit = point(point.reinterpret())
        override fun onLineString(lineString: LineString<Generic>): Unit = lineString(lineString.reinterpret())
        override fun onPolygon(polygon: Polygon<Generic>): Unit = polygon(polygon.reinterpret())
        override fun onMultiPoint(multiPoint: MultiPoint<Generic>): Unit = multiPoint(multiPoint.reinterpret())
        override fun onMultiLineString(multiLineString: MultiLineString<Generic>): Unit = multiLineString(multiLineString.reinterpret())
        override fun onMultiPolygon(multipolygon: MultiPolygon<Generic>): Unit = multiPolygon(multipolygon.reinterpret())
    }

    enum class GeometryType {
        POINT,
        LINE_STRING,
        POLYGON,
        MULTI_POINT,
        MULTI_LINE_STRING,
        MULTI_POLYGON,
        GEOMETRY_COLLECTION;


        companion object {

            fun getGeometryType(type: String): GeometryType {
                try {
                    return valueOf(type.toUpperCase())
                } catch (ignored: Exception) {
                    throw IllegalArgumentException("Invalid geometry type: $type")
                }

            }
        }
    }

}