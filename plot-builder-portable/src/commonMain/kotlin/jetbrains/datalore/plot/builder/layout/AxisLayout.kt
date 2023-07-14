/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import jetbrains.datalore.plot.builder.guide.Orientation
import jetbrains.datalore.plot.builder.layout.axis.AxisBreaksProviderFactory
import jetbrains.datalore.plot.builder.layout.axis.AxisLayouter
import jetbrains.datalore.plot.builder.layout.util.Insets
import org.jetbrains.letsPlot.core.plot.base.theme.AxisTheme

internal class AxisLayout(
    private val breaksProviderFactory: AxisBreaksProviderFactory,
    val orientation: Orientation,
    val theme: AxisTheme
) {

    fun doLayout(
        axisDomain: DoubleSpan,
        axisLength: Double,
        geomAreaInsets: Insets
    ): AxisLayoutInfo {
        val breaksProvider = breaksProviderFactory.createAxisBreaksProvider(axisDomain)
        val layouter = AxisLayouter.create(orientation, axisDomain, breaksProvider, geomAreaInsets, theme)

        return layouter.doLayout(axisLength)
    }
}
