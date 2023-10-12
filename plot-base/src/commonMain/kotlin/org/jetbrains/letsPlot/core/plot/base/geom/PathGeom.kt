/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomUtil
import org.jetbrains.letsPlot.core.plot.base.geom.util.LinesHelper
import org.jetbrains.letsPlot.core.plot.base.geom.util.TargetCollectorHelper
import org.jetbrains.letsPlot.core.plot.base.render.LegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot

open class PathGeom : GeomBase() {

    var animation: Any? = null
    var flat: Boolean = false
    var geodesic: Boolean = false

    override val legendKeyElementFactory: LegendKeyElementFactory
        get() = HLineGeom.LEGEND_KEY_ELEMENT_FACTORY

    protected open fun dataPoints(aesthetics: Aesthetics): Iterable<DataPointAesthetics> {
        return GeomUtil.with_X_Y(aesthetics.dataPoints())
    }

    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {

        val dataPoints = dataPoints(aesthetics)
        val linesHelper = LinesHelper(pos, coord, ctx)

        // TODO: refactor code duplication
        if (coord.isLinear || flat) {
            val variadicPathData = linesHelper.createVariadicPathData(dataPoints)
            val visualPathData = LinesHelper.createVisualPath(variadicPathData)

            // To not add interpolated points and to not show incorrect tooltips on them
            val targetCollectorHelper = TargetCollectorHelper(GeomKind.PATH, ctx)
            targetCollectorHelper.addVariadicPaths(variadicPathData)

            val svgPath = linesHelper.createPaths(visualPathData, closePath = false)
            root.appendNodes(svgPath)
        } else {
            val variadicPathData = linesHelper.createVariadicNonLinearPathData(dataPoints)
            val visualPathData = LinesHelper.createVisualPath(variadicPathData)
            val smoothed = linesHelper.interpolate(visualPathData)

            // To not add interpolated points and to not show incorrect tooltips on them
            val targetCollectorHelper = TargetCollectorHelper(GeomKind.PATH, ctx)
            targetCollectorHelper.addVariadicPaths(variadicPathData)

            val svgPath = linesHelper.createPaths(smoothed, closePath = false)
            root.appendNodes(svgPath)
        }
    }


    companion object {
        const val HANDLES_GROUPS = true
    }
}
