/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.builder.coord.CoordProvider
import jetbrains.datalore.plot.builder.guide.Orientation
import jetbrains.datalore.plot.builder.layout.axis.AxisBreaksProviderFactory
import jetbrains.datalore.plot.builder.layout.axis.AxisLayouter
import jetbrains.datalore.plot.builder.presentation.PlotLabelSpec
import jetbrains.datalore.plot.builder.theme.AxisTheme

class PlotAxisLayout internal constructor(
    private val breaksProviderFactory: AxisBreaksProviderFactory,
    private val domainX: ClosedRange<Double>, // ToDo: don't store it here (stored in XYPlotLayout)
    private val domainY: ClosedRange<Double>, // ToDo: don't store it here (stored in XYPlotLayout)
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
        displaySize: DoubleVector,
        maxTickLabelsBoundsStretched: DoubleRectangle?,
        coordProvider: CoordProvider
    ): AxisLayoutInfo {
        val layouter = createLayouter(displaySize, coordProvider)
        return layouter.doLayout(
            axisLength(displaySize, orientation),
            maxTickLabelsBoundsStretched
        )
    }

    private fun createLayouter(displaySize: DoubleVector, coordProvider: CoordProvider): AxisLayouter {
        val domains = (domainX to domainY)
        val axisDomain = axisDomain(domains, orientation)

        val breaksProvider = breaksProviderFactory.createAxisBreaksProvider(axisDomain)
        return AxisLayouter.create(orientation, axisDomain, breaksProvider, theme)
    }

    companion object {
        private val TICK_LABEL_SPEC = PlotLabelSpec.AXIS_TICK

        private fun initialTickLabelSize(orientation: Orientation): Double {
            return if (orientation.isHorizontal)
                TICK_LABEL_SPEC.height()
            else
                TICK_LABEL_SPEC.width(1)
        }

        private fun axisLength(displaySize: DoubleVector, orientation: Orientation): Double {
            return if (orientation.isHorizontal)
                displaySize.x
            else
                displaySize.y
        }

        private fun axisDomain(
            xyDomains: Pair<ClosedRange<Double>, ClosedRange<Double>>,
            orientation: Orientation
        ): ClosedRange<Double> {
            return if (orientation.isHorizontal)
                xyDomains.first
            else
                xyDomains.second
        }
    }
}
