/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.aes.AesScaling
import jetbrains.datalore.plot.base.geom.legend.HLineLegendKeyElementFactory
import jetbrains.datalore.plot.base.geom.util.GeomHelper
import jetbrains.datalore.plot.base.geom.util.GeomUtil
import jetbrains.datalore.plot.base.geom.util.HintColorUtil
import jetbrains.datalore.plot.base.interact.GeomTargetCollector.TooltipParams.Companion.tooltip
import jetbrains.datalore.plot.base.interact.TipLayoutHint
import jetbrains.datalore.plot.base.render.LegendKeyElementFactory
import jetbrains.datalore.plot.base.render.SvgRoot
import jetbrains.datalore.vis.svg.SvgLineElement

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

        val viewPort = when {
            ctx.flipped -> ctx.getAesBounds().flip()
            else -> ctx.getAesBounds()
        }
        val colorsByDataPoint = HintColorUtil.createColorMarkerMapper(GeomKind.H_LINE, ctx)

        val lines = ArrayList<SvgLineElement>()

        for (p in GeomUtil.withDefined(aesthetics.dataPoints(), Aes.YINTERCEPT)) {
            val intercept = p.interceptY()!!
            if (viewPort.yRange().contains(intercept)) {
                val start = DoubleVector(viewPort.left, intercept)
                val end = DoubleVector(viewPort.right, intercept)
                val line = helper.createLine(start, end, p)
                lines.add(line)

                val h = AesScaling.strokeWidth(p)
                val origin = DoubleVector(start.x, intercept - h / 2 - 2.0)
                val dimensions = DoubleVector(viewPort.dimension.x, h + 4.0)
                val rect = DoubleRectangle(origin, dimensions)
                ctx.targetCollector.addRectangle(
                    p.index(),
                    geomHelper.toClient(rect, p),
                    tooltip {
                        markerColors = colorsByDataPoint(p)
                    },
                    TipLayoutHint.Kind.CURSOR_TOOLTIP
                )
            }
        }

        lines.forEach { root.add(it) }
    }

    companion object {
        const val HANDLES_GROUPS = false

        val LEGEND_KEY_ELEMENT_FACTORY: LegendKeyElementFactory =
            HLineLegendKeyElementFactory()
    }
}
