package jetbrains.gis.tileprotocol

import jetbrains.datalore.base.projectionGeometry.*

import jetbrains.gis.common.twkb.Parser
import jetbrains.gis.common.twkb.Twkb


class TileGeometryParser(geometryCollection: GeometryCollection) {
    private val myGeometryConsumer: MyGeometryConsumer
    private val myParser: Parser

    val geometries: List<Typed.TileGeometry<LonLat>>
        get() = myGeometryConsumer.tileGeometries

    init {
        myGeometryConsumer = MyGeometryConsumer()
        myParser = Twkb.parser(geometryCollection.asTwkb(), myGeometryConsumer)
    }

    fun resume(): Boolean {
        return myParser.next()
    }

    private class MyGeometryConsumer : Twkb.GeometryConsumer {
        private val myTileGeometries = ArrayList<Typed.TileGeometry<LonLat>>()

        val tileGeometries: List<Typed.TileGeometry<LonLat>>
            get() = myTileGeometries

        override fun onPoint(point: Point) {
            myTileGeometries.add(Typed.TileGeometry.createMultiPoint(Typed.MultiPoint(listOf(point.reinterpret()))))
        }

        override fun onLineString(lineString: LineString) {
            myTileGeometries.add(
                Typed.TileGeometry.createMultiLineString(
                    Typed.MultiLineString(listOf(lineString.reinterpret()))
                )
            )
        }

        override fun onPolygon(polygon: Polygon) {
            myTileGeometries.add(Typed.TileGeometry.createMultiPolygon(MultiPolygon(listOf(polygon))))
        }

        override fun onMultiPoint(multiPoint: MultiPoint, idList: List<Int>) {
            if (idList.isEmpty()) {
                myTileGeometries.add(Typed.TileGeometry.createMultiPoint(multiPoint))
            } else {
                multiPoint.forEach(this::onPoint)
            }
        }

        override fun onMultiLineString(multiLineString: MultiLineString, idList: List<Int>) {
            if (idList.isEmpty()) {
                myTileGeometries.add(Typed.TileGeometry.createMultiLineString(multiLineString))
            } else {
                multiLineString.forEach(this::onLineString)
            }
        }

        override fun onMultiPolygon(multipolygon: MultiPolygon, idList: List<Int>) {
            if (idList.isEmpty()) {
                myTileGeometries.add(Typed.TileGeometry.createMultiPolygon(multipolygon))
            } else {
                multipolygon.forEach(this::onPolygon)
            }
        }
    }
}