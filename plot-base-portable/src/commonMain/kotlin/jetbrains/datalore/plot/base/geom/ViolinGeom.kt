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
        val groupedDataPoints = aesthetics.dataPoints().groupBy {  it.x() }
        val halfWidth = halfWidth(aesthetics, ctx)
        for ((_, nonOrderedDataPoints) in groupedDataPoints) {
            val dataPoints = GeomUtil.ordered_Y(nonOrderedDataPoints, false)
            val paths = helper.createBands(dataPoints, toLocationBound(-1.0, halfWidth), toLocationBound(1.0, halfWidth))
            paths.reverse()
            appendNodes(paths, root)

            helper.setAlphaEnabled(false)
            appendNodes(helper.createLines(dataPoints, toLocationBound(-1.0, halfWidth)), root)
            appendNodes(helper.createLines(dataPoints, toLocationBound(1.0, halfWidth)), root)
        }

        buildHints(aesthetics, pos, coord, ctx, -1.0)
        buildHints(aesthetics, pos, coord, ctx, 1.0)
    }

    private fun buildHints(aesthetics: Aesthetics, pos: PositionAdjustment, coord: CoordinateSystem, ctx: GeomContext, sign: Double) {
        val halfWidth = halfWidth(aesthetics, ctx)
        val geomHelper = GeomHelper(pos, coord, ctx)
        val multiPointDataList = MultiPointDataConstructor.createMultiPointDataByGroup(
            aesthetics.dataPoints(),
            MultiPointDataConstructor.singlePointAppender { p -> toClient(geomHelper, p, sign, halfWidth) },
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

    private fun toClient(geomHelper: GeomHelper, p: DataPointAesthetics, sign: Double, halfWidth: Double): DoubleVector? {
        val coord = toLocationBound(sign, halfWidth)(p)
        return if (coord != null) {
            geomHelper.toClient(coord, p)
        } else {
            null
        }
    }

    private fun halfWidth(aesthetics: Aesthetics, ctx: GeomContext): Double {
        val maxWeight: Double = aesthetics.dataPoints().map { it.weight()!! }.maxOrNull() ?: 0.0
        return ctx.getResolution(Aes.X) / (2 * maxWeight)
    }

    private fun toLocationBound(sign: Double, halfWidth: Double): (p: DataPointAesthetics) -> DoubleVector? {
        return fun (p: DataPointAesthetics): DoubleVector? {
            val x = p.x()!! + halfWidth * DEF_WIDTH * sign * p.weight()!!
            return if (SeriesUtil.isFinite(x) && SeriesUtil.isFinite(p.y())) {
                DoubleVector(x, p.y()!!)
            } else null
        }
    }

    companion object {
        const val HANDLES_GROUPS = true
        const val DEF_WIDTH = 0.95
    }

}