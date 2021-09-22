/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.coord

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Pair
import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.base.coord.Projection
import jetbrains.datalore.plot.base.scale.Mappers
import jetbrains.datalore.plot.base.scale.ScaleBreaks
import jetbrains.datalore.plot.common.data.SeriesUtil

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

    override fun adjustDomains(
        xDomain: ClosedRange<Double>,
        yDomain: ClosedRange<Double>,
        displaySize: DoubleVector
    ): Pair<ClosedRange<Double>, ClosedRange<Double>> {

        // account for limits
        val adjusted = super.adjustDomains(xDomain, yDomain, displaySize)

        @Suppress("NAME_SHADOWING")
        val xDomain = projectionX.toValidDomain(adjusted.first)

        @Suppress("NAME_SHADOWING")
        val yDomain = projectionY.toValidDomain(adjusted.second)

        // compute projected ratio
        val spanX = SeriesUtil.span(xDomain)
        val spanY = SeriesUtil.span(yDomain)
        val domainSquare: Pair<ClosedRange<Double>, ClosedRange<Double>> =
            if (spanX > spanY) {
                val center = xDomain.lowerEnd + spanX / 2
                val halfSpan = spanY / 2
                Pair(
                    ClosedRange(center - halfSpan, center + halfSpan),
                    yDomain
                )
            } else {
                val center = yDomain.lowerEnd + spanY / 2
                val halfSpan = spanX / 2
                Pair(
                    xDomain,
                    ClosedRange(center - halfSpan, center + halfSpan)
                )
            }

        val projectedXMin = projectionX.apply(domainSquare.first.lowerEnd)
        val projectedXMax = projectionX.apply(domainSquare.first.upperEnd)
        val projectedYMin = projectionY.apply(domainSquare.second.lowerEnd)
        val projectedYMax = projectionY.apply(domainSquare.second.upperEnd)

        val ratio = (projectedYMax - projectedYMin) / (projectedXMax - projectedXMin)
        val fixedCoord = FixedRatioCoordProvider(ratio, null, null, false)
        return fixedCoord.adjustDomains(xDomain, yDomain, displaySize)
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
