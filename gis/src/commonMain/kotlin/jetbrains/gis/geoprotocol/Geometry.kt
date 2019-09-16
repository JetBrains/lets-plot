package jetbrains.gis.geoprotocol

import jetbrains.datalore.base.projectionGeometry.Generic
import jetbrains.datalore.base.projectionGeometry.MultiPolygon

interface TypedGeometry<TypeT> {

    fun asMultipolygon(): MultiPolygon<TypeT>

    companion object {
        fun <TypeT> create(points: MultiPolygon<TypeT>): TypedGeometry<TypeT> {
            return object : TypedGeometry<TypeT> {
                override fun asMultipolygon(): MultiPolygon<TypeT> {
                    return points
                }
            }
        }
    }
}

typealias Geometry = TypedGeometry<Generic>