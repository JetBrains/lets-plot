/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.geom.util.*
import jetbrains.datalore.plot.base.interact.GeomTargetCollector
import jetbrains.datalore.plot.base.interact.TipLayoutHint
import jetbrains.datalore.plot.base.render.SvgRoot

class ViolinGeom : GeomBase() {
    var showHalf: Double = DEF_SHOW_HALF
    private var drawQuantiles: List<Double> = DEF_DRAW_QUANTILES
    private val negativeSign: Double
        get() = if (showHalf > 0.0) 0.0 else -1.0
    private val positiveSign: Double
        get() = if (showHalf < 0.0) 0.0 else 1.0

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
        val leftBoundTransform = toLocationBound(negativeSign, ctx)
        val rightBoundTransform = toLocationBound(positiveSign, ctx)

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
        val quantilesHelper = QuantilesHelper(pos, coord, ctx, drawQuantiles, Aes.Y, Aes.VIOLINWIDTH, Aes.X)
        val toLocationBoundStart: (DataPointAesthetics) -> DoubleVector = { p ->
            DoubleVector(toLocationBound(negativeSign, ctx)(p).x, p.y()!!)
        }
        val toLocationBoundEnd: (DataPointAesthetics) -> DoubleVector = { p ->
            DoubleVector(toLocationBound(positiveSign, ctx)(p).x, p.y()!!)
        }
        for (line in quantilesHelper.createGroupedQuantiles(dataPoints, toLocationBoundStart, toLocationBoundEnd)) {
            root.add(line)
        }
    }

    private fun toLocationBound(
        sign: Double,
        ctx: GeomContext
    ): (p: DataPointAesthetics) -> DoubleVector {
        return fun(p: DataPointAesthetics): DoubleVector {
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
        val DEF_DRAW_QUANTILES = emptyList<Double>()
        const val DEF_SHOW_HALF = 0.0

        const val HANDLES_GROUPS = true
    }

}