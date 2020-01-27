/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleSegment
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.aes.AesScaling
import jetbrains.datalore.plot.base.geom.util.GeomHelper
import jetbrains.datalore.plot.base.geom.util.GeomUtil
import jetbrains.datalore.plot.base.geom.util.HintColorUtil.fromColor
import jetbrains.datalore.plot.base.geom.util.HintsCollection
import jetbrains.datalore.plot.base.geom.util.HintsCollection.HintConfigFactory
import jetbrains.datalore.plot.base.geom.util.LinesHelper
import jetbrains.datalore.plot.base.interact.GeomTargetCollector.TooltipParams.Companion.params
import jetbrains.datalore.plot.base.interact.TipLayoutHint.Kind.HORIZONTAL_TOOLTIP
import jetbrains.datalore.plot.base.render.LegendKeyElementFactory
import jetbrains.datalore.plot.base.render.SvgRoot
import jetbrains.datalore.plot.common.data.SeriesUtil
import jetbrains.datalore.vis.svg.SvgGElement
import jetbrains.datalore.vis.svg.SvgLineElement

class LineRangeGeom : GeomBase() {

    override val legendKeyElementFactory: LegendKeyElementFactory
        get() = VLineGeom.LEGEND_KEY_ELEMENT_FACTORY

    private fun dataPoints(aesthetics: Aesthetics): Iterable<DataPointAesthetics> {
        return GeomUtil.with_X_Y(aesthetics.dataPoints())
    }

    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val dataPoints = dataPoints(aesthetics)
        val geomHelper = GeomHelper(pos, coord, ctx)
        val helper = geomHelper.createSvgElementHelper()

        val lines = ArrayList<SvgLineElement>()
        for (p in dataPoints) {
            val x = p.x()
            val ymin = p.ymin()
            val ymax = p.ymax()
            if (!SeriesUtil.allFinite(x, ymin, ymax)) {
                continue
            }

            val start = DoubleVector(x!!, ymin!!)
            val end = DoubleVector(x, ymax!!)
            val line = helper.createLine(start, end, p)
            lines.add(line)
            buildHints(start, end, p, ctx, geomHelper)
        }

        lines.forEach { root.add(it) }
    }

    private fun buildHints(
        start: DoubleVector,
        end: DoubleVector,
        p: DataPointAesthetics,
        ctx: GeomContext,
        geomHelper: GeomHelper
    ) {
        val width = 2.0
        val objectRadius = width / 2
        val height = start.y - end.y
        val clientRect = geomHelper.toClient(DoubleRectangle(start.x - objectRadius, start.y, width, height), p)

        val hint = HintConfigFactory()
            .defaultObjectRadius(objectRadius)
            .defaultX(p.x()!!)
            .defaultKind(HORIZONTAL_TOOLTIP)

        val hints = HintsCollection(p, geomHelper)
            .addHint(hint.create(Aes.YMAX))
            .addHint(hint.create(Aes.YMIN))
            .hints

        ctx.targetCollector.addRectangle(
            p.index(), clientRect,
            params()
                .setTipLayoutHints(hints)
                .setColor(fromColor(p))
        )
    }

    companion object {
        const val HANDLES_GROUPS = false
    }
}
