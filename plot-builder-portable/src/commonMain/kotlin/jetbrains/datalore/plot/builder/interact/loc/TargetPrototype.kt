/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact.loc

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.interact.GeomTarget
import jetbrains.datalore.plot.base.interact.GeomTargetCollector.TooltipParams
import jetbrains.datalore.plot.base.interact.HitShape
import jetbrains.datalore.plot.base.interact.HitShape.Kind.*
import jetbrains.datalore.plot.base.interact.TipLayoutHint
import jetbrains.datalore.plot.base.interact.TipLayoutHint.Companion.cursorTooltip
import jetbrains.datalore.plot.base.interact.TipLayoutHint.Companion.horizontalTooltip
import jetbrains.datalore.plot.base.interact.TipLayoutHint.Companion.rotatedTooltip
import jetbrains.datalore.plot.base.interact.TipLayoutHint.Companion.verticalTooltip
import jetbrains.datalore.plot.base.interact.TipLayoutHint.Kind.*

class TargetPrototype(
    internal val hitShape: HitShape,
    internal val indexMapper: (Int) -> Int,
    private val tooltipParams: TooltipParams,
    internal val tooltipKind: TipLayoutHint.Kind
) {

    internal fun createGeomTarget(hitCoord: DoubleVector, hitIndex: Int): GeomTarget {
        return GeomTarget(
            hitIndex,
            createTipLayoutHint(
                hitCoord = hitCoord,
                hitShape = hitShape,
                tooltipKind = tooltipKind,
                stemLength = tooltipParams.stemLength,
                fillColor = tooltipParams.fillColor,
                markerColors = tooltipParams.markerColors,
            ),
            tooltipParams.tipLayoutHints
        )
    }

    companion object {
        fun createTipLayoutHint(
            hitCoord: DoubleVector,
            hitShape: HitShape,
            tooltipKind: TipLayoutHint.Kind,
            stemLength: TipLayoutHint.StemLength,
            fillColor: Color?,
            markerColors: List<Color>
        ): TipLayoutHint {

            return when (hitShape.kind) {
                POINT -> when (tooltipKind) {
                    VERTICAL_TOOLTIP ->
                        verticalTooltip(
                            hitCoord,
                            hitShape.hintOffset,
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
                        hitShape.hintOffset,
                        stemLength,
                        fillColor,
                        markerColors
                    )
                    HORIZONTAL_TOOLTIP -> horizontalTooltip(
                        hitCoord,
                        hitShape.hintOffset,
                        stemLength,
                        fillColor,
                        markerColors
                    )
                    CURSOR_TOOLTIP -> cursorTooltip(hitCoord, stemLength, fillColor, markerColors)
                    ROTATED_TOOLTIP -> rotatedTooltip(hitCoord, objectRadius = 0.0, color = null, stemLength)
                    else -> error("Wrong TipLayoutHint.kind = $tooltipKind for RECT")
                }

                PATH -> when (tooltipKind) {
                    HORIZONTAL_TOOLTIP -> horizontalTooltip(hitCoord, 0.0, stemLength, fillColor, markerColors)
                    VERTICAL_TOOLTIP -> verticalTooltip(hitCoord, 0.0, stemLength, fillColor, markerColors)
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
