/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.interact

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color

// `open` - for Mockito tests
open class TipLayoutHint(
    open val kind: Kind,
    open val coord: DoubleVector?,
    open val objectRadius: Double,
    open val color: Color?,
    open val stemStyle: StemStyle
) {

    enum class StemStyle(val length: Double) {
        NORMAL(12.0),
        SHORT(5.0),
        NONE(0.0)
    }

    override fun toString(): String {
        return "$kind"
    }


    enum class Kind {
        VERTICAL_TOOLTIP,
        HORIZONTAL_TOOLTIP,
        CURSOR_TOOLTIP,
        X_AXIS_TOOLTIP,
        Y_AXIS_TOOLTIP
    }


    companion object {

        fun verticalTooltip(coord: DoubleVector?, objectRadius: Double, color: Color?, stemStyle: StemStyle = StemStyle.NORMAL): TipLayoutHint {
            return TipLayoutHint(
                Kind.VERTICAL_TOOLTIP,
                coord,
                objectRadius,
                color,
                stemStyle
            )
        }

        fun horizontalTooltip(coord: DoubleVector?, objectRadius: Double, color: Color?, stemStyle: StemStyle = StemStyle.NORMAL): TipLayoutHint {
            return TipLayoutHint(
                Kind.HORIZONTAL_TOOLTIP,
                coord,
                objectRadius,
                color,
                stemStyle
            )
        }

        fun cursorTooltip(coord: DoubleVector?, color: Color?, stemStyle: StemStyle = StemStyle.NORMAL): TipLayoutHint {
            return TipLayoutHint(
                kind = Kind.CURSOR_TOOLTIP,
                coord = coord,
                objectRadius = 0.0,
                color = color,
                stemStyle = stemStyle
            )
        }

        fun xAxisTooltip(coord: DoubleVector?, color: Color?, axisRadius: Double = 0.0, stemStyle: StemStyle = StemStyle.NONE): TipLayoutHint {
            return TipLayoutHint(
                kind = Kind.X_AXIS_TOOLTIP,
                coord = coord,
                objectRadius = axisRadius,
                color = color,
                stemStyle = stemStyle
            )
        }

        fun yAxisTooltip(coord: DoubleVector?, color: Color?, axisRadius: Double = 0.0, stemStyle: StemStyle = StemStyle.NONE): TipLayoutHint {
            return TipLayoutHint(
                kind = Kind.Y_AXIS_TOOLTIP,
                coord = coord,
                objectRadius = axisRadius,
                color = color,
                stemStyle = stemStyle
            )
        }
    }
}
