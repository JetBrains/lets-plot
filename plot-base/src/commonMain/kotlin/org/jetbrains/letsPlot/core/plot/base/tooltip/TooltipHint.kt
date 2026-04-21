/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.tooltip

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.Color

// `open` - for Mockito tests
open class TooltipHint(
    open val placement: Placement,
    open val coord: DoubleVector,
    open val objectRadius: Double,
    open val stemLength: StemLength,
    open val fillColor: Color?,
    open val markerColors: List<Color>
) {
    enum class StemLength(val value: Double) {
        NORMAL(12.0),
        NONE(0.0)
    }

    override fun toString(): String {
        return "$placement"
    }


    enum class Placement {
        VERTICAL,
        HORIZONTAL,
        CURSOR,
        X_AXIS,
        Y_AXIS,
        ROTATED
    }


    companion object {

        fun verticalTooltip(
            coord: DoubleVector,
            objectRadius: Double,
            stemLength: StemLength = StemLength.NORMAL,
            fillColor: Color? = null,
            markerColors: List<Color> = emptyList()
        ): TooltipHint {
            return TooltipHint(
                Placement.VERTICAL,
                coord,
                objectRadius,
                stemLength,
                fillColor,
                markerColors
            )
        }

        fun horizontalTooltip(
            coord: DoubleVector,
            objectRadius: Double,
            stemLength: StemLength = StemLength.NORMAL,
            fillColor: Color? = null,
            markerColors: List<Color> = emptyList()
        ): TooltipHint {
            return TooltipHint(
                Placement.HORIZONTAL,
                coord,
                objectRadius,
                stemLength,
                fillColor,
                markerColors
            )
        }

        fun cursorTooltip(
            coord: DoubleVector,
            stemLength: StemLength = StemLength.NORMAL,
            fillColor: Color? = null,
            markerColors: List<Color> = emptyList()
        ): TooltipHint {
            return TooltipHint(
                placement = Placement.CURSOR,
                coord,
                objectRadius = 0.0,
                stemLength,
                fillColor,
                markerColors
            )
        }

        fun xAxisTooltip(
            coord: DoubleVector,
            axisRadius: Double = 0.0,
            stemLength: StemLength = StemLength.NONE,
            fillColor: Color? = null
        ): TooltipHint {
            return TooltipHint(
                placement = Placement.X_AXIS,
                coord,
                objectRadius = axisRadius,
                stemLength,
                fillColor,
                markerColors = emptyList()
            )
        }

        fun yAxisTooltip(
            coord: DoubleVector,
            axisRadius: Double = 0.0,
            stemLength: StemLength = StemLength.NONE,
            fillColor: Color? = null
        ): TooltipHint {
            return TooltipHint(
                placement = Placement.Y_AXIS,
                coord,
                objectRadius = axisRadius,
                stemLength,
                fillColor,
                markerColors = emptyList()
            )
        }

        fun rotatedTooltip(coord: DoubleVector, objectRadius: Double, color: Color?, stemLength: StemLength = StemLength.NORMAL): TooltipHint {
            return TooltipHint(
                Placement.ROTATED,
                coord,
                objectRadius,
                stemLength,
                color,
                markerColors = emptyList()
            )
        }
    }
}
