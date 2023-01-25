/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout.axis.label

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.plot.base.render.svg.Text
import jetbrains.datalore.plot.base.scale.ScaleBreaks
import jetbrains.datalore.plot.builder.guide.Orientation
import jetbrains.datalore.plot.builder.layout.GeometryUtil
import jetbrains.datalore.plot.builder.presentation.LabelSpec
import jetbrains.datalore.plot.builder.theme.AxisTheme

internal class HorizontalRotatedLabelsLayout(
    orientation: Orientation,
    axisDomain: DoubleSpan,
    labelSpec: LabelSpec,
    breaks: ScaleBreaks,
    theme: AxisTheme,
    private val myRotationAngle: Double
) : AbstractFixedBreaksLabelsLayout(orientation, axisDomain, labelSpec, breaks, theme) {

    override fun doLayout(
        axisLength: Double,
        axisMapper: (Double?) -> Double?
    ): AxisLabelsLayoutInfo {

        val ticks = mapToAxis(breaks.transformedValues, axisMapper)
        val labelBoundsList = labelBoundsList(ticks, breaks.labels, HORIZONTAL_TICK_LOCATION)

        var overlap = false
        val bounds = labelBoundsList.fold(null) { acc: DoubleRectangle?, b ->
            overlap = overlap || acc != null && acc.xRange().connected(
                b.xRange().expanded(MIN_TICK_LABEL_DISTANCE / 2)
            )
            GeometryUtil.union(b, acc)
        }

        // add additional offsets for labels
        val yOffset: (DoubleRectangle) -> Double = when (orientation) {
            Orientation.TOP -> { d: DoubleRectangle -> -d.height / 2 }
            Orientation.BOTTOM -> { d: DoubleRectangle -> d.height / 2 }
            else -> throw IllegalStateException("Unsupported orientation $orientation")
        }
        val labelAdditionalOffsets = labelBoundsList.map { DoubleVector(0.0, yOffset(it)) }

        return createAxisLabelsLayoutInfoBuilder(bounds!!, overlap)
            .labelHorizontalAnchor(Text.HorizontalAnchor.MIDDLE)
            .labelVerticalAnchor(Text.VerticalAnchor.CENTER)
            .labelRotationAngle(-myRotationAngle)
            .labelAdditionalOffsets(labelAdditionalOffsets)
            .labelBoundsList(labelBoundsList.map(::alignToLabelMargin))  // for debug drawing
            .build()
    }

    override fun labelBounds(labelNormalSize: DoubleVector): DoubleRectangle {
        return BreakLabelsLayoutUtil.rotatedLabelBounds(labelNormalSize, myRotationAngle).let {
            BreakLabelsLayoutUtil.horizontalCenteredLabelBounds(it.dimension)
        }
    }
}