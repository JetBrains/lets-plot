/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout.axis.label

import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.plot.base.scale.ScaleBreaks
import jetbrains.datalore.plot.builder.layout.axis.AxisBreaksProvider
import jetbrains.datalore.plot.builder.presentation.Defaults
import jetbrains.datalore.plot.builder.presentation.PlotLabelSpec
import jetbrains.datalore.plot.builder.theme.AxisTheme
import jetbrains.datalore.vis.StyleSheet

abstract class AxisLabelsLayout protected constructor(
    val orientation: jetbrains.datalore.plot.builder.guide.Orientation,
    val axisDomain: DoubleSpan,
    val labelSpec: PlotLabelSpec,
    val theme: AxisTheme
) {

    protected val isHorizontal: Boolean
        get() = orientation.isHorizontal

    abstract fun doLayout(
        axisLength: Double,
        axisMapper: (Double?) -> Double?,
        maxLabelsBounds: DoubleRectangle?
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

    internal fun applyLabelsOffset(labelsBounds: DoubleRectangle): DoubleRectangle {
        return BreakLabelsLayoutUtil.applyLabelsOffset(
            labelsBounds,
            theme.tickLabelDistance(),
            orientation
        )
    }

    companion object {
        const val TICK_LABEL_HEIGHT = Defaults.Plot.Axis.TICK_FONT_SIZE.toDouble()
        const val INITIAL_TICK_LABEL_LENGTH = 4 // symbols
        const val MIN_TICK_LABEL_DISTANCE = 20.0  // px

        fun horizontalFlexBreaks(
            orientation: jetbrains.datalore.plot.builder.guide.Orientation,
            axisDomain: DoubleSpan,
            breaksProvider: AxisBreaksProvider,
            theme: AxisTheme,
            styleSheet: StyleSheet,
        ): AxisLabelsLayout {

            require(orientation.isHorizontal) { orientation.toString() }
            require(!breaksProvider.isFixedBreaks) { "fixed breaks" }
            return HorizontalFlexBreaksLabelsLayout(
                orientation,
                axisDomain,
                PlotLabelSpec.axisTick(styleSheet, theme.axis),
                breaksProvider,
                theme
            )
        }

        fun horizontalFixedBreaks(
            orientation: jetbrains.datalore.plot.builder.guide.Orientation,
            axisDomain: DoubleSpan,
            breaks: ScaleBreaks,
            theme: AxisTheme,
            styleSheet: StyleSheet
        ): AxisLabelsLayout {

            require(orientation.isHorizontal) { orientation.toString() }
            return HorizontalFixedBreaksLabelsLayout(
                orientation,
                axisDomain,
                PlotLabelSpec.axisTick(styleSheet, theme.axis),
                breaks,
                theme
            )
        }

        fun verticalFlexBreaks(
            orientation: jetbrains.datalore.plot.builder.guide.Orientation,
            axisDomain: DoubleSpan,
            breaksProvider: AxisBreaksProvider,
            theme: AxisTheme,
            styleSheet: StyleSheet
        ): AxisLabelsLayout {

            require(!orientation.isHorizontal) { orientation.toString() }
            require(!breaksProvider.isFixedBreaks) { "fixed breaks" }
            return VerticalFlexBreaksLabelsLayout(
                orientation,
                axisDomain,
                PlotLabelSpec.axisTick(styleSheet, theme.axis),
                breaksProvider,
                theme
            )
        }

        fun verticalFixedBreaks(
            orientation: jetbrains.datalore.plot.builder.guide.Orientation,
            axisDomain: DoubleSpan,
            breaks: ScaleBreaks,
            theme: AxisTheme,
            styleSheet: StyleSheet
        ): AxisLabelsLayout {
            require(!orientation.isHorizontal) { orientation.toString() }
            return VerticalFixedBreaksLabelsLayout(
                orientation,
                axisDomain,
                PlotLabelSpec.axisTick(styleSheet, theme.axis),
                breaks,
                theme
            )
        }
    }
}
