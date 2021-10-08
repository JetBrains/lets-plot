/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.theme

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.builder.presentation.Defaults.Plot

open class DefaultAxisTheme : AxisTheme {
    override fun showLine(): Boolean {
        return true
    }

    override fun showTickMarks(): Boolean {
        return true
    }

    override fun showLabels(): Boolean {
        return true
    }

    override fun showTitle(): Boolean {
        return true
    }

    override fun showTooltip(): Boolean {
        return true
    }

    override fun lineWidth(): Double {
        return Plot.Axis.LINE_WIDTH
    }

    override fun lineColor(): Color {
//        return Plot.Axis.LINE_COLOR
        return Color.BLACK
    }

    override fun tickMarkWidth(): Double {
        return Plot.Axis.TICK_LINE_WIDTH
    }

    override fun tickMarkColor(): Color {
//        return Plot.Axis.LINE_COLOR
        return Color.BLACK
    }

    override fun labelColor(): Color {
//        return Plot.Axis.TICK_COLOR
        return Color.BLACK
    }
}
