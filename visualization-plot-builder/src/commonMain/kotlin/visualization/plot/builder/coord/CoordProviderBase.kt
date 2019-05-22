package jetbrains.datalore.visualization.plot.builder.coord

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.visualization.plot.base.CoordinateSystem
import jetbrains.datalore.visualization.plot.base.coord.Coords
import jetbrains.datalore.visualization.plot.base.scale.MapperUtil
import jetbrains.datalore.visualization.plot.base.scale.Mappers
import jetbrains.datalore.visualization.plot.base.scale.Scale2
import jetbrains.datalore.visualization.plot.builder.layout.axis.GuideBreaks

internal abstract class CoordProviderBase : CoordProvider {

    override fun buildAxisScaleX(scaleProto: Scale2<Double>, domain: ClosedRange<Double>, axisLength: Double, breaks: GuideBreaks): Scale2<Double> {
        return buildAxisScaleDefault(scaleProto, domain, axisLength, breaks)
    }

    override fun buildAxisScaleY(scaleProto: Scale2<Double>, domain: ClosedRange<Double>, axisLength: Double, breaks: GuideBreaks): Scale2<Double> {
        return buildAxisScaleDefault(scaleProto, domain, axisLength, breaks)
    }

    override fun createCoordinateSystem(xDomain: ClosedRange<Double>, xAxisLength: Double, yDomain: ClosedRange<Double>, yAxisLength: Double): CoordinateSystem {
        return Coords.create(
                MapperUtil.map(xDomain, axisMapper(xDomain, xAxisLength)),
                MapperUtil.map(yDomain, axisMapper(yDomain, yAxisLength)))
    }

    companion object {
        fun axisMapper(domain: ClosedRange<Double>, axisLength: Double): (Double?) -> Double? {
            return Mappers.mul(domain, axisLength)
        }

        private fun buildAxisScaleDefault(scaleProto: Scale2<Double>, domain: ClosedRange<Double>, axisLength: Double, breaks: GuideBreaks): Scale2<Double> {
            return buildAxisScaleDefault(scaleProto, axisMapper(domain, axisLength), breaks)
        }

        fun buildAxisScaleDefault(scaleProto: Scale2<Double>, axisMapper: (Double?) -> Double?, breaks: GuideBreaks): Scale2<Double> {
            return scaleProto.with()
                    .breaks(breaks.domainValues)
                    .labels(breaks.labels)
                    .mapper(axisMapper)
                    .build()
        }
    }
}
