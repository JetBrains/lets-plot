package jetbrains.datalore.plot.builder.layout.axis.label

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.builder.layout.axis.GuideBreaks
import jetbrains.datalore.plot.builder.presentation.PlotLabelSpec
import jetbrains.datalore.plot.builder.theme.AxisTheme
import jetbrains.datalore.visualization.plot.base.render.svg.TextLabel

internal class HorizontalMultilineLabelsLayout(
    orientation: jetbrains.datalore.plot.builder.guide.Orientation,
    axisDomain: ClosedRange<Double>,
    labelSpec: PlotLabelSpec,
    breaks: GuideBreaks,
    theme: AxisTheme,
    private val myMaxLines: Int) :
        AbstractFixedBreaksLabelsLayout(orientation, axisDomain, labelSpec, breaks, theme) {

    private val myShelfIndexForTickIndex = ArrayList<Int>()

    private val labelAdditionalOffsets: List<DoubleVector>
        get() {
            val h = labelSpec.height() * LINE_HEIGHT
            val result = ArrayList<DoubleVector>()
            for (i in 0 until breaks.size()) {
                result.add(DoubleVector(0.0, myShelfIndexForTickIndex[i] * h))
            }
            return result
        }

    override fun doLayout(
            axisLength: Double,
            axisMapper: (Double?) -> Double?,
            maxLabelsBounds: DoubleRectangle?): AxisLabelsLayoutInfo {

        val boundsByShelfIndex = HashMap<Int, DoubleRectangle>()
        val ticks = mapToAxis(breaks.transformedValues, axisMapper)
        val boundsList = labelBoundsList(ticks, breaks.labels, HORIZONTAL_TICK_LOCATION)

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
                if (!shelfBounds.xRange().isConnected(ClosedRange.closed(labelBounds.left - MIN_DISTANCE, labelBounds.right + MIN_DISTANCE))) {
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
                .bounds(applyLabelsOffset(bounds))
                .smallFont(false)
                .overlap(linesCount > myMaxLines)
                .labelAdditionalOffsets(labelAdditionalOffsets)
                .labelHorizontalAnchor(TextLabel.HorizontalAnchor.MIDDLE)
                .labelVerticalAnchor(TextLabel.VerticalAnchor.TOP)
                .build()
    }

    override fun labelBounds(labelNormalSize: DoubleVector): DoubleRectangle {
        return BreakLabelsLayoutUtil.horizontalCenteredLabelBounds(labelNormalSize)
    }

    companion object {
        private const val LINE_HEIGHT = 1.2
        private const val MIN_DISTANCE = 60
    }
}
