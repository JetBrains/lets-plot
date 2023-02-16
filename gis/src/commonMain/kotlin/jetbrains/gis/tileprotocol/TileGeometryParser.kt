/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.gis.tileprotocol

import jetbrains.datalore.base.spatial.LonLat
import jetbrains.datalore.base.spatial.SimpleFeature
import jetbrains.datalore.base.typedGeometry.*
import jetbrains.gis.common.twkb.Twkb


class TileGeometryParser(geometryCollection: GeometryCollection) {
    private val myGeometryConsumer = MyGeometryConsumer()
    private val myParser = Twkb.parser(geometryCollection.asTwkb(), myGeometryConsumer)

    val geometries: List<Geometry<LonLat>>
        get() = myGeometryConsumer.tileGeometries

    fun resume() = myParser.next()

    private class MyGeometryConsumer : SimpleFeature.GeometryConsumer<Untyped> {
        private val myTileGeometries = ArrayList<Geometry<LonLat>>()

        val tileGeometries: List<Geometry<LonLat>>
            get() = myTileGeometries

        override fun onPoint(point: Vec<Untyped>) {
            myTileGeometries.add(Geometry.of(point.reinterpret()))
        }

        override fun onLineString(lineString: LineString<Untyped>) {
            myTileGeometries.add(Geometry.of(lineString.reinterpret()))
        }

        override fun onPolygon(polygon: Polygon<Untyped>) {
            myTileGeometries.add(Geometry.of(polygon.reinterpret()))
        }

        override fun onMultiPoint(multiPoint: MultiPoint<Untyped>) {
            myTileGeometries.add(Geometry.of(multiPoint.reinterpret()))
        }

        override fun onMultiLineString(multiLineString: MultiLineString<Untyped>) {
            myTileGeometries.add(Geometry.of(multiLineString.reinterpret()))
        }

        override fun onMultiPolygon(multipolygon: MultiPolygon<Untyped>) {
            myTileGeometries.add(Geometry.of(multipolygon.reinterpret()))
        }
    }
}