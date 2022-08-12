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
import jetbrains.datalore.plot.base.ScaleMapper
import jetbrains.datalore.plot.base.scale.ScaleBreaks

interface CoordProvider {
    val flipAxis: Boolean
    val projection: Projection

    fun with(
        xLim: DoubleSpan?,
        yLim: DoubleSpan?,
        flipped: Boolean
    ): CoordProvider

//    fun createCoordinateSystem(
//        xDomain: DoubleSpan,
//        xMapper: ScaleMapper<Double>,
//        yDomain: DoubleSpan,
//        yMapper: ScaleMapper<Double>,
//    ): CoordinateSystem

    fun createCoordinateSystem(
        domain: DoubleRectangle,
        clientSize: DoubleVector,
    ): CoordinateSystem

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

    fun buildAxisXScaleMapper(
        domain: DoubleSpan,
        axisLength: Double,
        yDomain: DoubleSpan,
    ): ScaleMapper<Double>

    fun buildAxisYScaleMapper(
        domain: DoubleSpan,
        axisLength: Double,
        xDomain: DoubleSpan,
    ): ScaleMapper<Double>

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
