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

    private class MyGeometryConsumer : SimpleFeature.GeometryConsumer<Generic> {
        private val myTileGeometries = ArrayList<Geometry<LonLat>>()

        val tileGeometries: List<Geometry<LonLat>>
            get() = myTileGeometries

        override fun onPoint(point: Vec<Generic>) {
            myTileGeometries.add(
                Geometry.createMultiPoint(
                MultiPoint(
                    listOf(point.reinterpret())
                )
            ))
        }

        override fun onLineString(lineString: LineString<Generic>) {
            myTileGeometries.add(
                Geometry.createMultiLineString(
                    MultiLineString(listOf(lineString.reinterpret()))
                )
            )
        }

        override fun onPolygon(polygon: Polygon<Generic>) {
            myTileGeometries.add(
                Geometry.createMultiPolygon(
                MultiPolygon(
                    listOf(polygon.reinterpret())
                )
            ))
        }

        override fun onMultiPoint(multiPoint: MultiPoint<Generic>) {
            myTileGeometries.add(Geometry.createMultiPoint(multiPoint.reinterpret()))
        }

        override fun onMultiLineString(multiLineString: MultiLineString<Generic>) {
            myTileGeometries.add(Geometry.createMultiLineString(multiLineString.reinterpret()))
        }

        override fun onMultiPolygon(multipolygon: MultiPolygon<Generic>) {
            myTileGeometries.add(Geometry.createMultiPolygon(multipolygon.reinterpret()))
        }
    }
}