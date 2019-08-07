package jetbrains.livemap.projections

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.projectionGeometry.GeoUtils.toDegrees
import jetbrains.datalore.base.projectionGeometry.GeoUtils.toRadians
import kotlin.math.*

internal class ConicEqualAreaProjection(y0: Double, y1: Double) : GeoProjection {
    private val myN: Double
    private val myC: Double
    private val myR0: Double

    init {
        val sy0 = sin(y0)
        myN = (sy0 + sin(y1)) / 2

        // Are the parallels symmetrical around the Equator?
        //if (Math.abs(n) < epsilon) return cylindricalEqualAreaRaw(y0);

        myC = 1 + sy0 * (2 * myN - sy0)
        myR0 = sqrt(myC) / myN
    }

    override fun validRect(): DoubleRectangle {
        return VALID_RECTANGLE
    }

    override fun project(v: DoubleVector): DoubleVector {
        var x = toRadians(v.x)
        val y = toRadians(v.y)

        val r = sqrt(myC - 2.0 * myN * sin(y)) / myN
        x *= myN

        val px = r * sin(x)
        val py = myR0 - r * cos(x)
        return if (px.isNaN() || py.isNaN()) {
            error("Value for DoubleVector isNaN px = $px and py = $py")
        } else {
            DoubleVector(px, py)
        }
    }

    override fun invert(v: DoubleVector): DoubleVector {
        val x = v.x
        val y = v.y
        val r0y = myR0 - y

        val ix = toDegrees(atan2(x, abs(r0y)) / myN * r0y.sign)
        val iy = toDegrees(asin((myC - (x * x + r0y * r0y) * myN * myN) / (2 * myN)))
        return if (ix.isNaN() || iy.isNaN()) {
            error("Value for DoubleVector isNaN ix = $ix and iy = $iy")
        } else {
            DoubleVector(ix, iy)
        }
    }

    companion object {
        private val VALID_RECTANGLE = DoubleRectangle.span(
            DoubleVector(-180.0, -90.0),
            DoubleVector(+180.0, +90.0)
        )
    }
}