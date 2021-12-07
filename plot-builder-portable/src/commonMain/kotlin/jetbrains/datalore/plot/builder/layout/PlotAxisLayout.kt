/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.plot.builder.guide.Orientation
import jetbrains.datalore.plot.builder.layout.axis.AxisBreaksProviderFactory
import jetbrains.datalore.plot.builder.layout.axis.AxisLayouter
import jetbrains.datalore.plot.builder.presentation.PlotLabelSpec
import jetbrains.datalore.plot.builder.theme.AxisTheme

internal class PlotAxisLayout constructor(
    private val breaksProviderFactory: AxisBreaksProviderFactory,
    private val theme: AxisTheme,
    private val orientation: Orientation
) : AxisLayout {

    override fun initialThickness(): Double {
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

    override fun doLayout(
        axisDomain: ClosedRange<Double>,
        axisLength: Double,
        maxTickLabelsBoundsStretched: DoubleRectangle?,
    ): AxisLayoutInfo {
        val breaksProvider = breaksProviderFactory.createAxisBreaksProvider(axisDomain)
        val layouter = AxisLayouter.create(orientation, axisDomain, breaksProvider, theme)

        return layouter.doLayout(axisLength, maxTickLabelsBoundsStretched)
    }

    companion object {
        private val TICK_LABEL_SPEC = PlotLabelSpec.AXIS_TICK

        private fun initialTickLabelSize(orientation: Orientation): Double {
            return if (orientation.isHorizontal)
                TICK_LABEL_SPEC.height()
            else
                TICK_LABEL_SPEC.width(1)
        }
    }
}
