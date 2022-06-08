/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout

import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.plot.builder.guide.Orientation
import jetbrains.datalore.plot.builder.layout.axis.AxisBreaksProviderFactory
import jetbrains.datalore.plot.builder.layout.axis.AxisLayouter
import jetbrains.datalore.plot.builder.theme.AxisTheme

internal class PlotAxisLayout constructor(
    private val breaksProviderFactory: AxisBreaksProviderFactory,
    private val theme: AxisTheme,
    private val orientation: Orientation
) : AxisLayout {

    override fun initialThickness(): Double {
        return PlotAxisLayoutUtil.initialThickness(orientation, theme)
    }

    override fun doLayout(
        axisDomain: DoubleSpan,
        axisLength: Double,
        maxTickLabelsBoundsStretched: DoubleRectangle?,
    ): AxisLayoutInfo {
        val breaksProvider = breaksProviderFactory.createAxisBreaksProvider(axisDomain)
        val layouter = AxisLayouter.create(orientation, axisDomain, breaksProvider, theme)

        return layouter.doLayout(axisLength, maxTickLabelsBoundsStretched)
    }
}
