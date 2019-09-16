package jetbrains.livemap.projections

import jetbrains.datalore.base.projectionGeometry.GeoUtils.toDegrees
import jetbrains.datalore.base.projectionGeometry.GeoUtils.toRadians
import jetbrains.datalore.base.projectionGeometry.LonLat
import jetbrains.datalore.base.projectionGeometry.Rect
import jetbrains.datalore.base.projectionGeometry.newSpanRectangle
import jetbrains.livemap.projections.ProjectionUtil.safePoint
import kotlin.math.*

internal abstract class AzimuthalBaseProjection : GeoProjection {

    override fun validRect(): Rect<LonLat> = VALID_RECTANGLE

    override fun project(v: LonLatPoint): GeographicPoint {

        val x = toRadians(v.x)
        val y = toRadians(v.y)
        val cx = cos(x)
        val cy = cos(y)
        val k = scale(cx * cy)

        val px = k * cy * sin(x)
        val py = k * sin(y)

        return safePoint(px, py)
    }

    override fun invert(v: GeographicPoint): LonLatPoint {

        val x = v.x
        val y = v.y
        val z = sqrt(x * x + y * y)
        val c = angle(z)
        val sc = sin(c)
        val cc = cos(c)

        val ix = toDegrees(atan2(x * sc, z * cc))
        val iy = toDegrees(if (z == 0.0) 0.0 else asin(y * sc / z))

        return safePoint(ix, iy)
    }

    protected abstract fun scale(cxcy: Double): Double

    protected abstract fun angle(z: Double): Double

    companion object {
        private val VALID_RECTANGLE = newSpanRectangle(
            LonLatPoint(-(180.0 - 1e-3), -90.0),
            LonLatPoint(+(180.0 - 1e-3), +90.0)
        )
    }
}