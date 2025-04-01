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
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomHelper
import org.jetbrains.letsPlot.core.plot.base.geom.util.HintColorUtil
import org.jetbrains.letsPlot.core.plot.base.geom.util.VerticalGeomHelper
import org.jetbrains.letsPlot.core.plot.base.render.LegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import org.jetbrains.letsPlot.core.plot.base.tooltip.TipLayoutHint

class CrossBarGeom : GeomBase(), WithWidth, WithHeight {

    private val verticalHelper = VerticalGeomHelper()
    var fattenMidline: Double = 2.5
    var widthUnit: DimensionUnit = DEF_WIDTH_UNIT

    override val legendKeyElementFactory: LegendKeyElementFactory
        get() = LEGEND_FACTORY

    override fun updateAestheticsDefaults(aestheticDefaults: AestheticsDefaults, flipped: Boolean): AestheticsDefaults {
        // 'isVertical' is no longer available, so we need to use 'flipped' to correctly remove the default value
        return if (flipped) {
            aestheticDefaults.with(Aes.X, Double.NaN) // The middle bar is optional
        } else {
            aestheticDefaults.with(Aes.Y, Double.NaN)
        }
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
            xAes = Aes.X,
            middleAes = Aes.Y,
            sizeAes = Aes.WIDTH, // do not flip as height is not defined for CrossBarGeom
            widthUnit = widthUnit,
            geomHelper,
            fatten = fattenMidline,
            flip = false
        )
        // tooltip
        verticalHelper.buildHints(
            hintAesList = listOf(Aes.YMIN, Aes.Y, Aes.YMAX),
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
        // height is not defined for CrossBarGeom, so after flipping the width aesthetic is responsible for the thickness
        return DimensionsUtil.dimensionSpan(p, coordAes, Aes.WIDTH, resolution, widthUnit)
    }

    private fun clientRectByDataPoint(
        geomHelper: GeomHelper
    ): (DataPointAesthetics) -> DoubleRectangle? {
        fun factory(p: DataPointAesthetics): DoubleRectangle? {
            val x = p.finiteOrNull(Aes.X) ?: return null
            val ymin = p.finiteOrNull(Aes.YMIN) ?: return null
            val ymax = p.finiteOrNull(Aes.YMAX) ?: return null
            val w = p.finiteOrNull(Aes.WIDTH) ?: return null

            val width = w * geomHelper.getUnitResolution(widthUnit, Aes.X)
            val origin = DoubleVector(x - width / 2, ymin)
            val dimension = DoubleVector(width, ymax - ymin)
            return DoubleRectangle(origin, dimension)
        }

        return { p ->
            factory(p)?.let { rect ->
                geomHelper.toClient(rect, p)
            }
        }
    }

    companion object {
        const val HANDLES_GROUPS = false
        private val LEGEND_FACTORY = BoxHelper.legendFactory(false)
        private val DEF_WIDTH_UNIT: DimensionUnit = DimensionUnit.RESOLUTION
    }
}
