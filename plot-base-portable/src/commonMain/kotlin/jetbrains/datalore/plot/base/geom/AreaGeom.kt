/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom


import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.geom.util.*
import jetbrains.datalore.plot.base.geom.util.MultiPointDataConstructor.reducer
import jetbrains.datalore.plot.base.geom.util.MultiPointDataConstructor.singlePointAppender
import jetbrains.datalore.plot.base.interact.GeomTargetCollector.TooltipParams
import jetbrains.datalore.plot.base.interact.TipLayoutHint
import jetbrains.datalore.plot.base.render.SvgRoot
import jetbrains.datalore.plot.base.stat.DensityStat

open class AreaGeom : GeomBase() {
    var quantiles: List<Double> = DensityStat.DEF_QUANTILES
    var quantileLines: Boolean = DEF_QUANTILE_LINES

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
        val dataPoints = dataPoints(aesthetics)

        val helper = LinesHelper(pos, coord, ctx)
        dataPoints.groupBy(DataPointAesthetics::fill).forEach { (_, points) ->
            val paths = helper.createBands(points, GeomUtil.TO_LOCATION_X_Y, GeomUtil.TO_LOCATION_X_ZERO, simplifyBorders = true)
            // If you want to retain the side edges of area: comment out the following codes,
            // and switch decorate method in LinesHelper.createBands
            appendNodes(paths.asReversed(), root)
        }

        helper.setAlphaEnabled(false)
        dataPoints.groupBy(DataPointAesthetics::color).forEach { (_, points) ->
            appendNodes(helper.createLines(points, GeomUtil.TO_LOCATION_X_Y).asReversed(), root)
        }

        if (quantileLines) buildQuantiles(root, dataPoints, pos, coord, ctx)

        dataPoints.groupBy { Pair(it.color(), it.fill()) }.forEach { (_, points) ->
            buildHints(points, pos, coord, ctx)
        }
    }

    private fun buildQuantiles(
        root: SvgRoot,
        dataPoints: Iterable<DataPointAesthetics>,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val quantilesHelper = QuantilesHelper(pos, coord, ctx, quantiles)
        val definedPoints = GeomUtil.withDefined(dataPoints, Aes.X, Aes.Y)
        val toLocationBoundStart: (DataPointAesthetics) -> DoubleVector = { p ->
            GeomUtil.TO_LOCATION_X_Y(p)!!
        }
        val toLocationBoundEnd: (DataPointAesthetics) -> DoubleVector = { p ->
            GeomUtil.TO_LOCATION_X_ZERO(p)!!
        }
        for (line in quantilesHelper.getQuantileLineElements(definedPoints, toLocationBoundStart, toLocationBoundEnd)) {
            root.add(line)
        }
    }

    private fun buildHints(dataPoints: Iterable<DataPointAesthetics>, pos: PositionAdjustment, coord: CoordinateSystem, ctx: GeomContext) {
        val geomHelper = GeomHelper(pos, coord, ctx)
        val multiPointDataList = MultiPointDataConstructor.createMultiPointDataByGroup(
            dataPoints,
            singlePointAppender { p -> toClient(geomHelper, p) },
            reducer(0.999, false)
        )

        val targetCollector = getGeomTargetCollector(ctx)
        for (multiPointData in multiPointDataList) {
            targetCollector.addPath(
                multiPointData.points,
                multiPointData.localToGlobalIndex,
                setupTooltipParams(multiPointData.aes, ctx),
                if (ctx.flipped) {
                    TipLayoutHint.Kind.VERTICAL_TOOLTIP
                } else {
                    TipLayoutHint.Kind.HORIZONTAL_TOOLTIP
                }
            )
        }
    }

    protected open fun setupTooltipParams(aes: DataPointAesthetics, ctx: GeomContext): TooltipParams {
        return TooltipParams(
            markerColors = HintColorUtil.createColorMarkerMapper(GeomKind.AREA, ctx).invoke(aes)
        )
    }

    private fun toClient(geomHelper: GeomHelper, p: DataPointAesthetics): DoubleVector? {
        val coord = GeomUtil.TO_LOCATION_X_Y(p)
        return if (coord != null) {
            geomHelper.toClient(coord, p)
        } else {
            null
        }
    }

    companion object {
//        val RENDERS = listOf(
//                Aes.X,
//                Aes.Y,
//                Aes.SIZE,
//                Aes.LINETYPE,
//                Aes.COLOR,
//                Aes.FILL,
//                Aes.ALPHA
//        )

        const val DEF_QUANTILE_LINES = false

        const val HANDLES_GROUPS = true
    }


}
