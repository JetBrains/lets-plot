/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom

import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.geom.util.*
import jetbrains.datalore.plot.base.geom.util.GeomUtil.ordered_X
import jetbrains.datalore.plot.base.geom.util.GeomUtil.with_X_Y
import jetbrains.datalore.plot.base.geom.util.HintsCollection.HintConfigFactory
import jetbrains.datalore.plot.base.interact.GeomTargetCollector
import jetbrains.datalore.plot.base.interact.TipLayoutHint.Kind.HORIZONTAL_TOOLTIP
import jetbrains.datalore.plot.base.interact.TipLayoutHint.Kind.VERTICAL_TOOLTIP
import jetbrains.datalore.plot.base.render.LegendKeyElementFactory
import jetbrains.datalore.plot.base.render.SvgRoot

class SmoothGeom : GeomBase() {

    override val legendKeyElementFactory: LegendKeyElementFactory
        get() = HLineGeom.LEGEND_KEY_ELEMENT_FACTORY

    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val dataPoints = ordered_X(with_X_Y(aesthetics.dataPoints()))
        val helper = LinesHelper(pos, coord, ctx)

        // Regression line
        helper.setAlphaEnabled(false)
        val regressionLines = helper.createLines(dataPoints, GeomUtil.TO_LOCATION_X_Y)
        appendNodes(regressionLines, root)

        // Confidence interval
        helper.setAlphaFilter(PROPORTION)
        helper.setWidthFilter(ZERO)
        val bands = helper.createBands(dataPoints, GeomUtil.TO_LOCATION_X_YMAX, GeomUtil.TO_LOCATION_X_YMIN)
        appendNodes(bands, root)

        buildHints(dataPoints, pos, coord, ctx)
    }

    private fun buildHints(
        dataPoints: Iterable<DataPointAesthetics>,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val helper = GeomHelper(pos, coord, ctx)
        val colorsByDataPoint = HintColorUtil.createColorMarkerMapper(GeomKind.SMOOTH, ctx)
        for (p in dataPoints) {
            val pX = p.x()!!
            val pY = p.y()!!
            val objectRadius = 0.0

            val hint = HintConfigFactory()
                .defaultObjectRadius(objectRadius)
                .defaultCoord(pX)
                .defaultKind(
                    if (ctx.flipped) VERTICAL_TOOLTIP else HORIZONTAL_TOOLTIP
                )
                .defaultColor(
                    p.fill()!!,
                    PROPORTION(p.alpha())
                )

            val hintsCollection = HintsCollection(p, helper)
                .addHint(hint.create(Aes.YMAX))
                .addHint(hint.create(Aes.YMIN))
                .addHint(hint.create(Aes.Y).color(p.color()!!))

            val clientCoord = helper.toClient(pX, pY, p)!!
            ctx.targetCollector.addPoint(
                p.index(), clientCoord, objectRadius,
                GeomTargetCollector.TooltipParams(
                    tipLayoutHints = hintsCollection.hints,
                    markerColors = colorsByDataPoint(p)
                )
            )
        }
    }

    companion object {
        const val HANDLES_GROUPS = true

        private val PROPORTION = { v: Double? -> if (v == null) null else v / 10 }
        private val ZERO = { _: Double? -> 0.0 }
    }
}
