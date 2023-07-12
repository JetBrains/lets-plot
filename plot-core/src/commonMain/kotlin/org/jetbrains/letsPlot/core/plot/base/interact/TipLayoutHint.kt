/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.interact

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.Color

// `open` - for Mockito tests
open class TipLayoutHint(
    open val kind: Kind,
    open val coord: DoubleVector?,
    open val objectRadius: Double,
    open val stemLength: StemLength,
    open val fillColor: Color?,
    open val markerColors: List<Color>
) {
    enum class StemLength(val value: Double) {
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
        Y_AXIS_TOOLTIP,
        ROTATED_TOOLTIP
    }


    companion object {

        fun verticalTooltip(
            coord: DoubleVector?,
            objectRadius: Double,
            stemLength: StemLength = StemLength.NORMAL,
            fillColor: Color? = null,
            markerColors: List<Color> = emptyList()
        ): TipLayoutHint {
            return TipLayoutHint(
                Kind.VERTICAL_TOOLTIP,
                coord,
                objectRadius,
                stemLength,
                fillColor,
                markerColors
            )
        }

        fun horizontalTooltip(
            coord: DoubleVector?,
            objectRadius: Double,
            stemLength: StemLength = StemLength.NORMAL,
            fillColor: Color? = null,
            markerColors: List<Color> = emptyList()
        ): TipLayoutHint {
            return TipLayoutHint(
                Kind.HORIZONTAL_TOOLTIP,
                coord,
                objectRadius,
                stemLength,
                fillColor,
                markerColors
            )
        }

        fun cursorTooltip(
            coord: DoubleVector?,
            stemLength: StemLength = StemLength.NORMAL,
            fillColor: Color? = null,
            markerColors: List<Color> = emptyList()
        ): TipLayoutHint {
            return TipLayoutHint(
                kind = Kind.CURSOR_TOOLTIP,
                coord,
                objectRadius = 0.0,
                stemLength,
                fillColor,
                markerColors
            )
        }

        fun xAxisTooltip(
            coord: DoubleVector?,
            axisRadius: Double = 0.0,
            stemLength: StemLength = StemLength.NONE,
            fillColor: Color? = null
        ): TipLayoutHint {
            return TipLayoutHint(
                kind = Kind.X_AXIS_TOOLTIP,
                coord,
                objectRadius = axisRadius,
                stemLength,
                fillColor,
                markerColors = emptyList()
            )
        }

        fun yAxisTooltip(
            coord: DoubleVector?,
            axisRadius: Double = 0.0,
            stemLength: StemLength = StemLength.NONE,
            fillColor: Color? = null
        ): TipLayoutHint {
            return TipLayoutHint(
                kind = Kind.Y_AXIS_TOOLTIP,
                coord,
                objectRadius = axisRadius,
                stemLength,
                fillColor,
                markerColors = emptyList()
            )
        }

        fun rotatedTooltip(coord: DoubleVector?, objectRadius: Double, color: Color?, stemLength: StemLength = StemLength.NORMAL): TipLayoutHint {
            return TipLayoutHint(
                Kind.ROTATED_TOOLTIP,
                coord,
                objectRadius,
                stemLength,
                color,
                markerColors = emptyList()
            )
        }
    }
}
