/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.coord

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.CoordinateSystem
import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.base.coord.Coords
import jetbrains.datalore.plot.base.scale.MapperUtil
import jetbrains.datalore.plot.base.scale.Mappers
import jetbrains.datalore.plot.base.scale.ScaleBreaks

internal abstract class CoordProviderBase(
    _xLim: ClosedRange<Double>?,
    _yLim: ClosedRange<Double>?,
    override val flipAxis: Boolean,
) : CoordProvider {

    private val xLim: ClosedRange<Double>? = when {
        flipAxis -> _yLim
        else -> _xLim
    }

    private val yLim: ClosedRange<Double>? = when {
        flipAxis -> _xLim
        else -> _yLim
    }

    override fun buildAxisScaleX(
        scaleProto: Scale<Double>,
        domain: ClosedRange<Double>,
        axisLength: Double,
        breaks: ScaleBreaks
    ): Scale<Double> {
        return buildAxisScaleDefault(
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
        breaks: ScaleBreaks
    ): Scale<Double> {
        return buildAxisScaleDefault(
            scaleProto,
            domain,
            axisLength,
            breaks
        )
    }

    override fun createCoordinateSystem(
        xDomain: ClosedRange<Double>,
        xAxisLength: Double,
        yDomain: ClosedRange<Double>,
        yAxisLength: Double
    ): CoordinateSystem {
        val mapperX = linearMapper(xDomain, xAxisLength)
        val mapperY = linearMapper(yDomain, yAxisLength)
        return Coords.create(
            MapperUtil.map(
                xDomain,
                mapperX
            ),
            MapperUtil.map(
                yDomain,
                mapperY
            ),
            xLim?.let { MapperUtil.map(it, mapperX) },
            yLim?.let { MapperUtil.map(it, mapperY) }
        )
    }

    override fun adjustDomains(
        xDomain: ClosedRange<Double>,
        yDomain: ClosedRange<Double>,
        displaySize: DoubleVector
    ): Pair<ClosedRange<Double>, ClosedRange<Double>> {
        return Pair(xLim ?: xDomain, yLim ?: yDomain)
    }

    companion object {
        fun linearMapper(domain: ClosedRange<Double>, axisLength: Double): (Double?) -> Double? {
            return Mappers.mul(domain, axisLength)
        }

        private fun buildAxisScaleDefault(
            scaleProto: Scale<Double>,
            domain: ClosedRange<Double>,
            axisLength: Double,
            breaks: ScaleBreaks
        ): Scale<Double> {
            return buildAxisScaleDefault(
                scaleProto,
                linearMapper(domain, axisLength),
                breaks
            )
        }

        fun buildAxisScaleDefault(
            scaleProto: Scale<Double>,
            axisMapper: (Double?) -> Double?,
            breaks: ScaleBreaks
        ): Scale<Double> {
            return scaleProto.with()
                .breaks(breaks.domainValues)
                .labels(breaks.labels)
                .mapper(axisMapper)
                .build()
        }
    }
}
