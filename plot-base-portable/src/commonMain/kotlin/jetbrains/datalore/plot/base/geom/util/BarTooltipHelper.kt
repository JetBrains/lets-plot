/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom.util

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.interact.GeomTargetCollector.TooltipParams.Companion.tooltip
import jetbrains.datalore.plot.base.interact.TipLayoutHint

object BarTooltipHelper {
    fun collectRectangleTargets(
        hintAesList: List<Aes<Double>>,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext,
        rectFactory: (DataPointAesthetics) -> DoubleRectangle?,
        fillColorMapper: (DataPointAesthetics) -> Color? = { null },
        colorMarkerMapper: (DataPointAesthetics) -> List<Color> = HintColorUtil.createColorMarkerMapper(null, ctx),
        defaultTooltipKind: TipLayoutHint.Kind? = null
    ) {
        val helper = GeomHelper(pos, coord, ctx)

        for (p in aesthetics.dataPoints()) {
            val rect = rectFactory(p) ?: continue

            val objectRadius = helper.toClient(DoubleRectangle(0.0, 0.0, rect.width, 0.0), p).run {
                if (ctx.flipped) {
                    height / 2.0
                } else {
                    width / 2.0
                }
            }
            val xCoord = rect.center.x
            val hintFactory = HintsCollection.HintConfigFactory()
                .defaultObjectRadius(objectRadius)
                .defaultX(xCoord)
                .defaultKind(
                    if (ctx.flipped) {
                        TipLayoutHint.Kind.ROTATED_TOOLTIP
                    } else {
                        TipLayoutHint.Kind.HORIZONTAL_TOOLTIP
                    }
                )

            val hintConfigs = hintAesList
                .fold(HintsCollection(p, helper)) { acc, aes ->
                    acc.addHint(hintFactory.create(aes))
                }

            ctx.targetCollector.addRectangle(
                p.index(),
                helper.toClient(rect, p),
                tooltip {
                    tipLayoutHints = hintConfigs.hints
                    fillColor = fillColorMapper(p)
                    markerColors = colorMarkerMapper(p)
                },
                tooltipKind = defaultTooltipKind ?: if (ctx.flipped) {
                    TipLayoutHint.Kind.VERTICAL_TOOLTIP
                } else {
                    TipLayoutHint.Kind.HORIZONTAL_TOOLTIP
                }
            )
        }
    }
}