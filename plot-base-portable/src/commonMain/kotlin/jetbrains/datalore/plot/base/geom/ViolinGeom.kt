/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.geom.util.*
import jetbrains.datalore.plot.base.interact.GeomTargetCollector.TooltipParams
import jetbrains.datalore.plot.base.interact.TipLayoutHint
import jetbrains.datalore.plot.base.render.SvgRoot

class ViolinGeom : GeomBase() {

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

        buildHints(dataPoints, ctx, helper, leftBoundTransform)
        buildHints(dataPoints, ctx, helper, rightBoundTransform)
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

    companion object {
        const val HANDLES_GROUPS = true
        const val WIDTH_SCALE = 0.95
    }

}