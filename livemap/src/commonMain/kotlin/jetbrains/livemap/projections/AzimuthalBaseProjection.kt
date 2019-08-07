package jetbrains.livemap.projections

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.projectionGeometry.GeoUtils.toDegrees
import jetbrains.datalore.base.projectionGeometry.GeoUtils.toRadians
import jetbrains.livemap.projections.ProjectionUtil.safeDoubleVector
import kotlin.math.*

internal abstract class AzimuthalBaseProjection : GeoProjection {

    override fun validRect(): DoubleRectangle = VALID_RECTANGLE

    override fun project(v: DoubleVector): DoubleVector {

        val x = toRadians(v.x)
        val y = toRadians(v.y)
        val cx = cos(x)
        val cy = cos(y)
        val k = scale(cx * cy)

        val px = k * cy * sin(x)
        val py = k * sin(y)

        return safeDoubleVector(px, py)
    }

    override fun invert(v: DoubleVector): DoubleVector {

        val x = v.x
        val y = v.y
        val z = sqrt(x * x + y * y)
        val c = angle(z)
        val sc = sin(c)
        val cc = cos(c)

        val ix = toDegrees(atan2(x * sc, z * cc))
        val iy = toDegrees(if (z == 0.0) 0.0 else asin(y * sc / z))

        return safeDoubleVector(ix, iy)
    }

    protected abstract fun scale(cxcy: Double): Double

    protected abstract fun angle(z: Double): Double

    companion object {
        private val VALID_RECTANGLE = DoubleRectangle.span(
            DoubleVector(-(180.0 - 1e-3), -90.0),
            DoubleVector(+(180.0 - 1e-3), +90.0)
        )
    }
}