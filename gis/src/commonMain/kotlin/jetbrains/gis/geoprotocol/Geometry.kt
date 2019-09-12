package jetbrains.gis.geoprotocol

import jetbrains.datalore.base.projectionGeometry.Generic
import jetbrains.datalore.base.projectionGeometry.Typed

interface TypedGeometry<ProjT> {

    fun asMultipolygon(): Typed.MultiPolygon<ProjT>

    companion object {
        fun <ProjT> create(points: Typed.MultiPolygon<ProjT>): TypedGeometry<ProjT> {
            return object : TypedGeometry<ProjT> {
                override fun asMultipolygon(): Typed.MultiPolygon<ProjT> {
                    return points
                }
            }
        }
    }
}

typealias Geometry = TypedGeometry<Generic>