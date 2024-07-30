/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom.util

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleSegment
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetCollector
import org.jetbrains.letsPlot.core.plot.base.tooltip.TipLayoutHint

class FlippableGeomHelper(
    internal val isVertical: Boolean
) {
    fun getEffectiveAes(aes: Aes<Double>): Aes<Double> {
        if (!isVertical) {
            when (aes) {
                Aes.X -> return Aes.Y
                Aes.Y -> return Aes.X
                Aes.YMIN -> return Aes.XMIN
                Aes.YMAX -> return Aes.XMAX
                Aes.XMIN -> return Aes.YMIN
                Aes.XMAX -> return Aes.YMAX
                Aes.WIDTH -> return Aes.HEIGHT
                Aes.HEIGHT -> return Aes.WIDTH
                else -> error("Aes ${aes.name} not allowed for function getEffectiveAes")
            }
        } else {
            return aes
        }
    }

    fun flip(point: DoubleVector): DoubleVector {
        return when (isVertical) {
            true -> point
            false -> point.flip()
        }
    }

    fun flip(clientRect: DoubleRectangle): DoubleRectangle {
        return when (isVertical) {
            true -> clientRect
            false -> clientRect.flip()
        }
    }

    fun flip(doubleSegment: DoubleSegment): DoubleSegment {
        return when (isVertical) {
            true -> doubleSegment
            false -> DoubleSegment(doubleSegment.start.flip(), doubleSegment.end.flip())
        }
    }

    fun buildHints(
        hintAesList: List<Aes<Double>>,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext,
        clientRectFactory: (DataPointAesthetics) -> DoubleRectangle?,
        fillColorMapper: (DataPointAesthetics) -> Color? = { null },
        colorMarkerMapper: (DataPointAesthetics) -> List<Color> =
            HintColorUtil.createColorMarkerMapper(null, ctx),
        defaultTooltipKind: TipLayoutHint.Kind? = null
    ) {
        val helper = GeomHelper(pos, coord, ctx)
        val isVerticallyOriented = when (isVertical) {
            true -> !ctx.flipped
            false -> ctx.flipped
        }

        for (p in aesthetics.dataPoints()) {
            val clientRect = clientRectFactory(p) ?: continue

            val objectRadius = with(clientRect) {
                when (isVerticallyOriented) {
                    true -> width / 2.0
                    false -> height / 2.0
                }
            }

            val hintFactory = HintsCollection.HintConfigFactory()
                .defaultObjectRadius(objectRadius)
                .defaultCoord(p[getEffectiveAes(Aes.X)]!!)
                .defaultKind(
                    if (isVerticallyOriented) {
                        TipLayoutHint.Kind.HORIZONTAL_TOOLTIP
                    } else {
                        TipLayoutHint.Kind.ROTATED_TOOLTIP
                    }
                )

            val hints = hintAesList
                .fold(HintsCollection(p, helper)) { acc, aes ->
                    acc.addHint(hintFactory.create(aes))
                }
                .hints

            if (true) {
                ctx.targetCollector.addRectangle(
                    p.index(),
                    clientRect,
                    GeomTargetCollector.TooltipParams(
                        tipLayoutHints = hints,
                        fillColor = fillColorMapper(p),
                        markerColors = colorMarkerMapper(p)
                    ),
                    tooltipKind = defaultTooltipKind ?: if (isVerticallyOriented) {
                        TipLayoutHint.Kind.HORIZONTAL_TOOLTIP
                    } else {
                        TipLayoutHint.Kind.VERTICAL_TOOLTIP
                    }
                )
            } else {
                ctx.targetCollector.addPolygon(
                    clientRect.points,
                    p.index(),
                    GeomTargetCollector.TooltipParams(
                        tipLayoutHints = hints,
                        fillColor = fillColorMapper(p),
                        markerColors = colorMarkerMapper(p)
                    ),
                )
            }
        }
    }
}