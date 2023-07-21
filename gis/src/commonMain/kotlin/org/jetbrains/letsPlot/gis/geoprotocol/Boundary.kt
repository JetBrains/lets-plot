/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.gis.geoprotocol

import org.jetbrains.letsPlot.commons.encoding.Base64
import org.jetbrains.letsPlot.commons.intern.spatial.GeoJson
import org.jetbrains.letsPlot.commons.intern.spatial.SimpleFeature
import org.jetbrains.letsPlot.commons.intern.typedGeometry.MultiPolygon
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Polygon
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Untyped
import org.jetbrains.letsPlot.gis.common.twkb.Twkb

interface Boundary<TypeT> {

    fun asMultipolygon(): MultiPolygon<TypeT>

    companion object {
        fun <TypeT> create(points: MultiPolygon<TypeT>): Boundary<TypeT> {
            return object : Boundary<TypeT> {
                override fun asMultipolygon(): MultiPolygon<TypeT> {
                    return points
                }
            }
        }
    }
}

object Boundaries {

    fun fromTwkb(boundary: String): Boundary<Untyped> = TinyBoundary(boundary)
    fun fromGeoJson(boundary: String): Boundary<Untyped> = GeoJsonBoundary(boundary)

    internal fun getRawData(boundary: Boundary<Untyped>): String {
        return (boundary as StringBoundary).rawData
    }

    // Used internally by GIS server for optimization.
// Workflow:
// GIS server receives encoded geometries(TWKB+Base64 or GeoJson) from PostreSQL.
// GIS server doesn't use geometries, only forwards them to a client. So instead of decoding geometries
// to List<List<List<DoubleVector>>> and encoding it back to TWKB/GeoJson before sending to client we
// just keep encoded data with help of StringGeometry type.
// Only GeometryStorageClient(PostreSQL user) and JsonFormatters/JsonParsers(client/server communication)
// should know about this optimization.
    private abstract class StringBoundary internal constructor(
        internal val rawData: String
    ) : Boundary<Untyped> {
        private val myMultipolygon: MultiPolygon<Untyped> by lazy { parse(rawData) }

        override fun asMultipolygon(): MultiPolygon<Untyped> = myMultipolygon

        internal abstract fun parse(boundary: String): MultiPolygon<Untyped>

        override fun hashCode(): Int = rawData.hashCode()
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as StringBoundary

            if (rawData != other.rawData) return false

            return true
        }

    }

    private class TinyBoundary internal constructor(
        boundary: String
    ) : StringBoundary(boundary) {

        override fun parse(boundary: String): MultiPolygon<Untyped> {
            val polygons = ArrayList<Polygon<Untyped>>()

            Twkb.parse(Base64.decode(boundary), object : SimpleFeature.GeometryConsumer<Untyped> {
                override fun onPolygon(polygon: Polygon<Untyped>) {
                    polygons.add(polygon)
                }

                override fun onMultiPolygon(multipolygon: MultiPolygon<Untyped>) {
                    polygons.addAll(multipolygon)
                }
            }
            )

            return MultiPolygon(polygons)
        }
    }

    private class GeoJsonBoundary internal constructor(
        boundary: String
    ) : StringBoundary(boundary) {

        override fun parse(boundary: String): MultiPolygon<Untyped> {
            var boundaryPolygon: MultiPolygon<Untyped>? = null

            GeoJson.parse<Untyped>(boundary) {
                onPolygon = { require(boundaryPolygon == null); boundaryPolygon = MultiPolygon(listOf(it)) }
                onMultiPolygon = { require(boundaryPolygon == null); boundaryPolygon = it }
            }

            return boundaryPolygon ?: MultiPolygon(emptyList())
        }
    }
}
