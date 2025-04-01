/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom


import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.geom.util.*
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetCollector
import org.jetbrains.letsPlot.core.plot.base.tooltip.TipLayoutHint.Kind.HORIZONTAL_TOOLTIP
import org.jetbrains.letsPlot.core.plot.base.tooltip.TipLayoutHint.Kind.VERTICAL_TOOLTIP

class RibbonGeom : GeomBase() {

    private fun finiteOrNull(x: Double?, y: Double?): DoubleVector? {
        return if (SeriesUtil.isFinite(x) && SeriesUtil.isFinite(y)) {
            DoubleVector(x!!, y!!)
        } else null
    }

    private fun dataPoints(aesthetics: Aesthetics): Iterable<DataPointAesthetics> {
        val data = GeomUtil.withDefined(aesthetics.dataPoints(), Aes.X, Aes.YMIN, Aes.YMAX)
        return GeomUtil.ordered_X(data)
    }

    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val dataPoints = dataPoints(aesthetics)
        val helper = LinesHelper(pos, coord, ctx)

        val upper = { p: DataPointAesthetics -> finiteOrNull(p[Aes.X], p[Aes.YMAX]) }
        val lower = { p: DataPointAesthetics -> finiteOrNull(p[Aes.X], p[Aes.YMIN]) }

        val paths = helper.createBands(dataPoints, upper, lower)
        root.appendNodes(paths)

        //if you want to retain the side edges of ribbon:
        //comment out the following codes, and switch decorate method in LinesHelper.createbands
        helper.setAlphaEnabled(false)

        root.appendNodes(helper.createLines(dataPoints, upper))
        root.appendNodes(helper.createLines(dataPoints, lower))

        buildHints(aesthetics, pos, coord, ctx)
    }

    private fun buildHints(aesthetics: Aesthetics, pos: PositionAdjustment, coord: CoordinateSystem, ctx: GeomContext) {
        val helper = GeomHelper(pos, coord, ctx)
        val colorMapper = HintColorUtil.createColorMarkerMapper(GeomKind.RIBBON, ctx)
        val hint = HintsCollection.HintConfigFactory()
            .defaultObjectRadius(0.0)
            .defaultKind(HORIZONTAL_TOOLTIP.takeUnless { ctx.flipped } ?: VERTICAL_TOOLTIP)

        val location = { p: DataPointAesthetics -> finiteOrNull(p[Aes.X], 0.0) }
        val upper = { p: DataPointAesthetics -> finiteOrNull(p[Aes.X], p[Aes.YMAX]) }
        val lower = { p: DataPointAesthetics -> finiteOrNull(p[Aes.X], p[Aes.YMIN]) }

        for (p in aesthetics.dataPoints()) {
            val x = location(p)?.let { helper.toClient(it, p) }?.x ?: continue
            val top = upper(p)?.let { helper.toClient(it, p) }?.y ?: continue
            val bottom = lower(p)?.let { helper.toClient(it, p) }?.y ?: continue

            hint.defaultCoord(p[Aes.X]!!)
                .defaultColor(p.fill()!!, alpha = null)

            val hintsCollection = HintsCollection(p, helper)
                .addHint(hint.create(Aes.YMAX))
                .addHint(hint.create(Aes.YMIN))

            val tooltipParams = GeomTargetCollector.TooltipParams(
                tipLayoutHints = hintsCollection.hints,
                markerColors = colorMapper(p)
            )

            ctx.targetCollector.addPoint(p.index(), DoubleVector(x, top), 0.0, tooltipParams)
            ctx.targetCollector.addPoint(p.index(), DoubleVector(x, bottom), 0.0, tooltipParams)
        }
    }

    companion object {
        const val HANDLES_GROUPS = true
    }
}
