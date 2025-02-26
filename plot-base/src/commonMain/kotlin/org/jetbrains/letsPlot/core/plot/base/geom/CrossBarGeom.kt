/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.aes.AestheticsDefaults
import org.jetbrains.letsPlot.core.plot.base.geom.util.BoxHelper
import org.jetbrains.letsPlot.core.plot.base.geom.util.FlippableGeomHelper
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomHelper
import org.jetbrains.letsPlot.core.plot.base.geom.util.HintColorUtil
import org.jetbrains.letsPlot.core.plot.base.render.LegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import org.jetbrains.letsPlot.core.plot.base.tooltip.TipLayoutHint

class CrossBarGeom(
    private val isVertical: Boolean
) : GeomBase(), WithWidth, WithHeight {

    private val flipHelper = FlippableGeomHelper(isVertical)
    var fattenMidline: Double = 2.5
    var widthUnit: DimensionUnit = DEF_WIDTH_UNIT

    override val legendKeyElementFactory: LegendKeyElementFactory
        get() = LEGEND_FACTORY

    override val wontRender: List<Aes<*>>
        get() {
            return listOf(Aes.XMIN, Aes.XMAX).map(::afterRotation)
        }

    override fun updateAestheticsDefaults(aestheticDefaults: AestheticsDefaults): AestheticsDefaults {
        return if (isVertical) {
            aestheticDefaults.with(Aes.Y, Double.NaN) // The middle bar is optional
        } else {
            aestheticDefaults.with(Aes.X, Double.NaN)
        }
    }

    private fun afterRotation(aes: Aes<Double>): Aes<Double> {
        return flipHelper.getEffectiveAes(aes)
    }

    private fun afterRotation(rectangle: DoubleRectangle): DoubleRectangle {
        return flipHelper.flip(rectangle)
    }

    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val geomHelper = GeomHelper(pos, coord, ctx)
        BoxHelper.buildBoxes(
            root, aesthetics, pos, coord, ctx,
            rectFactory = clientRectByDataPoint(geomHelper)
        )
        BoxHelper.buildMidlines(
            root,
            aesthetics,
            xAes = afterRotation(Aes.X),
            middleAes = afterRotation(Aes.Y),
            sizeAes = Aes.WIDTH, // do not flip as height is not defined for CrossBarGeom
            widthUnit = widthUnit,
            geomHelper,
            fatten = fattenMidline,
            flip = !isVertical
        )
        // tooltip
        flipHelper.buildHints(
            hintAesList = listOf(Aes.YMIN, Aes.Y, Aes.YMAX).map(::afterRotation),
            aesthetics = aesthetics,
            pos = pos,
            coord = coord,
            ctx = ctx,
            clientRectFactory = clientRectByDataPoint(geomHelper),
            fillColorMapper = { HintColorUtil.colorWithAlpha(it) },
            defaultTooltipKind = TipLayoutHint.Kind.CURSOR_TOOLTIP
        )
    }

    override fun widthSpan(
        p: DataPointAesthetics,
        coordAes: Aes<Double>,
        resolution: Double,
        isDiscrete: Boolean
    ): DoubleSpan? {
        return DimensionsUtil.dimensionSpan(p, coordAes, Aes.WIDTH, resolution, widthUnit)
    }

    override fun heightSpan(
        p: DataPointAesthetics,
        coordAes: Aes<Double>,
        resolution: Double,
        isDiscrete: Boolean
    ): DoubleSpan? {
        return DimensionsUtil.dimensionSpan(p, coordAes, Aes.WIDTH, resolution, widthUnit)
    }

    private fun clientRectByDataPoint(
        geomHelper: GeomHelper
    ): (DataPointAesthetics) -> DoubleRectangle? {
        val xAes = afterRotation(Aes.X)
        val yMinAes = afterRotation(Aes.YMIN)
        val yMaxAes = afterRotation(Aes.YMAX)
        val widthAes = Aes.WIDTH // do not flip as height is not defined for CrossBarGeom

        fun factory(p: DataPointAesthetics): DoubleRectangle? {
            val x = p.finiteOrNull(xAes) ?: return null
            val ymin = p.finiteOrNull(yMinAes) ?: return null
            val ymax = p.finiteOrNull(yMaxAes) ?: return null
            val w = p.finiteOrNull(widthAes) ?: return null

            val width = geomHelper.transformDimensionValue(w, widthUnit, xAes)
            val origin = DoubleVector(x - width / 2, ymin)
            val dimension = DoubleVector(width, ymax - ymin)
            return DoubleRectangle(origin, dimension)
        }

        return { p ->
            factory(p)?.let { rect ->
                geomHelper.toClient(afterRotation(rect), p)
            }
        }
    }

    companion object {
        const val HANDLES_GROUPS = false
        private val LEGEND_FACTORY = BoxHelper.legendFactory(false)
        private val DEF_WIDTH_UNIT: DimensionUnit = DimensionUnit.RESOLUTION
    }
}
