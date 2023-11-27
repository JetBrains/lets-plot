/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.aes.AesScaling
import org.jetbrains.letsPlot.core.plot.base.geom.legend.HLineLegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomHelper
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomUtil
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomUtil.extendHeight
import org.jetbrains.letsPlot.core.plot.base.geom.util.HintColorUtil
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetCollector
import org.jetbrains.letsPlot.core.plot.base.tooltip.TipLayoutHint
import org.jetbrains.letsPlot.core.plot.base.render.LegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgLineElement

class HLineGeom : GeomBase() {

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
        val colorsByDataPoint = HintColorUtil.createColorMarkerMapper(GeomKind.H_LINE, ctx)

        val lines = ArrayList<SvgLineElement>()

        for (p in GeomUtil.withDefined(aesthetics.dataPoints(), Aes.YINTERCEPT)) {
            val intercept = p.interceptY()!!
            if (viewPort.yRange().contains(intercept)) {
                // line
                val start = DoubleVector(viewPort.left, intercept)
                val end = DoubleVector(viewPort.right, intercept)
                val line = helper.createLine(start, end, p)
                if (line == null) continue
                lines.add(line)

                // tooltip
                val rect = geomHelper.toClient(DoubleRectangle.span(start, end), p)!!
                val h = AesScaling.strokeWidth(p) + 4.0
                val targetRect = extendHeight(rect, h, ctx.flipped)

                ctx.targetCollector.addRectangle(
                    p.index(),
                    targetRect,
                    GeomTargetCollector.TooltipParams(
                        markerColors = colorsByDataPoint(p)
                    ),
                    TipLayoutHint.Kind.CURSOR_TOOLTIP
                )
            }
        }

        lines.forEach { root.add(it) }
    }

    companion object {
        const val HANDLES_GROUPS = false
        val LEGEND_KEY_ELEMENT_FACTORY: LegendKeyElementFactory = HLineLegendKeyElementFactory(AesScaling::strokeWidth)
    }
}
