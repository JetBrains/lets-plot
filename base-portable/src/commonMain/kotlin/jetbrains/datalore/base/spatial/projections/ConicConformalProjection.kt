/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.spatial.projections

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import jetbrains.datalore.base.math.toDegrees
import jetbrains.datalore.base.math.toRadians
import kotlin.math.*

internal class ConicConformalProjection(y0: Double, y1: Double) : Projection {
    private val n: Double
    private val f: Double

    init {
        val cy0 = cos(y0)
        n = if (y0 == y1) sin(y0) else ln(cy0 / cos(y1)) / ln(tany(y1) / tany(y0))
        f = cy0 * tany(y0).pow(n) / n
    }


    override fun validDomain(): DoubleRectangle = VALID_RECTANGLE

    override fun project(v: DoubleVector): DoubleVector? {
        val x = toRadians(v.x)
        var y = toRadians(v.y)

        if (f > 0) {
            if (y < -PI / 2 + EPSILON) {
                y = -PI / 2 + EPSILON
            }
        } else {
            if (y > PI / 2 - EPSILON) {
                y = PI / 2 - EPSILON
            }
        }

        val r = f / tany(y).pow(n)

        val px = r * sin(n * x)
        val py = f - r * cos(n * x)
        return finiteDoubleVectorOrNull(px, py)
    }

    override fun invert(v: DoubleVector): DoubleVector? {
        val x = v.x
        val y = v.y
        val fy = f - y
        val r = n.sign * sqrt(x * x + fy * fy)

        val ix = toDegrees(atan2(x, abs(fy)) / n * fy.sign)
        val iy = toDegrees(2 * atan((f / r).pow(1 / n)) - PI / 2)
        return finiteDoubleVectorOrNull(ix, iy)
    }

    companion object {
        private val VALID_RECTANGLE = DoubleRectangle.LTRB(-180.0, -65.0, +180.0, +90.0)
        private const val EPSILON = 0.001

        private fun tany(y: Double): Double {
            return tan((PI / 2 + y) / 2)
        }
    }
}