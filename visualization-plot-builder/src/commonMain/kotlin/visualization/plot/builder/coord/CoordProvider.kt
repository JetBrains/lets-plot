package jetbrains.datalore.visualization.plot.builder.coord

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Pair
import jetbrains.datalore.visualization.plot.base.CoordinateSystem
import jetbrains.datalore.visualization.plot.base.Scale
import jetbrains.datalore.visualization.plot.builder.layout.axis.GuideBreaks

interface CoordProvider {
    fun createCoordinateSystem(xDomain: ClosedRange<Double>, xAxisLength: Double, yDomain: ClosedRange<Double>, yAxisLength: Double): CoordinateSystem

    fun buildAxisScaleX(scaleProto: Scale<Double>, domain: ClosedRange<Double>, axisLength: Double, breaks: GuideBreaks): Scale<Double>

    fun buildAxisScaleY(scaleProto: Scale<Double>, domain: ClosedRange<Double>, axisLength: Double, breaks: GuideBreaks): Scale<Double>

    fun adjustDomains(xDomain: ClosedRange<Double>, yDomain: ClosedRange<Double>, displaySize: DoubleVector): Pair<ClosedRange<Double>, ClosedRange<Double>>
}
