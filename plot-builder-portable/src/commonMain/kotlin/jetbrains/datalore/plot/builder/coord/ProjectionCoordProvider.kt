/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.coord

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.spatial.MercatorUtils
import jetbrains.datalore.base.values.Pair
import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.base.coord.Projection
import jetbrains.datalore.plot.base.scale.Mappers
import jetbrains.datalore.plot.builder.layout.axis.GuideBreaks
import jetbrains.datalore.plot.common.data.SeriesUtil

internal class ProjectionCoordProvider private constructor(
    private val myProjectionX: Projection,
    private val myProjectionY: Projection,
    xLim: ClosedRange<Double>?,
    yLim: ClosedRange<Double>?
)// square grid
//    : FixedRatioCoordProvider(1.0, xLim, yLim) {
    : CoordProviderBase(xLim, yLim) {

    override fun buildAxisScaleX(
        scaleProto: Scale<Double>,
        domain: ClosedRange<Double>,
        axisLength: Double,
        breaks: GuideBreaks
    ): Scale<Double> {
        return buildAxisScaleWithProjection(
            myProjectionX,
            scaleProto,
            domain,
            axisLength,
            breaks
        )
    }

    override fun buildAxisScaleY(
        scaleProto: Scale<Double>,
        domain: ClosedRange<Double>,
        axisLength: Double,
        breaks: GuideBreaks
    ): Scale<Double> {
        return buildAxisScaleWithProjection(
            myProjectionY,
            scaleProto,
            domain,
            axisLength,
            breaks
        )
    }


    override fun adjustDomainsImpl(
        xDomain: ClosedRange<Double>,
        yDomain: ClosedRange<Double>,
        displaySize: DoubleVector
    ): Pair<ClosedRange<Double>, ClosedRange<Double>> {
        val dx  = myProjectionX.apply(180.0) - myProjectionX.apply(-180.0)
        val dy  = myProjectionY.apply(85.0) - myProjectionY.apply(-85.0)
        val orig_dx = 360.0
        val orig_dy = 170.0
        val ratio = (dy/dx ) / (orig_dy/orig_dx)

        val prvd = FixedRatioCoordProvider(  ratio, null, null )
        return prvd.adjustDomains(xDomain, yDomain, displaySize)

//        val newDomainX = ClosedRange( myProjectionX.apply(-180.0), myProjectionX.apply(180.0))
//        val newDomainY = ClosedRange( myProjectionY.apply(-85.0), myProjectionY.apply(85.0))
//        val prvd = FixedRatioCoordProvider(  1.0 , null, null )

//        return prvd.adjustDomains(newDomainX, newDomainY, displaySize)
    }

    companion object {
//        fun withProjectionY(
//            projectionY: Projection,
//            xLim: ClosedRange<Double>?,
//            yLim: ClosedRange<Double>?
//        ): CoordProvider {
//            return ProjectionCoordProvider(
//                null,
//                projectionY,
//                xLim,
//                yLim
//            )
//        }

        fun withProjectionXY(
            projectionX: Projection,
            projectionY: Projection,
            xLim: ClosedRange<Double>?,
            yLim: ClosedRange<Double>?
        ): CoordProvider {
            return ProjectionCoordProvider(
                projectionX,
                projectionY,
                xLim,
                yLim
            )
        }

        private fun buildAxisScaleWithProjection(
            projection: Projection, scaleProto: Scale<Double>,
            domain: ClosedRange<Double>, axisLength: Double, breaks: GuideBreaks
        ): Scale<Double> {

            val validDomain = projection.toValidDomain(domain)
            val validDomainProjected = ClosedRange(
                projection.apply(validDomain.lowerEnd),
                projection.apply(validDomain.upperEnd)
            )

            val projectionInverse = Mappers.linear(validDomainProjected, validDomain)

            val linearMapper =
                axisMapper(
                    domain,
                    axisLength
                )
            val scaleMapper =
                twistScaleMapper(
                    projection,
                    projectionInverse,
                    linearMapper
                )
            val validBreaks =
                validateBreaks(
                    validDomain,
                    breaks
                )
            return buildAxisScaleDefault(
                scaleProto,
                scaleMapper,
                validBreaks
            )
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
            return GuideBreaks(
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
                val projected = projection.apply(v!!)
                val unProjected = projectionInverse(projected)
                scaleMapper(unProjected)
            }
        }
    }
}
