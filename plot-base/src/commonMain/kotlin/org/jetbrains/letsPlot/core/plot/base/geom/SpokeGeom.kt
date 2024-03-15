/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.geom.legend.HLineLegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomHelper
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomUtil
import org.jetbrains.letsPlot.core.plot.base.geom.util.TargetCollectorHelper
import org.jetbrains.letsPlot.core.plot.base.render.LegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import kotlin.math.cos
import kotlin.math.sin

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
        val tooltipHelper = TargetCollectorHelper(GeomKind.SPOKE, ctx)
        val geomHelper = GeomHelper(pos, coord, ctx)
        val svgElementHelper = geomHelper.createSvgElementHelper()
        svgElementHelper.setStrokeAlphaEnabled(true)
        svgElementHelper.setGeometryHandler { aes, lineString -> tooltipHelper.addLine(lineString, aes) }

        for (p in aesthetics.dataPoints()) {
            val x = p.finiteOrNull(Aes.X) ?: continue
            val y = p.finiteOrNull(Aes.Y) ?: continue
            val spoke = toSpoke(p) ?: continue
            val base = DoubleVector(x, y)
            val start = getStart(base, spoke, pivot)
            val end = getEnd(base, spoke, pivot)
            val line = svgElementHelper.createLine(start, end, p) ?: continue

            root.add(line)
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
        val loc = GeomUtil.TO_LOCATION_X_Y(p) ?: return null
        val base = loc.flipIf(coordAes != spanAxisAes)
        val spoke = toSpoke(p) ?: return null
        val start = getStart(base, spoke, pivot)
        val end = getEnd(base, spoke, pivot)
        return if (spanAxisAes == Aes.X) {
            DoubleSpan(start.x, end.x)
        } else {
            DoubleSpan(start.y, end.y)
        }
    }

    private fun toSpoke(p: DataPointAesthetics): DoubleVector? {
        val angle = p.finiteOrNull(Aes.ANGLE) ?: return null
        val radius = p.finiteOrNull(Aes.RADIUS) ?: return null

        return getSpoke(angle, radius)
    }

    enum class Pivot {
        TAIL, MIDDLE, TIP
    }

    companion object {
        val DEF_PIVOT = Pivot.TAIL

        fun createGeometry(
            x: Double,
            y: Double,
            angle: Double,
            radius: Double,
            pivot: Pivot
        ): List<DoubleVector> {
            val base = DoubleVector(x, y)
            val spoke = getSpoke(angle, radius)
            return listOf(getStart(base, spoke, pivot), getEnd(base, spoke, pivot))
        }

        private fun getStart(base: DoubleVector, spoke: DoubleVector, pivot: Pivot): DoubleVector {
            return when (pivot) {
                Pivot.TAIL -> base
                Pivot.MIDDLE -> base.subtract(spoke.mul(0.5))
                Pivot.TIP -> base.subtract(spoke)
            }
        }

        private fun getEnd(base: DoubleVector, spoke: DoubleVector, pivot: Pivot): DoubleVector {
            return when (pivot) {
                Pivot.TAIL -> base.add(spoke)
                Pivot.MIDDLE -> base.add(spoke.mul(0.5))
                Pivot.TIP -> base
            }
        }

        private fun getSpoke(angle: Double, radius: Double): DoubleVector {
            return DoubleVector(radius * cos(angle), radius * sin(angle))
        }

        const val HANDLES_GROUPS = false
    }
}