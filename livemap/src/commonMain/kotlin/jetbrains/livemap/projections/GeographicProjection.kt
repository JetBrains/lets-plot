package jetbrains.livemap.projections

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.projectionGeometry.GeoUtils.limitLat
import jetbrains.datalore.base.projectionGeometry.GeoUtils.limitLon

internal class GeographicProjection : GeoProjection {

    override fun project(v: DoubleVector): DoubleVector {
        return DoubleVector(limitLon(v.x), limitLat(v.y))
    }

    override fun invert(v: DoubleVector): DoubleVector {
        return DoubleVector(limitLon(v.x), limitLat(v.y))
    }

    override fun validRect(): DoubleRectangle {
        return VALID_RECTANGLE
    }

    companion object {
        private val VALID_RECTANGLE = DoubleRectangle.span(
            DoubleVector(-180.0, -90.0),
            DoubleVector(+180.0, +90.0)
        )
    }
}