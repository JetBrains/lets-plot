/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.projectionGeometry

enum class GeometryType {
    MULTI_POINT,
    MULTI_LINESTRING,
    MULTI_POLYGON;
}

class Geometry<TypeT> private constructor(
    val type: GeometryType,
    val multiPoint: MultiPoint<TypeT>?,
    val multiLineString: MultiLineString<TypeT>?,
    val multiPolygon: MultiPolygon<TypeT>?
) {
    companion object {
        fun <TypeT> createMultiPoint(multiPoint: MultiPoint<TypeT>): Geometry<TypeT> {
            return Geometry(
                GeometryType.MULTI_POINT,
                multiPoint,
                null,
                null
            )
        }

        fun <TypeT> createMultiLineString(multiLineString: MultiLineString<TypeT>): Geometry<TypeT> {
            return Geometry(
                GeometryType.MULTI_LINESTRING,
                null,
                multiLineString,
                null
            )
        }

        fun <TypeT> createMultiPolygon(multiPolygon: MultiPolygon<TypeT>): Geometry<TypeT> {
            return Geometry(
                GeometryType.MULTI_POLYGON,
                null,
                null,
                multiPolygon
            )
        }
    }
}