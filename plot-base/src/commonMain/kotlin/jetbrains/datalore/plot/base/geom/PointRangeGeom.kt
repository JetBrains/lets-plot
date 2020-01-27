/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.geom.legend.CompositeLegendKeyElementFactory
import jetbrains.datalore.plot.base.geom.legend.VLineLegendKeyElementFactory
import jetbrains.datalore.plot.base.geom.util.GeomHelper
import jetbrains.datalore.plot.base.geom.util.GeomUtil
import jetbrains.datalore.plot.base.geom.util.HintColorUtil.fromColor
import jetbrains.datalore.plot.base.geom.util.HintsCollection
import jetbrains.datalore.plot.base.geom.util.HintsCollection.HintConfigFactory
import jetbrains.datalore.plot.base.interact.GeomTargetCollector.TooltipParams.Companion.params
import jetbrains.datalore.plot.base.interact.TipLayoutHint.Kind.HORIZONTAL_TOOLTIP
import jetbrains.datalore.plot.base.render.LegendKeyElementFactory
import jetbrains.datalore.plot.base.render.SvgRoot
import jetbrains.datalore.plot.base.render.point.PointShapeSvg
import kotlin.math.max

class PointRangeGeom : GeomBase() {
    var fattenMidPoint: Double = DEF_FATTEN

    override val legendKeyElementFactory: LegendKeyElementFactory
        get() = CompositeLegendKeyElementFactory(
            VLineLegendKeyElementFactory(),
            PointLegendKeyElementFactory(DEF_FATTEN)
        )


    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val geomHelper = GeomHelper(pos, coord, ctx)
        val helper = geomHelper.createSvgElementHelper()

        for (p in GeomUtil.withDefined(aesthetics.dataPoints(), Aes.X, Aes.Y, Aes.YMIN, Aes.YMAX)) {
            val x = p.x()!!
            val y = p.y()!!
            val ymin = p.ymin()!!
            val ymax = p.ymax()!!

            // vertical line
            val start = DoubleVector(x, ymin)
            val end = DoubleVector(x, ymax)
            val line = helper.createLine(start, end, p)
            root.add(line)
            buildHints(start, end, p, ctx, geomHelper)

            // mid-point
            val location = geomHelper.toClient(DoubleVector(x, y), p)
            val shape = p.shape()!!
            val o = PointShapeSvg.create(shape, location, p, fattenMidPoint)
            root.add(wrap(o))
//            ctx.targetCollector.addPoint(
//                p.index(),
//                location,
//                shape.size(p) * fattenMidline / 2,
//                PointGeom.tooltipParams(p)
//            )
        }
    }

    private fun buildHints(
        start: DoubleVector,
        end: DoubleVector,
        p: DataPointAesthetics,
        ctx: GeomContext,
        geomHelper: GeomHelper
    ) {
        val width = max(p.width()!!, 2.0)
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

        const val DEF_FATTEN = 4.0
    }
}
