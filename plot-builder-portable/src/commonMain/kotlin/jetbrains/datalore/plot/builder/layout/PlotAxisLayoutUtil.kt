/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout

import jetbrains.datalore.plot.builder.guide.Orientation
import jetbrains.datalore.plot.builder.presentation.PlotLabelSpec
import jetbrains.datalore.plot.builder.theme.AxisTheme

internal object PlotAxisLayoutUtil {
    private val TICK_LABEL_SPEC = PlotLabelSpec.AXIS_TICK

    fun initialThickness(
        orientation: Orientation,
        theme: AxisTheme,
    ): Double {
        if (theme.showTickMarks() || theme.showLabels()) {
            val v = theme.tickLabelDistance()
            return if (theme.showLabels()) {
                v + initialTickLabelSize(orientation)
            } else {
                v
            }
        }
        return 0.0
    }

    private fun initialTickLabelSize(orientation: Orientation): Double {
        return if (orientation.isHorizontal)
            TICK_LABEL_SPEC.height()
        else
            TICK_LABEL_SPEC.width(1)
    }
}