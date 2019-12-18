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
        fun onMultiPoint(multiPoint: MultiPoint<Generic>, idList: List<Int>): Unit = error("MultiPoint isn't supported")
        fun onMultiLineString(multiLineString: MultiLineString<Generic>, idList: List<Int>): Unit = error("MultiLineString isn't supported")
        fun onMultiPolygon(multipolygon: MultiPolygon<Generic>, idList: List<Int>): Unit = error("MultiPolygon isn't supported")
    }

    class Consumer(
        var point: (Vec<Generic>) -> Unit = { error("Point isn't supported") },
        var lineString: (LineString<Generic>) -> Unit = { error("LineString isn't supported") },
        var polygon: (Polygon<Generic>) -> Unit = { error("Polygon isn't supported") },
        var multiPoint: (MultiPoint<Generic>, List<Int>) -> Unit = { _, _ -> error("MultiPoint isn't supported") },
        var multiLineString: (MultiLineString<Generic>, List<Int>) -> Unit = { _, _ -> error("MultiLineString isn't supported") },
        var multiPolygon: (MultiPolygon<Generic>, List<Int>) -> Unit = { _, _ -> error("MultiPolygon isn't supported") }
    ) : GeometryConsumer {
        override fun onPoint(point: Vec<Generic>): Unit = point(point)
        override fun onLineString(lineString: LineString<Generic>): Unit = lineString(lineString)
        override fun onPolygon(polygon: Polygon<Generic>): Unit = polygon(polygon)
        override fun onMultiPoint(multiPoint: MultiPoint<Generic>, idList: List<Int>): Unit = multiPoint(multiPoint, idList)
        override fun onMultiLineString(multiLineString: MultiLineString<Generic>, idList: List<Int>): Unit = multiLineString(multiLineString, idList)
        override fun onMultiPolygon(multipolygon: MultiPolygon<Generic>, idList: List<Int>): Unit = multiPolygon(multipolygon, idList)
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