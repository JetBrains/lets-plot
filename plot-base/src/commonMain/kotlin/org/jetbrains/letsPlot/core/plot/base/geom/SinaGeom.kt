/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.Aesthetics
import org.jetbrains.letsPlot.core.plot.base.CoordinateSystem
import org.jetbrains.letsPlot.core.plot.base.DataPointAesthetics
import org.jetbrains.letsPlot.core.plot.base.GeomContext
import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.plot.base.PositionAdjustment
import org.jetbrains.letsPlot.core.plot.base.aes.AesScaling
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomHelper
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomUtil
import org.jetbrains.letsPlot.core.plot.base.geom.util.HintColorUtil
import org.jetbrains.letsPlot.core.plot.base.geom.util.QuantilesHelper
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import org.jetbrains.letsPlot.core.plot.base.render.point.PointShapeSvg
import org.jetbrains.letsPlot.core.plot.base.stat.YDensityStat
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetCollector
import org.jetbrains.letsPlot.datamodel.svg.dom.slim.SvgSlimElements
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.random.Random

class SinaGeom : PointGeom() {
    var seed: Long? = null
    var quantiles: List<Double> = YDensityStat.DEF_QUANTILES

    private val rand = seed?.let { Random(seed!!) } ?: Random.Default

    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        // Almost the same as in ViolinGeom::buildLines()
        GeomUtil.withDefined(aesthetics.dataPoints(), Aes.X, Aes.Y, Aes.VIOLINWIDTH, Aes.WIDTH)
            .groupBy(DataPointAesthetics::x)
            .map { (x, nonOrderedPoints) -> x to GeomUtil.ordered_Y(nonOrderedPoints, false) }
            .forEach { (_, dataPoints) -> buildGroup(root, dataPoints, pos, coord, ctx) }
    }

    // Almost the same as in ViolinGeom::buildViolin()
    private fun buildGroup(
        root: SvgRoot,
        dataPoints: Iterable<DataPointAesthetics>,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val helper = GeomHelper(pos, coord, ctx)
        val quantilesHelper = QuantilesHelper(pos, coord, ctx, quantiles, Aes.X)
        val targetCollector = getGeomTargetCollector(ctx)
        val colorsByDataPoint = HintColorUtil.createColorMarkerMapper(GeomKind.POINT, ctx)
        val jitterTransform = toLocationBound(ctx)

        quantilesHelper.splitByQuantiles(dataPoints, Aes.Y).forEach { points ->
            // Almost the same as in PointGeom::buildIntern()
            val count = points.size
            val slimGroup = SvgSlimElements.g(count)
            for (i in 0 until count) {
                val p = points[i]
                if (p.finiteOrNull(Aes.SIZE) == null) continue
                val point = p.finiteVectorOrNull(Aes.X, Aes.Y) ?: continue
                val jitteredPoint = jitterTransform(p)
                val location = helper.toClient(jitteredPoint, p) ?: continue
                val shape = p.shape()!!
                val sizeUnitRatio = AesScaling.sizeUnitRatio(point, coord, sizeUnit, AesScaling.POINT_UNIT_SIZE)
                targetCollector.addPoint(
                    i, location, (shape.size(p, sizeUnitRatio) + shape.strokeWidth(p)) / 2,
                    GeomTargetCollector.TooltipParams(
                        markerColors = colorsByDataPoint(p)
                    )
                )
                val o = PointShapeSvg.create(shape, location, p, sizeUnitRatio)
                o.appendTo(slimGroup)
            }
            root.add(wrap(slimGroup))
        }
    }

    private fun toLocationBound(
        ctx: GeomContext
    ): (p: DataPointAesthetics) -> DoubleVector {
        return fun(p: DataPointAesthetics): DoubleVector {
            val sign = if (rand.nextBoolean()) 1 else -1
            val randomShift = rand.nextDouble()
            val widthLimit = ctx.getResolution(Aes.X) / 2 * p.width()!! * p.violinwidth()!!
            val x = p.x()!! + sign * randomShift * widthLimit // This formula is used to treat both sides equally (do not include ends)
            val y = p.y()!!
            return DoubleVector(x, y)
        }
    }

    companion object {
        const val HANDLES_GROUPS = true
    }
}
