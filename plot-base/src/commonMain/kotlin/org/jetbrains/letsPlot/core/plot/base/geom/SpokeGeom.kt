/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.aes.AesScaling
import org.jetbrains.letsPlot.core.plot.base.geom.legend.HLineLegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomHelper
import org.jetbrains.letsPlot.core.plot.base.geom.util.HintColorUtil
import org.jetbrains.letsPlot.core.plot.base.render.LegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetCollector
import kotlin.math.*

class SpokeGeom : GeomBase(), WithWidth, WithHeight {
    override val legendKeyElementFactory: LegendKeyElementFactory
        get() = HLineLegendKeyElementFactory(AesScaling::lineWidth)

    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val targetCollector = getGeomTargetCollector(ctx)
        val geomHelper = GeomHelper(pos, coord, ctx)
        val svgElementHelper = geomHelper.createSvgElementHelper()
        val colorsByDataPoint = HintColorUtil.createColorMarkerMapper(GeomKind.SPOKE, ctx)

        for (p in aesthetics.dataPoints()) {
            if (SeriesUtil.allFinite(p.x(), p.y(), p.angle(), p.radius())) {
                val x = p.x()!!
                val y = p.y()!!
                val start = DoubleVector(x, y)
                val spoke = Spoke(p.radius()!!, p.angle()!!)
                val end = getEnd(start, spoke)
                svgElementHelper.createLine(start, end, p)?.let { line ->
                    GeomHelper.decorate(line, p, applyAlphaToAll = true, strokeScaler = AesScaling::lineWidth)
                    root.add(line)
                }
                targetCollector.addPath(
                    listOf(
                        geomHelper.toClient(start, p)!!,
                        geomHelper.toClient(end, p)!!
                    ),
                    { p.index() },
                    GeomTargetCollector.TooltipParams(
                        markerColors = colorsByDataPoint(p)
                    )
                )
            }
        }
    }

    override fun widthSpan(
        p: DataPointAesthetics,
        coordAes: Aes<Double>,
        resolution: Double,
        isDiscrete: Boolean
    ): DoubleSpan? {
        val flip = coordAes == Aes.Y
        val start = xyVec(p, flip) ?: return null
        val spoke = spoke(p) ?: return null
        val end = getEnd(start, spoke)
        return DoubleSpan(start.x, end.x)
    }

    override fun heightSpan(
        p: DataPointAesthetics,
        coordAes: Aes<Double>,
        resolution: Double,
        isDiscrete: Boolean
    ): DoubleSpan? {
        val flip = coordAes == Aes.X
        val start = xyVec(p, flip) ?: return null
        val spoke = spoke(p) ?: return null
        val end = getEnd(start, spoke)
        return DoubleSpan(start.y, end.y)
    }

    private fun xyVec(p: DataPointAesthetics, flip: Boolean): DoubleVector? {
        val x = p.x().takeUnless { flip } ?: p.y()
        val y = p.y().takeUnless { flip } ?: p.x()
        if (!SeriesUtil.allFinite(x, y)) {
            return null
        }

        return DoubleVector(x!!, y!!)
    }

    private fun spoke(p: DataPointAesthetics): Spoke? {
        val angle = p.angle()
        val radius = p.radius()
        if (!SeriesUtil.allFinite(angle, radius)) {
            return null
        }

        return Spoke(radius!!, angle!!)
    }

    private fun getEnd(start: DoubleVector, spoke: Spoke): DoubleVector {
        return DoubleVector(start.x + spoke.dx, start.y + spoke.dy)
    }

    private data class Spoke(val radius: Double, val angle: Double) {
        val dx = radius * cos(angle)
        val dy = radius * sin(angle)
    }

    companion object {
        const val HANDLES_GROUPS = false
    }
}