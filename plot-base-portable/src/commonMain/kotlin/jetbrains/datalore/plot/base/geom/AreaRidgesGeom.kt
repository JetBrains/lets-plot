/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.geom.util.*
import jetbrains.datalore.plot.base.interact.GeomTargetCollector
import jetbrains.datalore.plot.base.interact.TipLayoutHint
import jetbrains.datalore.plot.base.render.SvgRoot

class AreaRidgesGeom : GeomBase() {
    var scale: Double = DEF_SCALE
    var minHeight: Double = DEF_MIN_HEIGHT
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
        var splitDataPoints: List<Iterable<DataPointAesthetics>>? = null
        val getSplitDataPoints: () -> List<Iterable<DataPointAesthetics>> = {
            if (splitDataPoints == null) splitDataPoints = splitDataToQuantiles(dataPoints)
            splitDataPoints!!
        }
        val pointsBunches = if (ctx.isMappedAes(Aes.FILL) || ctx.isMappedAes(Aes.COLOR)) getSplitDataPoints() else listOf(dataPoints)

        val helper = LinesHelper(pos, coord, ctx)
        val boundTransform = toLocationBound()

        for (points in pointsBunches) {
            val paths = helper.createBands(points, boundTransform) { p -> DoubleVector(p.x()!!, p.y()!!) }
            appendNodes(paths, root)
        }

        helper.setAlphaEnabled(false)
        for (points in pointsBunches) appendNodes(helper.createLines(points, boundTransform), root)

        if (quantileLines) {
            for (points in getSplitDataPoints()) drawQuantileLines(root, points, pos, coord, ctx)
        }

        buildHints(dataPoints, ctx, helper, boundTransform)
    }

    private fun splitDataToQuantiles(dataPoints: Iterable<DataPointAesthetics>): List<Iterable<DataPointAesthetics>> {
        val result = mutableListOf<Iterable<DataPointAesthetics>>()
        val sortedDataPoints = dataPoints.sortedWith(compareBy(DataPointAesthetics::group, DataPointAesthetics::quantile, DataPointAesthetics::x))
        val pointsItr = sortedDataPoints.iterator()
        var current: DataPointAesthetics? = null
        var prev: DataPointAesthetics? = null
        var dataPointsBunch: MutableList<DataPointAesthetics> = mutableListOf()
        while (pointsItr.hasNext()) {
            if (current != null) prev = current
            current = pointsItr.next()
            if (prev == null) continue
            dataPointsBunch.add(prev)
            if (prev.quantile() == current.quantile() ||
                (prev.quantile()?.isFinite() != true && current.quantile()?.isFinite() != true))
                continue
            dataPointsBunch.add(current)
            result.add(dataPointsBunch)
            dataPointsBunch = mutableListOf()
        }
        if (current != null) dataPointsBunch.add(current)
        if (dataPointsBunch.any()) result.add(dataPointsBunch)
        return result
    }

    private fun drawQuantileLines(
        root: SvgRoot,
        dataPoints: Iterable<DataPointAesthetics>,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val p = dataPoints.first()
        if (p.quantile()?.isFinite() == true) {
            drawQuantileLine(root, p, pos, coord, ctx)
            if (p.quantile() == 1.0) drawQuantileLine(root, dataPoints.last(), pos, coord, ctx)
        }
    }

    private fun drawQuantileLine(
        root: SvgRoot,
        dataPoint: DataPointAesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val svgElementHelper = GeomHelper(pos, coord, ctx).createSvgElementHelper()
        val start = toLocationBound()(dataPoint)
        val end = DoubleVector(dataPoint.x()!!, dataPoint.y()!!)
        val line = svgElementHelper.createLine(start, end, dataPoint)!!
        root.add(line)
    }

    private fun toLocationBound(): (p: DataPointAesthetics) -> DoubleVector {
        return fun(p: DataPointAesthetics): DoubleVector {
            val x = p.x()!!
            val y = p.y()!! + scale * p.height()!!
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

    companion object {
        const val DEF_SCALE = 3.0
        const val DEF_MIN_HEIGHT = 0.0
        const val DEF_QUANTILE_LINES = false

        const val HANDLES_GROUPS = true
    }
}