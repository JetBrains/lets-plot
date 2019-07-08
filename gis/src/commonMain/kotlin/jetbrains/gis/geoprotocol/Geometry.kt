package jetbrains.gis.geoprotocol

import jetbrains.datalore.base.projectionGeometry.Multipolygon

interface Geometry {

    fun asMultipolygon(): Multipolygon

    companion object {
        fun create(points: Multipolygon): Geometry {
            return object : Geometry {
                override fun asMultipolygon(): Multipolygon {
                    return points
                }
            }
        }
    }
}
