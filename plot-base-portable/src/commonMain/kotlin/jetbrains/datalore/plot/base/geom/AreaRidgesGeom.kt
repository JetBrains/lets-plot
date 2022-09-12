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
        val definedDataPoints = GeomUtil.withDefined(aesthetics.dataPoints(), Aes.X, Aes.Y, Aes.RIDGEHEIGHT, Aes.HEIGHT)
        val maxRidgeHeight = definedDataPoints.maxByOrNull { it.ridgeheight()!! }?.ridgeheight()!!
        definedDataPoints
            .sortedByDescending(DataPointAesthetics::y)
            .groupBy(DataPointAesthetics::y)
            .map { (y, nonOrderedPoints) -> y to GeomUtil.ordered_X(nonOrderedPoints) }
            .forEach { (_, dataPoints) ->
                splitDataPointsByMinHeight(dataPoints).forEach { buildRidge(root, it, maxRidgeHeight, pos, coord, ctx) }
            }
    }

    private fun splitDataPointsByMinHeight(dataPoints: Iterable<DataPointAesthetics>): MutableList<Iterable<DataPointAesthetics>> {
        val result = mutableListOf<Iterable<DataPointAesthetics>>()
        var dataPointsBunch: MutableList<DataPointAesthetics> = mutableListOf()
        for (p in dataPoints)
            if (p.ridgeheight()!! >= MIN_HEIGHT)
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
        maxRidgeHeight: Double,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val helper = LinesHelper(pos, coord, ctx)
        val boundTransform = toLocationBound(maxRidgeHeight)

        val paths = helper.createBands(dataPoints, boundTransform) { p -> DoubleVector(p.x()!!, p.y()!!) }
        appendNodes(paths, root)

        helper.setAlphaEnabled(false)
        appendNodes(helper.createLines(dataPoints, boundTransform), root)

        buildHints(dataPoints, ctx, helper, boundTransform)
    }

    private fun toLocationBound(maxRidgeHeight: Double): (p: DataPointAesthetics) -> DoubleVector {
        return fun(p: DataPointAesthetics): DoubleVector {
            val x = p.x()!!
            val y = p.y()!! + p.height()!! * p.ridgeheight()!! / maxRidgeHeight
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
        private const val MIN_HEIGHT = 0.0

        const val HANDLES_GROUPS = true
    }
}