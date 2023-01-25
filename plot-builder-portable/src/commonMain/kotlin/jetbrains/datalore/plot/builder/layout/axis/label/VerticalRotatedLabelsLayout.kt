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
import jetbrains.datalore.plot.builder.presentation.LabelSpec
import jetbrains.datalore.plot.builder.theme.AxisTheme

internal class VerticalRotatedLabelsLayout(
    orientation: Orientation,
    axisDomain: DoubleSpan,
    labelSpec: LabelSpec,
    breaks: ScaleBreaks,
    theme: AxisTheme,
    private val myRotationAngle: Double
) : AbstractFixedBreaksLabelsLayout(orientation, axisDomain, labelSpec, breaks, theme) {

    override fun doLayout(axisLength: Double, axisMapper: (Double?) -> Double?): AxisLabelsLayoutInfo {
        check(!orientation.isHorizontal)

        val ticks = mapToAxis(breaks.transformedValues, axisMapper)
        val labelBoundsList = labelBoundsList(ticks, breaks.labels) { y: Double -> DoubleVector(0.0, y) }

        // total bounds
        val maxLabelWidth = labelBoundsList.maxOfOrNull(DoubleRectangle::width) ?: 0.0
        var y1 = 0.0
        var y2 = 0.0
        if (!breaks.isEmpty) {
            val minIndex = ticks.indexOf(ticks.minBy { it })
            val maxIndex = ticks.indexOf(ticks.maxBy { it })

            y1 = ticks[minIndex]
            y2 = ticks[maxIndex]
            y1 -= labelBoundsList[minIndex].height / 2
            y2 += labelBoundsList[maxIndex].height / 2
        }
        val origin = DoubleVector(0.0, y1)
        val dimensions = DoubleVector(maxLabelWidth, y2 - y1)
        val bounds = DoubleRectangle(origin, dimensions)

        // add additional offsets for labels
        val xOffset: (DoubleRectangle) -> Double = when (orientation) {
            Orientation.LEFT -> { d: DoubleRectangle -> -d.width / 2 }
            Orientation.RIGHT -> { d: DoubleRectangle -> d.width / 2 }
            else -> throw IllegalStateException("Unsupported orientation $orientation")
        }
        val labelAdditionalOffsets = labelBoundsList.map { DoubleVector(xOffset(it), 0.0) }

        return createAxisLabelsLayoutInfoBuilder(bounds, overlap = false)
            .labelHorizontalAnchor(Text.HorizontalAnchor.MIDDLE)
            .labelVerticalAnchor(Text.VerticalAnchor.CENTER)
            .labelRotationAngle(-myRotationAngle)
            .labelAdditionalOffsets(labelAdditionalOffsets)
            .labelBoundsList(labelBoundsList.map(::alignToLabelMargin)) // for debug drawing
            .build()
    }

    override fun labelBounds(labelNormalSize: DoubleVector): DoubleRectangle {
        return BreakLabelsLayoutUtil.rotatedLabelBounds(labelNormalSize, myRotationAngle).let {
            // make vertical centered
            DoubleRectangle(0.0, -it.height / 2, it.width, it.height)
        }
    }
}