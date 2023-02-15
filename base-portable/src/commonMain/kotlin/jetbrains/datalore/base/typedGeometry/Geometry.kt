/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.typedGeometry

enum class GeometryType {
    MULTI_POINT,
    MULTI_LINESTRING,
    MULTI_POLYGON;
}

class Geometry<TypeT> private constructor(
    val type: GeometryType,
    private val myMultiPoint: MultiPoint<TypeT>?,
    private val myMultiLineString: MultiLineString<TypeT>?,
    private val myMultiPolygon: MultiPolygon<TypeT>?
) {
    val multiPoint: MultiPoint<TypeT>
        get() = myMultiPoint ?: error("$type is not a MultiPoint")
    val multiLineString: MultiLineString<TypeT>
        get() = myMultiLineString ?: error("$type is not a MultiLineString")
    val multiPolygon: MultiPolygon<TypeT>
        get() = myMultiPolygon ?: error("$type is not a MultiPolygon")

    companion object {
        fun <TypeT> of(multiPoint: MultiPoint<TypeT>): Geometry<TypeT> {
            return Geometry(
                type = GeometryType.MULTI_POINT,
                myMultiPoint = multiPoint,
                myMultiLineString = null,
                myMultiPolygon = null
            )
        }

        fun <TypeT> of(multiLineString: MultiLineString<TypeT>): Geometry<TypeT> {
            return Geometry(
                type = GeometryType.MULTI_LINESTRING,
                myMultiPoint = null,
                myMultiLineString = multiLineString,
                myMultiPolygon = null
            )
        }

        fun <TypeT> of(multiPolygon: MultiPolygon<TypeT>): Geometry<TypeT> {
            return Geometry(
                type = GeometryType.MULTI_POLYGON,
                myMultiPoint = null,
                myMultiLineString = null,
                myMultiPolygon = multiPolygon
            )
        }
    }
}