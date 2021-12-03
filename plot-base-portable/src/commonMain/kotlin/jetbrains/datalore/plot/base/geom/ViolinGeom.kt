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
import jetbrains.datalore.plot.common.data.SeriesUtil

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
        val helper = LinesHelper(pos, coord, ctx)
        val groupedDataPoints = aesthetics.dataPoints().groupBy { it.x() }
        for ((_, nonOrderedDataPoints) in groupedDataPoints) {
            val dataPoints = GeomUtil.ordered_Y(nonOrderedDataPoints, false)
            val paths = helper.createBands(dataPoints, toLocationBound(-1.0, ctx), toLocationBound(1.0, ctx))
            paths.reverse()
            appendNodes(paths, root)

            helper.setAlphaEnabled(false)
            appendNodes(helper.createLines(dataPoints, toLocationBound(-1.0, ctx)), root)
            appendNodes(helper.createLines(dataPoints, toLocationBound(1.0, ctx)), root)
        }

        buildHints(aesthetics, pos, coord, ctx, -1.0)
        buildHints(aesthetics, pos, coord, ctx, 1.0)
    }

    private fun buildHints(aesthetics: Aesthetics, pos: PositionAdjustment, coord: CoordinateSystem, ctx: GeomContext, sign: Double) {
        val geomHelper = GeomHelper(pos, coord, ctx)
        val multiPointDataList = MultiPointDataConstructor.createMultiPointDataByGroup(
            aesthetics.dataPoints(),
            MultiPointDataConstructor.singlePointAppender { p -> toClient(geomHelper, p, sign, ctx) },
            MultiPointDataConstructor.reducer(0.999, false)
        )

        val targetCollector = getGeomTargetCollector(ctx)
        for (multiPointData in multiPointDataList) {
            targetCollector.addPath(
                multiPointData.points,
                multiPointData.localToGlobalIndex,
                setupTooltipParams(multiPointData.aes),
                if (ctx.flipped) {
                    TipLayoutHint.Kind.VERTICAL_TOOLTIP
                } else {
                    TipLayoutHint.Kind.HORIZONTAL_TOOLTIP
                }
            )
        }
    }

    protected open fun setupTooltipParams(aes: DataPointAesthetics): GeomTargetCollector.TooltipParams {
        return GeomTargetCollector.TooltipParams.params().setColor(HintColorUtil.fromFill(aes))
    }

    private fun toClient(geomHelper: GeomHelper, p: DataPointAesthetics, sign: Double, ctx: GeomContext): DoubleVector? {
        val coord = toLocationBound(sign, ctx)(p)
        return coord?.let { geomHelper.toClient(it, p) }
    }

    private fun toLocationBound(sign: Double, ctx: GeomContext): (p: DataPointAesthetics) -> DoubleVector? {
        return fun (p: DataPointAesthetics): DoubleVector? {
            val x = p.x()!! + ctx.getResolution(Aes.X) / 2 * DEF_WIDTH * sign * p.violinwidth()!!
            val y = p.y()!!
            return if (SeriesUtil.isFinite(x) && SeriesUtil.isFinite(y)) {
                DoubleVector(x, y)
            } else null
        }
    }

    companion object {
        const val HANDLES_GROUPS = true
        const val DEF_WIDTH = 0.95
    }

}