/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.projections

import jetbrains.datalore.base.math.toDegrees
import jetbrains.datalore.base.math.toRadians
import jetbrains.datalore.base.projectionGeometry.*
import jetbrains.datalore.base.spatial.LonLat
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

        private val LON_LIMIT = Scalar<LonLat>(180.0 - 1e-3)
        private val LAT_LIMIT = Scalar<LonLat>(90.0)

        private val VALID_RECTANGLE = newSpanRectangle(
            newVec(-LON_LIMIT, -LAT_LIMIT),
            newVec(LON_LIMIT, LAT_LIMIT)
        )
    }
}