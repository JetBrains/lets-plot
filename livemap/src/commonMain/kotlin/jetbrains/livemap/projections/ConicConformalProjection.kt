package jetbrains.livemap.projections

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.projectionGeometry.GeoUtils.toDegrees
import jetbrains.datalore.base.projectionGeometry.GeoUtils.toRadians
import kotlin.math.*

internal class ConicConformalProjection(y0: Double, y1: Double) : GeoProjection {

    private val myN: Double
    private val myF: Double

    init {
        val cy0 = cos(y0)
        myN = if (y0 == y1) sin(y0) else ln(cy0 / cos(y1)) / ln(tany(y1) / tany(y0))
        myF = cy0 * tany(y0).pow(myN) / myN
    }


    override fun validRect(): DoubleRectangle {
        return VALID_RECTANGLE
    }

    override fun project(v: DoubleVector): DoubleVector {
        val x = toRadians(v.x)
        var y = toRadians(v.y)

        if (myF > 0) {
            if (y < -PI / 2 + EPSILON) {
                y = -PI / 2 + EPSILON
            }
        } else {
            if (y > PI / 2 - EPSILON) {
                y = PI / 2 - EPSILON
            }
        }

        val r = myF / tany(y).pow( myN)

        val px = r * sin(myN * x)
        val py = myF - r * cos(myN * x)
        return if (px.isNaN() || py.isNaN()) {
            error("Value for DoubleVector isNaN px = $px and py = $py")
        } else {
            DoubleVector(px, py)
        }
    }

    override fun invert(v: DoubleVector): DoubleVector {
        val x = v.x
        val y = v.y
        val fy = myF - y
        val r = myN.sign * sqrt(x * x + fy * fy)

        val ix = toDegrees(atan2(x, abs(fy)) / myN * fy.sign)
        val iy = toDegrees(2 * atan((myF / r).pow(1 / myN)) - PI / 2)
        return if (ix.isNaN() || iy.isNaN()) {
            error("Value for DoubleVector isNaN ix = $ix and iy = $iy")
        } else {
            DoubleVector(ix, iy)
        }
    }

    companion object {
        private val VALID_RECTANGLE = DoubleRectangle.span(
            DoubleVector(-180.0, -65.0),
            DoubleVector(+180.0, +90.0)
        )
        private const val EPSILON = 0.001

        private fun tany(y: Double): Double {
            return tan((PI / 2 + y) / 2)
        }
    }
}