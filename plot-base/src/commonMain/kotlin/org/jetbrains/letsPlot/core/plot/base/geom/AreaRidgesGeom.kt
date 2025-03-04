/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomUtil
import org.jetbrains.letsPlot.core.plot.base.geom.util.LinesHelper
import org.jetbrains.letsPlot.core.plot.base.geom.util.QuantilesHelper
import org.jetbrains.letsPlot.core.plot.base.geom.util.TargetCollectorHelper
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import org.jetbrains.letsPlot.core.plot.base.stat.DensityRidgesStat
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgNode

class AreaRidgesGeom : GeomBase(), WithHeight {
    var scale: Double = DEF_SCALE
    var minHeight: Double = DEF_MIN_HEIGHT
    var quantiles: List<Double> = DensityRidgesStat.DEF_QUANTILES
    var quantileLines: Boolean = DEF_QUANTILE_LINES

    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        buildLines(root, aesthetics, pos, coord, ctx)
    }

    private fun buildLines(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val definedDataPoints = GeomUtil.withDefined(aesthetics.dataPoints(), Aes.X, Aes.Y, Aes.HEIGHT)
        if (!definedDataPoints.any()) return
        definedDataPoints
            .sortedByDescending(DataPointAesthetics::y)
            .groupBy(DataPointAesthetics::y)
            .map { (y, nonOrderedPoints) -> y to GeomUtil.ordered_X(nonOrderedPoints) }
            .forEach { (_, dataPoints) ->
                splitDataPointsByMinHeight(dataPoints).forEach { buildRidge(root, it, pos, coord, ctx) }
            }
    }

    private fun splitDataPointsByMinHeight(dataPoints: Iterable<DataPointAesthetics>): List<Iterable<DataPointAesthetics>> {
        val result = mutableListOf<Iterable<DataPointAesthetics>>()
        var dataPointsBunch: MutableList<DataPointAesthetics> = mutableListOf()
        for (p in dataPoints)
            if (p.height()!! >= minHeight)
                dataPointsBunch.add(p)
            else {
                if (dataPointsBunch.any()) result.add(dataPointsBunch)
                dataPointsBunch = mutableListOf()
            }
        if (dataPointsBunch.any()) result.add(dataPointsBunch)
        return result
    }

    private fun buildRidge(
        root: SvgRoot,
        dataPoints: Iterable<DataPointAesthetics>,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val helper = LinesHelper(pos, coord, ctx)
        val quantilesHelper = QuantilesHelper(pos, coord, ctx, quantiles, Aes.Y)
        val boundTransform = toLocationBound(ctx)

        val targetCollectorHelper = TargetCollectorHelper(GeomKind.AREA_RIDGES, ctx)

        quantilesHelper.splitByQuantiles(dataPoints, Aes.X).forEach { points ->
            val paths = helper.createBands(
                points,
                boundTransform,
                GeomUtil.TO_LOCATION_X_Y,
                simplifyBorders = true
            )
            root.appendNodes(paths)

            helper.setAlphaEnabled(false)
            root.appendNodes(helper.createLines(points, boundTransform))

            val pathData = helper.createPathDataByGroup(points, boundTransform)
            targetCollectorHelper.addPaths(pathData)
        }

        if (quantileLines) {
            createQuantileLines(dataPoints, quantilesHelper, ctx).forEach(root::add)
        }
    }

    private fun createQuantileLines(
        dataPoints: Iterable<DataPointAesthetics>,
        quantilesHelper: QuantilesHelper,
        ctx: GeomContext
    ): List<SvgNode> {
        val toLocationBoundStart = toLocationBound(ctx)
        val toLocationBoundEnd = { p: DataPointAesthetics -> DoubleVector(p.x()!!, p.y()!!) }
        return quantilesHelper.getQuantileLineElements(dataPoints, Aes.X, toLocationBoundStart, toLocationBoundEnd)
    }

    private fun toLocationBound(ctx: GeomContext): (p: DataPointAesthetics) -> DoubleVector {
        return fun(p: DataPointAesthetics): DoubleVector {
            val x = p.x()!!
            val y = p.y()!! + ctx.getResolution(Aes.Y) * scale * p.height()!!
            return DoubleVector(x, y)
        }
    }

    override fun heightSpan(p: DataPointAesthetics, coordAes: Aes<Double>, resolution: Double, isDiscrete: Boolean): DoubleSpan? {
        val loc = p.finiteOrNull(coordAes) ?: return null
        val size = p.finiteOrNull(Aes.HEIGHT) ?: return null
        if (size < this.minHeight) return null
        val expand = resolution * this.scale * size
        return DoubleSpan(loc, loc + expand)
    }

    companion object {
        const val DEF_SCALE = 1.0
        const val DEF_MIN_HEIGHT = 0.0
        const val DEF_QUANTILE_LINES = false

        const val HANDLES_GROUPS = true
    }
}