/*
 * Copyright (c) 2020. JetBrains s.r.o.
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

internal class HorizontalMultilineLabelsLayout(
    orientation: Orientation,
    axisDomain: DoubleSpan,
    labelSpec: LabelSpec,
    breaks: ScaleBreaks,
    theme: AxisTheme,
    private val myMaxLines: Int
) : AbstractFixedBreaksLabelsLayout(orientation, axisDomain, labelSpec, breaks, theme) {

    private val myShelfIndexForTickIndex = ArrayList<Int>()

    private val labelAdditionalOffsets: List<DoubleVector>
        get() {
            val h = labelSpec.height() * LINE_HEIGHT
            val result = ArrayList<DoubleVector>()
            for (i in 0 until breaks.size) {
                result.add(DoubleVector(0.0, myShelfIndexForTickIndex[i] * h))
            }
            return result
        }

    override fun doLayout(
        axisLength: Double,
        axisMapper: (Double?) -> Double?
    ): AxisLabelsLayoutInfo {

        val boundsByShelfIndex = HashMap<Int, DoubleRectangle>()
        val ticks = mapToAxis(breaks.transformedValues, axisMapper)
        val boundsList = labelBoundsList(
            ticks, breaks.labels,
            HORIZONTAL_TICK_LOCATION
        )

        for (labelBounds in boundsList) {
            // find shelf with no overlap
            var shelfIndex = 0
            while (true) {
                if (!boundsByShelfIndex.containsKey(shelfIndex)) {
                    boundsByShelfIndex[shelfIndex] = labelBounds
                    myShelfIndexForTickIndex.add(shelfIndex)
                    break
                }

                var shelfBounds = boundsByShelfIndex[shelfIndex]!!
                // not overlapped?
                if (!shelfBounds.xRange()
                        .connected(DoubleSpan(labelBounds.left - MIN_DISTANCE, labelBounds.right + MIN_DISTANCE))
                ) {
                    myShelfIndexForTickIndex.add(shelfIndex)
                    shelfBounds = shelfBounds.union(labelBounds)
                    boundsByShelfIndex[shelfIndex] = shelfBounds
                    break
                }

                shelfIndex++
            }
        }

        var bounds = if (boundsByShelfIndex.isEmpty())
            DoubleRectangle(DoubleVector.ZERO, DoubleVector.ZERO)
        else
            boundsByShelfIndex[0]!!
        val h = labelSpec.height() * LINE_HEIGHT
        for (i in 0 until boundsByShelfIndex.size) {
            val shelfBounds = boundsByShelfIndex[i]!!
            bounds = bounds.union(shelfBounds.add(DoubleVector(0.0, i * h)))
        }

        val linesCount = boundsByShelfIndex.size
        return AxisLabelsLayoutInfo.Builder()
            .breaks(breaks)
            .bounds(applyLabelsMargins(bounds))
            .overlap(linesCount > myMaxLines)
            .labelAdditionalOffsets(labelAdditionalOffsets)
            .labelHorizontalAnchor(Text.HorizontalAnchor.MIDDLE)
            .labelVerticalAnchor(Text.VerticalAnchor.TOP)
            .build()
    }

    override fun labelBounds(labelNormalSize: DoubleVector): DoubleRectangle {
        return BreakLabelsLayoutUtil.horizontalCenteredLabelBounds(
            labelNormalSize
        )
    }

    companion object {
        private const val LINE_HEIGHT = 1.2
        private const val MIN_DISTANCE = 60
    }
}
