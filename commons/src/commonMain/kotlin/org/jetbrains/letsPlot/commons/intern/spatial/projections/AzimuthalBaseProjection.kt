/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.spatial.projections

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.math.toDegrees
import org.jetbrains.letsPlot.commons.intern.math.toRadians
import kotlin.math.*

internal abstract class AzimuthalBaseProjection : Projection {

    override fun validDomain(): DoubleRectangle = VALID_RECTANGLE

    override fun project(v: DoubleVector): DoubleVector? {

        val x = toRadians(v.x)
        val y = toRadians(v.y)
        val cx = cos(x)
        val cy = cos(y)
        val k = scale(cx * cy)

        val px = k * cy * sin(x)
        val py = k * sin(y)

        return finiteDoubleVectorOrNull(px, py)
    }

    override fun invert(v: DoubleVector): DoubleVector? {

        val x = v.x
        val y = v.y
        val z = sqrt(x * x + y * y)
        val c = angle(z)
        val sc = sin(c)
        val cc = cos(c)

        val ix = toDegrees(atan2(x * sc, z * cc))
        val iy = toDegrees(if (z == 0.0) 0.0 else asin(y * sc / z))

        return finiteDoubleVectorOrNull(ix, iy)
    }

    protected abstract fun scale(cxcy: Double): Double

    protected abstract fun angle(z: Double): Double

    companion object {

        private const val LON_LIMIT = 180.0 - 1e-3
        private const val LAT_LIMIT = 90.0

        private val VALID_RECTANGLE = DoubleRectangle.LTRB(-LON_LIMIT, -LAT_LIMIT, LON_LIMIT, LAT_LIMIT)
    }
}