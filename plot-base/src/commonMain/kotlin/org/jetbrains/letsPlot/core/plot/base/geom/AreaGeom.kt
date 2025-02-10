/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom


import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomUtil
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomUtil.TO_LOCATION_X_Y
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomUtil.TO_LOCATION_X_ZERO
import org.jetbrains.letsPlot.core.plot.base.geom.util.LinesHelper
import org.jetbrains.letsPlot.core.plot.base.geom.util.QuantilesHelper
import org.jetbrains.letsPlot.core.plot.base.geom.util.TargetCollectorHelper
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import org.jetbrains.letsPlot.core.plot.base.stat.DensityStat
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgNode

open class AreaGeom : GeomBase() {
    var quantiles: List<Double> = DensityStat.DEF_QUANTILES
    var quantileLines: Boolean = DEF_QUANTILE_LINES
    var flat: Boolean = false

    override fun rangeIncludesZero(aes: Aes<*>): Boolean = (aes == Aes.Y)

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
        helper.setResamplingEnabled(!coord.isLinear && !flat)

        // Alpha is disabled for strokes (but still applies to fill).
        helper.setAlphaEnabled(false)

        val quantilesHelper = QuantilesHelper(pos, coord, ctx, quantiles)
        val targetCollectorHelper = TargetCollectorHelper(tooltipsGeomKind(), ctx)

        val dataPoints = GeomUtil.withDefined(dataPoints(aesthetics), Aes.X, Aes.Y)
        val closePath = helper.meetsRadarPlotReq()
        dataPoints.sortedByDescending(DataPointAesthetics::group).groupBy(DataPointAesthetics::group)
            .forEach { (_, groupDataPoints) ->
                quantilesHelper.splitByQuantiles(groupDataPoints, Aes.X).forEach { points ->
                    val bands = helper.renderBands(
                        points,
                        TO_LOCATION_X_Y,
                        TO_LOCATION_X_ZERO,
                        simplifyBorders = false,
                        closePath = closePath
                    )
                    root.appendNodes(bands)

                    val upperPoints = helper.createPathData(points, TO_LOCATION_X_Y, closePath)

                    val line = helper.renderPaths(upperPoints, filled = false)
                    root.appendNodes(line)
                    targetCollectorHelper.addVariadicPaths(upperPoints)
                }

                if (quantileLines) {
                    createQuantileLines(groupDataPoints, quantilesHelper).forEach(root::add)
                }
            }
    }

    private fun createQuantileLines(
        dataPoints: Iterable<DataPointAesthetics>,
        quantilesHelper: QuantilesHelper
    ): List<SvgNode> {
        val toLocationBoundStart: (DataPointAesthetics) -> DoubleVector = { p -> TO_LOCATION_X_Y(p)!! }
        val toLocationBoundEnd: (DataPointAesthetics) -> DoubleVector = { p -> TO_LOCATION_X_ZERO(p)!! }
        return quantilesHelper.getQuantileLineElements(dataPoints, Aes.X, toLocationBoundStart, toLocationBoundEnd)
    }

    protected open fun tooltipsGeomKind() = GeomKind.AREA

    companion object {
        const val DEF_QUANTILE_LINES = false
        const val HANDLES_GROUPS = true
    }
}
