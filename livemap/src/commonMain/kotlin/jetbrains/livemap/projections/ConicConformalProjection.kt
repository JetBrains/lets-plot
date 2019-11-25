/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.projections

import jetbrains.datalore.base.math.toDegrees
import jetbrains.datalore.base.math.toRadians
import jetbrains.datalore.base.projectionGeometry.Rect
import jetbrains.datalore.base.projectionGeometry.explicitVec
import jetbrains.datalore.base.projectionGeometry.newSpanRectangle
import jetbrains.datalore.base.spatial.LonLat
import jetbrains.livemap.projections.ProjectionUtil.safePoint
import kotlin.math.*

internal class ConicConformalProjection(y0: Double, y1: Double) : GeoProjection {

    private val n: Double
    private val f: Double

    init {
        val cy0 = cos(y0)
        n = if (y0 == y1) sin(y0) else ln(cy0 / cos(y1)) / ln(tany(y1) / tany(y0))
        f = cy0 * tany(y0).pow(n) / n
    }


    override fun validRect(): Rect<LonLat> = VALID_RECTANGLE

    override fun project(v: LonLatPoint): GeographicPoint {
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

        val r = f / tany(y).pow( n)

        val px = r * sin(n * x)
        val py = f - r * cos(n * x)
        return safePoint(px, py)
    }

    override fun invert(v: GeographicPoint): LonLatPoint {
        val x = v.x
        val y = v.y
        val fy = f - y
        val r = n.sign * sqrt(x * x + fy * fy)

        val ix = toDegrees(atan2(x, abs(fy)) / n * fy.sign)
        val iy = toDegrees(2 * atan((f / r).pow(1 / n)) - PI / 2)
        return safePoint(ix, iy)
    }

    companion object {
        private val VALID_RECTANGLE = newSpanRectangle(
            explicitVec<LonLat>(-180.0, -65.0),
            explicitVec<LonLat>(+180.0, +90.0)
        )
        private const val EPSILON = 0.001

        private fun tany(y: Double): Double {
            return tan((PI / 2 + y) / 2)
        }
    }
}