/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout

import jetbrains.datalore.plot.builder.guide.Orientation
import jetbrains.datalore.plot.builder.presentation.PlotLabelSpec
import jetbrains.datalore.plot.builder.theme.AxisTheme

internal object PlotAxisLayoutUtil {

    fun initialThickness(
        orientation: Orientation,
        theme: AxisTheme,
    ): Double {
        if (theme.showTickMarks() || theme.showLabels()) {
            val v = theme.tickLabelDistance(orientation.isHorizontal)
            return if (theme.showLabels()) {
                v + initialTickLabelSize(orientation, PlotLabelSpecFactory.axisTick(theme))
            } else {
                v
            }
        }
        return 0.0
    }

    private fun initialTickLabelSize(orientation: Orientation, tickLabelSpec: PlotLabelSpec): Double {
        return if (orientation.isHorizontal)
            tickLabelSpec.height()
        else
            tickLabelSpec.width("_")
    }
}