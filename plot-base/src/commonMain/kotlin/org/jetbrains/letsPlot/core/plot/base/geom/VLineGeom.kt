/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.aes.AesScaling
import org.jetbrains.letsPlot.core.plot.base.geom.legend.VLineLegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomHelper
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomUtil
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomUtil.extend
import org.jetbrains.letsPlot.core.plot.base.geom.util.HintColorUtil
import org.jetbrains.letsPlot.core.plot.base.render.LegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetCollector

class VLineGeom : GeomBase() {

    override val legendKeyElementFactory: LegendKeyElementFactory
        get() = LEGEND_KEY_ELEMENT_FACTORY

    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val geomHelper = GeomHelper(pos, coord, ctx)
        val helper = geomHelper.createSvgElementHelper()
        helper.setStrokeAlphaEnabled(true)

        val viewPort = overallAesBounds(ctx)
        val colorMarkerMapper = HintColorUtil.createColorMarkerMapper(GeomKind.V_LINE, ctx)

        for (p in GeomUtil.withDefined(aesthetics.dataPoints(), Aes.XINTERCEPT)) {
            val intercept = p.interceptX()!!
            if (viewPort.xRange().contains(intercept)) {
                // line
                val start = DoubleVector(intercept, viewPort.top)
                val end = DoubleVector(intercept, viewPort.bottom)

                if (coord.isLinear) {

                    val line = helper.createLine(start, end, p) ?: continue
                    root.add(line)

                    // tooltip
                    val rect = geomHelper.toClient(DoubleRectangle.span(start, end), p)!!
                    val widthExpand = AesScaling.strokeWidth(p) + 4.0
                    // The tooltip point is on the top of the rectangle = on the plot border.
                    // To ensure that it will be displayed, move the rectangle a little inside the plot
                    // https://github.com/JetBrains/lets-plot/issues/610
                    val heightExpand = -2.0
                    val targetRect = extend(rect, ctx.flipped, widthExpand, heightExpand)

                    ctx.targetCollector.addRectangle(
                        p.index(),
                        targetRect,
                        GeomTargetCollector.TooltipParams(
                            markerColors = colorMarkerMapper(p)
                        )
                    )
                } else {
                    val (svgPath, lineString) = helper.createResampledLine(start, end, p)
                    root.add(svgPath)
                    ctx.targetCollector.addPath(
                        lineString,
                        { p.index() },
                        GeomTargetCollector.TooltipParams(markerColors = colorMarkerMapper(p))
                    )
                }
            }
        }
    }

    companion object {
        const val HANDLES_GROUPS = false
        val LEGEND_KEY_ELEMENT_FACTORY: LegendKeyElementFactory = VLineLegendKeyElementFactory()
    }
}
