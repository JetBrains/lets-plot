/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom.util

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.geom.util.HintColorUtil.createColorMarkerMapper
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetCollector
import org.jetbrains.letsPlot.core.plot.base.tooltip.TooltipHint
import org.jetbrains.letsPlot.core.plot.base.tooltip.TooltipHint.Placement.*

class RectangleTooltipHelper(
    pos: PositionAdjustment,
    coord: CoordinateSystem,
    private val ctx: GeomContext,
    private val hintAesList: List<Aes<Double>> = emptyList(),
    private val tooltipPlacement: TooltipHint.Placement = VERTICAL.takeIf { ctx.flipped } ?: HORIZONTAL,
    private val fillColorMapper: (DataPointAesthetics) -> Color? = { null },
    private val colorMarkerMapper: (DataPointAesthetics) -> List<Color> = createColorMarkerMapper(ctx),
    // Anchor the tooltip at the bar's tip (far end from zero): top for positive,
    // bottom for negative (or right/left when flipped). Off by default — other
    // rect-based geoms (tile, rect) aren't anchored relative to a zero baseline.
    private val snapToBarTip: Boolean = false,
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
            tooltipPlacement = CURSOR
        )

    }

    fun addTarget(p: DataPointAesthetics, rect: DoubleRectangle) {
        val tooltipAnchor: DoubleVector?
        val objectRadius = with(rect) {
            if (ctx.flipped) {
                tooltipAnchor = snapToBarTip.ifTrue {
                    val ax = if ((p.finiteOrNull(Aes.Y) ?: 0.0) >= 0) right else left
                    DoubleVector(ax, top + height / 2.0)
                }
                height / 2.0
            } else {
                tooltipAnchor = snapToBarTip.ifTrue {
                    val ay = if ((p.finiteOrNull(Aes.Y) ?: 0.0) >= 0) top else bottom
                    DoubleVector(left + width / 2.0, ay)
                }
                width / 2.0
            }
        }

        val hintFactory = HintsCollection.HintConfigFactory()
            .defaultObjectRadius(objectRadius)
            .defaultCoord(p.x()!!)
            .defaultKind(
                if (ctx.flipped) {
                    ROTATED
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
            rect,
            GeomTargetCollector.TooltipParams(
                tooltipHints = hintConfigs.hints,
                fillColor = fillColorMapper(p),
                markerColors = colorMarkerMapper(p)
            ),
            tooltipPlacement = tooltipPlacement,
            tooltipAnchor = tooltipAnchor
        )

    }

    private inline fun <T> Boolean.ifTrue(block: () -> T): T? = if (this) block() else null
}