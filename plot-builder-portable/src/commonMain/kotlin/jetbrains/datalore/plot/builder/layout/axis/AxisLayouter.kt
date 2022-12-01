/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout.axis

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.plot.base.ScaleMapper
import jetbrains.datalore.plot.base.scale.Mappers
import jetbrains.datalore.plot.builder.guide.Orientation
import jetbrains.datalore.plot.builder.layout.AxisLayoutInfo
import jetbrains.datalore.plot.builder.layout.axis.label.AxisLabelsLayout
import jetbrains.datalore.plot.builder.layout.axis.label.BreakLabelsLayoutUtil
import jetbrains.datalore.plot.builder.theme.AxisTheme

abstract class AxisLayouter(
    val orientation: Orientation,
    private val domainRange: DoubleSpan,
    private val labelsLayout: AxisLabelsLayout
) {

    fun doLayout(axisLength: Double, maxTickLabelsBounds: DoubleRectangle?): AxisLayoutInfo {
        val labelsInfo = labelsLayout.doLayout(axisLength, toAxisMapper(axisLength), maxTickLabelsBounds)
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
            tickLabelsBoundsMax = maxTickLabelsBounds,
            tickLabelsTextBounds = BreakLabelsLayoutUtil.textBounds(labelsBounds, labelsLayout.theme.tickLabelMargins(), orientation)
        )
    }

    protected abstract fun toAxisMapper(axisLength: Double): (Double?) -> Double?

    protected fun toScaleMapper(axisLength: Double): ScaleMapper<Double> {
        return Mappers.mul(domainRange, axisLength)
    }

    companion object {
        fun create(
            orientation: Orientation,
            axisDomain: DoubleSpan, breaksProvider: AxisBreaksProvider, theme: AxisTheme
        ): AxisLayouter {

            if (orientation.isHorizontal) {
                val labelsLayout: AxisLabelsLayout = if (breaksProvider.isFixedBreaks) {
                    AxisLabelsLayout.horizontalFixedBreaks(
                        orientation,
                        axisDomain,
                        breaksProvider.fixedBreaks,
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
                AxisLabelsLayout.verticalFixedBreaks(orientation, axisDomain, breaksProvider.fixedBreaks, theme)
            } else {
                AxisLabelsLayout.verticalFlexBreaks(orientation, axisDomain, breaksProvider, theme)
            }
            return VerticalAxisLayouter(
                orientation,
                axisDomain,
                labelsLayout
            )
        }
    }
}
