/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomUtil
import org.jetbrains.letsPlot.core.plot.base.geom.util.LinesHelper
import org.jetbrains.letsPlot.core.plot.base.geom.util.TargetCollectorHelper
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot

open class PolygonGeom : GeomBase() {

    protected fun dataPoints(aesthetics: Aesthetics): Iterable<DataPointAesthetics> {
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
        val targetCollectorHelper = TargetCollectorHelper(GeomKind.POLYGON, ctx)

        val pathData = linesHelper.createPathDataByGroup(dataPoints, GeomUtil.TO_LOCATION_X_Y)
        targetCollectorHelper.addPolygons(pathData)
        val svgPath = linesHelper.renderPaths(pathData.values, filled = true)
        root.appendNodes(svgPath)
    }

    companion object {
        const val HANDLES_GROUPS = true
    }
}
