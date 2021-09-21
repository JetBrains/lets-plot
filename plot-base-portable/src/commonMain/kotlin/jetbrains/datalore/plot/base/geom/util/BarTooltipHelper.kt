/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom.util

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.interact.GeomTargetCollector
import jetbrains.datalore.plot.base.interact.TipLayoutHint

object BarTooltipHelper {
    fun collectRectangleTargets(
        hintAesList: List<Aes<Double>>,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext,
        rectFactory: (DataPointAesthetics) -> DoubleRectangle?,
        colorFactory: (DataPointAesthetics) -> Color,
        tooltipKind: TipLayoutHint.Kind? = null
    ) {
        val helper = GeomHelper(pos, coord, ctx)

        for (p in aesthetics.dataPoints()) {
            val rect = rectFactory(p) ?: continue

            val clientRect = helper.toClient(DoubleRectangle(0.0, 0.0, rect.width, 0.0), p)
            var objectRadius = clientRect.width / 2.0

            var kindForOutliers = TipLayoutHint.Kind.HORIZONTAL_TOOLTIP
            var kindForGeneralTooltip = tooltipKind ?: TipLayoutHint.Kind.HORIZONTAL_TOOLTIP

            if (ctx.flipped) {
                objectRadius = clientRect.height / 2.0
                kindForOutliers = TipLayoutHint.Kind.ROTATED_TOOLTIP
                kindForGeneralTooltip = tooltipKind ?: TipLayoutHint.Kind.VERTICAL_TOOLTIP
            }

            val xCoord = rect.center.x
            val hintFactory = HintsCollection.HintConfigFactory()
                .defaultObjectRadius(objectRadius)
                .defaultX(xCoord)
                .defaultKind(kindForOutliers)

            val hintConfigs = hintAesList
                .fold(HintsCollection(p, helper)) { acc, aes ->
                    acc.addHint(hintFactory.create(aes))
                }

            ctx.targetCollector.addRectangle(
                p.index(),
                helper.toClient(rect, p),
                GeomTargetCollector.TooltipParams.params()
                    .setTipLayoutHints(hintConfigs.hints)
//                    .setColor(HintColorUtil.fromColor(p))
                    .setColor(colorFactory(p)),
                kindForGeneralTooltip
            )
        }
    }
}