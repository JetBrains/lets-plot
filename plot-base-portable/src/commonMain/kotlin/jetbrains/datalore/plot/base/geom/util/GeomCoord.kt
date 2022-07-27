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
        val transformed = transform(p)
        return myCoord.toClient(transformed)
    }

    fun toClient(r: DoubleRectangle): DoubleRectangle {
        val transformed = transform(r)
        return translateRect(transformed)
    }

    private fun transform(p: DoubleVector): DoubleVector {
        return when (yOrientation) {
            true -> myCoord.transform(p.flip())?.flip()
            false -> myCoord.transform(p)
        } ?: error("GeomCoord.transform($p) - result is null")
    }

    private fun transform(r: DoubleRectangle): DoubleRectangle {

        // "Rectangular" projections only.

        val leftTop = transform(r.origin)
        val rightBottom = transform(r.origin.add(r.dimension))
        return DoubleRectangle.span(leftTop, rightBottom)
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
