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
    open val showStem: Boolean = true
) {
    constructor(
        kind: Kind,
        coord: DoubleVector?,
        objectRadius: Double,
        color: Color?
    ) : this(kind, coord, objectRadius, color, true)

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

        fun verticalTooltip(coord: DoubleVector?, objectRadius: Double, color: Color?, showStem: Boolean = true): TipLayoutHint {
            return TipLayoutHint(
                Kind.VERTICAL_TOOLTIP,
                coord,
                objectRadius,
                color,
                showStem
            )
        }

        fun horizontalTooltip(coord: DoubleVector?, objectRadius: Double, color: Color?, showStem: Boolean = true): TipLayoutHint {
            return TipLayoutHint(
                Kind.HORIZONTAL_TOOLTIP,
                coord,
                objectRadius,
                color,
                showStem
            )
        }

        fun cursorTooltip(coord: DoubleVector?, color: Color?): TipLayoutHint {
            return TipLayoutHint(
                kind = Kind.CURSOR_TOOLTIP,
                coord = coord,
                objectRadius = 0.0,
                color = color
            )
        }

        fun xAxisTooltip(coord: DoubleVector?, color: Color?, axisRadius: Double = 0.0): TipLayoutHint {
            return TipLayoutHint(
                kind = Kind.X_AXIS_TOOLTIP,
                coord = coord,
                objectRadius = axisRadius,
                color = color
            )
        }

        fun yAxisTooltip(coord: DoubleVector?, color: Color?, axisRadius: Double = 0.0): TipLayoutHint {
            return TipLayoutHint(
                kind = Kind.Y_AXIS_TOOLTIP,
                coord = coord,
                objectRadius = axisRadius,
                color = color
            )
        }
    }
}
