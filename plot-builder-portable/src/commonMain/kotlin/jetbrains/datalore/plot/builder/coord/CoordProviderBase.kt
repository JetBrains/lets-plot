/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.coord

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.base.spatial.projections.Projection
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
        yDomain: DoubleSpan,
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
        xDomain: DoubleSpan,
        breaks: ScaleBreaks
    ): Scale<Double> {
        return buildAxisScaleDefault(
            scaleProto,
            breaks
        )
    }

    override fun buildAxisXScaleMapper(
        domain: DoubleSpan,
        axisLength: Double,
        yDomain: DoubleSpan
    ): ScaleMapper<Double> {
        return buildAxisScaleMapperDefault(domain, axisLength)
    }

    override fun buildAxisYScaleMapper(
        domain: DoubleSpan,
        axisLength: Double,
        xDomain: DoubleSpan
    ): ScaleMapper<Double> {
        return buildAxisScaleMapperDefault(domain, axisLength)
    }

    final override fun createCoordinateSystem(
        xDomain: DoubleSpan,
        xMapper: ScaleMapper<Double>,
        yDomain: DoubleSpan,
        yMapper: ScaleMapper<Double>,
    ): CoordinateSystem {
//        val mapperX = linearMapper(xDomain, xAxisLength)
//        val mapperY = linearMapper(yDomain, yAxisLength)

        val projection = object : Projection {
            override fun project(v: DoubleVector): DoubleVector? {
                val x = xMapper.invoke(v.x) ?: return null
                val y = yMapper.invoke(v.y) ?: return null
                return DoubleVector(x, y)
            }

            override fun invert(v: DoubleVector): DoubleVector? = TODO("Not yet implemented")
            override fun validRect(): DoubleRectangle = TODO("Not yet implemented")
            override val cylindrical: Boolean = false
        }

        return Coords.create(
            MapperUtil.map(xDomain, xMapper),
            MapperUtil.map(yDomain, yMapper),
            projection,
            hLim?.let { MapperUtil.map(it, xMapper) },
            vLim?.let { MapperUtil.map(it, yMapper) }
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
