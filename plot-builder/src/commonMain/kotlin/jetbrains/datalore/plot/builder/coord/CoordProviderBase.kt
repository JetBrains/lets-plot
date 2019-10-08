package jetbrains.datalore.plot.builder.coord

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.plot.builder.layout.axis.GuideBreaks
import jetbrains.datalore.visualization.plot.base.CoordinateSystem
import jetbrains.datalore.visualization.plot.base.Scale
import jetbrains.datalore.visualization.plot.base.coord.Coords
import jetbrains.datalore.visualization.plot.base.scale.MapperUtil
import jetbrains.datalore.visualization.plot.base.scale.Mappers

internal abstract class CoordProviderBase : jetbrains.datalore.plot.builder.coord.CoordProvider {

    override fun buildAxisScaleX(scaleProto: Scale<Double>, domain: ClosedRange<Double>, axisLength: Double, breaks: GuideBreaks): Scale<Double> {
        return jetbrains.datalore.plot.builder.coord.CoordProviderBase.Companion.buildAxisScaleDefault(
            scaleProto,
            domain,
            axisLength,
            breaks
        )
    }

    override fun buildAxisScaleY(scaleProto: Scale<Double>, domain: ClosedRange<Double>, axisLength: Double, breaks: GuideBreaks): Scale<Double> {
        return jetbrains.datalore.plot.builder.coord.CoordProviderBase.Companion.buildAxisScaleDefault(
            scaleProto,
            domain,
            axisLength,
            breaks
        )
    }

    override fun createCoordinateSystem(xDomain: ClosedRange<Double>, xAxisLength: Double, yDomain: ClosedRange<Double>, yAxisLength: Double): CoordinateSystem {
        return Coords.create(
                MapperUtil.map(xDomain,
                    jetbrains.datalore.plot.builder.coord.CoordProviderBase.Companion.axisMapper(xDomain, xAxisLength)
                ),
                MapperUtil.map(yDomain,
                    jetbrains.datalore.plot.builder.coord.CoordProviderBase.Companion.axisMapper(yDomain, yAxisLength)
                ))
    }

    companion object {
        fun axisMapper(domain: ClosedRange<Double>, axisLength: Double): (Double?) -> Double? {
            return Mappers.mul(domain, axisLength)
        }

        private fun buildAxisScaleDefault(scaleProto: Scale<Double>, domain: ClosedRange<Double>, axisLength: Double, breaks: GuideBreaks): Scale<Double> {
            return jetbrains.datalore.plot.builder.coord.CoordProviderBase.Companion.buildAxisScaleDefault(
                scaleProto,
                jetbrains.datalore.plot.builder.coord.CoordProviderBase.Companion.axisMapper(domain, axisLength),
                breaks
            )
        }

        fun buildAxisScaleDefault(scaleProto: Scale<Double>, axisMapper: (Double?) -> Double?, breaks: GuideBreaks): Scale<Double> {
            return scaleProto.with()
                    .breaks(breaks.domainValues)
                    .labels(breaks.labels)
                    .mapper(axisMapper)
                    .build()
        }
    }
}
