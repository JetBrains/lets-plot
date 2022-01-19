/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.aes.AestheticsBuilder
import jetbrains.datalore.plot.base.geom.util.*
import jetbrains.datalore.plot.base.interact.GeomTargetCollector.TooltipParams
import jetbrains.datalore.plot.base.interact.TipLayoutHint
import jetbrains.datalore.plot.base.render.SvgRoot

class ViolinGeom : GeomBase() {
    private var drawQuantiles = DEF_DRAW_QUANTILES

    fun setDrawQuantiles(quantiles: List<*>) {
        drawQuantiles = quantiles.map { it.toString().toDouble() }
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
        GeomUtil.withDefined(aesthetics.dataPoints(), Aes.X, Aes.Y, Aes.VIOLINWIDTH)
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

        // Calculate quantiles
        val vws = dataPoints.map { it.violinwidth()!! }
        val ys = dataPoints.map { it.y()!! }
        val xsMin = dataPoints.map { toLocationBound(-1.0, ctx)(it) }.map { it.x }
        val xsMax = dataPoints.map { toLocationBound(1.0, ctx)(it) }.map { it.x }
        val vwsSum = vws.sum()
        val dens = vws.runningReduce { cumSum, elem -> cumSum + elem }.map { it / vwsSum }
        val quantY = drawQuantiles.map { pwLinInterp(dens, ys)(it) }
        val quantXMin = quantY.map { pwLinInterp(ys, xsMin)(it) }
        val quantXMax = quantY.map { pwLinInterp(ys, xsMax)(it) }

        // Construct dataPoints by quantiles
        val quantilesColor = dataPoints.first().color()
        val quantilesSize = dataPoints.first().size()
        val quantileDataPoints = AestheticsBuilder(quantY.size)
            .xmin(AestheticsBuilder.list(quantXMin))
            .xmax(AestheticsBuilder.list(quantXMax))
            .y(AestheticsBuilder.list(quantY))
            .color(AestheticsBuilder.constant(quantilesColor))
            .size(AestheticsBuilder.constant(quantilesSize))
            .build()
            .dataPoints()

        // Draw quantiles
        val geomHelper = GeomHelper(pos, coord, ctx)
        val helper = geomHelper.createSvgElementHelper()
        for (q in quantileDataPoints) {
            val start = DoubleVector(q.xmin()!!, q.y()!!)
            val end = DoubleVector(q.xmax()!!, q.y()!!)
            val line = helper.createLine(start, end, q)
            root.add(line)

            // Draw tooltips
            val nearestDataPoint = dataPoints.first { p -> p.y()!! >= q.y()!! }
            buildQuantileHints(
                DoubleRectangle(start.x, start.y, end.x - start.x, 0.0),
                nearestDataPoint,
                ctx,
                geomHelper
            )
        }
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
            val x = p.x()!! + ctx.getResolution(Aes.X) / 2 * WIDTH_SCALE * sign * p.violinwidth()!!
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
        for (multiPointData in multiPointDataList) {
            targetCollector.addPath(
                multiPointData.points,
                multiPointData.localToGlobalIndex,
                TooltipParams.params().setColor(HintColorUtil.fromFill(multiPointData.aes)),
                if (ctx.flipped) {
                    TipLayoutHint.Kind.VERTICAL_TOOLTIP
                } else {
                    TipLayoutHint.Kind.HORIZONTAL_TOOLTIP
                }
            )
        }
    }

    private fun buildQuantileHints(
        rect: DoubleRectangle,
        p: DataPointAesthetics,
        ctx: GeomContext,
        geomHelper: GeomHelper
    ) {
        val clientRect = geomHelper.toClient(rect, p)
        val tooltipKind = if (ctx.flipped) {
            TipLayoutHint.Kind.ROTATED_TOOLTIP
        } else {
            TipLayoutHint.Kind.HORIZONTAL_TOOLTIP
        }

        val hint = HintsCollection.HintConfigFactory()
            .defaultObjectRadius(0.0)
            .defaultX(rect.center.x)
            .defaultKind(tooltipKind)

        val hints = HintsCollection(p, geomHelper)
            .addHint(hint.create(Aes.Y))
            .hints

        ctx.targetCollector.addRectangle(
            p.index(),
            clientRect,
            TooltipParams.params()
                .setTipLayoutHints(hints)
                .setColor(HintColorUtil.fromColor(p)),
            tooltipKind = tooltipKind
        )
    }

    companion object {
        val DEF_DRAW_QUANTILES = emptyList<Double>()

        const val HANDLES_GROUPS = true
        const val WIDTH_SCALE = 0.95
    }

}