/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.spatial

import jetbrains.datalore.base.json.FluentArray
import jetbrains.datalore.base.json.FluentObject
import jetbrains.datalore.base.json.JsonSupport
import jetbrains.datalore.base.typedGeometry.*

object GeoJson {
    private const val GEOMETRY_TYPE = "type"
    private const val GEOMETRY_COORDINATES = "coordinates"
    private const val GEOMETRY_LON_INDEX = 0
    private const val GEOMETRY_LAT_INDEX = 1

    fun parse(geoJson: String, handler: SimpleFeature.Consumer<LonLat>.() -> Unit) {
        val geoObj = FluentObject(JsonSupport.parseJson(geoJson))
        val geometryConsumer = SimpleFeature.Consumer<LonLat>().apply(handler)
        parse(geoObj, geometryConsumer)
    }

    private fun parse(obj: FluentObject, handler: SimpleFeature.GeometryConsumer) {
        val type = obj.getString(GEOMETRY_TYPE)

        if (type == "FeatureCollection") {
            obj.getArray("features").fluentObjectStream()
                .filter { it.getString("type") == "Feature" }
                .map { it.getObject("geometry") }
                .forEach { parse(it, handler) }
        } else {
            val coordinates = obj.getArray(GEOMETRY_COORDINATES)
            when (type) {
                "Point" -> parsePoint(coordinates).let(handler::onPoint)
                "LineString" -> parseLineString(coordinates).let(handler::onLineString)
                "Polygon" -> parsePolygon(coordinates).let(handler::onPolygon)
                "MultiPoint" -> parseMultiPoint(coordinates).let(handler::onMultiPoint)
                "MultiLineString" -> parseMultiLineString(coordinates).let(handler::onMultiLineString)
                "MultiPolygon" -> parseMultiPolygon(coordinates).let(handler::onMultiPolygon)
                else -> error("Not support GeoJson type: $type")
            }
        }
    }

    private fun parsePoint(jsonPoint: FluentArray): Vec<Generic> {
        return explicitVec<Generic>(
            jsonPoint.getDouble(GEOMETRY_LON_INDEX),
            jsonPoint.getDouble(GEOMETRY_LAT_INDEX)
        )
    }

    private fun parseLineString(jsonLineString: FluentArray): LineString<Generic> {
        return jsonLineString.mapArray(this::parsePoint).let(::LineString)
    }

    private fun parseRing(jsonRing: FluentArray): Ring<Generic> {
        return jsonRing.mapArray(this::parsePoint).let(::Ring)
    }

    private fun parseMultiPoint(jsonMultiPoint: FluentArray): MultiPoint<Generic> {
        return jsonMultiPoint.mapArray(this::parsePoint).let(::MultiPoint)
    }

    private fun parsePolygon(jsonPolygon: FluentArray): Polygon<Generic> {
        return jsonPolygon.mapArray(this::parseRing).let(::Polygon)
    }

    private fun parseMultiLineString(jsonLineStrings: FluentArray): MultiLineString<Generic> {
        return jsonLineStrings.mapArray(this::parseLineString).let(::MultiLineString)
    }

    private fun parseMultiPolygon(jsonMultiPolygon: FluentArray): MultiPolygon<Generic> {
        return jsonMultiPolygon.mapArray(this::parsePolygon).let(::MultiPolygon)
    }

    private fun <T> FluentArray.mapArray(f: (FluentArray) -> T): List<T> {
        return this.stream().map { f(FluentArray(it as List<Any?>)) }.toList()
    }
}
