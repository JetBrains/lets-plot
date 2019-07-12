package jetbrains.gis.geoprotocol.json

import jetbrains.datalore.base.encoding.Base64
import jetbrains.datalore.base.projectionGeometry.MultiPolygon
import jetbrains.datalore.base.projectionGeometry.Polygon
import jetbrains.gis.common.twkb.Twkb
import jetbrains.gis.geoprotocol.Geometry

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
        private val myMultipolygon: MultiPolygon by lazy { parse(rawData) }

        override fun asMultipolygon(): MultiPolygon {
            return myMultipolygon
        }

        internal abstract fun parse(geometry: String): MultiPolygon
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as StringGeometry

            if (rawData != other.rawData) return false

            return true
        }

        override fun hashCode(): Int {
            return rawData.hashCode()
        }
    }

    private class TinyGeometry internal constructor(geometry: String) : StringGeometry(geometry) {

        override fun parse(geometry: String): MultiPolygon {
            val polygons = ArrayList<Polygon>()

            //.withSeparator("\n", 76) // Fix for PostgreSQL: it puts \n every 76 chars
            val data = Base64.decode(geometry);
            Twkb.parse(data, object : Twkb.GeometryConsumer {
                override fun onPolygon(polygon: Polygon) {
                    polygons.add(polygon)
                }
                override fun onMultiPolygon(multipolygon: MultiPolygon, idList: List<Int>) {
                    polygons.addAll(multipolygon)
                }
            })

            return MultiPolygon(polygons)
        }
    }

    private class GeoJsonGeometry internal constructor(private val myGeometry: String) : StringGeometry(myGeometry) {

        override fun parse(geometry: String): MultiPolygon {
            return GeoJsonParser.parse(myGeometry)
        }
    }
}
