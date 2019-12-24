/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.gis.geoprotocol.json

import jetbrains.datalore.base.encoding.Base64
import jetbrains.datalore.base.spatial.SimpleFeature
import jetbrains.datalore.base.typedGeometry.Generic
import jetbrains.datalore.base.typedGeometry.MultiPolygon
import jetbrains.datalore.base.typedGeometry.Polygon
import jetbrains.gis.common.twkb.Twkb
import jetbrains.gis.geoprotocol.Boundary

object StringGeometries {
    fun fromTwkb(geometry: String): Boundary<Generic> = TinyBoundary(geometry)
    fun fromGeoJson(geometry: String): Boundary<Generic> = GeoJsonBoundary(geometry)

    internal fun getRawData(geometry: Boundary<Generic>): String {
        return (geometry as StringBoundary).rawData
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
    ) : Boundary<Generic> {
        private val myMultipolygon: MultiPolygon<Generic> by lazy { parse(rawData) }

        override fun asMultipolygon(): MultiPolygon<Generic> = myMultipolygon

        internal abstract fun parse(geometry: String): MultiPolygon<Generic>

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
        geometry: String
    ) : StringBoundary(geometry) {

        override fun parse(geometry: String): MultiPolygon<Generic> {
            val polygons = ArrayList<Polygon<Generic>>()

            Twkb.parse(Base64.decode(geometry), object : SimpleFeature.GeometryConsumer {
                override fun onPolygon(polygon: Polygon<Generic>) {
                    polygons.add(polygon)
                }
                override fun onMultiPolygon(multipolygon: MultiPolygon<Generic>) {
                    polygons.addAll(multipolygon)
                }
            }
            )

            return MultiPolygon(polygons)
        }
    }

    private class GeoJsonBoundary internal constructor(
        private val myGeometry: String
    ) : StringBoundary(myGeometry) {

        override fun parse(geometry: String): MultiPolygon<Generic> {
            return GeoJsonParser.parse(myGeometry)
        }
    }
}
