/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom.util

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.geom.util.HintColorUtil.createColorMarkerMapper
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetCollector
import org.jetbrains.letsPlot.core.plot.base.tooltip.TipLayoutHint
import org.jetbrains.letsPlot.core.plot.base.tooltip.TipLayoutHint.Kind.*

class RectangleTooltipHelper(
    private val pos: PositionAdjustment,
    private val coord: CoordinateSystem,
    private val ctx: GeomContext,
    private val hintAesList: List<Aes<Double>> = emptyList(),
    private val tooltipKind: TipLayoutHint.Kind = VERTICAL_TOOLTIP.takeIf { ctx.flipped } ?: HORIZONTAL_TOOLTIP,
    private val fillColorMapper: (DataPointAesthetics) -> Color? = { null },
    private val colorMarkerMapper: (DataPointAesthetics) -> List<Color> = createColorMarkerMapper(null, ctx),
) {
    private val helper = GeomHelper(pos, coord, ctx)


    fun addTarget(p: DataPointAesthetics, rect: List<DoubleVector>) {
        ctx.targetCollector.addPolygon(
            rect,
            p.index(),
            GeomTargetCollector.TooltipParams(
                fillColor = fillColorMapper(p),
                markerColors = colorMarkerMapper(p)
            ),
            tooltipKind = CURSOR_TOOLTIP
        )

    }

    fun addTarget(p: DataPointAesthetics, rect: DoubleRectangle) {
        val objectRadius = with(rect) {
            if (ctx.flipped) {
                height / 2.0
            } else {
                width / 2.0
            }
        }

        val hintFactory = HintsCollection.HintConfigFactory()
            .defaultObjectRadius(objectRadius)
            .defaultCoord(p.x()!!)
            .defaultKind(
                if (ctx.flipped) {
                    ROTATED_TOOLTIP
                } else {
                    HORIZONTAL_TOOLTIP
                }
            )

        val hintConfigs = hintAesList
            .fold(HintsCollection(p, helper)) { acc, aes ->
                acc.addHint(hintFactory.create(aes))
            }

        ctx.targetCollector.addRectangle(
            p.index(),
            rect,
            GeomTargetCollector.TooltipParams(
                tipLayoutHints = hintConfigs.hints,
                fillColor = fillColorMapper(p),
                markerColors = colorMarkerMapper(p)
            ),
            tooltipKind = tooltipKind
        )

    }

    fun collectRectangleTargets(aesthetics: Aesthetics, clientRectFactory: (DataPointAesthetics) -> DoubleRectangle?) {
        for (p in aesthetics.dataPoints()) {
            val clientRect = clientRectFactory(p) ?: continue
            addTarget(p, clientRect)
        }
    }

}