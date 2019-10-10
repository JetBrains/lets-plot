package jetbrains.datalore.plot.builder.layout.axis

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.plot.base.coord.Coords
import jetbrains.datalore.plot.builder.layout.axis.label.AxisLabelsLayout

class HorizontalAxisLayouter(orientation: jetbrains.datalore.plot.builder.guide.Orientation, domainRange: ClosedRange<Double>, labelsLayout: AxisLabelsLayout) : AxisLayouter(orientation, domainRange, labelsLayout) {

    override fun toAxisMapper(axisLength: Double): (Double?) -> Double? {
        val scaleMapper = toScaleMapper(axisLength)
        val cartesianX = Coords.toClientOffsetX(ClosedRange.closed(0.0, axisLength))
        return { v ->
            val mapped = scaleMapper(v)
            if (mapped != null) cartesianX(mapped) else null
        }
    }
}
