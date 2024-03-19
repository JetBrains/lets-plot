/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.geom.util.ArrowSpec
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomHelper
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomUtil.toLocation
import org.jetbrains.letsPlot.core.plot.base.render.LegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgPathDataBuilder

class CurveGeom : GeomBase() {

    var curvature: Double = DEF_CURVATURE   // amount of curvature
    var angle: Double = DEF_ANGLE           // amount to skew the control points of the curve
        set(value) {
            // make the angle lie between 0 and 180
            field = value % 180
            if (field < 0) field += 180
        }
    var ncp: Int = DEF_NCP                  // number of control points used to draw the curve
    var arrowSpec: ArrowSpec? = null
    var spacer: Double = DEF_SPACER         // additional space to shorten curve by moving the start/end

    override val legendKeyElementFactory: LegendKeyElementFactory
        get() = HLineGeom.LEGEND_KEY_ELEMENT_FACTORY

    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val geomHelper = GeomHelper(pos, coord, ctx)
        val svgElementHelper = geomHelper
            .createSvgElementHelper()
            .setInterpolation(SvgPathDataBuilder.Interpolation.BSPLINE)
            .setArrowSpec(arrowSpec)
            .setSpacer(spacer)

        for (p in aesthetics.dataPoints()) {
            val start = p.toLocation(Aes.X, Aes.Y) ?: continue
            val end = p.toLocation(Aes.XEND, Aes.YEND) ?: continue

            // Create curve geometry
            val svgElement = svgElementHelper.createCurve(
                start, end,
                curvature,
                -angle, // inverse because of using client coordinates
                ncp,
                p
            )!!.first

            root.add(svgElement)
        }
    }


    companion object {
        const val HANDLES_GROUPS = false

        const val DEF_ANGLE = 90.0
        const val DEF_CURVATURE = 0.5
        const val DEF_NCP = 5
        const val DEF_SPACER = 0.0
    }
}

