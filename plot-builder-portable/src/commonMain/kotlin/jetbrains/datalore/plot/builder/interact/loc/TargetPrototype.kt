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
import jetbrains.datalore.plot.base.interact.TipLayoutHint.Kind.*

class TargetPrototype(
    internal val hitShape: HitShape,
    internal val indexMapper: (Int) -> Int,
    private val tooltipParams: TooltipParams,
    private val tooltipKind: TipLayoutHint.Kind
) {

    internal fun createGeomTarget(hitCoord: DoubleVector, hitIndex: Int): GeomTarget {
        return GeomTarget(
            hitIndex,
            createTipLayoutHint(hitCoord, hitShape, tooltipParams.getColor(), tooltipKind, tooltipParams.getStemLength()),
            tooltipParams.getTipLayoutHints()
        )
    }

    companion object {
        fun createTipLayoutHint(
            hitCoord: DoubleVector,
            hitShape: HitShape,
            fill: Color,
            tooltipKind: TipLayoutHint.Kind,
            stemLength: TipLayoutHint.StemLength
        ): TipLayoutHint {

            return when (hitShape.kind) {
                POINT -> when (tooltipKind) {
                    VERTICAL_TOOLTIP -> TipLayoutHint.verticalTooltip(hitCoord, hitShape.point.radius, fill, stemLength)
                    else -> error("Wrong TipLayoutHint.kind = $tooltipKind for POINT")
                }

                RECT -> when (tooltipKind) {
                    VERTICAL_TOOLTIP -> TipLayoutHint.verticalTooltip(hitCoord, 0.0, fill, stemLength)
                    HORIZONTAL_TOOLTIP -> TipLayoutHint.horizontalTooltip(hitCoord, hitShape.rect.width / 2, fill, stemLength)
                    CURSOR_TOOLTIP -> TipLayoutHint.cursorTooltip(hitCoord, fill, stemLength)
                    else -> error("Wrong TipLayoutHint.kind = $tooltipKind for RECT")
                }

                PATH -> when (tooltipKind) {
                    HORIZONTAL_TOOLTIP -> TipLayoutHint.horizontalTooltip(hitCoord, 0.0, fill, stemLength)
                    else -> error("Wrong TipLayoutHint.kind = $tooltipKind for PATH")
                }

                POLYGON -> when (tooltipKind) {
                    CURSOR_TOOLTIP -> TipLayoutHint.cursorTooltip(hitCoord, fill, stemLength)
                    else -> error("Wrong TipLayoutHint.kind = $tooltipKind for POLYGON")
                }
            }
        }
    }
}
