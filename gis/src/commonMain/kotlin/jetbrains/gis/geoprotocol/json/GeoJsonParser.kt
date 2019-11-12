/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.gis.geoprotocol.json

import jetbrains.datalore.base.json.JsonSupport
import jetbrains.datalore.base.projectionGeometry.*
import jetbrains.gis.common.json.Arr
import jetbrains.gis.common.json.FluentArray
import jetbrains.gis.common.json.FluentObject

internal class GeoJsonParser private constructor() {

    private fun doParsing(data: String): MultiPolygon<Generic> {

        var original = JsonSupport.parseJson(data)
        val obj = HashMap<String, Any?>(original)
        val geometry = FluentObject(obj);
        val type = geometry.getString(GEOMETRY_TYPE)
        val coordinates = geometry.getArray(GEOMETRY_COORDINATES)

        if (type == GEOMETRY_MULTIPOLYGON) {
            return parseMultiPolygon(coordinates)
        } else if (type == GEOMETRY_POLYGON) {
            return MultiPolygon<Generic>(listOf(parsePolygon(coordinates)))
        }
        return MultiPolygon<Generic>(emptyList())
    }

    private fun parseMultiPolygon(jsonMultiPolygon: FluentArray): MultiPolygon<Generic> {
        return MultiPolygon<Generic>(parseJsonArrayOfArray(jsonMultiPolygon) {
            this.parsePolygon(
                FluentArray(it)
            )
        })
    }

    private fun parsePolygon(jsonPolygon: FluentArray): Polygon<Generic> {
        return Polygon<Generic>(parseJsonArrayOfArray(jsonPolygon) {
            parseRing(
                FluentArray(it)
            )
        })
    }

    private fun parseRing(jsonRing: FluentArray): Ring<Generic> {
        return Ring(parseJsonArrayOfArray(jsonRing) { this.parsePoint(FluentArray(it)) })
    }

    private fun parsePoint(jsonPoint: FluentArray): Vec<Generic> {
        return explicitVec<Generic>(
            jsonPoint.getDouble(GEOMETRY_LON_INDEX),
            jsonPoint.getDouble(GEOMETRY_LAT_INDEX)
        )
    }

    private fun <T> parseJsonArrayOfArray(jsonArray: FluentArray, converter: (Arr) -> T): List<T> {
        return jsonArray.stream().map { jsonValue -> converter(jsonValue as Arr) }.toList()
    }

    companion object {
        private const val GEOMETRY_MULTIPOLYGON = "MultiPolygon"
        private const val GEOMETRY_POLYGON = "Polygon"
        private const val GEOMETRY_TYPE = "type"
        private const val GEOMETRY_COORDINATES = "coordinates"
        private const val GEOMETRY_LON_INDEX = 0
        private const val GEOMETRY_LAT_INDEX = 1

        fun parse(data: String): MultiPolygon<Generic> {
            return GeoJsonParser().doParsing(data)
        }
    }
}
