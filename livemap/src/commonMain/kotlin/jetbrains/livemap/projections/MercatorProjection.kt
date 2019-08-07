package jetbrains.livemap.projections

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.projectionGeometry.GeoUtils.limitLat
import jetbrains.datalore.base.projectionGeometry.GeoUtils.limitLon
import jetbrains.datalore.base.projectionGeometry.MercatorUtils
import jetbrains.datalore.base.projectionGeometry.MercatorUtils.VALID_LATITUDE_RANGE
import jetbrains.datalore.base.projectionGeometry.MercatorUtils.VALID_LONGITUDE_RANGE

internal class MercatorProjection : GeoProjection {

    override fun project(v: DoubleVector) =
        DoubleVector(
            MercatorUtils.getMercatorX(limitLon(v.x)),
            MercatorUtils.getMercatorY(limitLat(v.y))
        )

    override fun invert(v: DoubleVector) =
        DoubleVector(
            limitLon(MercatorUtils.getLongitude(v.x)),
            limitLat(MercatorUtils.getLatitude(v.y))
        )

    override fun validRect(): DoubleRectangle = VALID_RECTANGLE

    companion object {
        private val VALID_RECTANGLE = DoubleRectangle.span(
            DoubleVector(VALID_LONGITUDE_RANGE.lowerEndpoint(), VALID_LATITUDE_RANGE.lowerEndpoint()),
            DoubleVector(VALID_LONGITUDE_RANGE.upperEndpoint(), VALID_LATITUDE_RANGE.upperEndpoint())
        )
    }
}