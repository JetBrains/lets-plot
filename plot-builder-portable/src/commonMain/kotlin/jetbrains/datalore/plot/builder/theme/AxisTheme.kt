/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.theme

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.builder.presentation.Defaults

interface AxisTheme {
    fun isOntop(): Boolean = false

    fun showLine(): Boolean

    fun showTickMarks(): Boolean

    fun showLabels(): Boolean

    fun showTitle(): Boolean

    fun showTooltip(): Boolean

    fun titleColor(): Color

    fun lineWidth(): Double

    fun lineColor(): Color

    fun tickMarkColor(): Color

    fun labelColor(): Color

    fun tickMarkWidth(): Double

    fun tickMarkLength(): Double

    fun tickLabelDistance(): Double {
        var result = Defaults.Plot.Axis.TICK_MARK_PADDING  // little space always
        if (showTickMarks()) {
            result += tickMarkLength()
        }
        return result
    }

    fun tooltipFill(): Color
    fun tooltipColor(): Color
    fun tooltipStrokeWidth(): Double
    fun tooltipTextColor(): Color
}
