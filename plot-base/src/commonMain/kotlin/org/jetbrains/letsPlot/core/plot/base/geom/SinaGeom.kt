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
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomHelper
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomUtil
import org.jetbrains.letsPlot.core.plot.base.geom.util.HintColorUtil
import org.jetbrains.letsPlot.core.plot.base.geom.util.QuantilesHelper
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import org.jetbrains.letsPlot.core.plot.base.render.point.PointShapeSvg
import org.jetbrains.letsPlot.core.plot.base.stat.BaseYDensityStat
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetCollector
import org.jetbrains.letsPlot.datamodel.svg.dom.slim.SvgSlimElements
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.random.Random

class SinaGeom : GeomBase() {
    var seed: Long? = null
    var quantiles: List<Double> = BaseYDensityStat.DEF_QUANTILES
    var showHalf: Double = DEF_SHOW_HALF

    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val rand = seed?.let { Random(seed!!) } ?: Random.Default
        val dataPoints = GeomUtil.withDefined(aesthetics.dataPoints(), Aes.X, Aes.Y)
        dataPoints
            .groupBy(DataPointAesthetics::x)
            .map { (x, nonOrderedPoints) -> x to GeomUtil.ordered_Y(nonOrderedPoints, false) }
            .forEach { (_, dataPoints) -> buildGroup(root, dataPoints, pos, coord, ctx, rand) }
    }

    private fun buildGroup(
        root: SvgRoot,
        dataPoints: Iterable<DataPointAesthetics>,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext,
        rand: Random
    ) {
        val helper = GeomHelper(pos, coord, ctx)
        val quantilesHelper = QuantilesHelper(pos, coord, ctx, quantiles, Aes.X)
        val targetCollector = getGeomTargetCollector(ctx)
        val colorsByDataPoint = HintColorUtil.createColorMarkerMapper(GeomKind.POINT, ctx)
        val jitterTransform = toJitterTransform(ctx, rand)

        quantilesHelper.splitByQuantiles(dataPoints, Aes.Y).forEach { points ->
            val slimGroup = SvgSlimElements.g(points.size)
            for (p in points) {
                p.size() ?: continue
                val shape = p.shape() ?: continue
                val point = jitterTransform(p) ?: continue
                val location = helper.toClient(point, p) ?: continue
                targetCollector.addPoint(
                    p.index(), location, (shape.size(p) + shape.strokeWidth(p)) / 2,
                    GeomTargetCollector.TooltipParams(
                        markerColors = colorsByDataPoint(p)
                    )
                )
                val o = PointShapeSvg.create(shape, location, p)
                o.appendTo(slimGroup)
            }
            root.add(wrap(slimGroup))
        }
    }

    private fun toJitterTransform(
        ctx: GeomContext,
        rand: Random
    ): (p: DataPointAesthetics) -> DoubleVector? {
        val resolutionX = ctx.getResolution(Aes.X)
        return fun(p: DataPointAesthetics): DoubleVector? {
            val (x, y) = p.finiteOrNull(Aes.X, Aes.Y) ?: return null
            val (width, violinWidth) = p.finiteOrNull(Aes.WIDTH, Aes.VIOLINWIDTH) ?: return null
            val signX = when {
                showHalf > 0 -> 1
                showHalf < 0 -> -1
                else -> if (rand.nextBoolean()) 1 else -1
            }
            val randomWidthShift = rand.nextDouble()
            val widthLimit = resolutionX / 2.0 * width * violinWidth
            // This formula with sign is used to treat both sides equally (do not include ends)
            return DoubleVector(x + signX * randomWidthShift * widthLimit, y)
        }
    }

    companion object {
        const val HANDLES_GROUPS = true

        const val DEF_SHOW_HALF = 0.0
    }
}
