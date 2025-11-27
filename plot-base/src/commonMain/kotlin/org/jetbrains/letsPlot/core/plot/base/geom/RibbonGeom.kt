/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom


import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.geom.util.*
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomUtil.TO_LOCATION_X_YMAX
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomUtil.TO_LOCATION_X_YMAX_WITH_FINITE_YMIN
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomUtil.TO_LOCATION_X_YMIN
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomUtil.TO_LOCATION_X_YMIN_WITH_FINITE_YMAX
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomUtil.TO_LOCATION_X_ZERO
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetCollector
import org.jetbrains.letsPlot.core.plot.base.tooltip.TipLayoutHint.Kind.HORIZONTAL_TOOLTIP
import org.jetbrains.letsPlot.core.plot.base.tooltip.TipLayoutHint.Kind.VERTICAL_TOOLTIP

class RibbonGeom : GeomBase() {

    override fun prepareDataPoints(dataPoints: Iterable<DataPointAesthetics>): Iterable<DataPointAesthetics> {
        val data = GeomUtil.with_X(dataPoints)
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

        val paths = helper.createBands(dataPoints, TO_LOCATION_X_YMAX_WITH_FINITE_YMIN, TO_LOCATION_X_YMIN_WITH_FINITE_YMAX)
        root.appendNodes(paths)

        //if you want to retain the side edges of ribbon:
        //comment out the following codes, and switch decorate method in LinesHelper.createBands
        helper.setAlphaEnabled(false)

        root.appendNodes(helper.createLines(dataPoints, TO_LOCATION_X_YMAX))
        root.appendNodes(helper.createLines(dataPoints, TO_LOCATION_X_YMIN))

        buildHints(aesthetics, pos, coord, ctx)
    }

    private fun buildHints(aesthetics: Aesthetics, pos: PositionAdjustment, coord: CoordinateSystem, ctx: GeomContext) {
        val helper = GeomHelper(pos, coord, ctx)
        val colorMapper = HintColorUtil.createColorMarkerMapper(GeomKind.RIBBON, ctx)
        val hint = HintsCollection.HintConfigFactory()
            .defaultObjectRadius(0.0)
            .defaultKind(HORIZONTAL_TOOLTIP.takeUnless { ctx.flipped } ?: VERTICAL_TOOLTIP)

        for (p in aesthetics.dataPoints()) {
            val x = TO_LOCATION_X_ZERO(p)?.let { helper.toClient(it, p) }?.x ?: continue
            val top = TO_LOCATION_X_YMAX(p)?.let { helper.toClient(it, p) }?.y
            val bottom = TO_LOCATION_X_YMIN(p)?.let { helper.toClient(it, p) }?.y

            hint.defaultCoord(p[Aes.X]!!)
                .defaultColor(p.fill()!!, alpha = null)

            val hintsCollection = HintsCollection(p, helper)
                .addHint(hint.create(Aes.YMAX))
                .addHint(hint.create(Aes.YMIN))

            val tooltipParams = GeomTargetCollector.TooltipParams(
                tipLayoutHints = hintsCollection.hints,
                markerColors = colorMapper(p)
            )

            if (top != null) {
                ctx.targetCollector.addPoint(p.index(), DoubleVector(x, top), 0.0, tooltipParams)
            }

            if (bottom != null) {
                ctx.targetCollector.addPoint(p.index(), DoubleVector(x, bottom), 0.0, tooltipParams)
            }
        }
    }

    companion object {
        const val HANDLES_GROUPS = true
    }
}
