package jetbrains.livemap.projections

import jetbrains.datalore.base.projectionGeometry.GeoUtils.limitLat
import jetbrains.datalore.base.projectionGeometry.GeoUtils.limitLon
import jetbrains.datalore.base.projectionGeometry.LonLat
import jetbrains.datalore.base.projectionGeometry.Rect
import jetbrains.datalore.base.projectionGeometry.newSpanRectangle

internal class GeographicProjection : GeoProjection {

    override fun project(v: LonLatPoint): GeographicPoint = GeographicPoint(limitLon(v.x), limitLat(v.y))

    override fun invert(v: GeographicPoint): LonLatPoint = LonLatPoint(limitLon(v.x), limitLat(v.y))
    
    override fun validRect(): Rect<LonLat> = VALID_RECTANGLE

    companion object {
        private val VALID_RECTANGLE = newSpanRectangle(
            LonLatPoint(-180.0, -90.0),
            LonLatPoint(+180.0, +90.0)
        )
    }
}