/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.gis.tileprotocol

import jetbrains.datalore.base.projectionGeometry.*

import jetbrains.gis.common.twkb.Parser
import jetbrains.gis.common.twkb.Twkb


class TileGeometryParser(geometryCollection: GeometryCollection) {
    private val myGeometryConsumer: MyGeometryConsumer
    private val myParser: Parser

    val geometries: List<TileGeometry<LonLat>>
        get() = myGeometryConsumer.tileGeometries

    init {
        myGeometryConsumer = MyGeometryConsumer()
        myParser = Twkb.parser(geometryCollection.asTwkb(), myGeometryConsumer)
    }

    fun resume(): Boolean {
        return myParser.next()
    }

    private class MyGeometryConsumer : Twkb.GeometryConsumer {
        private val myTileGeometries = ArrayList<TileGeometry<LonLat>>()

        val tileGeometries: List<TileGeometry<LonLat>>
            get() = myTileGeometries

        override fun onPoint(point: Point) {
            myTileGeometries.add(
                TileGeometry.createMultiPoint(
                MultiPoint(
                    listOf(point.reinterpret())
                )
            ))
        }

        override fun onLineString(lineString: LineString<Generic>) {
            myTileGeometries.add(
                TileGeometry.createMultiLineString(
                    MultiLineString(listOf(lineString.reinterpret()))
                )
            )
        }

        override fun onPolygon(polygon: Polygon<Generic>) {
            myTileGeometries.add(
                TileGeometry.createMultiPolygon(
                MultiPolygon(
                    listOf(polygon.reinterpret())
                )
            ))
        }

        override fun onMultiPoint(multiPoint: MultiPoint<Generic>, idList: List<Int>) {
            if (idList.isEmpty()) {
                myTileGeometries.add(TileGeometry.createMultiPoint(multiPoint.reinterpret()))
            } else {
                multiPoint.forEach(this::onPoint)
            }
        }

        override fun onMultiLineString(multiLineString: MultiLineString<Generic>, idList: List<Int>) {
            if (idList.isEmpty()) {
                myTileGeometries.add(TileGeometry.createMultiLineString(multiLineString.reinterpret()))
            } else {
                multiLineString.forEach(this::onLineString)
            }
        }

        override fun onMultiPolygon(multipolygon: MultiPolygon<Generic>, idList: List<Int>) {
            if (idList.isEmpty()) {
                myTileGeometries.add(TileGeometry.createMultiPolygon(multipolygon.reinterpret()))
            } else {
                multipolygon.forEach(this::onPolygon)
            }
        }
    }
}