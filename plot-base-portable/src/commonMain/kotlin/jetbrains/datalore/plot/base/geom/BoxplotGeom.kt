/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.geom.util.*
import jetbrains.datalore.plot.base.geom.util.GeomUtil.extendHeight
import jetbrains.datalore.plot.base.geom.util.HintColorUtil.colorWithAlpha
import jetbrains.datalore.plot.base.interact.TipLayoutHint
import jetbrains.datalore.plot.base.render.LegendKeyElementFactory
import jetbrains.datalore.plot.base.render.SvgRoot
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgLineElement

class BoxplotGeom : GeomBase() {

    var fattenMidline: Double = DEF_FATTEN_MIDLINE
    var whiskerWidth: Double = DEF_WHISKER_WIDTH

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
        CrossBarHelper.buildBoxes(
            root, aesthetics, pos, coord, ctx,
            clientRectByDataPoint(ctx, geomHelper, isHintRect = false)
        )
        buildLines(root, aesthetics, ctx, geomHelper)
        BarTooltipHelper.collectRectangleTargets(
            listOf(Aes.YMAX, Aes.UPPER, Aes.MIDDLE, Aes.LOWER, Aes.YMIN),
            aesthetics, pos, coord, ctx,
            clientRectByDataPoint(ctx, geomHelper, isHintRect = true),
            { colorWithAlpha(it) },
            defaultTooltipKind = TipLayoutHint.Kind.CURSOR_TOOLTIP
        )
    }

    private fun buildLines(
        root: SvgRoot,
        aesthetics: Aesthetics,
        ctx: GeomContext,
        geomHelper: GeomHelper
    ) {
        CrossBarHelper.buildMidlines(root, aesthetics, ctx, geomHelper, fattenMidline)

        val elementHelper = geomHelper.createSvgElementHelper()
        for (p in GeomUtil.withDefined(aesthetics.dataPoints(), Aes.X)) {
            val x = p.x()!!
            val halfWidth = p.width()?.let { it * ctx.getResolution(Aes.X) / 2 } ?: 0.0
            val halfFenceWidth = halfWidth * whiskerWidth

            val lines = ArrayList<SvgLineElement>()

            // lower whisker
            if (p.defined(Aes.LOWER) && p.defined(Aes.YMIN)) {
                val hinge = p.lower()!!
                val fence = p.ymin()!!
                // whisker line
                lines.add(
                    elementHelper.createLine(
                        DoubleVector(x, hinge),
                        DoubleVector(x, fence),
                        p
                    )!!
                )
                // fence line
                lines.add(
                    elementHelper.createLine(
                        DoubleVector(x - halfFenceWidth, fence),
                        DoubleVector(x + halfFenceWidth, fence),
                        p
                    )!!
                )
            }

            // upper whisker
            if (p.defined(Aes.UPPER) && p.defined(Aes.YMAX)) {
                val hinge = p.upper()!!
                val fence = p.ymax()!!
                // whisker line
                lines.add(
                    elementHelper.createLine(
                        DoubleVector(x, hinge),
                        DoubleVector(x, fence),
                        p
                    )!!
                )
                // fence line
                lines.add(
                    elementHelper.createLine(
                        DoubleVector(x - halfFenceWidth, fence),
                        DoubleVector(x + halfFenceWidth, fence),
                        p
                    )!!
                )

                lines.forEach { root.add(it) }
            }
        }
    }

    companion object {
        const val DEF_FATTEN_MIDLINE = 2.0
        const val DEF_WHISKER_WIDTH = 0.5
        const val HANDLES_GROUPS = false

        private val LEGEND_FACTORY = CrossBarHelper.legendFactory(true)

        private fun clientRectByDataPoint(
            ctx: GeomContext,
            geomHelper: GeomHelper,
            isHintRect: Boolean
        ): (DataPointAesthetics) -> DoubleRectangle? {
            return { p ->
                val clientRect = if (p.defined(Aes.X) &&
                    p.defined(Aes.LOWER) &&
                    p.defined(Aes.UPPER) &&
                    p.defined(Aes.WIDTH)
                ) {
                    val x = p.x()!!
                    val lower = p.lower()!!
                    val upper = p.upper()!!
                    val width = p.width()!! * ctx.getResolution(Aes.X)
                    geomHelper.toClient(
                        DoubleRectangle.XYWH(x - width / 2, lower, width, upper - lower),
                        p
                    )?.let {
                        if (isHintRect && upper == lower) {
                            // Add tooltips for geom_boxplot with zero height (issue #563)
                            extendHeight(it, 2.0, ctx.flipped)
                        } else {
                            it
                        }
                    }
                } else {
                    null
                }
                clientRect
            }
        }
    }
}
