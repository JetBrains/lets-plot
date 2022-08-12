/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.coord

import jetbrains.datalore.base.geometry.DoubleRectangles.boundingBox
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.base.spatial.projections.Projection
import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.base.ScaleMapper
import jetbrains.datalore.plot.base.scale.Mappers
import jetbrains.datalore.plot.base.scale.ScaleBreaks
import jetbrains.datalore.plot.common.data.SeriesUtil

internal class ProjectionCoordProvider(
    override val projection: Projection,
    xLim: DoubleSpan?,
    yLim: DoubleSpan?,
    flipped: Boolean
) : CoordProviderBase(xLim, yLim, flipped) {

    override fun with(
        xLim: DoubleSpan?,
        yLim: DoubleSpan?,
        flipped: Boolean
    ): CoordProvider {
        return ProjectionCoordProvider(projection, xLim, yLim, flipped)
    }

    protected override fun adjustDomainsIntern(
        hDomain: DoubleSpan,
        vDomain: DoubleSpan
    ): Pair<DoubleSpan, DoubleSpan> {
        @Suppress("NAME_SHADOWING")
        val xDomain = toValidDomain(projection.validRect().xRange(), hDomain)

        @Suppress("NAME_SHADOWING")
        val yDomain = toValidDomain(projection.validRect().yRange(), vDomain)
        return (xDomain to yDomain)
    }

    override fun adjustGeomSize(
        hDomain: DoubleSpan,
        vDomain: DoubleSpan,
        geomSize: DoubleVector
    ): DoubleVector {
        // Adjust geom dimensions ratio.
        val bbox = boundingBox(
            listOf(
                DoubleVector(hDomain.lowerEnd, vDomain.lowerEnd),
                DoubleVector(hDomain.lowerEnd, vDomain.upperEnd),
                DoubleVector(hDomain.upperEnd, vDomain.lowerEnd),
                DoubleVector(hDomain.upperEnd, vDomain.upperEnd)
            ).mapNotNull(projection::project)
        )
            ?: error("adjustGeomSize() - can't compute bbox")

        val domainRatio = bbox.width / bbox.height

        return FixedRatioCoordProvider.reshapeGeom(geomSize, domainRatio)
    }

    override fun buildAxisScaleX(
        scaleProto: Scale<Double>,
        domain: DoubleSpan,
        yDomain: DoubleSpan,
        breaks: ScaleBreaks
    ): Scale<Double> {
        return if (projection.nonlinear) {
            buildAxisScaleWithProjection(
                projection.validRect().xRange(),
                scaleProto,
                domain,
                breaks
            )
        } else {
            super.buildAxisScaleX(scaleProto, domain, yDomain, breaks)
        }
    }

    override fun buildAxisScaleY(
        scaleProto: Scale<Double>,
        domain: DoubleSpan,
        xDomain: DoubleSpan,
        breaks: ScaleBreaks
    ): Scale<Double> {
        return if (projection.nonlinear) {
            buildAxisScaleWithProjection(
                projection.validRect().yRange(),
                scaleProto,
                domain,
                breaks
            )
        } else {
            super.buildAxisScaleY(scaleProto, domain, xDomain, breaks)
        }
    }

    override fun buildAxisXScaleMapper(
        domain: DoubleSpan,
        axisLength: Double,
        yDomain: DoubleSpan
    ): ScaleMapper<Double> {
        return if (projection.nonlinear) {
            val validDomain = toValidDomain(projection.validRect().xRange(), domain)
            buildAxisScaleMapperWithProjection(
                { x -> projection.project(DoubleVector(x, yDomain.lowerEnd))?.x ?: Double.NaN },
                domain,
                validDomain,
                axisLength,
            )
        } else {
            super.buildAxisXScaleMapper(domain, axisLength, yDomain)
        }
    }

    override fun buildAxisYScaleMapper(
        domain: DoubleSpan,
        axisLength: Double,
        xDomain: DoubleSpan
    ): ScaleMapper<Double> {
        return if (projection.nonlinear) {
            val validDomain = toValidDomain(projection.validRect().yRange(), domain)
            buildAxisScaleMapperWithProjection(
                { y -> projection.project(DoubleVector(xDomain.lowerEnd, y))?.y ?: Double.NaN },
                domain,
                validDomain,
                axisLength,
            )
        } else {
            super.buildAxisXScaleMapper(domain, axisLength, xDomain)
        }
    }

    companion object {
        private fun toValidDomain(validRange: DoubleSpan, domain: DoubleSpan): DoubleSpan {
            if (validRange.connected(domain)) {
                return validRange.intersection(domain)
            }
            throw IllegalArgumentException("Illegal range: $domain")
        }

        private fun buildAxisScaleWithProjection(
            validRange: DoubleSpan,
            scaleProto: Scale<Double>,
            domain: DoubleSpan,
            breaks: ScaleBreaks
        ): Scale<Double> {
            val validDomain = toValidDomain(validRange, domain)

            val validBreaks = validateBreaks(validDomain, breaks)
            return buildAxisScaleDefault(
                scaleProto,
                validBreaks
            )
        }

        private fun validateBreaks(validDomain: DoubleSpan, breaks: ScaleBreaks): ScaleBreaks {
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
            return ScaleBreaks(
                validDomainValues,
                validTransformedValues,
                validLabels
            )
        }

        private fun buildAxisScaleMapperWithProjection(
            projection: (Double) -> Double,
            domain: DoubleSpan,
            validDomain: DoubleSpan,
            axisLength: Double,
        ): ScaleMapper<Double> {
            val linearMapper = linearMapper(
                domain,
                axisLength
            )

            val validDomainProjected = DoubleSpan(
                projection(validDomain.lowerEnd),
                projection(validDomain.upperEnd)
            )

            val projectionInverse = Mappers.linear(validDomainProjected, validDomain)
            return twistScaleMapper(
                projection,
                projectionInverse,
                linearMapper
            )
        }

        private fun twistScaleMapper(
            projection: (Double) -> Double,
            projectionInverse: ScaleMapper<Double>,
            scaleMapper: ScaleMapper<Double>
        ): ScaleMapper<Double> {
            return object : ScaleMapper<Double> {
                override fun invoke(v: Double?): Double? {
                    return v?.let {
                        val projected = projection(it)
                        val unProjected = projectionInverse(projected)
                        scaleMapper(unProjected)
                    }
                }
            }
        }
    }
}
