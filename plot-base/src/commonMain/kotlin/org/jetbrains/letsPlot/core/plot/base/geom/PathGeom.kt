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
        linesHelper.setResamplingEnabled(!coord.isLinear && !flat)

        val pathData = linesHelper.createPathData(dataPoints, GeomUtil.TO_LOCATION_X_Y)
        val targetCollectorHelper = TargetCollectorHelper(GeomKind.PATH, ctx)
        targetCollectorHelper.addVariadicPaths(pathData)

        val svgPath = linesHelper.renderPaths(pathData, closePath = false)
        root.appendNodes(svgPath)
    }


    companion object {
        const val HANDLES_GROUPS = true
    }
}
