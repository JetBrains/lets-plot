/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.gis.tileprotocol

import jetbrains.datalore.base.projectionGeometry.*
import jetbrains.datalore.base.spatial.LonLat

import jetbrains.gis.common.twkb.Parser
import jetbrains.gis.common.twkb.Twkb


class TileGeometryParser(geometryCollection: GeometryCollection) {
    private val myGeometryConsumer: MyGeometryConsumer
    private val myParser: Parser

    val geometries: List<Geometry<LonLat>>
        get() = myGeometryConsumer.tileGeometries

    init {
        myGeometryConsumer = MyGeometryConsumer()
        myParser = Twkb.parser(geometryCollection.asTwkb(), myGeometryConsumer)
    }

    fun resume(): Boolean {
        return myParser.next()
    }

    private class MyGeometryConsumer : Twkb.GeometryConsumer {
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

        override fun onMultiPoint(multiPoint: MultiPoint<Generic>, idList: List<Int>) {
            if (idList.isEmpty()) {
                myTileGeometries.add(Geometry.createMultiPoint(multiPoint.reinterpret()))
            } else {
                multiPoint.forEach(this::onPoint)
            }
        }

        override fun onMultiLineString(multiLineString: MultiLineString<Generic>, idList: List<Int>) {
            if (idList.isEmpty()) {
                myTileGeometries.add(Geometry.createMultiLineString(multiLineString.reinterpret()))
            } else {
                multiLineString.forEach(this::onLineString)
            }
        }

        override fun onMultiPolygon(multipolygon: MultiPolygon<Generic>, idList: List<Int>) {
            if (idList.isEmpty()) {
                myTileGeometries.add(Geometry.createMultiPolygon(multipolygon.reinterpret()))
            } else {
                multipolygon.forEach(this::onPolygon)
            }
        }
    }
}