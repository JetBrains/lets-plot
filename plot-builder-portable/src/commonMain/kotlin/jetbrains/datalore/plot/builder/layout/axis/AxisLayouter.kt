/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout.axis

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.ScaleMapper
import org.jetbrains.letsPlot.core.plot.base.scale.Mappers
import org.jetbrains.letsPlot.core.plot.base.scale.ScaleBreaks
import jetbrains.datalore.plot.builder.guide.Orientation
import jetbrains.datalore.plot.builder.layout.AxisLayoutInfo
import jetbrains.datalore.plot.builder.layout.axis.label.AxisLabelsLayout
import jetbrains.datalore.plot.builder.layout.axis.label.BreakLabelsLayoutUtil
import jetbrains.datalore.plot.builder.layout.util.Insets
import jetbrains.datalore.plot.builder.presentation.Defaults.Common.Axis
import jetbrains.datalore.plot.builder.theme.AxisTheme

internal abstract class AxisLayouter(
    val orientation: Orientation,
    private val domainRange: DoubleSpan,
    private val labelsLayout: AxisLabelsLayout
) {

    fun doLayout(
        axisLength: Double
    ): AxisLayoutInfo {
        val labelsInfo = labelsLayout.doLayout(axisLength, toAxisMapper(axisLength))
        val axisBreaks = labelsInfo.breaks!!
        val labelsBounds = labelsInfo.bounds!!
        return AxisLayoutInfo(
            axisLength = axisLength,
            axisDomain = domainRange,
            orientation = orientation,
            axisBreaks = axisBreaks,
            tickLabelsBounds = labelsBounds,
            tickLabelRotationAngle = labelsInfo.labelRotationAngle,
            tickLabelHorizontalAnchor = labelsInfo.labelHorizontalAnchor,
            tickLabelVerticalAnchor = labelsInfo.labelVerticalAnchor,
            tickLabelAdditionalOffsets = labelsInfo.labelAdditionalOffsets,
            tickLabelsTextBounds = BreakLabelsLayoutUtil.textBounds(
                labelsBounds,
                labelsLayout.theme.tickLabelMargins(),
                orientation
            ),
            tickLabelBoundsList = labelsInfo.labelBoundsList
        )
    }

    protected abstract fun toAxisMapper(axisLength: Double): (Double?) -> Double?

    protected fun toScaleMapper(axisLength: Double): ScaleMapper<Double> {
        return Mappers.mul(domainRange, axisLength)
    }

    companion object {
        fun create(
            orientation: Orientation,
            axisDomain: DoubleSpan,
            breaksProvider: AxisBreaksProvider,
            geomAreaInsets: Insets,
            theme: AxisTheme
        ): AxisLayouter {

            if (orientation.isHorizontal) {
                val labelsLayout: AxisLabelsLayout = if (breaksProvider.isFixedBreaks) {
                    val trimmedScaleBreaks = with(breaksProvider.fixedBreaks) {
                        ScaleBreaks(domainValues, transformedValues, labels.map(::trimLongValues))
                    }
                    AxisLabelsLayout.horizontalFixedBreaks(
                        orientation,
                        axisDomain,
                        trimmedScaleBreaks,
                        geomAreaInsets,
                        theme
                    )
                } else {
                    AxisLabelsLayout.horizontalFlexBreaks(orientation, axisDomain, breaksProvider, theme)
                }
                return HorizontalAxisLayouter(
                    orientation,
                    axisDomain,
                    labelsLayout
                )
            }

            // vertical
            val labelsLayout: AxisLabelsLayout = if (breaksProvider.isFixedBreaks) {
                val trimmedScaleBreaks = with(breaksProvider.fixedBreaks) {
                    ScaleBreaks(domainValues, transformedValues, labels.map(::trimLongValues))
                }
                AxisLabelsLayout.verticalFixedBreaks(orientation, axisDomain, trimmedScaleBreaks, theme)
            } else {
                AxisLabelsLayout.verticalFlexBreaks(orientation, axisDomain, breaksProvider, theme)
            }
            return VerticalAxisLayouter(
                orientation,
                axisDomain,
                labelsLayout
            )
        }

        private fun trimLongValues(text : String) : String {
            return if (text.length <= Axis.LABEL_MAX_LENGTH) {
                text
            } else {
                text.take(Axis.LABEL_MAX_LENGTH) + ".."
            }
        }
    }
}
