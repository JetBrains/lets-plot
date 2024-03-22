/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.geom.legend.HLineLegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.geom.util.ArrowSpec
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomHelper
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomHelper.SvgElementHelper
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomUtil.toLocation
import org.jetbrains.letsPlot.core.plot.base.geom.util.TargetCollectorHelper
import org.jetbrains.letsPlot.core.plot.base.render.LegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot

class SpokeGeom : GeomBase(), WithWidth, WithHeight {
    var arrowSpec: ArrowSpec? = null
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
            .setStrokeAlphaEnabled(true)
            .setArrowSpec(arrowSpec)

        for (p in aesthetics.dataPoints()) {
            val start = p.toLocation(Aes.X, Aes.Y) ?: continue
            val angle = p.finiteOrNull(Aes.ANGLE) ?: continue
            val radius = p.finiteOrNull(Aes.RADIUS) ?: continue
            val (svg, geometry) = svgElementHelper.createSpoke(start, angle, radius, pivot.factor, p) ?: continue

            tooltipHelper.addLine(geometry, p)
            root.add(svg)
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
        val base = p.toLocation(Aes.X, Aes.Y)?.flipIf(coordAes != spanAxisAes) ?: return null
        val angle = p.finiteOrNull(Aes.ANGLE) ?: return null
        val radius = p.finiteOrNull(Aes.RADIUS) ?: return null
        val elementHelper = SvgElementHelper()
        val (_, geometry) = elementHelper.createSpoke(base, angle, radius, pivot.factor, p) ?: return null

        require(geometry.size == 2)
        val (start, end) = geometry

        return if (spanAxisAes == Aes.X) {
            DoubleSpan(start.x, end.x)
        } else {
            DoubleSpan(start.y, end.y)
        }
    }

    enum class Pivot(
        val factor: Double
    ) {
        TAIL(0.0), MIDDLE(0.5), TIP(1.0)
    }

    companion object {
        val DEF_PIVOT = Pivot.TAIL
        const val HANDLES_GROUPS = false
    }
}