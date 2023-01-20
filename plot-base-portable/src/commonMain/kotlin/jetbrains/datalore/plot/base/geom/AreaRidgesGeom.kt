/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.geom.util.*
import jetbrains.datalore.plot.base.interact.GeomTargetCollector
import jetbrains.datalore.plot.base.interact.TipLayoutHint
import jetbrains.datalore.plot.base.render.SvgRoot
import jetbrains.datalore.plot.base.stat.DensityRidgesStat
import jetbrains.datalore.plot.common.data.SeriesUtil
import jetbrains.datalore.vis.svg.SvgLineElement

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
        val boundTransform = toLocationBound(ctx)

        dataPoints.groupBy(DataPointAesthetics::fill).forEach { (_, points) ->
            val paths = helper.createBands(
                points,
                boundTransform,
                { p -> DoubleVector(p.x()!!, p.y()!!) },
                simplifyBorders = true
            )
            appendNodes(paths, root)
        }

        helper.setAlphaEnabled(false)
        dataPoints.groupBy(DataPointAesthetics::color).forEach { (_, points) ->
            appendNodes(helper.createLines(points, boundTransform), root)
        }

        if (quantileLines) getQuantileLines(dataPoints, pos, coord, ctx).forEach { quantileLine ->
            root.add(quantileLine)
        }

        dataPoints.groupBy { Pair(it.color(), it.fill()) }.forEach { (_, points) ->
            buildHints(points, ctx, helper, boundTransform)
        }
    }

    private fun getQuantileLines(
        dataPoints: Iterable<DataPointAesthetics>,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ): List<SvgLineElement> {
        val quantilesHelper = QuantilesHelper(pos, coord, ctx, quantiles, Aes.Y)
        val toLocationBoundStart = toLocationBound(ctx)
        val toLocationBoundEnd = { p: DataPointAesthetics -> DoubleVector(p.x()!!, p.y()!!) }
        return quantilesHelper.getQuantileLineElements(dataPoints, toLocationBoundStart, toLocationBoundEnd)
    }

    private fun toLocationBound(ctx: GeomContext): (p: DataPointAesthetics) -> DoubleVector {
        return fun(p: DataPointAesthetics): DoubleVector {
            val x = p.x()!!
            val y = p.y()!! + ctx.getResolution(Aes.Y) * scale * p.height()!!
            return DoubleVector(x, y)
        }
    }

    private fun buildHints(
        dataPoints: Iterable<DataPointAesthetics>,
        ctx: GeomContext,
        helper: GeomHelper,
        boundTransform: (p: DataPointAesthetics) -> DoubleVector
    ) {
        val multiPointDataList = MultiPointDataConstructor.createMultiPointDataByGroup(
            dataPoints,
            MultiPointDataConstructor.singlePointAppender { p ->
                boundTransform(p).let { helper.toClient(it, p) }
            },
            MultiPointDataConstructor.reducer(0.999, false)
        )
        val targetCollector = getGeomTargetCollector(ctx)
        val colorMarkerMapper = HintColorUtil.createColorMarkerMapper(GeomKind.AREA_RIDGES, ctx)

        for (multiPointData in multiPointDataList) {
            targetCollector.addPath(
                multiPointData.points,
                multiPointData.localToGlobalIndex,
                GeomTargetCollector.TooltipParams(
                    markerColors = colorMarkerMapper(multiPointData.aes)
                ),
                if (ctx.flipped) {
                    TipLayoutHint.Kind.VERTICAL_TOOLTIP
                } else {
                    TipLayoutHint.Kind.HORIZONTAL_TOOLTIP
                }
            )
        }
    }

    override fun heightSpan(p: DataPointAesthetics, coordAes: Aes<Double>, resolution: Double, isDiscrete: Boolean): DoubleSpan? {
        val sizeAes = Aes.HEIGHT
        val scaledResolution = resolution * this.scale

        val loc = p[coordAes]
        val size = p[sizeAes]

        return if (SeriesUtil.allFinite(loc, size)) {
            loc!!
            val expand = scaledResolution * size!!
            if (size >= this.minHeight) {
                DoubleSpan(loc, loc + expand)
            } else {
                null
            }
        } else {
            null
        }
    }

    companion object {
        const val DEF_SCALE = 1.0
        const val DEF_MIN_HEIGHT = 0.0
        const val DEF_QUANTILE_LINES = false

        const val HANDLES_GROUPS = true
    }
}