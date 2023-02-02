/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout.axis.label

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.plot.base.scale.ScaleBreaks
import jetbrains.datalore.plot.builder.guide.Orientation
import jetbrains.datalore.plot.builder.layout.PlotLabelSpecFactory
import jetbrains.datalore.plot.builder.layout.axis.AxisBreaksProvider
import jetbrains.datalore.plot.builder.layout.util.Insets
import jetbrains.datalore.plot.builder.presentation.LabelSpec
import jetbrains.datalore.plot.builder.theme.AxisTheme

internal abstract class AxisLabelsLayout protected constructor(
    val orientation: Orientation,
    val axisDomain: DoubleSpan,
    val theme: AxisTheme
) {
    protected val labelSpec: LabelSpec = PlotLabelSpecFactory.axisTick(theme)

    protected val isHorizontal: Boolean
        get() = orientation.isHorizontal

    abstract fun doLayout(
        axisLength: Double,
        axisMapper: (Double?) -> Double?,
    ): AxisLabelsLayoutInfo

    internal fun mapToAxis(
        breaks: List<Double>,
        axisMapper: (Double?) -> Double?
    ): List<Double> {

        return BreakLabelsLayoutUtil.mapToAxis(
            breaks,
            axisDomain,
            axisMapper
        )
    }

    internal fun applyLabelMargins(bounds: DoubleRectangle): DoubleRectangle {
        return BreakLabelsLayoutUtil.applyLabelMargins(
            bounds,
            if (theme.showTickMarks()) theme.tickMarkLength() else 0.0,
            theme.tickLabelMargins(),
            orientation
        )
    }

    internal fun alignToLabelMargin(bounds: DoubleRectangle): DoubleRectangle {
        return BreakLabelsLayoutUtil.alignToLabelMargin(
            bounds,
            if (theme.showTickMarks()) theme.tickMarkLength() else 0.0,
            theme.tickLabelMargins(),
            orientation
        )
    }

    companion object {
        const val INITIAL_TICK_LABEL = "0000" // Typical tick label to estimate number of breaks (chosen by eye)
        const val MIN_TICK_LABEL_DISTANCE = 20.0  // px

        fun horizontalFlexBreaks(
            orientation: Orientation,
            axisDomain: DoubleSpan,
            breaksProvider: AxisBreaksProvider,
            theme: AxisTheme
        ): AxisLabelsLayout {

            require(orientation.isHorizontal) { orientation.toString() }
            require(!breaksProvider.isFixedBreaks) { "fixed breaks" }
            return HorizontalFlexBreaksLabelsLayout(
                orientation,
                axisDomain,
                breaksProvider,
                theme
            )
        }

        fun horizontalFixedBreaks(
            orientation: Orientation,
            axisDomain: DoubleSpan,
            breaks: ScaleBreaks,
            geomAreaInsets: Insets,
            theme: AxisTheme
        ): AxisLabelsLayout {

            require(orientation.isHorizontal) { orientation.toString() }
            return HorizontalFixedBreaksLabelsLayout(
                orientation,
                axisDomain,
                breaks,
                geomAreaInsets,
                theme
            )
        }

        fun verticalFlexBreaks(
            orientation: Orientation,
            axisDomain: DoubleSpan,
            breaksProvider: AxisBreaksProvider,
            theme: AxisTheme
        ): AxisLabelsLayout {

            require(!orientation.isHorizontal) { orientation.toString() }
            require(!breaksProvider.isFixedBreaks) { "fixed breaks" }
            return VerticalFlexBreaksLabelsLayout(
                orientation,
                axisDomain,
                breaksProvider,
                theme
            )
        }

        fun verticalFixedBreaks(
            orientation: Orientation,
            axisDomain: DoubleSpan,
            breaks: ScaleBreaks,
            theme: AxisTheme
        ): AxisLabelsLayout {

            require(!orientation.isHorizontal) { orientation.toString() }
            return VerticalFixedBreaksLabelsLayout(
                orientation,
                axisDomain,
                breaks,
                theme
            )
        }
    }
}
