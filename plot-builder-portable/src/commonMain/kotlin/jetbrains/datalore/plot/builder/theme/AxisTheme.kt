/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.theme

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.builder.presentation.Defaults

interface AxisTheme {
    fun showLine(): Boolean

    fun showTickMarks(): Boolean

    fun showLabels(): Boolean

    fun showTitle(): Boolean

    fun showTooltip(): Boolean

    fun lineWidth(): Double

    fun lineColor(): Color

    fun tickMarkColor(): Color

    fun labelColor(): Color

    fun tickMarkWidth(): Double

    fun tickLabelDistance(): Double {
        var result = Defaults.Plot.Axis.TICK_MARK_PADDING  // little space always
        if (showTickMarks()) {
            result += Defaults.Plot.Axis.TICK_MARK_LENGTH
        }
        return result
    }
}
