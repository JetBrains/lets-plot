/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.geom.util.*
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomUtil.ordered_X
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomUtil.with_X_Y
import org.jetbrains.letsPlot.core.plot.base.geom.util.HintsCollection.HintConfigFactory
import org.jetbrains.letsPlot.core.plot.base.render.LegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetCollector
import org.jetbrains.letsPlot.core.plot.base.tooltip.TipLayoutHint.Kind.HORIZONTAL_TOOLTIP
import org.jetbrains.letsPlot.core.plot.base.tooltip.TipLayoutHint.Kind.VERTICAL_TOOLTIP

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

        helper.setAlphaEnabled(false)

        // Confidence interval
        val bands = helper.createBands(dataPoints, GeomUtil.TO_LOCATION_X_YMAX, GeomUtil.TO_LOCATION_X_YMIN)
        root.appendNodes(bands)

        // Regression line
        val regressionLines = helper.createLines(dataPoints, GeomUtil.TO_LOCATION_X_Y)
        root.appendNodes(regressionLines)

        buildHints(dataPoints, pos, coord, ctx)
    }

    private fun buildHints(
        dataPoints: Iterable<DataPointAesthetics>,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val helper = GeomHelper(pos, coord, ctx)

        val linesHelper = LinesHelper(pos, coord, ctx)
        val paths = linesHelper.createPaths(dataPoints, GeomUtil.TO_LOCATION_X_Y)

        val objectRadius = 0.0
        val colorsByDataPoint = HintColorUtil.createColorMarkerMapper(GeomKind.SMOOTH, ctx)

        paths.forEach { path ->
            path.aesthetics.windowed(size = 2) { (aes1, aes2) ->
                val p1 = aes1.finiteVectorOrNull(Aes.X, Aes.Y) ?: return@windowed
                val p2 = aes2.finiteVectorOrNull(Aes.X, Aes.Y) ?: return@windowed
                val client1 = helper.toClient(p1, aes1) ?: return@windowed
                val client2 = helper.toClient(p2, aes2) ?: return@windowed
                val color = aes1.color() ?: return@windowed
                val fill = aes1.fill() ?: return@windowed

                val hint = HintConfigFactory()
                    .defaultObjectRadius(objectRadius)
                    .defaultCoord(p1.x)
                    .defaultKind(if (ctx.flipped) VERTICAL_TOOLTIP else HORIZONTAL_TOOLTIP)
                    .defaultColor(fill, aes1.alpha())

                val hintsCollection = HintsCollection(aes1, helper)
                    .addHint(hint.create(Aes.YMAX))
                    .addHint(hint.create(Aes.YMIN))
                    .addHint(hint.create(Aes.Y).color(color))

                ctx.targetCollector.addPath(
                    points = listOf(client1, client2),
                    localToGlobalIndex = { aes1.index() },
                    GeomTargetCollector.TooltipParams(
                        tipLayoutHints = hintsCollection.hints,
                        markerColors = colorsByDataPoint(aes1)
                    )
                )
            }
        }

    }

    companion object {
        const val HANDLES_GROUPS = true
    }
}
