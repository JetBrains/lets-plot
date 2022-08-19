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
    final override val xLim: DoubleSpan?,
    final override val yLim: DoubleSpan?,
    override val flipAxis: Boolean,
) : CoordProvider {

    override val projection: Projection = identity()

    init {
        require(xLim == null || xLim.length > 0.0) { "Coord x-limits range should be > 0.0" }
        require(yLim == null || yLim.length > 0.0) { "Coord y-limits range should be > 0.0" }
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
