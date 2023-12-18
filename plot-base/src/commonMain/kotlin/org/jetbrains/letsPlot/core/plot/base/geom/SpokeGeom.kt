/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil.finiteOrNull
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.geom.legend.HLineLegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomHelper
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomUtil
import org.jetbrains.letsPlot.core.plot.base.geom.util.HintColorUtil
import org.jetbrains.letsPlot.core.plot.base.render.LegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetCollector
import kotlin.math.*

class SpokeGeom : GeomBase(), WithWidth, WithHeight {
    var pivot: Pivot = DEF_PIVOT

    override val legendKeyElementFactory: LegendKeyElementFactory
        get() = HLineLegendKeyElementFactory()

    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val targetCollector = getGeomTargetCollector(ctx)
        val geomHelper = GeomHelper(pos, coord, ctx)
        val svgElementHelper = geomHelper.createSvgElementHelper().also {
            it.setStrokeAlphaEnabled(true)
        }
        val colorsByDataPoint = HintColorUtil.createColorMarkerMapper(GeomKind.SPOKE, ctx)

        for (p in aesthetics.dataPoints()) {
            val x = finiteOrNull(p.x()) ?: continue
            val y = finiteOrNull(p.y()) ?: continue
            val base = DoubleVector(x, y)
            val spoke = toSpoke(p) ?: continue
            val start = getStart(base, spoke)
            val end = getEnd(base, spoke)
            svgElementHelper.createLine(start, end, p)?.let { line ->
                root.add(line)

                val clientStart = DoubleVector(line.x1().get()!!, line.y1().get()!!)
                val clientEnd = DoubleVector(line.x2().get()!!, line.y2().get()!!)
                targetCollector.addPath(
                    listOf(clientStart, clientEnd),
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
        return calculateSpan(p, coordAes, Aes.X)
    }

    override fun heightSpan(
        p: DataPointAesthetics,
        coordAes: Aes<Double>,
        resolution: Double,
        isDiscrete: Boolean
    ): DoubleSpan? {
        return calculateSpan(p, coordAes, Aes.Y)
    }

    private fun calculateSpan(
        p: DataPointAesthetics,
        coordAes: Aes<Double>,
        spanAxisAes: Aes<Double>
    ): DoubleSpan? {
        val base = GeomUtil.TO_LOCATION_X_Y(p)?.also {
            if (coordAes != spanAxisAes) it.flip()
        } ?: return null
        val spoke = toSpoke(p) ?: return null
        val start = getStart(base, spoke)
        val end = getEnd(base, spoke)
        return if (spanAxisAes == Aes.X) {
            DoubleSpan(start.x, end.x)
        } else {
            DoubleSpan(start.y, end.y)
        }
    }

    private fun toSpoke(p: DataPointAesthetics): DoubleVector? {
        val angle = finiteOrNull(p.angle()) ?: return null
        val radius = finiteOrNull(p.radius()) ?: return null

        return DoubleVector(radius * cos(angle), radius * sin(angle))
    }

    private fun getStart(base: DoubleVector, spoke: DoubleVector): DoubleVector {
        return when (pivot) {
            Pivot.TAIL -> base
            Pivot.MIDDLE -> base.subtract(spoke.mul(0.5))
            Pivot.TIP -> base.subtract(spoke)
        }
    }

    private fun getEnd(base: DoubleVector, spoke: DoubleVector): DoubleVector {
        return when (pivot) {
            Pivot.TAIL -> base.add(spoke)
            Pivot.MIDDLE -> base.add(spoke.mul(0.5))
            Pivot.TIP -> base
        }
    }

    enum class Pivot {
        TAIL, MIDDLE, TIP
    }

    companion object {
        val DEF_PIVOT = Pivot.TAIL

        const val HANDLES_GROUPS = false
    }
}