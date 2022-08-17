/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.coord

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.base.spatial.projections.Projection
import jetbrains.datalore.plot.base.CoordinateSystem
import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.base.coord.CoordinatesMapper
import jetbrains.datalore.plot.base.coord.Coords
import jetbrains.datalore.plot.base.scale.ScaleBreaks

interface CoordProvider {
    val flipAxis: Boolean
    val projection: Projection

    fun with(
        xLim: DoubleSpan?,
        yLim: DoubleSpan?,
        flipped: Boolean
    ): CoordProvider

    fun createCoordinateMapper(
        domain: DoubleRectangle,
        clientSize: DoubleVector,
    ): CoordinatesMapper {
        return CoordinatesMapper(domain, clientSize, projection)
    }

    fun createCoordinateSystem(
        domain: DoubleRectangle,
        clientSize: DoubleVector,
    ): CoordinateSystem {
        val coordMapper = CoordinatesMapper(domain, clientSize, projection)
        return Coords.create(coordMapper)
    }

    fun buildAxisScaleX(
        scaleProto: Scale<Double>,
        domain: DoubleSpan,
        yDomain: DoubleSpan,
        breaks: ScaleBreaks
    ): Scale<Double>

    fun buildAxisScaleY(
        scaleProto: Scale<Double>,
        domain: DoubleSpan,
        xDomain: DoubleSpan,
        breaks: ScaleBreaks
    ): Scale<Double>

    fun adjustDomains(
        hDomain: DoubleSpan,
        vDomain: DoubleSpan,
    ): Pair<DoubleSpan, DoubleSpan>

    fun adjustGeomSize(
        hDomain: DoubleSpan,
        vDomain: DoubleSpan,
        geomSize: DoubleVector
    ): DoubleVector
}
