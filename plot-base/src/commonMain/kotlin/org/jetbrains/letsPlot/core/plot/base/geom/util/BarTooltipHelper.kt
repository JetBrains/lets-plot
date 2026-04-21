/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom.util

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetCollector
import org.jetbrains.letsPlot.core.plot.base.tooltip.TooltipHint
import org.jetbrains.letsPlot.core.plot.base.tooltip.TooltipHint.Placement.HORIZONTAL
import org.jetbrains.letsPlot.core.plot.base.tooltip.TooltipHint.Placement.VERTICAL

object BarTooltipHelper {
    fun collectRectangleTargets(
        hintAesList: List<Aes<Double>>,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext,
        clientRectFactory: (DataPointAesthetics) -> DoubleRectangle?,
        fillColorMapper: (DataPointAesthetics) -> Color? = { null },
        colorMarkerMapper: (DataPointAesthetics) -> List<Color> = HintColorUtil.createColorMarkerMapper(ctx),
        defaultTooltipPlacement: TooltipHint.Placement = VERTICAL.takeIf { ctx.flipped } ?: HORIZONTAL
    ) {
        val helper = GeomHelper(pos, coord, ctx)

        for (p in aesthetics.dataPoints()) {
            val clientRect = clientRectFactory(p) ?: continue

            val objectRadius = with (clientRect) {
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
                        TooltipHint.Placement.ROTATED
                    } else {
                        HORIZONTAL
                    }
                )

            val hintConfigs = hintAesList
                .fold(HintsCollection(p, helper)) { acc, aes ->
                    acc.addHint(hintFactory.create(aes))
                }

            ctx.targetCollector.addRectangle(
                p.index(),
                clientRect,
                GeomTargetCollector.TooltipParams(
                    tooltipHints = hintConfigs.hints,
                    fillColor = fillColorMapper(p),
                    markerColors = colorMarkerMapper(p)
                ),
                tooltipPlacement = defaultTooltipPlacement
            )
        }
    }
}