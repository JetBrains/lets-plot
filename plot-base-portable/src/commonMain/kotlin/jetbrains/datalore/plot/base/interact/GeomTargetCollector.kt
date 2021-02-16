/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.interact

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.interact.TipLayoutHint.Kind

interface GeomTargetCollector {

    fun addPoint(
        index: Int,
        point: DoubleVector,
        radius: Double,
        tooltipParams: TooltipParams,
        tooltipKind: Kind = Kind.VERTICAL_TOOLTIP
    )

    fun addRectangle(
        index: Int,
        rectangle: DoubleRectangle,
        tooltipParams: TooltipParams,
        tooltipKind: Kind = Kind.HORIZONTAL_TOOLTIP
    )

    fun addPath(
        points: List<DoubleVector>,
        localToGlobalIndex: (Int) -> Int,
        tooltipParams: TooltipParams,
        tooltipKind: Kind = Kind.HORIZONTAL_TOOLTIP
    )

    fun addPolygon(
        points: List<DoubleVector>,
        localToGlobalIndex: (Int) -> Int,
        tooltipParams: TooltipParams,
        tooltipKind: Kind = Kind.CURSOR_TOOLTIP
    )

    class TooltipParams {

        private var myTipLayoutHints = emptyMap<Aes<*>, TipLayoutHint>()
        private var myColor = Color.GRAY
        private var myStemLength = TipLayoutHint.StemLength.NORMAL

        fun getTipLayoutHints(): Map<Aes<*>, TipLayoutHint> {
            return myTipLayoutHints
        }

        fun setTipLayoutHints(tipLayoutHints: Map<Aes<*>, TipLayoutHint>): TooltipParams {
            myTipLayoutHints = tipLayoutHints
            return this
        }

        fun getColor(): Color {
            return myColor
        }

        fun setColor(color: Color): TooltipParams {
            myColor = color
            return this
        }

        fun getStemLength(): TipLayoutHint.StemLength {
            return myStemLength
        }

        fun setStemLength(stemLength: TipLayoutHint.StemLength): TooltipParams {
            myStemLength = stemLength
            return this
        }

        companion object {
            fun params(): TooltipParams {
                return TooltipParams()
            }
        }
    }
}
