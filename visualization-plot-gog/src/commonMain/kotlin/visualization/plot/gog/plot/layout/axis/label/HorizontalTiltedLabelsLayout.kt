package jetbrains.datalore.visualization.plot.gog.plot.layout.axis.label

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.projectionGeometry.GeoUtils.toRadians
import jetbrains.datalore.visualization.plot.gog.core.render.svg.TextLabel
import jetbrains.datalore.visualization.plot.gog.plot.guide.Orientation
import jetbrains.datalore.visualization.plot.gog.plot.guide.Orientation.BOTTOM
import jetbrains.datalore.visualization.plot.gog.plot.layout.axis.GuideBreaks
import jetbrains.datalore.visualization.plot.gog.plot.presentation.PlotLabelSpec
import jetbrains.datalore.visualization.plot.gog.plot.theme.AxisTheme
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

internal class HorizontalTiltedLabelsLayout(orientation: Orientation,
                                            axisDomain: ClosedRange<Double>, labelSpec: PlotLabelSpec, breaks: GuideBreaks, theme: AxisTheme) : AbstractFixedBreaksLabelsLayout(orientation, axisDomain, labelSpec, breaks, theme) {

    private val labelHorizontalAnchor: TextLabel.HorizontalAnchor
        get() {
            if (orientation === BOTTOM) {
                return TextLabel.HorizontalAnchor.RIGHT
            }
            throw RuntimeException("Not implemented")
        }

    private val labelVerticalAnchor: TextLabel.VerticalAnchor
        get() = TextLabel.VerticalAnchor.TOP

    override fun doLayout(axisLength: Double, axisMapper: (Double) -> Double, maxLabelsBounds: DoubleRectangle?): AxisLabelsLayoutInfo {
        val height = labelSpec.height()

        val ticks = mapToAxis(breaks.transformedValues, axisMapper)
        var overlap = false
        if (breaks.size() >= 2) {
            val minTickDistance = abs((height + MIN_DISTANCE) / SIN)
            val tickDistance = abs(ticks[0] - ticks[1])
            overlap = tickDistance < minTickDistance
        }

        val bounds = labelsBounds(ticks, breaks.labels, AbstractFixedBreaksLabelsLayout.HORIZONTAL_TICK_LOCATION)
        return createAxisLabelsLayoutInfoBuilder(bounds!!, overlap)
                .labelHorizontalAnchor(labelHorizontalAnchor)
                .labelVerticalAnchor(labelVerticalAnchor)
                .labelRotationAngle(ROTATION_DEGREE)
                .build()
    }

    override fun labelBounds(labelDim: DoubleVector): DoubleRectangle {
        // only works for RIGHT-TOP anchor ang angle 0..-90
        if (!(ROTATION_DEGREE >= -90 && ROTATION_DEGREE <= 0
                        && labelHorizontalAnchor === TextLabel.HorizontalAnchor.RIGHT
                        && labelVerticalAnchor === TextLabel.VerticalAnchor.TOP)) {
            throw RuntimeException("Not implemented")
        }


        val w = abs(labelDim.x * COS) + 2 * abs(labelDim.y * SIN)
        val h = abs(labelDim.x * SIN) + abs(labelDim.y * COS)
        val x = -(abs(labelDim.x * COS) + abs(labelDim.y * SIN))
        val y = 0.0

        return DoubleRectangle(x, y, w, h)
    }

    companion object {
        private val MIN_DISTANCE = 5.0
        private val ROTATION_DEGREE = -30.0

        private val SIN = sin(toRadians(ROTATION_DEGREE))
        private val COS = cos(toRadians(ROTATION_DEGREE))
    }
}
