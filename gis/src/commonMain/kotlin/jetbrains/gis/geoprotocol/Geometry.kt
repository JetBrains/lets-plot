package jetbrains.gis.geoprotocol

import jetbrains.datalore.base.projectionGeometry.MultiPolygon

interface Geometry {

    fun asMultipolygon(): MultiPolygon

    companion object {
        fun create(points: MultiPolygon): Geometry {
            return object : Geometry {
                override fun asMultipolygon(): MultiPolygon {
                    return points
                }
            }
        }
    }
}
