/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.Aesthetics
import jetbrains.datalore.plot.base.CoordinateSystem
import jetbrains.datalore.plot.base.GeomContext
import jetbrains.datalore.plot.base.PositionAdjustment
import jetbrains.datalore.plot.base.aes.AesScaling
import jetbrains.datalore.plot.base.geom.legend.VLineLegendKeyElementFactory
import jetbrains.datalore.plot.base.geom.util.GeomHelper
import jetbrains.datalore.plot.base.geom.util.HintColorUtil
import jetbrains.datalore.plot.base.interact.GeomTargetCollector
import jetbrains.datalore.plot.base.render.LegendKeyElementFactory
import jetbrains.datalore.plot.base.render.SvgRoot
import jetbrains.datalore.plot.common.data.SeriesUtil
import jetbrains.datalore.vis.svg.SvgLineElement
import kotlin.math.max

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

        val viewPort = aesViewPort(aesthetics)

        val lines = ArrayList<SvgLineElement>()
        for (p in aesthetics.dataPoints()) {
            val intercept = p.interceptX()
            if (SeriesUtil.isFinite(intercept)) {
                if (viewPort.xRange().contains(intercept!!)) {
                    val start = DoubleVector(intercept, viewPort.top)
                    val end = DoubleVector(intercept, viewPort.bottom)

                    val width = max(AesScaling.strokeWidth(p), 2.0) * 2.0
                    val origin = DoubleVector(intercept - width / 2, end.y)
                    val dimensions = DoubleVector(width, 0.0)
                    val rect = DoubleRectangle(origin, dimensions)

                    if (coord.contains(rect)) {
                        val line = helper.createLine(start, end, p)
                        lines.add(line)

                        ctx.targetCollector.addRectangle(
                            p.index(),
                            geomHelper.toClient(rect, p),
                            GeomTargetCollector.TooltipParams.params()
                                .setColor(HintColorUtil.fromColor(p))
                        )
                    }
                }
            }
        }

        lines.forEach { root.add(it) }
    }

    companion object {
        const val HANDLES_GROUPS = false
        val LEGEND_KEY_ELEMENT_FACTORY: LegendKeyElementFactory =
            VLineLegendKeyElementFactory()
    }
}
