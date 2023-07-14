/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.tooltip.loc

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTarget
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetCollector.TooltipParams
import org.jetbrains.letsPlot.core.plot.base.tooltip.HitShape
import org.jetbrains.letsPlot.core.plot.base.tooltip.HitShape.Kind.*
import org.jetbrains.letsPlot.core.plot.base.tooltip.TipLayoutHint
import org.jetbrains.letsPlot.core.plot.base.tooltip.TipLayoutHint.Companion.cursorTooltip
import org.jetbrains.letsPlot.core.plot.base.tooltip.TipLayoutHint.Companion.horizontalTooltip
import org.jetbrains.letsPlot.core.plot.base.tooltip.TipLayoutHint.Companion.rotatedTooltip
import org.jetbrains.letsPlot.core.plot.base.tooltip.TipLayoutHint.Companion.verticalTooltip
import org.jetbrains.letsPlot.core.plot.base.tooltip.TipLayoutHint.Kind.*

class TargetPrototype(
    internal val hitShape: HitShape,
    internal val indexMapper: (Int) -> Int,
    private val tooltipParams: TooltipParams,
    internal val tooltipKind: TipLayoutHint.Kind
) {

    internal fun createGeomTarget(hitCoord: DoubleVector, hitIndex: Int, objectRadius: Double = 0.0): GeomTarget {
        return GeomTarget(
            hitIndex,
            createTipLayoutHint(
                hitCoord = hitCoord,
                hitShapeKind = hitShape.kind,
                tooltipKind = tooltipKind,
                stemLength = tooltipParams.stemLength,
                fillColor = tooltipParams.fillColorFactory(hitIndex),
                markerColors = tooltipParams.markerColorsFactory(hitIndex),
                objectRadius = objectRadius
            ),
            tooltipParams.tipLayoutHints
        )
    }

    companion object {
        fun createTipLayoutHint(
            hitCoord: DoubleVector,
            hitShapeKind: HitShape.Kind,
            tooltipKind: TipLayoutHint.Kind,
            stemLength: TipLayoutHint.StemLength,
            fillColor: Color?,
            markerColors: List<Color>,
            objectRadius: Double
        ): TipLayoutHint {

            return when (hitShapeKind) {
                POINT -> when (tooltipKind) {
                    VERTICAL_TOOLTIP ->
                        verticalTooltip(
                            hitCoord,
                            objectRadius,
                            stemLength,
                            fillColor,
                            markerColors
                        )

                    CURSOR_TOOLTIP -> cursorTooltip(hitCoord, stemLength, fillColor, markerColors)
                    else -> error("Wrong TipLayoutHint.kind = $tooltipKind for POINT")
                }

                RECT -> when (tooltipKind) {
                    VERTICAL_TOOLTIP -> verticalTooltip(
                        hitCoord,
                        objectRadius,
                        stemLength,
                        fillColor,
                        markerColors
                    )
                    HORIZONTAL_TOOLTIP -> horizontalTooltip(
                        hitCoord,
                        objectRadius,
                        stemLength,
                        fillColor,
                        markerColors
                    )
                    CURSOR_TOOLTIP -> cursorTooltip(hitCoord, stemLength, fillColor, markerColors)
                    ROTATED_TOOLTIP -> rotatedTooltip(hitCoord, objectRadius = 0.0, color = null, stemLength)
                    else -> error("Wrong TipLayoutHint.kind = $tooltipKind for RECT")
                }

                PATH -> when (tooltipKind) {
                    HORIZONTAL_TOOLTIP -> horizontalTooltip(hitCoord, objectRadius = 0.0, stemLength, fillColor, markerColors)
                    VERTICAL_TOOLTIP -> verticalTooltip(hitCoord, objectRadius = 0.0, stemLength, fillColor, markerColors)
                    else -> error("Wrong TipLayoutHint.kind = $tooltipKind for PATH")
                }

                POLYGON -> when (tooltipKind) {
                    CURSOR_TOOLTIP -> cursorTooltip(hitCoord, stemLength, fillColor, markerColors)
                    else -> error("Wrong TipLayoutHint.kind = $tooltipKind for POLYGON")
                }
            }
        }
    }
}
