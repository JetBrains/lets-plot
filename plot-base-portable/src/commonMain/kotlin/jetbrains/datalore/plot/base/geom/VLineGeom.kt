/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.aes.AesScaling
import jetbrains.datalore.plot.base.geom.legend.VLineLegendKeyElementFactory
import jetbrains.datalore.plot.base.geom.util.GeomHelper
import jetbrains.datalore.plot.base.geom.util.GeomUtil
import jetbrains.datalore.plot.base.geom.util.GeomUtil.extendTrueWidth
import jetbrains.datalore.plot.base.geom.util.HintColorUtil
import jetbrains.datalore.plot.base.interact.GeomTargetCollector
import jetbrains.datalore.plot.base.render.LegendKeyElementFactory
import jetbrains.datalore.plot.base.render.SvgRoot
import jetbrains.datalore.vis.svg.SvgLineElement

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

        val lines = ArrayList<SvgLineElement>()
        for (p in GeomUtil.withDefined(aesthetics.dataPoints(), Aes.XINTERCEPT)) {
            val intercept = p.interceptX()!!
            if (viewPort.xRange().contains(intercept)) {
                // line
                val start = DoubleVector(intercept, viewPort.top)
                val end = DoubleVector(intercept, viewPort.bottom)
                val line = helper.createLine(start, end, p)
                if (line == null) continue
                lines.add(line)

                // tooltip
                val rect = geomHelper.toClient(DoubleRectangle.span(start, end), p)!!
                val w = AesScaling.strokeWidth(p) + 4.0
                val targetRect = extendTrueWidth(rect, w, ctx).let {
                    // The tooltip point is on the top of the rectangle = on the plot border.
                    // To ensure that it will be displayed, move the rectangle a little inside the plot.
                    // https://github.com/JetBrains/lets-plot/issues/610:
                    if (!ctx.flipped) {
                        DoubleRectangle(it.xRange(), it.yRange().expanded(-2.0))
                    } else {
                        it
                    }
                }

                ctx.targetCollector.addRectangle(
                    p.index(),
                    targetRect,
                    GeomTargetCollector.TooltipParams(
                        markerColors = colorMarkerMapper(p)
                    )
                )
            }
        }

        lines.forEach { root.add(it) }
    }

    companion object {
        const val HANDLES_GROUPS = false
        val LEGEND_KEY_ELEMENT_FACTORY: LegendKeyElementFactory = VLineLegendKeyElementFactory()
    }
}
