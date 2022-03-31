/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.coord

import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.plot.base.CoordinateSystem
import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.base.ScaleMapper
import jetbrains.datalore.plot.base.coord.Coords
import jetbrains.datalore.plot.base.scale.MapperUtil
import jetbrains.datalore.plot.base.scale.Mappers
import jetbrains.datalore.plot.base.scale.ScaleBreaks

internal abstract class CoordProviderBase(
    _xLim: DoubleSpan?,
    _yLim: DoubleSpan?,
    override val flipAxis: Boolean,
) : CoordProvider {

    private val hLim: DoubleSpan? = when {
        flipAxis -> _yLim
        else -> _xLim
    }

    private val vLim: DoubleSpan? = when {
        flipAxis -> _xLim
        else -> _yLim
    }

    override fun buildAxisScaleX(
        scaleProto: Scale<Double>,
        domain: DoubleSpan,
        breaks: ScaleBreaks
    ): Scale<Double> {
        return buildAxisScaleDefault(
            scaleProto,
            breaks
        )
    }

    override fun buildAxisScaleY(
        scaleProto: Scale<Double>,
        domain: DoubleSpan,
        breaks: ScaleBreaks
    ): Scale<Double> {
        return buildAxisScaleDefault(
            scaleProto,
            breaks
        )
    }

    override fun buildAxisXScaleMapper(domain: DoubleSpan, axisLength: Double): ScaleMapper<Double> {
        return buildAxisScaleMapperDefault(domain, axisLength)
    }

    override fun buildAxisYScaleMapper(domain: DoubleSpan, axisLength: Double): ScaleMapper<Double> {
        return buildAxisScaleMapperDefault(domain, axisLength)
    }

    override fun createCoordinateSystem(
        xDomain: DoubleSpan,
        xAxisLength: Double,
        yDomain: DoubleSpan,
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
            hLim?.let { MapperUtil.map(it, mapperX) },
            vLim?.let { MapperUtil.map(it, mapperY) }
        )
    }

    final override fun adjustDomains(
        hDomain: DoubleSpan,
        vDomain: DoubleSpan,
    ): Pair<DoubleSpan, DoubleSpan> {
        return adjustDomainsIntern(
            hDomain = hLim ?: hDomain,
            vDomain = vLim ?: vDomain
        )
    }

    protected open fun adjustDomainsIntern(
        hDomain: DoubleSpan,
        vDomain: DoubleSpan
    ): Pair<DoubleSpan, DoubleSpan> {
        return (hDomain to vDomain)
    }


    companion object {
        fun linearMapper(domain: DoubleSpan, axisLength: Double): ScaleMapper<Double> {
            return Mappers.mul(domain, axisLength)
        }

        private fun buildAxisScaleMapperDefault(
            domain: DoubleSpan,
            axisLength: Double,
        ): ScaleMapper<Double> {
            return linearMapper(domain, axisLength)
        }

        fun buildAxisScaleDefault(
            scaleProto: Scale<Double>,
            breaks: ScaleBreaks
        ): Scale<Double> {
            return scaleProto.with()
                .breaks(breaks.domainValues)
                .labels(breaks.labels)
                .build()
        }
    }
}
