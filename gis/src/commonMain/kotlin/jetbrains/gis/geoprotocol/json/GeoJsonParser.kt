package jetbrains.gis.geoprotocol.json

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.json.JsonSupport
import jetbrains.datalore.base.projectionGeometry.MultiPolygon
import jetbrains.datalore.base.projectionGeometry.Polygon
import jetbrains.datalore.base.projectionGeometry.Ring
import jetbrains.gis.common.json.FluentJsonArray
import jetbrains.gis.common.json.FluentJsonObject
import jetbrains.gis.common.json.JsonArray

internal class GeoJsonParser private constructor() {

    private fun doParsing(data: String): MultiPolygon {
        val obj = HashMap<String, Any?>(JsonSupport.parseJson(data))
        val geometry = FluentJsonObject(obj);
        val type = geometry.getString(GEOMETRY_TYPE)
        val coordinates = geometry.getArray(GEOMETRY_COORDINATES)

        if (type == GEOMETRY_MULTIPOLYGON) {
            return parseMultiPolygon(coordinates)
        } else if (type == GEOMETRY_POLYGON) {
            return MultiPolygon.create(parsePolygon(coordinates))
        }
        return MultiPolygon.create()
    }

    private fun parseMultiPolygon(jsonMultiPolygon: FluentJsonArray): MultiPolygon {
        return MultiPolygon(parseJsonArrayOfArray(jsonMultiPolygon) { this.parsePolygon(FluentJsonArray(it)) })
    }

    private fun parsePolygon(jsonPolygon: FluentJsonArray): Polygon {
        return Polygon(parseJsonArrayOfArray(jsonPolygon) {parseRing(FluentJsonArray(it))})
    }

    private fun parseRing(jsonRing: FluentJsonArray): Ring {
        return Ring(parseJsonArrayOfArray(jsonRing) { this.parsePoint(FluentJsonArray(it)) })
    }

    private fun parsePoint(jsonPoint: FluentJsonArray): DoubleVector {
        return DoubleVector(
            jsonPoint.getDouble(GEOMETRY_LON_INDEX),
            jsonPoint.getDouble(GEOMETRY_LAT_INDEX)
        )
    }

    private fun <T> parseJsonArrayOfArray(jsonArray: FluentJsonArray, converter: (JsonArray) -> T): List<T> {
        return jsonArray.stream().map { jsonValue -> converter(jsonValue as JsonArray) }.toList()
    }

    companion object {
        private const val GEOMETRY_MULTIPOLYGON = "MultiPolygon"
        private const val GEOMETRY_POLYGON = "Polygon"
        private const val GEOMETRY_TYPE = "type"
        private const val GEOMETRY_COORDINATES = "coordinates"
        private const val GEOMETRY_LON_INDEX = 0
        private const val GEOMETRY_LAT_INDEX = 1

        fun parse(data: String): MultiPolygon {
            return GeoJsonParser().doParsing(data)
        }
    }
}
