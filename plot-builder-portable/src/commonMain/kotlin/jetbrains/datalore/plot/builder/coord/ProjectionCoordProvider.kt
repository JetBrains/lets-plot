/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.coord

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.base.coord.Projection
import jetbrains.datalore.plot.base.scale.Mappers
import jetbrains.datalore.plot.base.scale.ScaleBreaks
import jetbrains.datalore.plot.common.data.SeriesUtil
import kotlin.math.abs

internal class ProjectionCoordProvider(
    private val projectionX: Projection,
    private val projectionY: Projection,
    xLim: ClosedRange<Double>?,
    yLim: ClosedRange<Double>?,
    flipped: Boolean
) : CoordProviderBase(xLim, yLim, flipped) {

    override fun with(
        xLim: ClosedRange<Double>?,
        yLim: ClosedRange<Double>?,
        flipped: Boolean
    ): CoordProvider {
        return ProjectionCoordProvider(projectionX, projectionY, xLim, yLim, flipped)
    }

    protected override fun adjustDomainsIntern(
        hDomain: ClosedRange<Double>,
        vDomain: ClosedRange<Double>
    ): Pair<ClosedRange<Double>, ClosedRange<Double>> {
        @Suppress("NAME_SHADOWING")
        val xDomain = projectionX.toValidDomain(hDomain)

        @Suppress("NAME_SHADOWING")
        val yDomain = projectionY.toValidDomain(vDomain)
        return (xDomain to yDomain)
    }

    override fun adjustGeomSize(
        hDomain: ClosedRange<Double>,
        vDomain: ClosedRange<Double>,
        geomSize: DoubleVector
    ): DoubleVector {
        // Adjust geom dimensions ratio.
        val h0 = projectionX.apply(hDomain.lowerEnd)
        val h1 = projectionX.apply(hDomain.upperEnd)
        val v0 = projectionY.apply(vDomain.lowerEnd)
        val v1 = projectionY.apply(vDomain.upperEnd)

        val domainRatio = abs(h1 - h0) / abs(v1 - v0)
        return FixedRatioCoordProvider.reshapeGeom(geomSize, domainRatio)
    }

    override fun buildAxisScaleX(
        scaleProto: Scale<Double>,
        domain: ClosedRange<Double>,
        axisLength: Double,
        breaks: ScaleBreaks
    ): Scale<Double> {
        return if (projectionX.nonlinear) {
            buildAxisScaleWithProjection(
                projectionX,
                scaleProto,
                domain,
                axisLength,
                breaks
            )
        } else {
            super.buildAxisScaleX(scaleProto, domain, axisLength, breaks)
        }
    }

    override fun buildAxisScaleY(
        scaleProto: Scale<Double>,
        domain: ClosedRange<Double>,
        axisLength: Double,
        breaks: ScaleBreaks
    ): Scale<Double> {
        return if (projectionY.nonlinear) {
            buildAxisScaleWithProjection(
                projectionY,
                scaleProto,
                domain,
                axisLength,
                breaks
            )
        } else {
            super.buildAxisScaleY(scaleProto, domain, axisLength, breaks)
        }
    }

    companion object {
        private fun buildAxisScaleWithProjection(
            projection: Projection, scaleProto: Scale<Double>,
            domain: ClosedRange<Double>,
            axisLength: Double,
            breaks: ScaleBreaks
        ): Scale<Double> {

            val validDomain = projection.toValidDomain(domain)
            val validDomainProjected = ClosedRange(
                projection.apply(validDomain.lowerEnd),
                projection.apply(validDomain.upperEnd)
            )

            val projectionInverse = Mappers.linear(validDomainProjected, validDomain)

            val linearMapper = linearMapper(
                domain,
                axisLength
            )
            val scaleMapper = twistScaleMapper(
                projection,
                projectionInverse,
                linearMapper
            )
            val validBreaks = validateBreaks(validDomain, breaks)
            return buildAxisScaleDefault(
                scaleProto,
                scaleMapper,
                validBreaks
            )
        }

        private fun validateBreaks(validDomain: ClosedRange<Double>, breaks: ScaleBreaks): ScaleBreaks {
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

        private fun twistScaleMapper(
            projection: Projection, projectionInverse: (Double) -> Double,
            scaleMapper: (Double?) -> Double?
        ): (Double?) -> Double? {
            return { v ->
                v?.run {
                    val projected = projection.apply(v)
                    val unProjected = projectionInverse(projected)
                    scaleMapper(unProjected)
                }
            }
        }
    }
}
