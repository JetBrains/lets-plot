package jetbrains.gis.tileprotocol

import jetbrains.datalore.base.projectionGeometry.*
import jetbrains.gis.common.twkb.Parser
import jetbrains.gis.common.twkb.Twkb
import jetbrains.gis.tileprotocol.TileFeature.TileGeometry

class TileGeometryParser(geometryCollection: GeometryCollection) {
    private val myGeometryConsumer: MyGeometryConsumer
    private val myParser: Parser

    val geometries: List<TileGeometry>
        get() = myGeometryConsumer.tileGeometries

    init {
        myGeometryConsumer = MyGeometryConsumer()
        myParser = Twkb.parser(geometryCollection.asTwkb(), myGeometryConsumer)
    }

    fun resume(): Boolean {
        return myParser.next()
    }

    private class MyGeometryConsumer : Twkb.GeometryConsumer {
        private val myTileGeometries = ArrayList<TileGeometry>()

        val tileGeometries: List<TileGeometry>
            get() = myTileGeometries

        override fun onPoint(point: Point) {
            myTileGeometries.add(TileGeometry.createMultiPoint(MultiPoint(listOf(point))))
        }

        override fun onLineString(lineString: LineString) {
            myTileGeometries.add(
                TileGeometry.createMultiLineString(
                    MultiLineString(listOf(lineString))
                )
            )
        }

        override fun onPolygon(polygon: Polygon) {
            myTileGeometries.add(TileGeometry.createMultiPolygon(MultiPolygon(listOf(polygon))))
        }

        override fun onMultiPoint(multiPoint: MultiPoint, idList: List<Int>) {
            if (idList.isEmpty()) {
                myTileGeometries.add(TileGeometry.createMultiPoint(multiPoint))
            } else {
                multiPoint.forEach(this::onPoint)
            }
        }

        override fun onMultiLineString(multiLineString: MultiLineString, idList: List<Int>) {
            if (idList.isEmpty()) {
                myTileGeometries.add(TileGeometry.createMultiLineString(multiLineString))
            } else {
                multiLineString.forEach(this::onLineString)
            }
        }

        override fun onMultiPolygon(multipolygon: MultiPolygon, idList: List<Int>) {
            if (idList.isEmpty()) {
                myTileGeometries.add(TileGeometry.createMultiPolygon(multipolygon))
            } else {
                multipolygon.forEach(this::onPolygon)
            }
        }
    }
}