package jetbrains.livemap.projections

import jetbrains.datalore.base.projectionGeometry.GeoUtils.limitLat
import jetbrains.datalore.base.projectionGeometry.GeoUtils.limitLon
import jetbrains.datalore.base.projectionGeometry.LonLat
import jetbrains.datalore.base.projectionGeometry.MercatorUtils
import jetbrains.datalore.base.projectionGeometry.MercatorUtils.VALID_LATITUDE_RANGE
import jetbrains.datalore.base.projectionGeometry.MercatorUtils.VALID_LONGITUDE_RANGE
import jetbrains.datalore.base.projectionGeometry.Rect
import jetbrains.datalore.base.projectionGeometry.newSpanRectangle

internal class MercatorProjection : GeoProjection {

    override fun project(v: LonLatPoint): GeographicPoint =
        GeographicPoint(
            MercatorUtils.getMercatorX(limitLon(v.x)),
            MercatorUtils.getMercatorY(limitLat(v.y))
        )

    override fun invert(v: GeographicPoint): LonLatPoint =
        LonLatPoint(
            limitLon(MercatorUtils.getLongitude(v.x)),
            limitLat(MercatorUtils.getLatitude(v.y))
        )

    override fun validRect(): Rect<LonLat> = VALID_RECTANGLE

    companion object {
        private val VALID_RECTANGLE = newSpanRectangle(
            LonLatPoint(VALID_LONGITUDE_RANGE.lowerEndpoint(), VALID_LATITUDE_RANGE.lowerEndpoint()),
            LonLatPoint(VALID_LONGITUDE_RANGE.upperEndpoint(), VALID_LATITUDE_RANGE.upperEndpoint())
        ) as Rect<LonLat>
    }
}