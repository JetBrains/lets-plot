package jetbrains.datalore.plot.builder.layout.axis.label

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.render.svg.TextLabel
import jetbrains.datalore.plot.builder.layout.GeometryUtil
import jetbrains.datalore.plot.builder.layout.axis.GuideBreaks
import jetbrains.datalore.plot.builder.presentation.PlotLabelSpec
import jetbrains.datalore.plot.builder.theme.AxisTheme

internal abstract class AbstractFixedBreaksLabelsLayout(orientation: jetbrains.datalore.plot.builder.guide.Orientation,
                                                        axisDomain: ClosedRange<Double>, labelSpec: PlotLabelSpec, protected val breaks: GuideBreaks, theme: AxisTheme) : AxisLabelsLayout(orientation, axisDomain, labelSpec, theme) {

    private fun labelBounds(labelLocation: DoubleVector, labelLength: Int): DoubleRectangle {
        val dim = labelSpec.dimensions(labelLength)
        val labelBounds = labelBounds(dim)
        return labelBounds.add(labelLocation)
    }

    protected abstract fun labelBounds(labelNormalSize: DoubleVector): DoubleRectangle

    fun labelsBounds(tickPositions: List<Double>, tickLabels: List<String>, toTickLocation: (Double) -> DoubleVector): DoubleRectangle? {
        val boundsList = labelBoundsList(tickPositions, breaks.labels, toTickLocation)
        var bounds: DoubleRectangle? = null
        for (labelBounds in boundsList) {
            bounds = GeometryUtil.union(labelBounds, bounds)
        }
        return bounds
    }

    fun labelBoundsList(tickPositions: List<Double>, tickLabels: List<String>, toTickLocation: (Double) -> DoubleVector): List<DoubleRectangle> {
        val result = ArrayList<DoubleRectangle>()
        val labels = tickLabels.iterator()
        for (pos in tickPositions) {
            val label = labels.next()
            val bounds = labelBounds(toTickLocation(pos), label.length)
            result.add(bounds)
        }
        return result
    }


    fun createAxisLabelsLayoutInfoBuilder(bounds: DoubleRectangle, overlap: Boolean): AxisLabelsLayoutInfo.Builder {
        return AxisLabelsLayoutInfo.Builder()
                .breaks(breaks)
                .bounds(applyLabelsOffset(bounds))
                .smallFont(false)
                .overlap(overlap)
    }

    fun noLabelsLayoutInfo(axisLength: Double, orientation: jetbrains.datalore.plot.builder.guide.Orientation): AxisLabelsLayoutInfo {
        if (orientation.isHorizontal) {
            var bounds = DoubleRectangle(axisLength / 2, 0.0, 0.0, 0.0) // empty bounds in the middle of the axis;
            bounds = applyLabelsOffset(bounds)
            return AxisLabelsLayoutInfo.Builder()
                    .breaks(breaks)
                    .bounds(bounds)
                    .smallFont(false)
                    .overlap(false)
                    .labelAdditionalOffsets(null)
                    .labelHorizontalAnchor(TextLabel.HorizontalAnchor.MIDDLE)
                    .labelVerticalAnchor(TextLabel.VerticalAnchor.TOP)
                    .build()
        }

        throw IllegalStateException("Not implemented for $orientation")
    }

    companion object {
        val HORIZONTAL_TICK_LOCATION = { x: Double -> DoubleVector(x, 0.0) }
    }

}
