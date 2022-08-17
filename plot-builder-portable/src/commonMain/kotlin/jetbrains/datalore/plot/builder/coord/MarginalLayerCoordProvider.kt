/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.coord

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.base.unsupported.UNSUPPORTED
import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.base.scale.ScaleBreaks
import jetbrains.datalore.plot.builder.MarginSide

internal class MarginalLayerCoordProvider(
    private val margin: MarginSide,
    private val coreCoordProvider: CoordProvider,
) : CoordProviderBase(
    _xLim = null,
    _yLim = null,
    flipAxis = false
) {
    init {
        check(!coreCoordProvider.flipAxis) {
            "`flipped` corrdinate system is not supported on plots with marginal layers."
        }
    }

    override fun with(xLim: DoubleSpan?, yLim: DoubleSpan?, flipped: Boolean): CoordProvider {
        UNSUPPORTED("MarginalLayerCoordProvider.with()")
    }

    override fun buildAxisScaleX(
        scaleProto: Scale<Double>,
        domain: DoubleSpan,
        yDomain: DoubleSpan,
        breaks: ScaleBreaks
    ): Scale<Double> {
        UNSUPPORTED("MarginalLayerCoordProvider.buildAxisScaleX()")
    }

    override fun buildAxisScaleY(
        scaleProto: Scale<Double>,
        domain: DoubleSpan,
        xDomain: DoubleSpan,
        breaks: ScaleBreaks
    ): Scale<Double> {
        UNSUPPORTED("MarginalLayerCoordProvider.buildAxisScaleY()")
    }

    override fun adjustGeomSize(hDomain: DoubleSpan, vDomain: DoubleSpan, geomSize: DoubleVector): DoubleVector {
        UNSUPPORTED("MarginalLayerCoordProvider.adjustGeomSize()")
    }

    override fun adjustDomainsIntern(hDomain: DoubleSpan, vDomain: DoubleSpan): Pair<DoubleSpan, DoubleSpan> {
        UNSUPPORTED("MarginalLayerCoordProvider.adjustDomainsIntern()")
    }
}