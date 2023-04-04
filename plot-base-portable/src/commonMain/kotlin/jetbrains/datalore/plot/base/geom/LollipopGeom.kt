/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.geom.util.GeomHelper
import jetbrains.datalore.plot.base.geom.util.GeomUtil
import jetbrains.datalore.plot.base.render.SvgRoot
import kotlin.math.pow

class LollipopGeom : PointGeom() {
    var slope: Double = DEF_SLOPE
    var intercept: Double = DEF_INTERCEPT

    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val helper = GeomHelper(pos, coord, ctx).createSvgElementHelper(false)
        for (p in GeomUtil.withDefined(aesthetics.dataPoints(), Aes.X, Aes.Y)) {
            val x = p.x()!!
            val y = p.y()!!
            val point = DoubleVector(x, y)
            val projX = (x + slope * (y - intercept)) / (1 + slope.pow(2))
            val projY = slope * projX + intercept
            val proj = DoubleVector(projX, projY)
            val line = helper.createLine(proj, point, p, true) ?: continue
            root.add(line)
        }

        super.buildIntern(root, aesthetics, pos, coord, ctx)
    }

    companion object {
        const val DEF_SLOPE = 0.0
        const val DEF_INTERCEPT = 0.0

        const val HANDLES_GROUPS = PointGeom.HANDLES_GROUPS
    }
}