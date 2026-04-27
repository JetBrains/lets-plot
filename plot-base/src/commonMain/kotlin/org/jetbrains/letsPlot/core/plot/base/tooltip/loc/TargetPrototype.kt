/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.tooltip.loc

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTarget
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetCollector.TooltipParams
import org.jetbrains.letsPlot.core.plot.base.tooltip.HitShape
import org.jetbrains.letsPlot.core.plot.base.tooltip.HitShape.Kind.*
import org.jetbrains.letsPlot.core.plot.base.tooltip.TooltipHint
import org.jetbrains.letsPlot.core.plot.base.tooltip.TooltipHint.Companion.cursorTooltip
import org.jetbrains.letsPlot.core.plot.base.tooltip.TooltipHint.Companion.horizontalTooltip
import org.jetbrains.letsPlot.core.plot.base.tooltip.TooltipHint.Companion.rotatedTooltip
import org.jetbrains.letsPlot.core.plot.base.tooltip.TooltipHint.Companion.verticalTooltip
import org.jetbrains.letsPlot.core.plot.base.tooltip.TooltipHint.Placement.*

class TargetPrototype(
    internal val hitShape: HitShape,
    internal val indexMapper: (Int) -> Int,
    private val tooltipParams: TooltipParams,
    internal val tooltipPlacement: TooltipHint.Placement,
    internal val tooltipAnchor: DoubleVector? = null
) {

    internal fun createGeomTarget(hitCoord: DoubleVector, hitIndex: Int, objectRadius: Double = 0.0): GeomTarget {
        return GeomTarget(
            hitIndex,
            createTooltipHint(
                hitCoord = hitCoord,
                hitShapeKind = hitShape.kind,
                tooltipPlacement = tooltipPlacement,
                stemLength = tooltipParams.stemLength,
                fillColor = tooltipParams.fillColorFactory(hitIndex),
                markerColors = tooltipParams.markerColorsFactory(hitIndex),
                objectRadius = objectRadius
            ),
            tooltipParams.tooltipHints
        )
    }

    companion object {
        fun createTooltipHint(
            hitCoord: DoubleVector,
            hitShapeKind: HitShape.Kind,
            tooltipPlacement: TooltipHint.Placement,
            stemLength: TooltipHint.StemLength,
            fillColor: Color?,
            markerColors: List<Color>,
            objectRadius: Double
        ): TooltipHint {

            return when (hitShapeKind) {
                POINT -> when (tooltipPlacement) {
                    VERTICAL ->
                        verticalTooltip(
                            hitCoord,
                            objectRadius,
                            stemLength,
                            fillColor,
                            markerColors
                        )

                    CURSOR -> cursorTooltip(hitCoord, stemLength, fillColor, markerColors)
                    else -> error("Wrong TooltipHint.placement = $tooltipPlacement for POINT")
                }

                RECT -> when (tooltipPlacement) {
                    VERTICAL -> verticalTooltip(
                        hitCoord,
                        objectRadius,
                        stemLength,
                        fillColor,
                        markerColors
                    )

                    HORIZONTAL -> horizontalTooltip(
                        hitCoord,
                        objectRadius,
                        stemLength,
                        fillColor,
                        markerColors
                    )

                    CURSOR -> cursorTooltip(hitCoord, stemLength, fillColor, markerColors)
                    ROTATED -> rotatedTooltip(hitCoord, objectRadius = 0.0, color = null, stemLength)
                    else -> error("Wrong TooltipHint.placement = $tooltipPlacement for RECT")
                }

                PATH -> when (tooltipPlacement) {
                    HORIZONTAL -> horizontalTooltip(
                        hitCoord,
                        objectRadius = 0.0,
                        stemLength,
                        fillColor,
                        markerColors
                    )

                    VERTICAL -> verticalTooltip(
                        hitCoord,
                        objectRadius = 0.0,
                        stemLength,
                        fillColor,
                        markerColors
                    )

                    else -> error("Wrong TooltipHint.placement = $tooltipPlacement for PATH")
                }

                POLYGON -> when (tooltipPlacement) {
                    CURSOR -> cursorTooltip(hitCoord, stemLength, fillColor, markerColors)
                    else -> error("Wrong TooltipHint.placement = $tooltipPlacement for POLYGON")
                }
            }
        }
    }
}
