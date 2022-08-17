/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.coord

import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.base.spatial.projections.Projection
import jetbrains.datalore.base.spatial.projections.identity
import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.base.scale.ScaleBreaks

internal abstract class CoordProviderBase(
    _xLim: DoubleSpan?,
    _yLim: DoubleSpan?,
    override val flipAxis: Boolean,
) : CoordProvider {

    override val projection: Projection = identity()

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
