/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom


import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.geom.util.*
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomUtil.TO_LOCATION_X_Y
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import org.jetbrains.letsPlot.core.plot.base.stat.DensityStat
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgLineElement

open class AreaGeom : GeomBase() {
    var quantiles: List<Double> = DensityStat.DEF_QUANTILES
    var quantileLines: Boolean = DEF_QUANTILE_LINES

    override fun rangeIncludesZero(aes: org.jetbrains.letsPlot.core.plot.base.Aes<*>): Boolean = (aes == org.jetbrains.letsPlot.core.plot.base.Aes.Y)

    protected fun dataPoints(aesthetics: Aesthetics): Iterable<DataPointAesthetics> {
        return GeomUtil.ordered_X(aesthetics.dataPoints())
    }

    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val helper = LinesHelper(pos, coord, ctx)
        val geomHelper = GeomHelper(pos, coord, ctx)
        val quantilesHelper = QuantilesHelper(pos, coord, ctx, quantiles)
        val targetCollectorHelper = TargetCollectorHelper(tooltipsGeomKind(), ctx)

        val dataPoints = GeomUtil.withDefined(aesthetics.dataPoints(), org.jetbrains.letsPlot.core.plot.base.Aes.X, org.jetbrains.letsPlot.core.plot.base.Aes.Y)
        dataPoints.sortedByDescending(DataPointAesthetics::group).groupBy(DataPointAesthetics::group)
            .forEach { (_, groupDataPoints) ->
                quantilesHelper.splitByQuantiles(groupDataPoints, org.jetbrains.letsPlot.core.plot.base.Aes.X).forEach { points ->
                    val paths = helper.createBands(points, TO_LOCATION_X_Y, GeomUtil.TO_LOCATION_X_ZERO)
                    // If you want to retain the side edges of area: comment out the following codes,
                    // and switch decorate method in LinesHelper.createBands
                    root.appendNodes(paths)

                    helper.setAlphaEnabled(false)
                    root.appendNodes(helper.createLines(points, TO_LOCATION_X_Y))

                    val pathData = helper.createPathDataByGroup(points, TO_LOCATION_X_Y)
                    targetCollectorHelper.addPaths(pathData)
                }

                if (quantileLines) {
                    createQuantileLines(groupDataPoints, quantilesHelper).forEach(root::add)
                }
            }
    }

    private fun createQuantileLines(
        dataPoints: Iterable<DataPointAesthetics>,
        quantilesHelper: QuantilesHelper
    ): List<SvgLineElement> {
        val toLocationBoundStart: (DataPointAesthetics) -> DoubleVector = { p -> TO_LOCATION_X_Y(p)!! }
        val toLocationBoundEnd: (DataPointAesthetics) -> DoubleVector = { p -> GeomUtil.TO_LOCATION_X_ZERO(p)!! }
        return quantilesHelper.getQuantileLineElements(dataPoints, org.jetbrains.letsPlot.core.plot.base.Aes.X, toLocationBoundStart, toLocationBoundEnd)
    }

    protected open fun tooltipsGeomKind() = GeomKind.AREA

    companion object {
        const val DEF_QUANTILE_LINES = false
        const val HANDLES_GROUPS = true
    }
}
