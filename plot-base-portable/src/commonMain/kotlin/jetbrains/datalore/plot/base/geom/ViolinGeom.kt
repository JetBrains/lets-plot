/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.aes.AestheticsBuilder
import jetbrains.datalore.plot.base.geom.util.*
import jetbrains.datalore.plot.base.interact.GeomTargetCollector.TooltipParams.Companion.tooltip
import jetbrains.datalore.plot.base.interact.TipLayoutHint
import jetbrains.datalore.plot.base.render.SvgRoot

class ViolinGeom : GeomBase() {
    private var drawQuantiles: List<Double> = DEF_DRAW_QUANTILES

    fun setDrawQuantiles(quantiles: List<Double>) {
        drawQuantiles = quantiles
    }

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
        GeomUtil.withDefined(aesthetics.dataPoints(), Aes.X, Aes.Y, Aes.VIOLINWIDTH, Aes.WIDTH)
            .groupBy(DataPointAesthetics::x)
            .map { (x, nonOrderedPoints) -> x to GeomUtil.ordered_Y(nonOrderedPoints, false) }
            .forEach { (_, dataPoints) -> buildViolin(root, dataPoints, pos, coord, ctx) }
    }

    private fun buildViolin(
        root: SvgRoot,
        dataPoints: Iterable<DataPointAesthetics>,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val helper = LinesHelper(pos, coord, ctx)
        val leftBoundTransform = toLocationBound(-1.0, ctx)
        val rightBoundTransform = toLocationBound(1.0, ctx)

        val paths = helper.createBands(dataPoints, leftBoundTransform, rightBoundTransform)
        appendNodes(paths, root)

        helper.setAlphaEnabled(false)
        appendNodes(helper.createLines(dataPoints, leftBoundTransform), root)
        appendNodes(helper.createLines(dataPoints, rightBoundTransform), root)

        buildQuantiles(root, dataPoints, pos, coord, ctx)

        buildHints(dataPoints, ctx, helper, leftBoundTransform)
        buildHints(dataPoints, ctx, helper, rightBoundTransform)
    }

    private fun buildQuantiles(
        root: SvgRoot,
        dataPoints: Iterable<DataPointAesthetics>,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        if (drawQuantiles.isEmpty()) return

        val geomHelper = GeomHelper(pos, coord, ctx)
        val helper = geomHelper.createSvgElementHelper()
        for ((group, dataPointsGroup) in dataPoints.groupBy { it.group() }) {
            for (p in calculateQuantiles(dataPointsGroup, group)) {
                val xmin = toLocationBound(-1.0, ctx)(p).x
                val xmax = toLocationBound(1.0, ctx)(p).x
                val start = DoubleVector(xmin, p.y()!!)
                val end = DoubleVector(xmax, p.y()!!)
                val line = helper.createLine(start, end, p)
                root.add(line)
            }
        }
    }

    private fun calculateQuantiles(
        dataPoints: Iterable<DataPointAesthetics>,
        group: Int?
    ): Iterable<DataPointAesthetics> {
        val x = dataPoints.first().x()!!
        val vws = dataPoints.map { it.violinwidth()!! }
        val ys = dataPoints.map { it.y()!! }
        val vwsSum = vws.sum()
        val dens = vws.runningReduce { cumSum, elem -> cumSum + elem }.map { it / vwsSum }
        val quantY = drawQuantiles.map { pwLinInterp(dens, ys)(it) }
        val quantViolinWidth = quantY.map { pwLinInterp(ys, vws)(it) }
        val quantilesColor = dataPoints.first().color()
        val quantilesSize = dataPoints.first().size()

        return AestheticsBuilder(quantY.size)
            .x(AestheticsBuilder.constant(x))
            .y(AestheticsBuilder.list(quantY))
            .violinwidth(AestheticsBuilder.list(quantViolinWidth))
            .group(AestheticsBuilder.constant(group ?: 0))
            .color(AestheticsBuilder.constant(quantilesColor))
            .size(AestheticsBuilder.constant(quantilesSize))
            .build()
            .dataPoints()
    }

    private fun pwLinInterp(x: List<Double>, y: List<Double>): (Double) -> Double {
        // Returns (bounded) piecewise linear interpolation function
        return fun (t: Double): Double {
            val i = x.indexOfFirst { it >= t }
            if (i == 0) return y.first()
            if (i == -1) return y.last()
            val a = (y[i] - y[i - 1]) / (x[i] - x[i - 1])
            val b = y[i - 1] - a * x[i - 1]
            return a * t + b
        }
    }

    private fun toLocationBound(
        sign: Double,
        ctx: GeomContext
    ): (p: DataPointAesthetics) -> DoubleVector {
        return fun (p: DataPointAesthetics): DoubleVector {
            val x = p.x()!! + ctx.getResolution(Aes.X) / 2 * sign * p.width()!! * p.violinwidth()!!
            val y = p.y()!!
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
        val colorMarkerMapper = HintColorUtil.createColorMarkerMapper(GeomKind.VIOLIN, ctx)

        for (multiPointData in multiPointDataList) {
            targetCollector.addPath(
                multiPointData.points,
                multiPointData.localToGlobalIndex,
                tooltip {
                    markerColors = colorMarkerMapper(multiPointData.aes)
                },
                if (ctx.flipped) {
                    TipLayoutHint.Kind.VERTICAL_TOOLTIP
                } else {
                    TipLayoutHint.Kind.HORIZONTAL_TOOLTIP
                }
            )
        }
    }

    companion object {
        val DEF_DRAW_QUANTILES = emptyList<Double>()

        const val HANDLES_GROUPS = true
    }

}