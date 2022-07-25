/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom.util

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.CoordinateSystem
import kotlin.math.max
import kotlin.math.min

class GeomCoord(
    private val myCoord: CoordinateSystem,
    private val yOrientation: Boolean
) {

    fun toClient(p: DoubleVector): DoubleVector {
        // Can't move projection application to CoordSystem - AxisUtil draws grid using toClient()
        // See: jetbrains/datalore/plot/builder/AxisUtil.kt:45

        val projected = project(p)

        return myCoord.toClient(projected)
    }

    private fun project(p: DoubleVector): DoubleVector {
        val projected = when (yOrientation) {
            true -> myCoord.projection.project(p.flip())?.flip()
            false -> myCoord.projection.project(p)
        } ?: error("CoordinateSystem.toClient() - projected point is null")
        return projected
    }

    private fun project(r: DoubleRectangle, xResolution: Double? = null, yResolution: Double? = null): DoubleRectangle {
        val resolution = project(DoubleVector(xResolution ?: 0.0, yResolution ?: 0.0))
        val origin = project(r.origin)
        val dim = project(DoubleVector(r.width, r.height))

        val width = if (xResolution != null) resolution.x * r.width else dim.x
        val height = if (yResolution != null) resolution.y * r.height else dim.y
        return DoubleRectangle.XYWH(origin.x, origin.y, width, height)
    }

    fun toClient(r: DoubleRectangle, xResolution: Double? = null, yResolution: Double? = null): DoubleRectangle {
        val projected = project(r, xResolution, yResolution)
        return translateRect(projected)
    }

    private fun translateRect(r: DoubleRectangle): DoubleRectangle {
        @Suppress("NAME_SHADOWING")
        var r = r
        val xy1 = r.origin
        val xy2 = DoubleVector(r.right, r.bottom)

        val xy1cl = myCoord.toClient(xy1)
        val xy2cl = myCoord.toClient(xy2)
        if (xy1 != xy1cl || xy2 != xy2cl) {
            val xMin = min(xy1cl.x, xy2cl.x)
            val yMin = min(xy1cl.y, xy2cl.y)
            val xMax = max(xy1cl.x, xy2cl.x)
            val yMax = max(xy1cl.y, xy2cl.y)

            r = DoubleRectangle(xMin, yMin, xMax - xMin, yMax - yMin)
        }
        return r
    }
}
