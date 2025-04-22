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
import kotlin.math.abs
import kotlin.random.Random

class SinaGeom : PointGeom() {
    var seed: Long? = null
    var jitterY: Boolean = DEF_JITTER_Y
    var quantiles: List<Double> = YDensityStat.DEF_QUANTILES
    var showHalf: Double = DEF_SHOW_HALF

    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val rand = seed?.let { Random(seed!!) } ?: Random.Default
        // Almost the same as in ViolinGeom::buildLines()
        val dataPoints = GeomUtil.withDefined(aesthetics.dataPoints(), Aes.X, Aes.Y, Aes.VIOLINWIDTH, Aes.WIDTH)
        if (!integerish(dataPoints.map { it.y()!! })) {
            jitterY = false
        }
        dataPoints
            .groupBy(DataPointAesthetics::x)
            .map { (x, nonOrderedPoints) -> x to GeomUtil.ordered_Y(nonOrderedPoints, false) }
            .forEach { (_, dataPoints) -> buildGroup(root, dataPoints, pos, coord, ctx, rand) }
    }

    // Almost the same as in ViolinGeom::buildViolin()
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
        val jitterTransform = toLocationBound(ctx, rand)

        quantilesHelper.splitByQuantiles(dataPoints, Aes.Y).forEach { points ->
            // Almost the same as in PointGeom::buildIntern()
            val slimGroup = SvgSlimElements.g(points.size)
            for (p in points) {
                if (p.finiteOrNull(Aes.SIZE) == null) continue
                val point = p.finiteVectorOrNull(Aes.X, Aes.Y) ?: continue
                val jitteredPoint = jitterTransform(p)
                val location = helper.toClient(jitteredPoint, p) ?: continue
                val shape = p.shape()!!
                val sizeUnitRatio = AesScaling.sizeUnitRatio(point, coord, sizeUnit, AesScaling.POINT_UNIT_SIZE)
                targetCollector.addPoint(
                    p.index(), location, (shape.size(p, sizeUnitRatio) + shape.strokeWidth(p)) / 2,
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
        ctx: GeomContext,
        rand: Random
    ): (p: DataPointAesthetics) -> DoubleVector {
        val resolutionX = ctx.getResolution(Aes.X)
        val resolutionY = ctx.getResolution(Aes.Y)
        return fun(p: DataPointAesthetics): DoubleVector {
            val signX = when {
                showHalf > 0 -> 1
                showHalf < 0 -> -1
                else -> if (rand.nextBoolean()) 1 else -1
            }
            val signY = if (rand.nextBoolean()) 1 else -1
            val randomWidthShift = rand.nextDouble()
            val randomHeightShift = rand.nextDouble()
            val widthLimit = resolutionX / 2 * p.width()!! * p.violinwidth()!!
            val heightLimit = if (jitterY) DY * resolutionY else 0.0
            val x = p.x()!! + signX * randomWidthShift * widthLimit // This formula is used to treat both sides equally (do not include ends)
            val y = p.y()!! + signY * randomHeightShift * heightLimit
            return DoubleVector(x, y)
        }
    }

    private fun integerish(values: List<Double>): Boolean {
        return values.all { abs(it - it.toLong()) < INTEGERISH_EPSILON }
    }

    companion object {
        const val HANDLES_GROUPS = true

        const val DEF_JITTER_Y = true
        const val DEF_SHOW_HALF = 0.0

        private const val DY = 0.25
        private const val INTEGERISH_EPSILON = 1e-12
    }
}
