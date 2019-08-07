package jetbrains.livemap.projections

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.projectionGeometry.GeoUtils.toDegrees
import jetbrains.datalore.base.projectionGeometry.GeoUtils.toRadians
import jetbrains.livemap.projections.ProjectionUtil.safeDoubleVector
import kotlin.math.*

internal class ConicEqualAreaProjection(y0: Double, y1: Double) : GeoProjection {
    private val n: Double
    private val c: Double
    private val r0: Double

    init {
        val sy0 = sin(y0)
        n = (sy0 + sin(y1)) / 2

        // Are the parallels symmetrical around the Equator?
        //if (Math.abs(n) < epsilon) return cylindricalEqualAreaRaw(y0);

        c = 1 + sy0 * (2 * n - sy0)
        r0 = sqrt(c) / n
    }

    override fun validRect(): DoubleRectangle = VALID_RECTANGLE

    override fun project(v: DoubleVector): DoubleVector {
        var x = toRadians(v.x)
        val y = toRadians(v.y)

        val r = sqrt(c - 2.0 * n * sin(y)) / n
        x *= n

        val px = r * sin(x)
        val py = r0 - r * cos(x)
        return safeDoubleVector(px, py)
    }

    override fun invert(v: DoubleVector): DoubleVector {
        val x = v.x
        val y = v.y
        val r0y = r0 - y

        val ix = toDegrees(atan2(x, abs(r0y)) / n * r0y.sign)
        val iy = toDegrees(asin((c - (x * x + r0y * r0y) * n * n) / (2 * n)))
        return safeDoubleVector(ix, iy)
    }

    companion object {
        private val VALID_RECTANGLE = DoubleRectangle.span(
            DoubleVector(-180.0, -90.0),
            DoubleVector(+180.0, +90.0)
        )
    }
}