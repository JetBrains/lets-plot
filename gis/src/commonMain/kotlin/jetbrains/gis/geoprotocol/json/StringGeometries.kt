package jetbrains.gis.protocol.json

import com.google.common.io.BaseEncoding
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.projectionGeometry.Multipolygon
import jetbrains.datalore.base.projectionGeometry.Polygon
import jetbrains.gis.common.twkb.TWKB
import jetbrains.gis.common.twkb.TWKB.GeometryConsumer
import jetbrains.gis.protocol.Geometry
import java.util.ArrayList
import java.util.Objects

object StringGeometries {
    fun fromTwkb(geometry: String): Geometry {
        return TinyGeometry(geometry)
    }

    fun fromGeoJson(geometry: String): Geometry {
        return GeoJsonGeometry(geometry)
    }

    internal fun getRawData(geometry: Geometry): String {
        return (geometry as StringGeometry).rawData
    }

    // Used internally by GIS server for optimization.
    // Workflow:
    // GIS server receives encoded geometries(TWKB+Base64 or GeoJson) from PostreSQL.
    // GIS server doesn't use geometries, only forwards them to client. So instead of decoding geometries
    // to List<List<List<DoubleVector>>> and encoding it back to TWKB/GeoJson before sending to client we
    // just keep encoded data with help of StringGeometry type.
    // Only GeometryStorageClient(PostreSQL user) and JsonFormatters/JsonParsers(client/server communication)
    // should know about this optimization.
    private abstract class StringGeometry internal constructor(internal val rawData: String) : Geometry {
        private var myMultipolygon: Multipolygon? = null

        fun asMultipolygon(): Multipolygon? {
            if (myMultipolygon == null) {
                myMultipolygon = parse(rawData)
            }
            return myMultipolygon
        }

        internal abstract fun parse(geometry: String): Multipolygon

        override fun equals(o: Any?): Boolean {
            if (this === o) return true
            if (o == null || javaClass != o.javaClass) return false
            val that = o as StringGeometry?
            return rawData == that!!.rawData
        }

        override fun hashCode(): Int {
            return Objects.hash(rawData)
        }
    }

    private class TinyGeometry internal constructor(geometry: String) : StringGeometry(geometry) {

        override fun parse(geometry: String): Multipolygon {
            val polygons = ArrayList<Polygon>()
            val data = BaseEncoding
                .base64()
                .withSeparator("\n", 76) // Fix for PostgreSQL: it puts \n every 76 chars
                .decode(geometry)

            TWKB.parse(data, object : GeometryConsumer() {
                fun onPoint(p: DoubleVector) {
                    throw IllegalArgumentException("Points are not supported")
                }

                fun onLineString(lineString: LineString) {
                    throw IllegalArgumentException("LineString are not supported")
                }

                fun onPolygon(polygon: Polygon) {
                    polygons.add(polygon)
                }

                fun onMultiPoint(multiPoint: MultiPoint, idList: List<Int>) {
                    throw IllegalArgumentException("MultiPoint are not supported")
                }

                fun onMultiLineString(multiLineString: MultiLineString, idList: List<Int>) {
                    throw IllegalArgumentException("MultiLineString are not supported")
                }

                fun onMultipolygon(multipolygon: Multipolygon, idList: List<Int>) {
                    polygons.addAll(multipolygon)
                }
            })

            return Multipolygon(polygons)
        }
    }

    private class GeoJsonGeometry internal constructor(private val myGeometry: String) : StringGeometry(myGeometry) {

        override fun parse(geometry: String): Multipolygon {
            return GeoJsonParser.parse(myGeometry)
        }
    }
}
