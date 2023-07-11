/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.spatial

import org.jetbrains.letsPlot.commons.intern.json.FluentArray
import org.jetbrains.letsPlot.commons.intern.json.FluentObject
import org.jetbrains.letsPlot.commons.intern.json.JsonSupport
import org.jetbrains.letsPlot.commons.intern.typedGeometry.*
import org.jetbrains.letsPlot.commons.intern.typedGeometry.*

object GeoJson {
    private const val LON_INDEX = 0
    private const val LAT_INDEX = 1

    fun <T> parse(geoJson: String, handler: SimpleFeature.Consumer<T>.() -> Unit) {
        val geoObj = FluentObject(JsonSupport.parseJson(geoJson))
        val geometryConsumer = SimpleFeature.Consumer<T>().apply(handler)
        Parser<T>().parse(geoObj, geometryConsumer)
    }

    fun <T> parse(geoJson: String, consumer: SimpleFeature.GeometryConsumer<T>) {
        val geoObj = FluentObject(JsonSupport.parseJson(geoJson))
        Parser<T>().parse(geoObj, consumer)
    }

    private class Parser<T> {

        internal fun parse(obj: FluentObject, handler: SimpleFeature.GeometryConsumer<T>) {
            when (val type = obj.getString("type")) {
                "FeatureCollection" -> {
                    require(obj.contains("features")) { "GeoJson: Missing 'features' in 'FeatureCollection'" }

                    obj.getArray("features").fluentObjectStream()
                        .filter { it.getString("type") == "Feature" }
                        .map { it.getObject("geometry") }
                        .forEach { parse(it, handler) }
                }

                "GeometryCollection" -> {
                    require(obj.contains("geometries")) { "GeoJson: Missing 'geometries' in 'GeometryCollection'" }

                    obj.getArray("geometries").fluentObjectStream()
                        .forEach { parse(it, handler) }
                }

                else -> {
                    require(obj.contains("coordinates")) { "GeoJson: Missing 'coordinates' in $type" }

                    val coordinates = obj.getArray("coordinates")
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
        }

        private fun parsePoint(jsonPoint: FluentArray): Vec<T> {
            return explicitVec<T>(
                jsonPoint.getDouble(LON_INDEX),
                jsonPoint.getDouble(LAT_INDEX)
            )
        }

        private fun parseLineString(jsonLineString: FluentArray): LineString<T> {
            return jsonLineString.mapArray(this::parsePoint).let(::LineString)
        }

        private fun parseRing(jsonRing: FluentArray): Ring<T> {
            return jsonRing.mapArray(this::parsePoint).let(::Ring)
        }

        private fun parseMultiPoint(jsonMultiPoint: FluentArray): MultiPoint<T> {
            return jsonMultiPoint.mapArray(this::parsePoint).let(::MultiPoint)
        }

        private fun parsePolygon(jsonPolygon: FluentArray): Polygon<T> {
            return jsonPolygon.mapArray(this::parseRing).let(::Polygon)
        }

        private fun parseMultiLineString(jsonLineStrings: FluentArray): MultiLineString<T> {
            return jsonLineStrings.mapArray(this::parseLineString).let(::MultiLineString)
        }

        private fun parseMultiPolygon(jsonMultiPolygon: FluentArray): MultiPolygon<T> {
            return jsonMultiPolygon.mapArray(this::parsePolygon).let(::MultiPolygon)
        }

        private fun <T> FluentArray.mapArray(f: (FluentArray) -> T): List<T> {
            return this.stream().map { f(FluentArray(it as List<Any?>)) }.toList()
        }
    }
}
