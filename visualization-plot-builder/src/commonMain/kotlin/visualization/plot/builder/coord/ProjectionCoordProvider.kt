package jetbrains.datalore.visualization.plot.builder.coord

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.visualization.plot.base.coord.Projection
import jetbrains.datalore.visualization.plot.base.scale.Mappers
import jetbrains.datalore.visualization.plot.base.scale.Scale2
import jetbrains.datalore.visualization.plot.builder.layout.axis.GuideBreaks
import jetbrains.datalore.visualization.plot.common.data.SeriesUtil

internal class ProjectionCoordProvider private constructor(private val myProjectionX: Projection?, private val myProjectionY: Projection?)// square grid
    : FixedRatioCoordProvider(1.0) {

    override fun buildAxisScaleX(scaleProto: Scale2<Double>, domain: ClosedRange<Double>, axisLength: Double, breaks: GuideBreaks): Scale2<Double> {
        return if (myProjectionX != null) {
            buildAxisScaleWithProjection(myProjectionX, scaleProto, domain, axisLength, breaks)
        } else super.buildAxisScaleX(scaleProto, domain, axisLength, breaks)
    }

    override fun buildAxisScaleY(scaleProto: Scale2<Double>, domain: ClosedRange<Double>, axisLength: Double, breaks: GuideBreaks): Scale2<Double> {
        return if (myProjectionY != null) {
            buildAxisScaleWithProjection(myProjectionY, scaleProto, domain, axisLength, breaks)
        } else super.buildAxisScaleY(scaleProto, domain, axisLength, breaks)
    }

    companion object {
        fun withProjectionY(projectionY: Projection): CoordProvider {
            return ProjectionCoordProvider(null, projectionY)
        }

        private fun buildAxisScaleWithProjection(projection: Projection, scaleProto: Scale2<Double>,
                                                 domain: ClosedRange<Double>, axisLength: Double, breaks: GuideBreaks): Scale2<Double> {

            val validDomain = projection.toValidDomain(domain)
            val validDomainProjected = ClosedRange.closed(
                    projection.apply(validDomain.lowerEndpoint())!!,
                    projection.apply(validDomain.upperEndpoint())!!
            )

            val projectionInverse = Mappers.linear(validDomainProjected, validDomain)

            val linearMapper = axisMapper(domain, axisLength)
            val scaleMapper = twistScaleMapper(projection, projectionInverse, linearMapper)
            val validBreaks = validateBreaks(validDomain, breaks)
            return buildAxisScaleDefault(scaleProto, scaleMapper, validBreaks)
        }

        private fun validateBreaks(validDomain: ClosedRange<Double>, breaks: GuideBreaks): GuideBreaks {
            val validIndices = ArrayList<Int>()
            var i = 0
            for (v in breaks.domainValues) {
                if (v is Double && validDomain.contains(v)) {
                    validIndices.add(i)
                }
                i++
            }

            if (validIndices.size == breaks.domainValues.size) {
                return breaks
            }

            val validDomainValues = SeriesUtil.pickAtIndices(breaks.domainValues, validIndices)
            val validLabels = SeriesUtil.pickAtIndices(breaks.labels, validIndices)
            val validTransformedValues = SeriesUtil.pickAtIndices(breaks.transformedValues, validIndices)
            return GuideBreaks(validDomainValues, validTransformedValues, validLabels)
        }

        private fun twistScaleMapper(
                projection: Projection, projectionInverse: (Double) -> Double,
                scaleMapper: (Double?) -> Double?): (Double?) -> Double? {
            return { v ->
                val projected = projection.apply(v)
                val unProjected = projectionInverse(projected!!)
                scaleMapper(unProjected)
            }
        }
    }
}
