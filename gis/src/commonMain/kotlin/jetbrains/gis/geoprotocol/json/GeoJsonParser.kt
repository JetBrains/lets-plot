package jetbrains.gis.protocol.json

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.json.Json
import jetbrains.datalore.base.json.JsonArray
import jetbrains.datalore.base.json.JsonObject
import jetbrains.datalore.base.projectionGeometry.Multipolygon
import jetbrains.datalore.base.projectionGeometry.Polygon
import jetbrains.datalore.base.projectionGeometry.Ring
import jetbrains.gis.common.json.JsonUtils.*
import java.util.function.Function

internal class GeoJsonParser private constructor() {

    private fun doParsing(data: String): Multipolygon {
        val geometry = Json.parse(data) as JsonObject
        val type = readString(geometry, GEOMETRY_TYPE)
        val coordinates = getArray(geometry, GEOMETRY_COORDINATES)

        if (type == GEOMETRY_MULTIPOLYGON) {
            return parseMultiPolygon(coordinates)
        } else if (type == GEOMETRY_POLYGON) {
            return Multipolygon.create(parsePolygon(coordinates))
        }
        return Multipolygon.create()
    }

    private fun parseMultiPolygon(jsonMultiPolygon: JsonArray): Multipolygon {
        return Multipolygon(parseJsonArrayOfArray(jsonMultiPolygon, Function<JsonArray, T> { this.parsePolygon(it) }))
    }

    private fun parsePolygon(jsonPolygon: JsonArray): Polygon {
        return Polygon(parseJsonArrayOfArray(jsonPolygon, Function<JsonArray, T> { this.parseRing(it) }))
    }

    private fun parseRing(jsonRing: JsonArray): Ring {
        return Ring(parseJsonArrayOfArray(jsonRing, Function<JsonArray, T> { this.parsePoint(it) }))
    }

    private fun parsePoint(jsonPoint: JsonArray): DoubleVector {
        return DoubleVector(
            readDouble(jsonPoint, GEOMETRY_LON_INDEX),
            readDouble(jsonPoint, GEOMETRY_LAT_INDEX)
        )
    }

    private fun <T> parseJsonArrayOfArray(jsonArray: JsonArray, converter: Function<JsonArray, T>): List<T> {
        return parseJsonArray(jsonArray) { jsonValue -> converter.apply(jsonValue as JsonArray) }
    }

    companion object {
        private val GEOMETRY_MULTIPOLYGON = "MultiPolygon"
        private val GEOMETRY_POLYGON = "Polygon"
        private val GEOMETRY_TYPE = "type"
        private val GEOMETRY_COORDINATES = "coordinates"
        private val GEOMETRY_LON_INDEX = 0
        private val GEOMETRY_LAT_INDEX = 1

        fun parse(data: String): Multipolygon {
            return GeoJsonParser().doParsing(data)
        }
    }
}
