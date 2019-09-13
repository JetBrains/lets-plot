package jetbrains.livemap.projections

import jetbrains.datalore.base.projectionGeometry.GeoUtils.limitLat
import jetbrains.datalore.base.projectionGeometry.GeoUtils.limitLon
import jetbrains.datalore.base.projectionGeometry.LonLatRectangle
import jetbrains.datalore.base.projectionGeometry.MercatorUtils
import jetbrains.datalore.base.projectionGeometry.MercatorUtils.VALID_LATITUDE_RANGE
import jetbrains.datalore.base.projectionGeometry.MercatorUtils.VALID_LONGITUDE_RANGE
import jetbrains.datalore.base.projectionGeometry.Point
import jetbrains.datalore.base.projectionGeometry.newSpanRectangle

internal class MercatorProjection : GeoProjection {

    override fun project(v: LonLatPoint) =
        Point(
            MercatorUtils.getMercatorX(limitLon(v.x)),
            MercatorUtils.getMercatorY(limitLat(v.y))
        )

    override fun invert(v: Point) =
        LonLatPoint(
            limitLon(MercatorUtils.getLongitude(v.x)),
            limitLat(MercatorUtils.getLatitude(v.y))
        )

    override fun validRect(): LonLatRectangle = VALID_RECTANGLE

    companion object {
        private val VALID_RECTANGLE = newSpanRectangle(
            LonLatPoint(VALID_LONGITUDE_RANGE.lowerEndpoint(), VALID_LATITUDE_RANGE.lowerEndpoint()),
            LonLatPoint(VALID_LONGITUDE_RANGE.upperEndpoint(), VALID_LATITUDE_RANGE.upperEndpoint())
        ) as LonLatRectangle
    }
}