/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.coord

import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.CoordinateSystem
import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.base.ScaleMapper
import jetbrains.datalore.plot.base.scale.ScaleBreaks

interface CoordProvider {
    val flipAxis: Boolean

    fun with(
        xLim: DoubleSpan?,
        yLim: DoubleSpan?,
        flipped: Boolean
    ): CoordProvider

    fun createCoordinateSystem(
        xDomain: DoubleSpan,
        xAxisLength: Double,
        yDomain: DoubleSpan,
        yAxisLength: Double
    ): CoordinateSystem

    fun buildAxisScaleX(
        scaleProto: Scale<Double>,
        domain: DoubleSpan,
        breaks: ScaleBreaks
    ): Scale<Double>

    fun buildAxisScaleY(
        scaleProto: Scale<Double>,
        domain: DoubleSpan,
        breaks: ScaleBreaks
    ): Scale<Double>

    fun buildAxisXScaleMapper(
        domain: DoubleSpan,
        axisLength: Double,
    ): ScaleMapper<Double>

    fun buildAxisYScaleMapper(
        domain: DoubleSpan,
        axisLength: Double,
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
