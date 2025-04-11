/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.geom.util.BarTooltipHelper
import org.jetbrains.letsPlot.core.plot.base.geom.util.BoxHelper
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomHelper
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomUtil.extendHeight
import org.jetbrains.letsPlot.core.plot.base.geom.util.HintColorUtil.colorWithAlpha
import org.jetbrains.letsPlot.core.plot.base.render.LegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import org.jetbrains.letsPlot.core.plot.base.tooltip.TipLayoutHint

class BoxplotGeom : GeomBase(), WithWidth {

    var fattenMidline: Double = DEF_FATTEN_MIDLINE
    var whiskerWidth: Double = DEF_WHISKER_WIDTH
    var widthUnit: DimensionUnit = DEF_WIDTH_UNIT

    override val legendKeyElementFactory: LegendKeyElementFactory
        get() = LEGEND_FACTORY

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
            clientRectByDataPoint(ctx, geomHelper, widthUnit, isHintRect = false)
        )
        buildLines(root, aesthetics, geomHelper)
        BarTooltipHelper.collectRectangleTargets(
            listOf(Aes.YMAX, Aes.UPPER, Aes.MIDDLE, Aes.LOWER, Aes.YMIN),
            aesthetics, pos, coord, ctx,
            clientRectByDataPoint(ctx, geomHelper, widthUnit, isHintRect = true),
            { colorWithAlpha(it) },
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

    private fun buildLines(
        root: SvgRoot,
        aesthetics: Aesthetics,
        geomHelper: GeomHelper
    ) {
        BoxHelper.buildMidlines(
            root,
            aesthetics,
            xAes = Aes.X,
            middleAes = Aes.MIDDLE,
            sizeAes = Aes.WIDTH,
            widthUnit = widthUnit,
            geomHelper,
            fatten = fattenMidline
        )

        val elementHelper = geomHelper.createSvgElementHelper()
        for (p in aesthetics.dataPoints()) {
            val x = p.finiteOrNull(Aes.X) ?: continue
            val w = p.finiteOrNull(Aes.WIDTH) ?: 0.0

            val halfWidth = w * geomHelper.getUnitResolution(widthUnit, Aes.X) / 2
            val halfFenceWidth = halfWidth * whiskerWidth

            // lower whisker
            p.finiteOrNull(Aes.LOWER, Aes.YMIN)?.let { (hinge, fence) ->
                // whisker line
                root.add(
                    elementHelper.createLine(
                        DoubleVector(x, hinge),
                        DoubleVector(x, fence),
                        p
                    )!!.first
                )
                // fence line
                root.add(
                    elementHelper.createLine(
                        DoubleVector(x - halfFenceWidth, fence),
                        DoubleVector(x + halfFenceWidth, fence),
                        p
                    )!!.first
                )
            }

            // upper whisker
            p.finiteOrNull(Aes.UPPER, Aes.YMAX)?.let { (hinge, fence) ->
                // whisker line
                root.add(
                    elementHelper.createLine(
                        DoubleVector(x, hinge),
                        DoubleVector(x, fence),
                        p
                    )!!.first
                )
                // fence line
                root.add(
                    elementHelper.createLine(
                        DoubleVector(x - halfFenceWidth, fence),
                        DoubleVector(x + halfFenceWidth, fence),
                        p
                    )!!.first
                )
            }
        }
    }

    companion object {
        const val DEF_FATTEN_MIDLINE = 2.0
        const val DEF_WHISKER_WIDTH = 0.5
        private val DEF_WIDTH_UNIT: DimensionUnit = DimensionUnit.RESOLUTION
        const val HANDLES_GROUPS = false

        private val LEGEND_FACTORY = BoxHelper.legendFactory(true)

        private fun clientRectByDataPoint(
            ctx: GeomContext,
            geomHelper: GeomHelper,
            widthUnit: DimensionUnit,
            isHintRect: Boolean
        ): (DataPointAesthetics) -> DoubleRectangle? {
            fun factory(p: DataPointAesthetics): DoubleRectangle? {
                val x = p.finiteOrNull(Aes.X) ?: return null
                val lower = p.finiteOrNull(Aes.LOWER) ?: return null
                val upper = p.finiteOrNull(Aes.UPPER) ?: return null
                val w = p.finiteOrNull(Aes.WIDTH) ?: return null

                val width = w * geomHelper.getUnitResolution(widthUnit, Aes.X)
                val rect = DoubleRectangle.XYWH(x - width / 2, lower, width, upper - lower)

                return geomHelper.toClient(rect, p)?.let {
                    if (isHintRect && upper == lower) {
                        // Add tooltips for geom_boxplot with zero height (issue #563)
                        extendHeight(it, 2.0, ctx.flipped)
                    } else {
                        it
                    }
                }
            }

            return ::factory
        }
    }
}
