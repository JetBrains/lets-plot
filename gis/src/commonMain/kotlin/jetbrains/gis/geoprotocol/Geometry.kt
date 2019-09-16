package jetbrains.gis.geoprotocol

import jetbrains.datalore.base.projectionGeometry.Generic
import jetbrains.datalore.base.projectionGeometry.Typed

interface TypedGeometry<TypeT> {

    fun asMultipolygon(): Typed.MultiPolygon<TypeT>

    companion object {
        fun <TypeT> create(points: Typed.MultiPolygon<TypeT>): TypedGeometry<TypeT> {
            return object : TypedGeometry<TypeT> {
                override fun asMultipolygon(): Typed.MultiPolygon<TypeT> {
                    return points
                }
            }
        }
    }
}

typealias Geometry = TypedGeometry<Generic>